package dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization;

import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockVariantOrderer;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class YAIFlightPylonCompat {
    public static final String MOD_ID =
            "yet_another_industrialization";

    public static final ResourceLocation CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "flight_pylon"
            );

    private static final String BLOCK_ENTITY_CLASS =
            "me.luligabi.yet_another_industrialization."
                    + "common.block.machine.flight_pylon."
                    + "FlightPylonBlockEntity";

    private static final String COMPANION_FIELD_NAME =
            "Companion";

    private static final String TIERS_GETTER_NAME =
            "getTIERS";

    private static final String ACTIVE_TIER_GETTER_NAME =
            "getTier";

    private static final String TIER_BLOCK_ID_GETTER_NAME =
            "getBlockId";

    private static final Map<Class<?>, Method>
            ACTIVE_TIER_GETTERS =
            new ConcurrentHashMap<>();

    private static final Map<Class<?>, Method>
            TIER_BLOCK_ID_GETTERS =
            new ConcurrentHashMap<>();

    private static volatile List<ResourceLocation>
            cachedTierOrder =
            List.of();

    private YAIFlightPylonCompat() {
    }

    public static boolean isController(
            ResourceLocation controllerId
    ) {
        return CONTROLLER_ID.equals(
                controllerId
        );
    }

    public static boolean isBlockEntityClassName(
            String className
    ) {
        return BLOCK_ENTITY_CLASS.equals(
                className
        );
    }

    public static List<ResourceLocation> getTierOrder() {
        List<ResourceLocation> currentCache =
                cachedTierOrder;

        if (!currentCache.isEmpty()) {
            return currentCache;
        }

        synchronized (YAIFlightPylonCompat.class) {
            currentCache =
                    cachedTierOrder;

            if (!currentCache.isEmpty()) {
                return currentCache;
            }

            List<?> tiers =
                    loadTierObjects();

            List<ResourceLocation> tierOrder =
                    new ArrayList<>(
                            tiers.size()
                    );

            Set<ResourceLocation> uniqueTierIds =
                    new LinkedHashSet<>();

            for (Object tier : tiers) {
                ResourceLocation tierBlockId =
                        getTierBlockId(
                                tier
                        );

                if (!uniqueTierIds.add(
                        tierBlockId
                )) {
                    throw new IllegalStateException(
                            "YAI Flight Pylon contains "
                                    + "duplicate tier block "
                                    + tierBlockId
                    );
                }

                tierOrder.add(
                        tierBlockId
                );
            }

            if (tierOrder.isEmpty()) {
                throw new IllegalStateException(
                        "YAI Flight Pylon tier list is empty"
                );
            }

            cachedTierOrder =
                    List.copyOf(
                            tierOrder
                    );

            MIMultiblockEditor.LOGGER.info(
                    "Flight Pylon tier order for {}: {}",
                    CONTROLLER_ID,
                    cachedTierOrder
            );

            return cachedTierOrder;
        }
    }

    public static List<KubeJSStructureRegistration>
    orderRegistrations(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (!isController(
                controllerId
        )) {
            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " is not the YAI Flight Pylon"
            );
        }

        if (
                resolution.adapterType()
                        != MultiblockAdapterType
                        .YAI_FLIGHT_PYLON
        ) {
            throw new IllegalArgumentException(
                    "Flight Pylon controller "
                            + controllerId
                            + " was resolved to adapter "
                            + resolution.adapterType().id()
                            + ", but "
                            + MultiblockAdapterType
                            .YAI_FLIGHT_PYLON
                            .id()
                            + " was expected"
            );
        }

        for (
                KubeJSStructureRegistration registration
                : registrations
        ) {
            if (
                    registration
                            .variantId()
                            .isEmpty()
            ) {
                throw new IllegalArgumentException(
                        "Flight Pylon structure "
                                + registration.structureId()
                                + " must define .variant(...)"
                );
            }
        }

        return MultiblockVariantOrderer
                .orderByVariantKeys(
                        controllerId,
                        resolution.adapterType(),
                        registrations,
                        getTierOrder()
                );
    }

    public static int getActiveTierIndex(
            Object blockEntity
    ) {
        ResourceLocation activeTierId =
                getActiveTierId(
                        blockEntity
                );

        List<ResourceLocation> tierOrder =
                getTierOrder();

        int tierIndex =
                tierOrder.indexOf(
                        activeTierId
                );

        if (tierIndex < 0) {
            throw new IllegalStateException(
                    "YAI Flight Pylon selected unknown "
                            + "tier block "
                            + activeTierId
                            + ". Available tiers: "
                            + tierOrder
            );
        }

        return tierIndex;
    }

    private static ResourceLocation getActiveTierId(
            Object blockEntity
    ) {
        if (blockEntity == null) {
            throw new IllegalArgumentException(
                    "Flight Pylon block entity cannot be null"
            );
        }

        try {
            Method getTierMethod =
                    ACTIVE_TIER_GETTERS
                            .computeIfAbsent(
                                    blockEntity.getClass(),
                                    type -> findPublicMethod(
                                            type,
                                            ACTIVE_TIER_GETTER_NAME
                                    )
                            );

            Object tier =
                    getTierMethod.invoke(
                            blockEntity
                    );

            if (tier == null) {
                throw new IllegalStateException(
                        "YAI Flight Pylon getTier() "
                                + "returned null"
                );
            }

            return getTierBlockId(
                    tier
            );
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to read the active YAI "
                            + "Flight Pylon tier",
                    exception
            );
        }
    }

    private static List<?> loadTierObjects() {
        requireModLoaded();

        try {
            Class<?> blockEntityClass =
                    Class.forName(
                            BLOCK_ENTITY_CLASS,
                            true,
                            YAIFlightPylonCompat
                                    .class
                                    .getClassLoader()
                    );

            Field companionField =
                    blockEntityClass.getField(
                            COMPANION_FIELD_NAME
                    );

            Object companion =
                    companionField.get(
                            null
                    );

            if (companion == null) {
                throw new IllegalStateException(
                        "YAI Flight Pylon Companion is null"
                );
            }

            Method getTiersMethod =
                    companion
                            .getClass()
                            .getMethod(
                                    TIERS_GETTER_NAME
                            );

            Object rawTiers =
                    getTiersMethod.invoke(
                            companion
                    );

            if (!(rawTiers instanceof List<?> tiers)) {
                throw new IllegalStateException(
                        "YAI Flight Pylon "
                                + TIERS_GETTER_NAME
                                + "() returned "
                                + (
                                rawTiers == null
                                        ? "null"
                                        : rawTiers
                                        .getClass()
                                        .getName()
                        )
                                + " instead of java.util.List"
                );
            }

            if (tiers.isEmpty()) {
                throw new IllegalStateException(
                        "YAI Flight Pylon tier list "
                                + "is empty. Tiers may not "
                                + "have been initialized yet"
                );
            }

            return tiers;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to load YAI Flight Pylon tiers",
                    exception
            );
        }
    }

    private static ResourceLocation getTierBlockId(
            Object tier
    ) {
        if (tier == null) {
            throw new IllegalStateException(
                    "YAI Flight Pylon tier list "
                            + "contains null"
            );
        }

        try {
            Method blockIdGetter =
                    TIER_BLOCK_ID_GETTERS
                            .computeIfAbsent(
                                    tier.getClass(),
                                    type -> findPublicMethod(
                                            type,
                                            TIER_BLOCK_ID_GETTER_NAME
                                    )
                            );

            Object rawBlockId =
                    blockIdGetter.invoke(
                            tier
                    );

            if (!(rawBlockId
                    instanceof ResourceLocation blockId)) {

                throw new IllegalStateException(
                        "YAI Flight Pylon tier "
                                + tier
                                + " returned invalid block ID "
                                + rawBlockId
                );
            }

            return blockId;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to read a YAI Flight "
                            + "Pylon tier block ID",
                    exception
            );
        }
    }

    private static Method findPublicMethod(
            Class<?> type,
            String methodName
    ) {
        try {
            return type.getMethod(
                    methodName
            );
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException(
                    "Class "
                            + type.getName()
                            + " has no public method "
                            + methodName
                            + "()",
                    exception
            );
        }
    }

    private static void requireModLoaded() {
        if (!ModList.get().isLoaded(
                MOD_ID
        )) {
            throw new IllegalStateException(
                    "Cannot use YAI Flight Pylon "
                            + "compatibility because mod "
                            + MOD_ID
                            + " is not loaded"
            );
        }
    }
}
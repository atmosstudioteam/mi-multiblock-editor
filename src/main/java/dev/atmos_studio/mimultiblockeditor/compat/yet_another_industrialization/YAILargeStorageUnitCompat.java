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

public final class YAILargeStorageUnitCompat {
    public static final String MOD_ID =
            "yet_another_industrialization";

    public static final ResourceLocation CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "large_storage_unit"
            );

    private static final String BLOCK_ENTITY_CLASS =
            "me.luligabi.yet_another_industrialization."
                    + "common.block.machine.large_storage_unit."
                    + "LargeStorageUnitBlockEntity";

    private static final String COMPANION_FIELD_NAME =
            "Companion";

    private static final String TIERS_GETTER_NAME =
            "getTIERS";

    private static final String ACTIVE_TIER_FIELD_NAME =
            "activeTier";

    private static final String ACTIVE_SHAPE_GETTER_NAME =
            "getActiveShape";

    private static final String TIER_BLOCK_ID_GETTER_NAME =
            "getBlockId";

    private static final Map<Class<?>, Field>
            ACTIVE_TIER_FIELDS =
            new ConcurrentHashMap<>();

    private static final Map<Class<?>, Method>
            ACTIVE_SHAPE_GETTERS =
            new ConcurrentHashMap<>();

    private static final Map<Class<?>, Method>
            TIER_BLOCK_ID_GETTERS =
            new ConcurrentHashMap<>();

    private static volatile List<ResourceLocation>
            cachedTierOrder =
            List.of();

    private YAILargeStorageUnitCompat() {
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

        synchronized (YAILargeStorageUnitCompat.class) {
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
                            "YAI Large Storage Unit contains "
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
                        "YAI Large Storage Unit tier list is empty"
                );
            }

            cachedTierOrder =
                    List.copyOf(
                            tierOrder
                    );

            MIMultiblockEditor.LOGGER.info(
                    "Large Storage Unit tier order for {}: {}",
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
                            + " is not the YAI Large Storage Unit"
            );
        }

        if (
                resolution.adapterType()
                        != MultiblockAdapterType
                        .YAI_LARGE_STORAGE_UNIT
        ) {
            throw new IllegalArgumentException(
                    "Large Storage Unit controller "
                            + controllerId
                            + " was resolved to adapter "
                            + resolution.adapterType().id()
                            + ", but "
                            + MultiblockAdapterType
                            .YAI_LARGE_STORAGE_UNIT
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
                        "Large Storage Unit structure "
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
        if (blockEntity == null) {
            throw new IllegalArgumentException(
                    "Large Storage Unit block entity cannot be null"
            );
        }

        try {
            Field activeTierField =
                    ACTIVE_TIER_FIELDS
                            .computeIfAbsent(
                                    blockEntity.getClass(),
                                    type -> findField(
                                            type,
                                            ACTIVE_TIER_FIELD_NAME
                                    )
                            );

            Object activeTierComponent =
                    activeTierField.get(
                            blockEntity
                    );

            if (activeTierComponent == null) {
                throw new IllegalStateException(
                        "YAI Large Storage Unit activeTier "
                                + "component is null"
                );
            }

            Method activeShapeGetter =
                    ACTIVE_SHAPE_GETTERS
                            .computeIfAbsent(
                                    activeTierComponent.getClass(),
                                    type -> findPublicMethod(
                                            type,
                                            ACTIVE_SHAPE_GETTER_NAME
                                    )
                            );

            Object rawActiveShape =
                    activeShapeGetter.invoke(
                            activeTierComponent
                    );

            if (!(rawActiveShape
                    instanceof Number number)) {
                throw new IllegalStateException(
                        "YAI Large Storage Unit active tier "
                                + "getter returned invalid value "
                                + rawActiveShape
                );
            }

            int activeTierIndex =
                    number.intValue();

            int tierCount =
                    getTierOrder().size();

            if (
                    activeTierIndex < 0
                            || activeTierIndex >= tierCount
            ) {
                throw new IllegalStateException(
                        "YAI Large Storage Unit selected invalid "
                                + "tier index "
                                + activeTierIndex
                                + ". Available tier count: "
                                + tierCount
                );
            }

            return activeTierIndex;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to read the active YAI "
                            + "Large Storage Unit tier",
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
                            YAILargeStorageUnitCompat
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
                        "YAI Large Storage Unit Companion is null"
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
                        "YAI Large Storage Unit "
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
                        "YAI Large Storage Unit tier list "
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
                    "Failed to load YAI Large Storage Unit tiers",
                    exception
            );
        }
    }

    private static ResourceLocation getTierBlockId(
            Object tier
    ) {
        if (tier == null) {
            throw new IllegalStateException(
                    "YAI Large Storage Unit tier list "
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
                        "YAI Large Storage Unit tier "
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
                    "Failed to read a YAI Large Storage "
                            + "Unit tier block ID",
                    exception
            );
        }
    }

    private static Field findField(
            Class<?> type,
            String fieldName
    ) {
        Class<?> currentType =
                type;

        while (currentType != null) {
            try {
                Field field =
                        currentType.getDeclaredField(
                                fieldName
                        );

                field.setAccessible(
                        true
                );

                return field;
            } catch (NoSuchFieldException ignored) {
                currentType =
                        currentType.getSuperclass();
            }
        }

        throw new IllegalStateException(
                "Class "
                        + type.getName()
                        + " has no field "
                        + fieldName
        );
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
                    "Cannot use YAI Large Storage Unit "
                            + "compatibility because mod "
                            + MOD_ID
                            + " is not loaded"
            );
        }
    }
}
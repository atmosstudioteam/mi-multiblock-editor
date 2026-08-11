package dev.atmos_studio.mimultiblockeditor.compat.extended_industrialization;

import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockVariantOrderer;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ExtendedTeslaTowerCompat {
    public static final String MOD_ID =
            "extended_industrialization";

    public static final ResourceLocation CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "tesla_tower"
            );

    private static final String BLOCK_ENTITY_CLASS =
            "net.swedz.extended_industrialization."
                    + "machines.blockentity.multiblock."
                    + "teslatower.TeslaTowerBlockEntity";

    private ExtendedTeslaTowerCompat() {
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
        requireModLoaded();

        Map<?, ?> tiersByWinding =
                loadTiersByWinding();

        List<TierOrderEntry> tierEntries =
                new ArrayList<>(
                        tiersByWinding.size()
                );

        for (
                Map.Entry<?, ?> entry
                : tiersByWinding.entrySet()
        ) {
            if (!(entry.getKey()
                    instanceof ResourceLocation windingId)) {

                throw new IllegalStateException(
                        "Tesla Tower tier map contains "
                                + "an invalid winding ID: "
                                + entry.getKey()
                );
            }

            Object tier =
                    entry.getValue();

            if (tier == null) {
                throw new IllegalStateException(
                        "Tesla Tower tier for winding "
                                + windingId
                                + " is null"
                );
            }

            int maxDistance =
                    readMaxDistance(
                            tier,
                            windingId
                    );

            tierEntries.add(
                    new TierOrderEntry(
                            windingId,
                            maxDistance
                    )
            );
        }

        tierEntries.sort(
                Comparator.comparingInt(
                        TierOrderEntry::maxDistance
                )
        );

        Set<ResourceLocation> uniqueIds =
                new LinkedHashSet<>();

        List<ResourceLocation> tierOrder =
                new ArrayList<>(
                        tierEntries.size()
                );

        for (
                TierOrderEntry tierEntry
                : tierEntries
        ) {
            if (!uniqueIds.add(
                    tierEntry.windingId()
            )) {
                throw new IllegalStateException(
                        "Tesla Tower contains duplicate "
                                + "winding tier "
                                + tierEntry.windingId()
                );
            }

            tierOrder.add(
                    tierEntry.windingId()
            );
        }

        if (tierOrder.isEmpty()) {
            throw new IllegalStateException(
                    "Extended Industrialization "
                            + "Tesla Tower tier list is empty"
            );
        }

        MIMultiblockEditor.LOGGER.info(
                "Tesla Tower winding tier order for {}: {}",
                CONTROLLER_ID,
                tierOrder
        );

        return List.copyOf(
                tierOrder
        );
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
                            + " is not the Extended "
                            + "Industrialization Tesla Tower"
            );
        }

        if (
                resolution.adapterType()
                        != MultiblockAdapterType
                        .TESSERACT_MULTIPLIED_CRAFTING
        ) {
            throw new IllegalArgumentException(
                    "Tesla Tower controller "
                            + controllerId
                            + " was resolved to adapter "
                            + resolution.adapterType().id()
                            + ", but "
                            + MultiblockAdapterType
                            .TESSERACT_MULTIPLIED_CRAFTING
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
                        "Tesla Tower structure "
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

    private static Map<?, ?> loadTiersByWinding() {
        try {
            Class<?> blockEntityClass =
                    Class.forName(
                            BLOCK_ENTITY_CLASS,
                            true,
                            ExtendedTeslaTowerCompat
                                    .class
                                    .getClassLoader()
                    );

            Method getTiersMethod =
                    blockEntityClass.getMethod(
                            "getTiersByWinding"
                    );

            Object rawTiers =
                    getTiersMethod.invoke(
                            null
                    );

            if (!(rawTiers instanceof Map<?, ?> tiers)) {
                throw new IllegalStateException(
                        "TeslaTowerBlockEntity."
                                + "getTiersByWinding() returned "
                                + (
                                rawTiers == null
                                        ? "null"
                                        : rawTiers
                                        .getClass()
                                        .getName()
                        )
                                + " instead of java.util.Map"
                );
            }

            return tiers;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to load Extended "
                            + "Industrialization "
                            + "Tesla Tower tiers",
                    exception
            );
        }
    }

    private static int readMaxDistance(
            Object tier,
            ResourceLocation windingId
    ) {
        try {
            Method maxDistanceMethod =
                    tier.getClass().getMethod(
                            "maxDistance"
                    );

            Object rawDistance =
                    maxDistanceMethod.invoke(
                            tier
                    );

            if (!(rawDistance instanceof Number number)) {
                throw new IllegalStateException(
                        "Tesla Tower tier "
                                + windingId
                                + " returned invalid "
                                + "maxDistance value "
                                + rawDistance
                );
            }

            return number.intValue();
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to read maxDistance from "
                            + "Tesla Tower tier "
                            + windingId,
                    exception
            );
        }
    }

    private static void requireModLoaded() {
        if (!ModList.get().isLoaded(
                MOD_ID
        )) {
            throw new IllegalStateException(
                    "Cannot use Tesla Tower "
                            + "compatibility because mod "
                            + MOD_ID
                            + " is not loaded"
            );
        }
    }

    private record TierOrderEntry(
            ResourceLocation windingId,
            int maxDistance
    ) {
    }
}
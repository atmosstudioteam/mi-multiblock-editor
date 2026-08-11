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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ExtendedLargeElectricFurnaceCompat {
    public static final String MOD_ID =
            "extended_industrialization";

    public static final ResourceLocation CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "large_electric_furnace"
            );

    private static final String BLOCK_ENTITY_CLASS =
            "net.swedz.extended_industrialization."
                    + "machines.blockentity.multiblock."
                    + "LargeElectricFurnaceBlockEntity";

    private ExtendedLargeElectricFurnaceCompat() {
    }

    public static boolean isController(
            ResourceLocation controllerId
    ) {
        return CONTROLLER_ID.equals(
                controllerId
        );
    }

    public static List<ResourceLocation> getTierOrder() {
        List<?> tiers =
                loadTierObjects();

        List<ResourceLocation> tierOrder =
                new ArrayList<>(
                        tiers.size()
                );

        Set<ResourceLocation> uniqueTierIds =
                new LinkedHashSet<>();

        for (Object tier : tiers) {
            ResourceLocation coilId =
                    getTierCoilId(
                            tier
                    );

            if (!uniqueTierIds.add(
                    coilId
            )) {
                throw new IllegalStateException(
                        "Extended Industrialization "
                                + "Large Electric Furnace "
                                + "contains duplicate coil tier "
                                + coilId
                );
            }

            tierOrder.add(
                    coilId
            );
        }

        MIMultiblockEditor.LOGGER.info(
                "Large Electric Furnace coil tier order "
                        + "for {}: {}",
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
                            + "Industrialization "
                            + "Large Electric Furnace"
            );
        }

        if (resolution.adapterType()
                != MultiblockAdapterType
                .TESSERACT_MULTIPLIED_CRAFTING) {

            throw new IllegalArgumentException(
                    "Large Electric Furnace controller "
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
            if (registration
                    .variantId()
                    .isEmpty()) {

                throw new IllegalArgumentException(
                        "Large Electric Furnace structure "
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

    private static List<?> loadTierObjects() {
        requireModLoaded();

        try {
            Class<?> blockEntityClass =
                    Class.forName(
                            BLOCK_ENTITY_CLASS,
                            true,
                            ExtendedLargeElectricFurnaceCompat
                                    .class
                                    .getClassLoader()
                    );

            Method getTiersMethod =
                    blockEntityClass.getMethod(
                            "getTiers"
                    );

            Object rawTiers =
                    getTiersMethod.invoke(
                            null
                    );

            if (!(rawTiers instanceof List<?> tiers)) {
                throw new IllegalStateException(
                        "LargeElectricFurnaceBlockEntity."
                                + "getTiers() returned "
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
                        "Extended Industrialization "
                                + "Large Electric Furnace tier "
                                + "list is empty. Tiers may not "
                                + "have been initialized yet"
                );
            }

            return tiers;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to load Extended "
                            + "Industrialization Large "
                            + "Electric Furnace tiers",
                    exception
            );
        }
    }

    private static ResourceLocation getTierCoilId(
            Object tier
    ) {
        if (tier == null) {
            throw new IllegalStateException(
                    "Large Electric Furnace tier "
                            + "list contains null"
            );
        }

        try {
            Method blockIdMethod =
                    tier.getClass().getMethod(
                            "blockId"
                    );

            Object rawBlockId =
                    blockIdMethod.invoke(
                            tier
                    );

            if (!(rawBlockId
                    instanceof ResourceLocation blockId)) {

                throw new IllegalStateException(
                        "Large Electric Furnace tier "
                                + tier
                                + " returned invalid coil ID "
                                + rawBlockId
                );
            }

            return blockId;
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to read the coil ID from "
                            + "an Extended Industrialization "
                            + "Large Electric Furnace tier",
                    exception
            );
        }
    }

    private static void requireModLoaded() {
        if (!ModList.get().isLoaded(
                MOD_ID
        )) {
            throw new IllegalStateException(
                    "Cannot use Large Electric Furnace "
                            + "compatibility because mod "
                            + MOD_ID
                            + " is not loaded"
            );
        }
    }
}
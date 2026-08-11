package dev.atmos_studio.mimultiblockeditor.adapter;

import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MultiblockVariantOrderer {
    private static final String
            INDUSTRIALIZATION_OVERDRIVE_MOD_ID =
            "industrialization_overdrive";

    private static final ResourceLocation
            PYROLYSE_OVEN_CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    INDUSTRIALIZATION_OVERDRIVE_MOD_ID,
                    "pyrolyse_oven"
            );

    private static final String
            PYROLYSE_OVEN_BLOCK_ENTITY_CLASS =
            "dev.wp.industrialization_overdrive."
                    + "machines.blockentities.multiblock."
                    + "PyrolyseOvenBlockEntity";

    private MultiblockVariantOrderer() {
    }

    public static List<KubeJSStructureRegistration> order(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {

        if (PYROLYSE_OVEN_CONTROLLER_ID.equals(
                controllerId
        )) {
            return orderPyrolyseOven(
                    controllerId,
                    resolution,
                    registrations
            );
        }

        return switch (resolution.adapterType()) {
            case ELECTRIC_BLAST_FURNACE ->
                    orderElectricBlastFurnace(
                            controllerId,
                            registrations
                    );

            default ->
                    rejectUnexpectedVariants(
                            controllerId,
                            resolution,
                            registrations
                    );
        };
    }

    private static List<KubeJSStructureRegistration>
    orderElectricBlastFurnace(
            ResourceLocation controllerId,
            List<KubeJSStructureRegistration> registrations
    ) {
        List<ResourceLocation> expectedVariants =
                ElectricBlastFurnaceBlockEntity
                        .tiers
                        .stream()
                        .map(
                                ElectricBlastFurnaceBlockEntity
                                        .Tier::coilBlockId
                        )
                        .toList();

        return orderByVariantKeys(
                controllerId,
                MultiblockAdapterType
                        .ELECTRIC_BLAST_FURNACE,
                registrations,
                expectedVariants,
                true
        );
    }

    private static List<KubeJSStructureRegistration>
    orderPyrolyseOven(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (resolution.adapterType()
                != MultiblockAdapterType
                .TESSERACT_MULTIPLIED_CRAFTING) {

            throw new IllegalArgumentException(
                    "Pyrolyse Oven controller "
                            + controllerId
                            + " was resolved to adapter "
                            + resolution.adapterType().id()
                            + ", but adapter "
                            + MultiblockAdapterType
                            .TESSERACT_MULTIPLIED_CRAFTING
                            .id()
                            + " was expected"
            );
        }

        List<ResourceLocation> expectedVariants =
                loadPyrolyseOvenTierOrder(
                        controllerId
                );

        return orderByVariantKeys(
                controllerId,
                resolution.adapterType(),
                registrations,
                expectedVariants,
                false
        );
    }

    private static List<ResourceLocation>
    loadPyrolyseOvenTierOrder(
            ResourceLocation controllerId
    ) {
        if (!ModList.get().isLoaded(
                INDUSTRIALIZATION_OVERDRIVE_MOD_ID
        )) {
            throw new IllegalStateException(
                    "Cannot resolve Pyrolyse Oven tiers for "
                            + controllerId
                            + " because mod "
                            + INDUSTRIALIZATION_OVERDRIVE_MOD_ID
                            + " is not loaded"
            );
        }

        try {
            Class<?> blockEntityClass =
                    Class.forName(
                            PYROLYSE_OVEN_BLOCK_ENTITY_CLASS,
                            true,
                            MultiblockVariantOrderer
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
                        "Method "
                                + PYROLYSE_OVEN_BLOCK_ENTITY_CLASS
                                + ".getTiers() returned "
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
                        "Pyrolyse Oven tier list is empty for "
                                + controllerId
                                + ". Industrialization Overdrive "
                                + "tiers may not have been initialized yet"
                );
            }

            List<ResourceLocation> expectedVariants =
                    new ArrayList<>(
                            tiers.size()
                    );

            for (Object tier : tiers) {
                if (tier == null) {
                    throw new IllegalStateException(
                            "Pyrolyse Oven tier list contains null"
                    );
                }

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
                            "Pyrolyse Oven tier "
                                    + tier
                                    + " returned invalid blockId: "
                                    + rawBlockId
                    );
                }

                expectedVariants.add(
                        blockId
                );
            }

            validateExpectedVariantKeys(
                    controllerId,
                    MultiblockAdapterType
                            .TESSERACT_MULTIPLIED_CRAFTING,
                    expectedVariants
            );

            MIMultiblockEditor.LOGGER.info(
                    "Pyrolyse Oven tier order for {}: {}",
                    controllerId,
                    expectedVariants
            );

            return List.copyOf(
                    expectedVariants
            );
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to read Pyrolyse Oven tiers "
                            + "from optional mod class "
                            + PYROLYSE_OVEN_BLOCK_ENTITY_CLASS,
                    exception
            );
        }
    }

    public static List<KubeJSStructureRegistration>
    orderByVariantKeys(
            ResourceLocation controllerId,
            MultiblockAdapterType adapterType,
            List<KubeJSStructureRegistration> registrations,
            List<ResourceLocation> expectedVariants
    ) {
        return orderByVariantKeys(
                controllerId,
                adapterType,
                registrations,
                expectedVariants,
                true
        );
    }

    private static List<KubeJSStructureRegistration>
    orderByVariantKeys(
            ResourceLocation controllerId,
            MultiblockAdapterType adapterType,
            List<KubeJSStructureRegistration> registrations,
            List<ResourceLocation> expectedVariants,
            boolean allowLegacyRegistrationOrder
    ) {
        validateExpectedVariantKeys(
                controllerId,
                adapterType,
                expectedVariants
        );

        boolean hasAnyVariant =
                registrations
                        .stream()
                        .anyMatch(
                                registration ->
                                        registration
                                                .variantId()
                                                .isPresent()
                        );

        boolean hasVariantForEveryShape =
                registrations
                        .stream()
                        .allMatch(
                                registration ->
                                        registration
                                                .variantId()
                                                .isPresent()
                        );

        if (!hasAnyVariant) {
            if (!allowLegacyRegistrationOrder) {
                throw new IllegalArgumentException(
                        "Controller "
                                + controllerId
                                + " using adapter "
                                + adapterType.id()
                                + " requires .variant(...) "
                                + "on every KubeJS shape"
                );
            }

            MIMultiblockEditor.LOGGER.warn(
                    "Controller {} uses legacy ordered KubeJS "
                            + "shape registration for adapter {}. "
                            + "Add .variant(...) to every shape "
                            + "to make the order independent",
                    controllerId,
                    adapterType.id()
            );

            return List.copyOf(
                    registrations
            );
        }

        /*
         * Нельзя смешивать формы с variant и без него.
         */
        if (!hasVariantForEveryShape) {
            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " mixes KubeJS shapes with "
                            + ".variant(...) and shapes without it. "
                            + "Either every shape must define a "
                            + "variant or none of them may define one"
            );
        }

        Map<ResourceLocation, KubeJSStructureRegistration>
                registrationsByVariant =
                new LinkedHashMap<>();

        for (
                KubeJSStructureRegistration registration
                : registrations
        ) {
            ResourceLocation variantId =
                    registration
                            .variantId()
                            .orElseThrow();

            KubeJSStructureRegistration previous =
                    registrationsByVariant.putIfAbsent(
                            variantId,
                            registration
                    );

            if (previous != null) {
                throw new IllegalArgumentException(
                        "Duplicate variant "
                                + variantId
                                + " for controller "
                                + controllerId
                                + ": structures "
                                + previous.structureId()
                                + " and "
                                + registration.structureId()
                );
            }
        }

        Set<ResourceLocation> expectedSet =
                new LinkedHashSet<>(
                        expectedVariants
                );

        Set<ResourceLocation> actualSet =
                new LinkedHashSet<>(
                        registrationsByVariant.keySet()
                );

        Set<ResourceLocation> missingVariants =
                new LinkedHashSet<>(
                        expectedSet
                );

        missingVariants.removeAll(
                actualSet
        );

        Set<ResourceLocation> unknownVariants =
                new LinkedHashSet<>(
                        actualSet
                );

        unknownVariants.removeAll(
                expectedSet
        );

        if (!missingVariants.isEmpty()
                || !unknownVariants.isEmpty()) {

            throw new IllegalArgumentException(
                    "Variant mismatch for controller "
                            + controllerId
                            + " and adapter "
                            + adapterType.id()
                            + ". Missing variants: "
                            + missingVariants
                            + "; unknown variants: "
                            + unknownVariants
            );
        }

        List<KubeJSStructureRegistration>
                orderedRegistrations =
                new ArrayList<>(
                        expectedVariants.size()
                );

        for (
                ResourceLocation expectedVariant
                : expectedVariants
        ) {
            orderedRegistrations.add(
                    registrationsByVariant.get(
                            expectedVariant
                    )
            );
        }

        MIMultiblockEditor.LOGGER.info(
                "Ordered {} variant-keyed KubeJS shape(s) "
                        + "for controller {} using adapter {}: {}",
                orderedRegistrations.size(),
                controllerId,
                adapterType.id(),
                expectedVariants
        );

        return List.copyOf(
                orderedRegistrations
        );
    }

    private static void validateExpectedVariantKeys(
            ResourceLocation controllerId,
            MultiblockAdapterType adapterType,
            List<ResourceLocation> expectedVariants
    ) {
        if (expectedVariants == null
                || expectedVariants.isEmpty()) {

            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " using adapter "
                            + adapterType.id()
                            + " has no expected variant keys"
            );
        }

        if (expectedVariants
                .stream()
                .anyMatch(variant -> variant == null)) {

            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " using adapter "
                            + adapterType.id()
                            + " has a null expected variant key"
            );
        }

        Set<ResourceLocation> uniqueVariants =
                new LinkedHashSet<>(
                        expectedVariants
                );

        if (uniqueVariants.size()
                != expectedVariants.size()) {

            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " using adapter "
                            + adapterType.id()
                            + " has duplicate expected variants: "
                            + expectedVariants
            );
        }
    }

    private static List<KubeJSStructureRegistration>
    rejectUnexpectedVariants(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {
        List<ResourceLocation> unexpectedVariants =
                registrations
                        .stream()
                        .flatMap(
                                registration ->
                                        registration
                                                .variantId()
                                                .stream()
                        )
                        .toList();

        if (!unexpectedVariants.isEmpty()) {
            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " uses .variant(...) with adapter "
                            + resolution
                            .adapterType()
                            .id()
                            + ", but this adapter does not support "
                            + "variant-keyed shapes. Variants: "
                            + unexpectedVariants
            );
        }

        return List.copyOf(
                registrations
        );
    }
}
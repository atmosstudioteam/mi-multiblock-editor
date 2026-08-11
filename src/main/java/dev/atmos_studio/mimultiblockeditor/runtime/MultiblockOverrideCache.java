package dev.atmos_studio.mimultiblockeditor.runtime;

import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockVariantOrderer;
import dev.atmos_studio.mimultiblockeditor.adapter.NuclearReactorAdapterRules;
import dev.atmos_studio.mimultiblockeditor.adapter.LargeTankAdapterRules;
import aztech.modern_industrialization.config.MIStartupConfig;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolver;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.compile.ShapeCompiler;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MultiblockOverrideCache {
    private static volatile Map<
            ResourceLocation,
            CompiledMultiblockOverride
            > overrides = Map.of();

    private static volatile long generation = 0;

    private static volatile int compiledDefinitionCount = -1;

    private MultiblockOverrideCache() {
    }

    public static synchronized void ensureBuilt() {
        int currentDefinitionCount =
                KubeJSStructureRegistry.size();

        if (compiledDefinitionCount
                == currentDefinitionCount) {
            return;
        }

        rebuild();
    }

    public static synchronized void rebuild() {
        Map<
                ResourceLocation,
                CompiledMultiblockOverride
                > compiledOverrides =
                new LinkedHashMap<>();

        Map<
                ResourceLocation,
                List<KubeJSStructureRegistration>
                > definitions =
                KubeJSStructureRegistry.byController();

        boolean hadCompilationFailures = false;
        int skippedUnsupported = 0;

        for (Map.Entry<
                ResourceLocation,
                List<KubeJSStructureRegistration>
                > controllerEntry : definitions.entrySet()) {

            ResourceLocation controllerId =
                    controllerEntry.getKey();

            List<KubeJSStructureRegistration> registrations =
                    controllerEntry.getValue();

            try {
                validateRegistrations(
                        controllerId,
                        registrations
                );

                MultiblockAdapterResolution resolution =
                        MultiblockAdapterResolver.resolve(
                                controllerId
                        );

                logResolution(
                        resolution
                );

                if (!resolution.supported()) {
                    skippedUnsupported++;

                    MIMultiblockEditor.LOGGER.warn(
                            "Skipping unsupported multiblock "
                                    + "override for {}: adapter={}, "
                                    + "blockEntity={}, reason={}",
                            controllerId,
                            resolution.adapterType().id(),
                            resolution.blockEntityClassName(),
                            resolution.details()
                    );

                    continue;
                }

                List<KubeJSStructureRegistration>
                        orderedRegistrations =
                        MultiblockVariantOrderer.order(
                                controllerId,
                                resolution,
                                registrations
                        );

                validateAdapterRequirements(
                        controllerId,
                        resolution,
                        orderedRegistrations
                );

                int shapeCount =
                        getShapeCountToCompile(
                                resolution,
                                orderedRegistrations
                        );

                ShapeTemplate[] shapes =
                        new ShapeTemplate[
                                shapeCount
                                ];

                for (int index = 0;
                     index < shapeCount;
                     index++) {

                    KubeJSStructureRegistration registration =
                            orderedRegistrations.get(index);

                    shapes[index] = ShapeCompiler.compile(
                            registration.structureId(),
                            registration.definition()
                    );
                }

                CompiledMultiblockOverride compiledOverride =
                        new CompiledMultiblockOverride(
                                resolution.adapterType(),
                                shapes
                        );

                compiledOverrides.put(
                        controllerId,
                        compiledOverride
                );

                MIMultiblockEditor.LOGGER.info(
                        "Compiled KubeJS multiblock override "
                                + "for {} with {} shape(s), adapter={}",
                        controllerId,
                        compiledOverride.size(),
                        compiledOverride.adapterType().id()
                );
            } catch (RuntimeException exception) {
                hadCompilationFailures = true;

                MIMultiblockEditor.LOGGER.error(
                        "Failed to compile KubeJS multiblock "
                                + "override for {}",
                        controllerId,
                        exception
                );
            }
        }

        overrides = Map.copyOf(
                compiledOverrides
        );

        generation++;

        if (hadCompilationFailures) {
            compiledDefinitionCount = -1;
        } else {
            compiledDefinitionCount =
                    KubeJSStructureRegistry.size();
        }

        MIMultiblockEditor.LOGGER.info(
                "Loaded {} compiled KubeJS MI "
                        + "multiblock override(s), skipped {} "
                        + "unsupported controller(s); generation {}",
                overrides.size(),
                skippedUnsupported,
                generation
        );
    }

    private static void validateRegistrations(
            ResourceLocation controllerId,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (registrations == null
                || registrations.isEmpty()) {
            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " has no registered structures"
            );
        }
    }

    private static int getShapeCountToCompile(
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (resolution.adapterType()
                == MultiblockAdapterType
                .DISTILLATION_TOWER) {

            return MIStartupConfig.INSTANCE
                    .maxDistillationTowerHeight
                    .getAsInt();
        }

        return registrations.size();
    }

    private static void validateAdapterRequirements(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {
        switch (resolution.adapterType()) {
            case ELECTRIC_BLAST_FURNACE ->
                    validateElectricBlastFurnace(
                            controllerId,
                            registrations
                    );

            case STEAM_BOILER ->
                    validateSingleShape(
                            controllerId,
                            resolution.adapterType(),
                            registrations
                    );

            case DISTILLATION_TOWER ->
                    validateDistillationTower(
                            controllerId,
                            registrations
                    );

            case LARGE_TANK ->
                    LargeTankAdapterRules.validate(
                            controllerId,
                            registrations
                    );

            case NUCLEAR_REACTOR ->
                    NuclearReactorAdapterRules.validate(
                            controllerId,
                            registrations
                    );

            default -> {
            }
        }
    }

    private static void validateElectricBlastFurnace(
            ResourceLocation controllerId,
            List<KubeJSStructureRegistration> registrations
    ) {
        List<ResourceLocation> tierOrder =
                ElectricBlastFurnaceBlockEntity.tiers
                        .stream()
                        .map(
                                ElectricBlastFurnaceBlockEntity
                                        .Tier::coilBlockId
                        )
                        .toList();

        int expectedShapeCount =
                tierOrder.size();

        int actualShapeCount =
                registrations.size();

        MIMultiblockEditor.LOGGER.info(
                "Electric Blast Furnace tier order for {}: {}",
                controllerId,
                tierOrder
        );

        if (actualShapeCount != expectedShapeCount) {
            throw new IllegalArgumentException(
                    "Electric Blast Furnace controller "
                            + controllerId
                            + " requires exactly "
                            + expectedShapeCount
                            + " KubeJS structure shape(s), "
                            + "but "
                            + actualShapeCount
                            + " were registered. "
                            + "Shapes must be registered in this "
                            + "coil tier order: "
                            + tierOrder
            );
        }
    }

    private static void validateSingleShape(
            ResourceLocation controllerId,
            MultiblockAdapterType adapterType,
            List<KubeJSStructureRegistration> registrations
    ) {
        int actualShapeCount =
                registrations.size();

        if (actualShapeCount != 1) {
            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " using adapter "
                            + adapterType.id()
                            + " requires exactly 1 KubeJS "
                            + "structure shape, but "
                            + actualShapeCount
                            + " were registered"
            );
        }
    }

    private static void validateDistillationTower(
            ResourceLocation controllerId,
            List<KubeJSStructureRegistration> registrations
    ) {
        int configuredShapeCount =
                MIStartupConfig.INSTANCE
                        .maxDistillationTowerHeight
                        .getAsInt();

        int registeredShapeCount =
                registrations.size();

        MIMultiblockEditor.LOGGER.info(
                "Distillation Tower {} has configured height {}; "
                        + "{} ordered KubeJS shape definition(s) "
                        + "were registered",
                controllerId,
                configuredShapeCount,
                registeredShapeCount
        );

        if (registeredShapeCount
                < configuredShapeCount) {

            throw new IllegalArgumentException(
                    "Distillation Tower controller "
                            + controllerId
                            + " requires at least "
                            + configuredShapeCount
                            + " ordered KubeJS structure "
                            + "shape(s), but only "
                            + registeredShapeCount
                            + " were registered. "
                            + "Shape 0 must represent 1 fluid "
                            + "output, shape 1 must represent 2 "
                            + "fluid outputs, and so on"
            );
        }

        if (registeredShapeCount
                > configuredShapeCount) {

            MIMultiblockEditor.LOGGER.info(
                    "Distillation Tower {} will use the first "
                            + "{} of {} registered shape definition(s); "
                            + "the remaining definitions exceed the "
                            + "configured maximum height and will be ignored",
                    controllerId,
                    configuredShapeCount,
                    registeredShapeCount
            );
        }
    }

    private static void logResolution(
            MultiblockAdapterResolution resolution
    ) {
        if (resolution.supported()) {
            MIMultiblockEditor.LOGGER.info(
                    "Resolved multiblock controller {}: "
                            + "blockEntity={}, adapter={}, supported=true",
                    resolution.controllerId(),
                    resolution.blockEntityClassName(),
                    resolution.adapterType().id()
            );
        } else {
            MIMultiblockEditor.LOGGER.warn(
                    "Resolved multiblock controller {}: "
                            + "blockEntity={}, adapter={}, "
                            + "supported=false",
                    resolution.controllerId(),
                    resolution.blockEntityClassName(),
                    resolution.adapterType().id()
            );
        }
    }

    public static CompiledMultiblockOverride get(
            ResourceLocation controllerId
    ) {
        return overrides.get(
                controllerId
        );
    }

    public static Map<
            ResourceLocation,
            CompiledMultiblockOverride
            > snapshot() {

        return overrides;
    }

    public static long generation() {
        return generation;
    }

    public static synchronized void clear() {
        overrides = Map.of();
        compiledDefinitionCount = -1;
        generation++;

        MIMultiblockEditor.LOGGER.info(
                "Cleared MI multiblock override cache; generation {}",
                generation
        );
    }
}
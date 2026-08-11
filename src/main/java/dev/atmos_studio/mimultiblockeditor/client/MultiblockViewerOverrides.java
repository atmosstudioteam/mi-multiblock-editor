package dev.atmos_studio.mimultiblockeditor.client;

import aztech.modern_industrialization.compat.rei.machines.ReiMachineRecipes;
import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.runtime.CompiledMultiblockOverride;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;

public final class MultiblockViewerOverrides {
    private MultiblockViewerOverrides() {
    }

    public static synchronized void apply() {
        Map<
                ResourceLocation,
                CompiledMultiblockOverride
                > overrides =
                MultiblockOverrideCache.snapshot();

        if (overrides.isEmpty()) {
            return;
        }

        Set<ResourceLocation> overriddenControllers =
                overrides.keySet();

        int oldSize =
                ReiMachineRecipes.multiblockShapes.size();

        ReiMachineRecipes.multiblockShapes.removeIf(
                entry -> overriddenControllers.contains(
                        entry.machine()
                )
        );

        int removed =
                oldSize
                        - ReiMachineRecipes
                        .multiblockShapes
                        .size();

        int added = 0;

        for (Map.Entry<
                ResourceLocation,
                CompiledMultiblockOverride
                > entry : overrides.entrySet()) {

            ResourceLocation controllerId =
                    entry.getKey();

            CompiledMultiblockOverride override =
                    entry.getValue();

            for (int index = 0;
                 index < override.size();
                 index++) {

                String alternative =
                        override.size() == 1
                                ? null
                                : "mi_multiblock_editor_"
                                + index;

                ReiMachineRecipes.registerMultiblockShape(
                        controllerId,
                        override.shapeAt(index),
                        alternative
                );

                added++;
            }
        }

        MIMultiblockEditor.LOGGER.info(
                "Updated MI multiblock viewer shapes: "
                        + "removed {}, added {}",
                removed,
                added
        );
    }
}
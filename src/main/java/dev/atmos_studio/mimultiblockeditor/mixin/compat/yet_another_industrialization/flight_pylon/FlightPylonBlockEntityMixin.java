package dev.atmos_studio.mimultiblockeditor.mixin.compat.yet_another_industrialization.flight_pylon;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization.YAIFlightPylonCompat;
import dev.atmos_studio.mimultiblockeditor.runtime.CompiledMultiblockOverride;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets =
                "me.luligabi.yet_another_industrialization."
                        + "common.block.machine.flight_pylon."
                        + "FlightPylonBlockEntity",
        remap = false
)
public abstract class FlightPylonBlockEntityMixin {

    @Inject(
            method = "getActiveShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementActiveShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        MultiblockMachineBlockEntity self =
                (MultiblockMachineBlockEntity)
                        (Object) this;

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        self.getBlockState().getBlock()
                );

        if (!YAIFlightPylonCompat
                .isController(
                        controllerId
                )) {
            return;
        }

        CompiledMultiblockOverride override =
                MultiblockOverrideCache.get(
                        controllerId
                );

        if (override == null) {
            return;
        }

        if (
                override.adapterType()
                        != MultiblockAdapterType
                        .YAI_FLIGHT_PYLON
        ) {
            return;
        }

        int activeTierIndex =
                YAIFlightPylonCompat
                        .getActiveTierIndex(
                                this
                        );

        callback.setReturnValue(
                override.shapeAt(
                        activeTierIndex
                )
        );
    }
}
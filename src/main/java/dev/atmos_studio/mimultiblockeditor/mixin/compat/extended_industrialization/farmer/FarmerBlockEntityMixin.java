package dev.atmos_studio.mimultiblockeditor.mixin.compat.extended_industrialization.farmer;

import aztech.modern_industrialization.machines.multiblocks.ShapeMatcher;
import dev.atmos_studio.mimultiblockeditor.compat.extended_industrialization.ExtendedFarmerCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets =
                "net.swedz.extended_industrialization."
                        + "machines.blockentity.multiblock.farmer."
                        + "FarmerBlockEntity",
        remap = false
)
public abstract class FarmerBlockEntityMixin {

    @Inject(
            method = "createShapeMatcher",
            at = @At("HEAD")
    )
    private void mme$applyFarmerOverride(
            CallbackInfoReturnable<ShapeMatcher> callback
    ) {
        ExtendedFarmerCompat
                .applyRuntimeOverride(
                        this
                );
    }
}
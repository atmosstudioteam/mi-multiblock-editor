package dev.atmos_studio.mimultiblockeditor.mixin.compat.yet_another_industrialization.arboreous_greenhouse;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization.YAIArboreousGreenhouseCompat;
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
                        + "common.block.machine.arboreous_greenhouse."
                        + "ArboreousGreenhouseBlockEntity",
        remap = false
)
public abstract class ArboreousGreenhouseBlockEntityMixin {

    @Inject(
            method = "getActiveShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementActiveShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        CompiledMultiblockOverride override =
                mme$getOverride();

        if (override == null) {
            return;
        }

        int activeSoilIndex =
                YAIArboreousGreenhouseCompat
                        .getActiveSoilIndex(
                                this
                        );

        callback.setReturnValue(
                override.shapeAt(
                        activeSoilIndex
                )
        );
    }

    @Inject(
            method = "getBigShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementBigShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        CompiledMultiblockOverride override =
                mme$getOverride();

        if (override == null) {
            return;
        }

        callback.setReturnValue(
                override.shapeAt(
                        0
                )
        );
    }

    private CompiledMultiblockOverride
    mme$getOverride() {
        MultiblockMachineBlockEntity self =
                (MultiblockMachineBlockEntity)
                        (Object) this;

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        self.getBlockState().getBlock()
                );

        if (!YAIArboreousGreenhouseCompat
                .isController(
                        controllerId
                )) {
            return null;
        }

        return MultiblockOverrideCache.get(
                controllerId
        );
    }
}
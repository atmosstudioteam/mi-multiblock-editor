package dev.atmos_studio.mimultiblockeditor.mixin;

import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.runtime.CompiledMultiblockOverride;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = LargeTankMultiblockBlockEntity.class,
        remap = false
)
public abstract class LargeTankMultiblockBlockEntityMixin {
    @Shadow
    private ActiveShapeComponent activeShape;

    @Inject(
            method = "getActiveShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementActiveShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        CompiledMultiblockOverride override =
                mme$getReplacementOverride();

        if (override == null) {
            return;
        }

        int shapeIndex =
                activeShape.getActiveShapeIndex();

        if (shapeIndex < 0
                || shapeIndex >= override.size()) {
            return;
        }

        callback.setReturnValue(
                override.shapeAt(
                        shapeIndex
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
                mme$getReplacementOverride();

        if (override == null) {
            return;
        }

        callback.setReturnValue(
                override.shapeAt(
                        override.size() - 1
                )
        );
    }

    private CompiledMultiblockOverride
    mme$getReplacementOverride() {

        LargeTankMultiblockBlockEntity self =
                (LargeTankMultiblockBlockEntity)
                        (Object) this;

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        self.getBlockState().getBlock()
                );

        CompiledMultiblockOverride override =
                MultiblockOverrideCache.get(
                        controllerId
                );

        if (override == null) {
            return null;
        }

        if (override.adapterType()
                != MultiblockAdapterType.LARGE_TANK) {
            return null;
        }

        return override;
    }
}
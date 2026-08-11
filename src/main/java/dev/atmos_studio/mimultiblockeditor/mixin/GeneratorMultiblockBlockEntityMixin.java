package dev.atmos_studio.mimultiblockeditor.mixin;

import aztech.modern_industrialization.machines.blockentities.multiblocks.GeneratorMultiblockBlockEntity;
import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.runtime.CompiledMultiblockOverride;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = GeneratorMultiblockBlockEntity.class,
        remap = false
)
public abstract class GeneratorMultiblockBlockEntityMixin {
    @Shadow
    @Final
    private ActiveShapeComponent activeShape;

    @Unique
    private long mme$appliedOverrideGeneration =
            Long.MIN_VALUE;

    @Inject(
            method = "getActiveShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementActiveShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        GeneratorMultiblockBlockEntity self =
                (GeneratorMultiblockBlockEntity) (Object) this;

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        self.getBlockState().getBlock()
                );

        CompiledMultiblockOverride override =
                MultiblockOverrideCache.get(controllerId);

        if (override == null) {
            return;
        }

        callback.setReturnValue(
                override.shapeAt(0)
        );
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void mme$applyMultiblockOverride(
            CallbackInfo callbackInfo
    ) {
        GeneratorMultiblockBlockEntity self =
                (GeneratorMultiblockBlockEntity) (Object) this;

        Level level = self.getLevel();

        if (level == null) {
            return;
        }

        long currentGeneration =
                MultiblockOverrideCache.generation();

        if (mme$appliedOverrideGeneration
                == currentGeneration) {
            return;
        }

        mme$appliedOverrideGeneration =
                currentGeneration;

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        self.getBlockState().getBlock()
                );

        CompiledMultiblockOverride override =
                MultiblockOverrideCache.get(controllerId);

        if (override == null) {
            return;
        }

        if (override.size() != 1) {
            MIMultiblockEditor.LOGGER.warn(
                    "Generator controller {} has {} replacement shapes, "
                            + "but generators currently support exactly one. "
                            + "Only the first shape will be used.",
                    controllerId,
                    override.size()
            );
        }

        ShapeTemplate replacementShape =
                override.shapeAt(0);

        ActiveShapeComponentAccessor accessor =
                (ActiveShapeComponentAccessor)
                        (Object) activeShape;

        accessor.mme$setActiveShape(0);
        accessor.mme$setShapeTemplates(
                new ShapeTemplate[]{
                        replacementShape
                }
        );

        if (!level.isClientSide) {
            self.unlink();
            self.setChanged();
            self.sync(false);

            MIMultiblockEditor.LOGGER.info(
                    "Applied generator replacement shape "
                            + "to controller {} at {}",
                    controllerId,
                    self.getBlockPos()
            );
        }
    }
}
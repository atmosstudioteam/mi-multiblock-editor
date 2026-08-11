package dev.atmos_studio.mimultiblockeditor.mixin;

import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity;
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
        value = AbstractCraftingMultiblockBlockEntity.class,
        remap = false
)
public abstract class AbstractCraftingMultiblockBlockEntityMixin {
    @Shadow
    @Final
    protected ActiveShapeComponent activeShape;

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
        AbstractCraftingMultiblockBlockEntity self =
                (AbstractCraftingMultiblockBlockEntity)
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
            return;
        }

        ActiveShapeComponentAccessor accessor =
                (ActiveShapeComponentAccessor)
                        (Object) activeShape;

        callback.setReturnValue(
                override.shapeAt(
                        accessor.mme$getActiveShape()
                )
        );
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void mme$applyMultiblockOverride(
            CallbackInfo callbackInfo
    ) {
        AbstractCraftingMultiblockBlockEntity self =
                (AbstractCraftingMultiblockBlockEntity)
                        (Object) this;

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
                MultiblockOverrideCache.get(
                        controllerId
                );

        if (override == null) {
            return;
        }

        ShapeTemplate[] replacementShapes =
                override.shapes();

        ActiveShapeComponentAccessor accessor =
                (ActiveShapeComponentAccessor)
                        (Object) activeShape;

        int oldIndex =
                accessor.mme$getActiveShape();

        int newIndex = Math.max(
                0,
                Math.min(
                        oldIndex,
                        replacementShapes.length - 1
                )
        );

        if (newIndex != oldIndex) {
            accessor.mme$setActiveShape(
                    newIndex
            );
        }

        accessor.mme$setShapeTemplates(
                replacementShapes
        );

        if (!level.isClientSide) {
            self.unlink();
            self.setChanged();
            self.sync(false);

            MIMultiblockEditor.LOGGER.info(
                    "Applied {} replacement shape(s) "
                            + "to controller {} at {}",
                    replacementShapes.length,
                    controllerId,
                    self.getBlockPos()
            );
        }
    }
}
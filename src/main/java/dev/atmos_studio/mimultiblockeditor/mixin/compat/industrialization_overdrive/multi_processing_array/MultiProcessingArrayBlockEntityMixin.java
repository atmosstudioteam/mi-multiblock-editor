package dev.atmos_studio.mimultiblockeditor.mixin.compat.industrialization_overdrive.multi_processing_array;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
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
                "dev.wp.industrialization_overdrive."
                        + "machines.blockentities.multiblock."
                        + "MultiProcessingArrayBlockEntity",
        remap = false
)
public abstract class MultiProcessingArrayBlockEntityMixin {

    /*
     * Industrialization Overdrive хранит отдельный
     * статический массив оригинальных форм и возвращает
     * его последнюю форму через getBigShape().
     *
     * Здесь мы подменяем её на последнюю KubeJS-форму.
     */
    @Inject(
            method = "getBigShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementBigShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        MultiblockMachineBlockEntity self =
                (MultiblockMachineBlockEntity)
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

        if (override.adapterType()
                != MultiblockAdapterType
                .TESSERACT_MULTIPLIED_CRAFTING) {
            return;
        }

        ShapeTemplate[] replacementShapes =
                override.shapes();

        if (replacementShapes.length == 0) {
            return;
        }

        callback.setReturnValue(
                replacementShapes[
                        replacementShapes.length - 1
                        ]
        );
    }
}
package dev.atmos_studio.mimultiblockeditor.mixin;

import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.runtime.CompiledMultiblockOverride;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets =
                "net.swedz.tesseract.neoforge.compat.mi.machine."
                        + "blockentity.multiblock."
                        + "BasicMultiblockMachineBlockEntity",
        remap = false
)
public abstract class TesseractBasicMultiblockBlockEntityMixin {

    @Shadow
    @Final
    protected ActiveShapeComponent activeShape;

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

        int activeShapeIndex =
                Mth.clamp(
                        activeShape.getActiveShapeIndex(),
                        0,
                        replacementShapes.length - 1
                );

        callback.setReturnValue(
                replacementShapes[
                        activeShapeIndex
                        ]
        );
    }
}
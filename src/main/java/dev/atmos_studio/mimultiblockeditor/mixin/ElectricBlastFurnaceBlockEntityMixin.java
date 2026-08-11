package dev.atmos_studio.mimultiblockeditor.mixin;

import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.runtime.CompiledMultiblockOverride;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = ElectricBlastFurnaceBlockEntity.class,
        remap = false
)
public abstract class ElectricBlastFurnaceBlockEntityMixin {
    @Inject(
            method = "getBigShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementBigShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        ElectricBlastFurnaceBlockEntity self =
                (ElectricBlastFurnaceBlockEntity)
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
                .ELECTRIC_BLAST_FURNACE) {
            return;
        }

        callback.setReturnValue(
                override.shapeAt(
                        override.size() - 1
                )
        );
    }
}
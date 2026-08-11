package dev.atmos_studio.mimultiblockeditor.mixin;

import aztech.modern_industrialization.machines.blockentities.multiblocks.SteamBoilerMultiblockBlockEntity;
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
        value = SteamBoilerMultiblockBlockEntity.class,
        remap = false
)
public abstract class SteamBoilerMultiblockBlockEntityMixin {
    @Inject(
            method = "getActiveShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementActiveShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        SteamBoilerMultiblockBlockEntity self =
                (SteamBoilerMultiblockBlockEntity)
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
                != MultiblockAdapterType.STEAM_BOILER) {
            return;
        }

        callback.setReturnValue(
                override.shapeAt(0)
        );
    }
}
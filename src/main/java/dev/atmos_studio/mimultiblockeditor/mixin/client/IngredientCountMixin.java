package dev.atmos_studio.mimultiblockeditor.mixin.client;

import aztech.modern_industrialization.client.compat.viewer.abstraction.IngredientCount;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = IngredientCount.class,
        remap = false
)
public abstract class IngredientCountMixin {
    @Shadow
    public int count;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void mme$initializeMaterialCount(
            Ingredient ingredient,
            CallbackInfo callbackInfo
    ) {
        count = 1;
    }
}
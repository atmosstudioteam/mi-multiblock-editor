package dev.atmos_studio.mimultiblockeditor.mixin.client;

import aztech.modern_industrialization.client.compat.viewer.abstraction.IngredientCount;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IngredientCount.class, remap = false)
public abstract class IngredientCountMixin {

    @Shadow
    public int count;

    /**
     * Modern Industrialization 2.5.5 creates IngredientCount using
     * IngredientCount(Ingredient), but does not initialize count.
     *
     * In MI 2.5.6 this constructor no longer exists. It was replaced with
     * IngredientCount(Ingredient, int), which initializes count itself.
     *
     * require = 0 makes this injection optional, allowing the same build
     * to work with both MI 2.5.5 and MI 2.5.6.
     */
    @Inject(
            method = "<init>(Lnet/minecraft/world/item/crafting/Ingredient;)V",
            at = @At("RETURN"),
            require = 0
    )
    private void mme$initializeMaterialCount(
            Ingredient ingredient,
            CallbackInfo callbackInfo
    ) {
        this.count = 1;
    }
}
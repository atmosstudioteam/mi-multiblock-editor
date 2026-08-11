package dev.atmos_studio.mimultiblockeditor.mixin.client;

import dev.atmos_studio.mimultiblockeditor.client.MultiblockViewerOverrides;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        targets = "aztech.modern_industrialization.client.compat.viewer.usage.MultiblockCategory",
        remap = false
)
public abstract class MultiblockCategoryMixin {
    @Inject(
            method = "buildRecipes",
            at = @At("HEAD")
    )
    private void mme$replaceViewerShapes(
            CallbackInfo callbackInfo
    ) {
        MultiblockOverrideCache.ensureBuilt();
        MultiblockViewerOverrides.apply();
    }
}
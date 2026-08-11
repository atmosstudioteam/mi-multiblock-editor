package dev.atmos_studio.mimultiblockeditor.mixin.compat.extended_industrialization.tesla_tower;

import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolver;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.compat.extended_industrialization.ExtendedTeslaTowerCompat;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = MultiblockAdapterResolver.class,
        remap = false
)
public abstract class TeslaTowerAdapterResolverMixin {

    @Inject(
            method = "resolve",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void mme$resolveTeslaTower(
            ResourceLocation controllerId,
            CallbackInfoReturnable<
                    MultiblockAdapterResolution
                    > callback
    ) {
        MultiblockAdapterResolution current =
                callback.getReturnValue();

        if (current == null) {
            return;
        }

        boolean matchingController =
                ExtendedTeslaTowerCompat
                        .isController(
                                controllerId
                        );

        boolean matchingBlockEntity =
                ExtendedTeslaTowerCompat
                        .isBlockEntityClassName(
                                current
                                        .blockEntityClassName()
                        );

        if (
                !matchingController
                        && !matchingBlockEntity
        ) {
            return;
        }

        callback.setReturnValue(
                new MultiblockAdapterResolution(
                        controllerId,
                        MultiblockAdapterType
                                .TESSERACT_MULTIPLIED_CRAFTING,
                        current.blockClassName(),
                        current.blockEntityClassName(),
                        "Supported by the Extended "
                                + "Industrialization "
                                + "Tesla Tower adapter"
                )
        );
    }
}
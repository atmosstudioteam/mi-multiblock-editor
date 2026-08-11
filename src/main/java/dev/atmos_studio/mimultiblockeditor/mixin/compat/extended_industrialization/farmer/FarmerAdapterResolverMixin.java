package dev.atmos_studio.mimultiblockeditor.mixin.compat.extended_industrialization.farmer;

import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolver;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.compat.extended_industrialization.ExtendedFarmerCompat;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = MultiblockAdapterResolver.class,
        remap = false
)
public abstract class FarmerAdapterResolverMixin {

    @Inject(
            method = "resolve",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void mme$resolveExtendedFarmer(
            ResourceLocation controllerId,
            CallbackInfoReturnable<
                    MultiblockAdapterResolution
                    > callback
    ) {
        if (!ExtendedFarmerCompat
                .isFarmerController(
                        controllerId
                )) {
            return;
        }

        MultiblockAdapterResolution current =
                callback.getReturnValue();

        if (current == null
                || !ExtendedFarmerCompat
                .isFarmerClassName(
                        current.blockEntityClassName()
                )) {
            return;
        }

        callback.setReturnValue(
                new MultiblockAdapterResolution(
                        controllerId,
                        MultiblockAdapterType
                                .EXTENDED_FARMER,
                        current.blockClassName(),
                        current.blockEntityClassName(),
                        "Supported by the Extended "
                                + "Industrialization farmer adapter"
                )
        );
    }
}
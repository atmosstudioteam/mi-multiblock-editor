package dev.atmos_studio.mimultiblockeditor.mixin.compat.extended_industrialization.tesla_tower;

import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockVariantOrderer;
import dev.atmos_studio.mimultiblockeditor.compat.extended_industrialization.ExtendedTeslaTowerCompat;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(
        value = MultiblockVariantOrderer.class,
        remap = false
)
public abstract class TeslaTowerVariantOrdererMixin {

    @Inject(
            method = "order",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void mme$orderTeslaTower(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations,
            CallbackInfoReturnable<
                    List<KubeJSStructureRegistration>
                    > callback
    ) {
        if (!ExtendedTeslaTowerCompat
                .isController(
                        controllerId
                )) {
            return;
        }

        callback.setReturnValue(
                ExtendedTeslaTowerCompat
                        .orderRegistrations(
                                controllerId,
                                resolution,
                                registrations
                        )
        );
    }
}
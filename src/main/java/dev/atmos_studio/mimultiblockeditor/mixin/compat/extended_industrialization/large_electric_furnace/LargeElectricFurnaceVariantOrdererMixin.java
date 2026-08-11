package dev.atmos_studio.mimultiblockeditor.mixin.compat.extended_industrialization.large_electric_furnace;

import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockVariantOrderer;
import dev.atmos_studio.mimultiblockeditor.compat.extended_industrialization.ExtendedLargeElectricFurnaceCompat;
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
public abstract class LargeElectricFurnaceVariantOrdererMixin {

    @Inject(
            method = "order",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void mme$orderLargeElectricFurnace(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations,
            CallbackInfoReturnable<
                    List<KubeJSStructureRegistration>
                    > callback
    ) {
        if (!ExtendedLargeElectricFurnaceCompat
                .isController(
                        controllerId
                )) {
            return;
        }

        callback.setReturnValue(
                ExtendedLargeElectricFurnaceCompat
                        .orderRegistrations(
                                controllerId,
                                resolution,
                                registrations
                        )
        );
    }
}
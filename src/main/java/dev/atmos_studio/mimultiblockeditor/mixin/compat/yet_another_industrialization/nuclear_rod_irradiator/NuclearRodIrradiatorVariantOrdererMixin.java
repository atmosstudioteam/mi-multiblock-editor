package dev.atmos_studio.mimultiblockeditor.mixin.compat.yet_another_industrialization.nuclear_rod_irradiator;

import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockVariantOrderer;
import dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization.YAINuclearRodIrradiatorCompat;
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
public abstract class NuclearRodIrradiatorVariantOrdererMixin {

    @Inject(
            method = "order",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void mme$orderNuclearRodIrradiator(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations,
            CallbackInfoReturnable<
                    List<KubeJSStructureRegistration>
                    > callback
    ) {
        if (!YAINuclearRodIrradiatorCompat
                .isController(
                        controllerId
                )) {
            return;
        }

        callback.setReturnValue(
                YAINuclearRodIrradiatorCompat
                        .orderRegistrations(
                                controllerId,
                                resolution,
                                registrations
                        )
        );
    }
}
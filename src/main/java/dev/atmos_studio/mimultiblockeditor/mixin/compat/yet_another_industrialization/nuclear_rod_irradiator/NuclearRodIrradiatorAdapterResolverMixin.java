package dev.atmos_studio.mimultiblockeditor.mixin.compat.yet_another_industrialization.nuclear_rod_irradiator;

import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolver;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization.YAINuclearRodIrradiatorCompat;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = MultiblockAdapterResolver.class,
        remap = false
)
public abstract class NuclearRodIrradiatorAdapterResolverMixin {

    @Inject(
            method = "resolve",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void mme$resolveNuclearRodIrradiator(
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
                YAINuclearRodIrradiatorCompat
                        .isController(
                                controllerId
                        );

        boolean matchingBlockEntity =
                YAINuclearRodIrradiatorCompat
                        .isBlockEntityClassName(
                                current.blockEntityClassName()
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
                                .YAI_NUCLEAR_ROD_IRRADIATOR,
                        current.blockClassName(),
                        current.blockEntityClassName(),
                        "Supported by the YAI Nuclear "
                                + "Rod Irradiator adapter"
                )
        );
    }
}
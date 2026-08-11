package dev.atmos_studio.mimultiblockeditor.mixin.compat.yet_another_industrialization.flight_pylon;

import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolver;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization.YAIFlightPylonCompat;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = MultiblockAdapterResolver.class,
        remap = false
)
public abstract class FlightPylonAdapterResolverMixin {

    @Inject(
            method = "resolve",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void mme$resolveFlightPylon(
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
                YAIFlightPylonCompat
                        .isController(
                                controllerId
                        );

        boolean matchingBlockEntity =
                YAIFlightPylonCompat
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
                                .YAI_FLIGHT_PYLON,
                        current.blockClassName(),
                        current.blockEntityClassName(),
                        "Supported by the YAI "
                                + "Flight Pylon adapter"
                )
        );
    }
}
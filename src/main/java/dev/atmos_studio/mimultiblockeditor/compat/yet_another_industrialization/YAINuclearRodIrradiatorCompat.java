package dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization;

import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class YAINuclearRodIrradiatorCompat {
    public static final String MOD_ID =
            "yet_another_industrialization";

    public static final ResourceLocation CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "nuclear_rod_irradiator"
            );

    private static final String BLOCK_ENTITY_CLASS =
            "me.luligabi.yet_another_industrialization."
                    + "common.block.machine."
                    + "nuclear_rod_irradiator."
                    + "NuclearRodIrradiatorBlockEntity";

    private YAINuclearRodIrradiatorCompat() {
    }

    public static boolean isController(
            ResourceLocation controllerId
    ) {
        return CONTROLLER_ID.equals(
                controllerId
        );
    }

    public static boolean isBlockEntityClassName(
            String className
    ) {
        return BLOCK_ENTITY_CLASS.equals(
                className
        );
    }

    public static List<KubeJSStructureRegistration>
    orderRegistrations(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (!isController(
                controllerId
        )) {
            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " is not the YAI "
                            + "Nuclear Rod Irradiator"
            );
        }

        if (
                resolution.adapterType()
                        != MultiblockAdapterType
                        .YAI_NUCLEAR_ROD_IRRADIATOR
        ) {
            throw new IllegalArgumentException(
                    "Nuclear Rod Irradiator controller "
                            + controllerId
                            + " was resolved to adapter "
                            + resolution.adapterType().id()
                            + ", but "
                            + MultiblockAdapterType
                            .YAI_NUCLEAR_ROD_IRRADIATOR
                            .id()
                            + " was expected"
            );
        }

        if (registrations.size() != 1) {
            throw new IllegalArgumentException(
                    "Nuclear Rod Irradiator controller "
                            + controllerId
                            + " requires exactly one "
                            + "KubeJS structure, but "
                            + registrations.size()
                            + " were registered"
            );
        }

        KubeJSStructureRegistration registration =
                registrations.get(0);

        if (registration.variantId().isPresent()) {
            throw new IllegalArgumentException(
                    "Nuclear Rod Irradiator structure "
                            + registration.structureId()
                            + " must not define .variant(...)"
            );
        }

        MIMultiblockEditor.LOGGER.info(
                "Validated single KubeJS shape for "
                        + "YAI Nuclear Rod Irradiator {}",
                controllerId
        );

        return List.copyOf(
                registrations
        );
    }
}
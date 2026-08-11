package dev.atmos_studio.mimultiblockeditor.kubejs;

import dev.atmos_studio.mimultiblockeditor.data.MultiblockStructureDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Optional;

public record KubeJSStructureRegistration(
        ResourceLocation structureId,
        ResourceLocation controllerId,
        Optional<ResourceLocation> variantId,
        MultiblockStructureDefinition definition
) {
    public KubeJSStructureRegistration {
        Objects.requireNonNull(
                structureId,
                "structureId"
        );

        Objects.requireNonNull(
                controllerId,
                "controllerId"
        );

        Objects.requireNonNull(
                variantId,
                "variantId"
        );

        Objects.requireNonNull(
                definition,
                "definition"
        );
    }

    public KubeJSStructureRegistration(
            ResourceLocation structureId,
            ResourceLocation controllerId,
            MultiblockStructureDefinition definition
    ) {
        this(
                structureId,
                controllerId,
                Optional.empty(),
                definition
        );
    }

    public KubeJSStructureRegistration(
            ResourceLocation structureId,
            ResourceLocation controllerId,
            ResourceLocation variantId,
            MultiblockStructureDefinition definition
    ) {
        this(
                structureId,
                controllerId,
                Optional.ofNullable(variantId),
                definition
        );
    }
}
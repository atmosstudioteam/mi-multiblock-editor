package dev.atmos_studio.mimultiblockeditor.data;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record MultiblockStructureDefinition(
        ResourceLocation hatchCasingId,
        List<List<String>> layers,
        Map<Character, StructureMemberDefinition> mappings
) {
    public MultiblockStructureDefinition {
        Objects.requireNonNull(
                hatchCasingId,
                "hatchCasingId"
        );
        Objects.requireNonNull(
                layers,
                "layers"
        );
        Objects.requireNonNull(
                mappings,
                "mappings"
        );

        layers = layers.stream()
                .map(List::copyOf)
                .toList();

        mappings = Map.copyOf(
                new LinkedHashMap<>(mappings)
        );

        if (layers.isEmpty()) {
            throw new IllegalArgumentException(
                    "A multiblock structure must contain "
                            + "at least one layer"
            );
        }
    }
}
package dev.atmos_studio.mimultiblockeditor.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record StructureMemberDefinition(
        StructureMemberType type,
        ResourceLocation blockId,
        Optional<ResourceLocation> tagId,
        Map<String, String> stateProperties,
        List<ResourceLocation> hatchTypes
) {
    public StructureMemberDefinition {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(blockId, "blockId");
        Objects.requireNonNull(tagId, "tagId");
        Objects.requireNonNull(
                stateProperties,
                "stateProperties"
        );
        Objects.requireNonNull(
                hatchTypes,
                "hatchTypes"
        );

        tagId = tagId.map(Objects::requireNonNull);
        stateProperties = Map.copyOf(stateProperties);
        hatchTypes = List.copyOf(hatchTypes);

        if (type == StructureMemberType.TAG
                && tagId.isEmpty()) {
            throw new IllegalArgumentException(
                    "A TAG structure member must have a tag ID"
            );
        }

        if (type != StructureMemberType.TAG
                && tagId.isPresent()) {
            throw new IllegalArgumentException(
                    type + " structure member cannot have a tag ID"
            );
        }

        if (type != StructureMemberType.STATE
                && !stateProperties.isEmpty()) {
            throw new IllegalArgumentException(
                    type
                            + " structure member cannot have "
                            + "BlockState properties"
            );
        }
    }

    public static StructureMemberDefinition block(
            ResourceLocation blockId,
            List<ResourceLocation> hatchTypes
    ) {
        return new StructureMemberDefinition(
                StructureMemberType.BLOCK,
                blockId,
                Optional.empty(),
                Map.of(),
                hatchTypes
        );
    }

    public static StructureMemberDefinition tag(
            ResourceLocation tagId,
            ResourceLocation previewBlockId,
            List<ResourceLocation> hatchTypes
    ) {
        return new StructureMemberDefinition(
                StructureMemberType.TAG,
                previewBlockId,
                Optional.of(tagId),
                Map.of(),
                hatchTypes
        );
    }

    public static StructureMemberDefinition state(
            ResourceLocation blockId,
            Map<String, String> stateProperties,
            List<ResourceLocation> hatchTypes
    ) {
        return new StructureMemberDefinition(
                StructureMemberType.STATE,
                blockId,
                Optional.empty(),
                stateProperties,
                hatchTypes
        );
    }
}
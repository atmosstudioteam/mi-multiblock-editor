package dev.atmos_studio.mimultiblockeditor.runtime;

import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;

import java.util.Objects;

public record CompiledMultiblockOverride(
        MultiblockAdapterType adapterType,
        ShapeTemplate[] shapes
) {
    public CompiledMultiblockOverride {
        Objects.requireNonNull(
                adapterType,
                "adapterType"
        );

        Objects.requireNonNull(
                shapes,
                "shapes"
        );

        if (!adapterType.supported()) {
            throw new IllegalArgumentException(
                    "Cannot create a compiled override "
                            + "for unsupported adapter "
                            + adapterType.id()
            );
        }

        if (shapes.length == 0) {
            throw new IllegalArgumentException(
                    "A compiled multiblock override "
                            + "must contain at least one shape"
            );
        }

        shapes = shapes.clone();
    }

    @Override
    public ShapeTemplate[] shapes() {
        return shapes.clone();
    }

    public ShapeTemplate shapeAt(
            int requestedIndex
    ) {
        int index = Math.max(
                0,
                Math.min(
                        requestedIndex,
                        shapes.length - 1
                )
        );

        return shapes[index];
    }

    public int size() {
        return shapes.length;
    }
}
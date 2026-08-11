package dev.atmos_studio.mimultiblockeditor.adapter;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public record MultiblockAdapterResolution(
        ResourceLocation controllerId,
        MultiblockAdapterType adapterType,
        String blockClassName,
        String blockEntityClassName,
        String details
) {
    public MultiblockAdapterResolution {
        Objects.requireNonNull(
                controllerId,
                "controllerId"
        );

        Objects.requireNonNull(
                adapterType,
                "adapterType"
        );

        Objects.requireNonNull(
                blockClassName,
                "blockClassName"
        );

        Objects.requireNonNull(
                blockEntityClassName,
                "blockEntityClassName"
        );

        Objects.requireNonNull(
                details,
                "details"
        );
    }

    public boolean supported() {
        return adapterType.supported();
    }
}
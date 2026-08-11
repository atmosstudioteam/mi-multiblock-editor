package dev.atmos_studio.mimultiblockeditor.kubejs;

import net.minecraft.resources.ResourceLocation;

public final class MIMultiblocksBinding {
    private static final int
            DISTILLATION_TOWER_MAXIMUM_HEIGHT = 32;

    public static final MIMultiblocksBinding INSTANCE =
            new MIMultiblocksBinding();

    private MIMultiblocksBinding() {
    }

    public MultiblockStructureBuilder create(
            String structureId
    ) {
        if (structureId == null
                || structureId.isBlank()) {

            throw new IllegalArgumentException(
                    "Multiblock structure ID cannot be empty"
            );
        }

        return new MultiblockStructureBuilder(
                ResourceLocation.parse(
                        structureId.trim()
                )
        );
    }

    public int distillationTowerMaximumHeight() {
        return DISTILLATION_TOWER_MAXIMUM_HEIGHT;
    }
}
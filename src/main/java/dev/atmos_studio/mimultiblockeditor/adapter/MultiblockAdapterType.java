package dev.atmos_studio.mimultiblockeditor.adapter;

public enum MultiblockAdapterType {
    STANDARD_CRAFTING(
            "standard_crafting",
            true
    ),

    GENERATOR(
            "generator",
            true
    ),

    TESSERACT_MULTIPLIED_CRAFTING(
            "tesseract_multiplied_crafting",
            true
    ),

    EXTENDED_FARMER(
            "extended_farmer",
            true
    ),

    YAI_FLIGHT_PYLON(
            "yai_flight_pylon",
            true
    ),

    YAI_LARGE_STORAGE_UNIT(
            "yai_large_storage_unit",
            true
    ),

    YAI_NUCLEAR_ROD_IRRADIATOR(
            "yai_nuclear_rod_irradiator",
            true
    ),

    ELECTRIC_BLAST_FURNACE(
            "electric_blast_furnace",
            true
    ),

    DISTILLATION_TOWER(
            "distillation_tower",
            true
    ),

    STEAM_BOILER(
            "steam_boiler",
            true
    ),

    LARGE_TANK(
            "large_tank",
            true
    ),

    NUCLEAR_REACTOR(
            "nuclear_reactor",
            true
    ),

    UNKNOWN_MULTIBLOCK(
            "unknown_multiblock",
            false
    ),

    NOT_MULTIBLOCK(
            "not_multiblock",
            false
    ),

    NOT_MACHINE_BLOCK(
            "not_machine_block",
            false
    ),

    UNKNOWN_CONTROLLER(
            "unknown_controller",
            false
    ),

    RESOLUTION_ERROR(
            "resolution_error",
            false
    );

    private final String id;
    private final boolean supported;

    MultiblockAdapterType(
            String id,
            boolean supported
    ) {
        this.id = id;
        this.supported = supported;
    }

    public String id() {
        return id;
    }

    public boolean supported() {
        return supported;
    }
}
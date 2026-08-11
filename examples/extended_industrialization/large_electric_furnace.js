(function registerExtendedIndustrializationLargeElectricFurnace() {
    var CONTROLLER_ID =
        'extended_industrialization:large_electric_furnace';

    var CASING_ID =
        'modern_industrialization:heatproof_machine_casing';

    var HATCH_TYPES = [
        'modern_industrialization:item_input',
        'modern_industrialization:item_output',
        'modern_industrialization:energy_input'
    ];

    var TIERS = [
        {
            structureId:
                'example:large_electric_furnace_cupronickel',

            coilId:
                'modern_industrialization:cupronickel_coil'
        },
        {
            structureId:
                'example:large_electric_furnace_kanthal',

            coilId:
                'modern_industrialization:kanthal_coil'
        }
    ];

    function registerTier(
        tier
    ) {
        MIMultiblocks
            .create(
                tier.structureId
            )
            .requiresMod(
                'extended_industrialization'
            )
            .controller(
                CONTROLLER_ID
            )
            .variant(
                tier.coilId
            )
            .hatchCasing(
                CASING_ID
            )

            .layer([
                'AAA',
                'AAA',
                'A#A'
            ])

            .layer([
                'CCC',
                'C C',
                'CCC'
            ])

            .layer([
                'AAA',
                'AAA',
                'AAA'
            ])

            .mappingBlock(
                'A',
                CASING_ID,
                HATCH_TYPES
            )

            .mappingBlock(
                'C',
                tier.coilId,
                []
            )

            .register();

    }

    for (
        var tierIndex = 0;
        tierIndex < TIERS.length;
        tierIndex++
    ) {
        registerTier(
            TIERS[tierIndex]
        );
    }

})();

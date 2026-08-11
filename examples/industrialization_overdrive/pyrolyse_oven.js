(function registerIndustrializationOverdrivePyrolyseOven() {
    const CONTROLLER_ID =
        'industrialization_overdrive:pyrolyse_oven';

    const CASING_ID =
        'modern_industrialization:bronze_plated_bricks';

    const TIERS = [
        {
            structureId:
                'example:pyrolyse_oven_cupronickel',
            coilId:
                'modern_industrialization:cupronickel_coil'
        },
        {
            structureId:
                'example:pyrolyse_oven_kanthal',
            coilId:
                'modern_industrialization:kanthal_coil'
        }
    ];

    function registerTier(tier) {
        MIMultiblocks
            .create(
                tier.structureId
            )

            .requiresMod(
                'industrialization_overdrive'
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
                'CCC',
                'CCC',
                'AAA'
            ])

            .layer([
                'AAA',
                'C C',
                'C C',
                'A#A'
            ])

            .layer([
                'AAA',
                'CCC',
                'CCC',
                'AAA'
            ])

            .mappingBlock(
                'A',
                CASING_ID,
                [
                    'modern_industrialization:item_input',
                    'modern_industrialization:item_output',
                    'modern_industrialization:fluid_input',
                    'modern_industrialization:fluid_output',
                    'modern_industrialization:energy_input'
                ]
            )

            .mappingBlock(
                'C',
                tier.coilId,
                []
            )

            .register();

    }

    for (const tier of TIERS) {
        registerTier(
            tier
        );
    }

})();

(function registerExtendedIndustrializationTeslaTower() {
    var CONTROLLER_ID =
        'extended_industrialization:tesla_tower';

    var CLEAN_STAINLESS_STEEL_CASING =
        'modern_industrialization:clean_stainless_steel_machine_casing';

    var STAINLESS_STEEL_PIPE =
        'modern_industrialization:stainless_steel_machine_casing_pipe';

    var POLISHED_SILVER_CASING =
        'extended_industrialization:polished_silver_machine_casing';

    var ENERGY_HATCH_TYPES = [
        'modern_industrialization:energy_input'
    ];

    var TIERS = [
        {
            name:
                'copper',

            windingId:
                'extended_industrialization:copper_tesla_winding'
        },
        {
            name:
                'electrum',

            windingId:
                'extended_industrialization:electrum_tesla_winding'
        },
        {
            name:
                'aluminum',

            windingId:
                'extended_industrialization:aluminum_tesla_winding'
        },
        {
            name:
                'annealed_copper',

            windingId:
                'extended_industrialization:annealed_copper_tesla_winding'
        },
        {
            name:
                'superconductor',

            windingId:
                'extended_industrialization:superconductor_tesla_winding'
        }
    ];

    function createLayers() {
        var windingOnly = [
            '       ',
            '       ',
            '       ',
            '   W   ',
            '       ',
            '       ',
            '       '
        ];

        var ringedWinding = [
            '       ',
            '  TTT  ',
            ' T P T ',
            ' TPWPT ',
            ' T P T ',
            '  TTT  ',
            '       '
        ];

        var ballEnd = [
            '       ',
            '       ',
            '  TTT  ',
            '  TTT  ',
            '  TTT  ',
            '       ',
            '       '
        ];

        var ballMiddle = [
            '       ',
            '  TTT  ',
            ' TTTTT ',
            ' TTTTT ',
            ' TTTTT ',
            '  TTT  ',
            '       '
        ];

        return [
            [
                ' SSSSS ',
                'SSWWWSS',
                'SWWWWWS',
                'SWWWWWS',
                'SWWWWWS',
                'SSWWWSS',
                ' SS#SS '
            ],

            [
                '       ',
                '  WWW  ',
                ' W   W ',
                ' W W W ',
                ' W   W ',
                '  WWW  ',
                '       '
            ],

            windingOnly,
            windingOnly,
            ringedWinding,
            windingOnly,
            ringedWinding,
            windingOnly,
            ringedWinding,
            windingOnly,
            ringedWinding,
            windingOnly,

            ballEnd,
            ballMiddle,
            ballMiddle,
            ballMiddle,
            ballEnd
        ];
    }

    function registerTier(
        tier
    ) {
        var structureId =
            'example:tesla_tower_'
            + tier.name;

        var layers =
            createLayers();

        var builder =
            MIMultiblocks
                .create(
                    structureId
                )
                .requiresMod(
                    'extended_industrialization'
                )
                .controller(
                    CONTROLLER_ID
                )
                .variant(
                    tier.windingId
                )
                .hatchCasing(
                    CLEAN_STAINLESS_STEEL_CASING
                );

        for (
            var layerIndex = 0;
            layerIndex < layers.length;
            layerIndex++
        ) {
            builder.layer(
                layers[layerIndex]
            );
        }

        builder
            .mappingBlock(
                'S',
                CLEAN_STAINLESS_STEEL_CASING,
                ENERGY_HATCH_TYPES
            )

            .mappingBlock(
                'P',
                STAINLESS_STEEL_PIPE,
                []
            )

            .mappingBlock(
                'T',
                POLISHED_SILVER_CASING,
                []
            )

            .mappingBlock(
                'W',
                tier.windingId,
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

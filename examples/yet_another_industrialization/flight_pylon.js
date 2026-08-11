(function registerYAIFlightPylon() {
    var CONTROLLER_ID =
        'yet_another_industrialization:flight_pylon';

    var HATCH_CASING_ID =
        'modern_industrialization:steel';

    var STEEL_CASING_ID =
        'modern_industrialization:steel_machine_casing';

    var ENERGY_HATCH_TYPES = [
        'modern_industrialization:energy_input'
    ];

    var TIERS = [
        {
            name:
                'tiny',

            blockId:
                'modern_industrialization:steel_machine_casing'
        },
        {
            name:
                'small',

            blockId:
                'modern_industrialization:advanced_machine_casing'
        },
        {
            name:
                'medium',

            blockId:
                'modern_industrialization:turbo_machine_casing'
        },
        {
            name:
                'large',

            blockId:
                'modern_industrialization:highly_advanced_machine_casing'
        },
        {
            name:
                'huge',

            blockId:
                'modern_industrialization:quantum_machine_casing'
        }
    ];

    function createLayers() {
        return [
            [
                '     ',
                ' AAA ',
                ' AAA ',
                ' A#A ',
                '     '
            ],

            [
                '     ',
                '  T  ',
                ' TTT ',
                '  T  ',
                '     '
            ],

            [
                '  T  ',
                '     ',
                'T   T',
                '     ',
                '  T  '
            ],

            [
                '     ',
                ' T T ',
                '     ',
                ' T T ',
                '     '
            ],

            [
                '     ',
                ' T T ',
                '     ',
                ' T T ',
                '     '
            ],

            [
                '     ',
                ' T T ',
                '  T  ',
                ' T T ',
                '     '
            ],

            [
                '     ',
                '     ',
                '  T  ',
                '     ',
                '     '
            ],

            [
                '     ',
                '     ',
                '  T  ',
                '     ',
                '     '
            ],

            [
                '     ',
                '     ',
                '  T  ',
                '     ',
                '     '
            ]
        ];
    }

    function registerTier(
        tier
    ) {
        var structureId =
            'example:flight_pylon_'
            + tier.name;

        var layers =
            createLayers();

        var builder =
            MIMultiblocks
                .create(
                    structureId
                )
                .requiresMod(
                    'yet_another_industrialization'
                )
                .controller(
                    CONTROLLER_ID
                )
                .variant(
                    tier.blockId
                )
                .hatchCasing(
                    HATCH_CASING_ID
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
                'A',
                STEEL_CASING_ID,
                ENERGY_HATCH_TYPES
            )

            .mappingBlock(
                'T',
                tier.blockId,
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

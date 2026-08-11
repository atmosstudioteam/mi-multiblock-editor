(function registerYAILargeStorageUnit() {
    var CONTROLLER_ID =
        'yet_another_industrialization:large_storage_unit';

    var BATTERY_CASING_ID =
        'yet_another_industrialization:battery_casing';

    var GLASS_TAG_ID =
        'c:glass_blocks';

    var STORAGE_HATCH_TYPES = [
        'yet_another_industrialization:large_storage_unit_input',
        'yet_another_industrialization:large_storage_unit_output'
    ];

    var TIERS = [
        {
            name:
                'lv',

            blockId:
                'minecraft:redstone_block',

            hullId:
                'modern_industrialization:basic_machine_hull'
        },
        {
            name:
                'mv',

            blockId:
                'modern_industrialization:silicon_block',

            hullId:
                'modern_industrialization:advanced_machine_hull'
        },
        {
            name:
                'hv',

            blockId:
                'modern_industrialization:sodium_block',

            hullId:
                'modern_industrialization:turbo_machine_hull'
        },
        {
            name:
                'ev',

            blockId:
                'yet_another_industrialization:cadmium_block',

            hullId:
                'modern_industrialization:highly_advanced_machine_hull'
        },
        {
            name:
                'superconductor',

            blockId:
                'modern_industrialization:plutonium_block',

            hullId:
                'modern_industrialization:quantum_machine_hull'
        },
        {
            name:
                'ultimate',

            blockId:
                'yet_another_industrialization:singularity_block',

            hullId:
                'modern_industrialization:quantum_machine_hull'
        }
    ];

    function createLayers() {
        return [
            [
                'CCCCC',
                'CGGGC',
                'CGGGC',
                'CGGGC',
                'CCCCC'
            ],

            [
                'CGGGC',
                'GHBHG',
                'GBBBG',
                'GHBHG',
                'CG#GC'
            ],

            [
                'CGGGC',
                'GBBBG',
                'GBBBG',
                'GBBBG',
                'CGGGC'
            ],

            [
                'CGGGC',
                'GHBHG',
                'GBBBG',
                'GHBHG',
                'CGGGC'
            ],

            [
                'CCCCC',
                'CGGGC',
                'CGGGC',
                'CGGGC',
                'CCCCC'
            ]
        ];
    }

    function registerTier(
        tier
    ) {
        var structureId =
            'example:large_storage_unit_'
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
                    BATTERY_CASING_ID
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
                'C',
                BATTERY_CASING_ID,
                STORAGE_HATCH_TYPES
            )

            .mappingTag(
                'G',
                GLASS_TAG_ID,
                'minecraft:glass',
                []
            )

            .mappingBlock(
                'B',
                tier.blockId,
                []
            )

            .mappingBlock(
                'H',
                tier.hullId,
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

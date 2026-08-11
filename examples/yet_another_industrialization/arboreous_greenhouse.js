(function registerYAIArboreousGreenhouse() {
    const CONTROLLER_ID =
        'yet_another_industrialization:arboreous_greenhouse';

    const CASING_ID =
        'modern_industrialization:heatproof_machine_casing';

    const HANGING_LANTERN_TAG =
        'mi_multiblock_editor:hanging_lantern';

    const CASING_HATCHES = [
        'modern_industrialization:item_input',
        'modern_industrialization:item_output',
        'modern_industrialization:fluid_input',
        'modern_industrialization:energy_input'
    ];

    const BASE_LAYER = [
        '  AAAAA  ',
        ' ASSSSSA ',
        'ASSSSSSSA',
        'ASSSSSSSA',
        'ASSSSSSSA',
        'ASSSSSSSA',
        'ASSSSSSSA',
        ' ASSSSSA ',
        '  AA#AA  '
    ];

    const DOME_LAYER_1 = [
        '  GGGGG  ',
        ' A     A ',
        'G       G',
        'G       G',
        'G       G',
        'G       G',
        'G       G',
        ' A     A ',
        '  GGGGG  '
    ];

    const DOME_LAYER_2 = [
        '   GGG   ',
        '  A   A  ',
        ' A     A ',
        'G       G',
        'G       G',
        'G       G',
        ' A     A ',
        '  A   A  ',
        '   GGG   '
    ];

    const DOME_LAYER_3 = [
        '         ',
        '   AAA   ',
        '  A   A  ',
        ' A     A ',
        ' A     A ',
        ' A     A ',
        '  A   A  ',
        '   AAA   ',
        '         '
    ];

    const DOME_LAYER_4 = [
        '         ',
        '         ',
        '   GGG   ',
        '  G   G  ',
        '  G L G  ',
        '  G   G  ',
        '   GGG   ',
        '         ',
        '         '
    ];

    const DOME_LAYER_5 = [
        '         ',
        '         ',
        '         ',
        '   GGG   ',
        '   GAG   ',
        '   GGG   ',
        '         ',
        '         ',
        '         '
    ];

    const LAYERS = [
        BASE_LAYER,
        DOME_LAYER_1,
        DOME_LAYER_1,
        DOME_LAYER_1,
        DOME_LAYER_2,
        DOME_LAYER_3,
        DOME_LAYER_4,
        DOME_LAYER_5
    ];

    const TIERS = [
        {
            structureId:
                'example:arboreous_greenhouse_grass_block',

            tierId:
                'yet_another_industrialization:grass_block',

            previewBlockId:
                'minecraft:grass_block',

            requiredMods: []
        },
        {
            structureId:
                'example:arboreous_greenhouse_sand',

            tierId:
                'yet_another_industrialization:sand',

            previewBlockId:
                'minecraft:sand',

            requiredMods: []
        },
        {
            structureId:
                'example:arboreous_greenhouse_mycelium',

            tierId:
                'yet_another_industrialization:mycelium',

            previewBlockId:
                'minecraft:mycelium',

            requiredMods: []
        },
        {
            structureId:
                'example:arboreous_greenhouse_netherrack',

            tierId:
                'yet_another_industrialization:netherrack',

            previewBlockId:
                'minecraft:netherrack',

            requiredMods: []
        },
        {
            structureId:
                'example:arboreous_greenhouse_end_stone',

            tierId:
                'yet_another_industrialization:end_stone',

            previewBlockId:
                'minecraft:end_stone',

            requiredMods: []
        },
        {
            structureId:
                'example:arboreous_greenhouse_echo_soil',

            tierId:
                'yet_another_industrialization:echo_soil',

            previewBlockId:
                'deeperdarker:echo_soil',

            requiredMods: [
                'deeperdarker'
            ]
        },
        {
            structureId:
                'example:arboreous_greenhouse_deepsoil',

            tierId:
                'yet_another_industrialization:deepsoil',

            previewBlockId:
                'undergarden:deepsoil',

            requiredMods: [
                'undergarden'
            ]
        }
    ];

    function createDynamicSoilTag(
        tierId
    ) {
        const separator =
            tierId.indexOf(':');

        if (
            separator <= 0
            || separator === tierId.length - 1
        ) {
            throw new Error(
                'Invalid Arboreous Greenhouse tier ID: '
                    + tierId
            );
        }

        const namespace =
            tierId.substring(
                0,
                separator
            );

        const path =
            tierId.substring(
                separator + 1
            );

        return 'mi_multiblock_editor:'
            + 'yai_greenhouse_soil/'
            + namespace
            + '/'
            + path;
    }

    function registerTier(
        tier
    ) {
        const builder =
            MIMultiblocks
                .create(
                    tier.structureId
                )
                .requiresMod(
                    'yet_another_industrialization'
                );

        for (
            const requiredMod
            of tier.requiredMods
        ) {
            builder.requiresMod(
                requiredMod
            );
        }

        builder
            .controller(
                CONTROLLER_ID
            )
            .variant(
                tier.tierId
            )
            .hatchCasing(
                CASING_ID
            );

        for (const layer of LAYERS) {
            builder.layer(
                layer
            );
        }

        builder
            .mappingBlock(
                'A',
                CASING_ID,
                CASING_HATCHES
            )

            .mappingTag(
                'S',
                createDynamicSoilTag(
                    tier.tierId
                ),
                tier.previewBlockId,
                []
            )

            .mappingTag(
                'G',
                'c:glass_blocks',
                'minecraft:glass',
                []
            )

            .mappingTag(
                'L',
                HANGING_LANTERN_TAG,
                'minecraft:lantern',
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

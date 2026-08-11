(function registerExtendedIndustrializationProcessingArray() {
    const CONTROLLER_ID =
        'extended_industrialization:processing_array';

    const CLEAN_STAINLESS_CASING_ID =
        'modern_industrialization:'
            + 'clean_stainless_steel_machine_casing';

    const STAINLESS_PIPE_ID =
        'modern_industrialization:'
            + 'stainless_steel_machine_casing_pipe';

    const SHAPES = [
        {
            id: 'example:processing_array_8',
            depth: 3,
            machines: 8
        },
        {
            id: 'example:processing_array_16',
            depth: 5,
            machines: 16
        },
        {
            id: 'example:processing_array_32',
            depth: 7,
            machines: 32
        },
        {
            id: 'example:processing_array_64',
            depth: 9,
            machines: 64
        }
    ];

    function repeatRows(count, row) {
        const rows = [];

        for (let index = 0; index < count; index++) {
            rows.push(row);
        }

        return rows;
    }

    function createBottomLayer(depth) {
        const rows = repeatRows(
            depth - 1,
            'OOO'
        );

        rows.push('EEE');

        return rows;
    }

    function createMiddleLayer(depth) {
        const rows = repeatRows(
            depth - 1,
            'GPG'
        );

        rows.push('H#H');

        return rows;
    }

    function createTopLayer(depth) {
        const rows = repeatRows(
            depth - 1,
            'III'
        );

        rows.push('EEE');

        return rows;
    }

    function registerShape(shape) {
        MIMultiblocks
            .create(shape.id)

            .requiresMod(
                'extended_industrialization'
            )

            .controller(
                CONTROLLER_ID
            )

            .hatchCasing(
                CLEAN_STAINLESS_CASING_ID
            )

            .layer(
                createBottomLayer(
                    shape.depth
                )
            )

            .layer(
                createMiddleLayer(
                    shape.depth
                )
            )

            .layer(
                createTopLayer(
                    shape.depth
                )
            )

            .mappingBlock(
                'I',
                CLEAN_STAINLESS_CASING_ID,
                [
                    'modern_industrialization:item_input',
                    'modern_industrialization:fluid_input'
                ]
            )

            .mappingBlock(
                'O',
                CLEAN_STAINLESS_CASING_ID,
                [
                    'modern_industrialization:item_output',
                    'modern_industrialization:fluid_output'
                ]
            )

            .mappingBlock(
                'E',
                CLEAN_STAINLESS_CASING_ID,
                [
                    'modern_industrialization:energy_input'
                ]
            )

            .mappingBlock(
                'P',
                STAINLESS_PIPE_ID,
                []
            )

            .mappingTag(
                'G',
                'c:glass_blocks',
                'minecraft:glass',
                []
            )

            .mappingTag(
                'H',
                'c:glass_blocks',
                'minecraft:glass',
                [
                    'modern_industrialization:energy_input'
                ]
            )

            .register();

    }

    for (const shape of SHAPES) {
        registerShape(shape);
    }

})();

(function registerYAINuclearRodIrradiator() {
    var CONTROLLER_ID =
        'yet_another_industrialization:nuclear_rod_irradiator';

    var NUCLEAR_CASING_ID =
        'modern_industrialization:nuclear_casing';

    var NUCLEAR_PIPE_ID =
        'modern_industrialization:nuclear_alloy_machine_casing_pipe';

    MIMultiblocks
        .create(
            'example:nuclear_rod_irradiator'
        )
        .requiresMod(
            'yet_another_industrialization'
        )
        .controller(
            CONTROLLER_ID
        )
        .hatchCasing(
            NUCLEAR_CASING_ID
        )

        .layer([
            ' EEE ',
            'EEEEE',
            'EEEEE',
            'EEEEE',
            ' EEE '
        ])

        .layer([
            'CCCCC',
            'CPPPC',
            'CPPPC',
            'CPPPC',
            'CCCCC'
        ])

        .layer([
            'CCCCC',
            'CPPPC',
            'CPPPC',
            'CPPPC',
            'CC#CC'
        ])

        .layer([
            'CCCCC',
            'CPPPC',
            'CPPPC',
            'CPPPC',
            'CCCCC'
        ])

        .layer([
            ' EEE ',
            'ENNNE',
            'ENINE',
            'ENNNE',
            ' EEE '
        ])

        .mappingBlock(
            'C',
            NUCLEAR_CASING_ID,
            []
        )

        .mappingBlock(
            'P',
            NUCLEAR_PIPE_ID,
            []
        )

        .mappingBlock(
            'E',
            NUCLEAR_CASING_ID,
            [
                'modern_industrialization:energy_input'
            ]
        )

        .mappingBlock(
            'N',
            NUCLEAR_CASING_ID,
            [
                'modern_industrialization:nuclear_item'
            ]
        )

        .mappingBlock(
            'I',
            NUCLEAR_CASING_ID,
            [
                'modern_industrialization:item_input'
            ]
        )

        .register();

})();

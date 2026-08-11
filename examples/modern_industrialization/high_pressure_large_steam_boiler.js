MIMultiblocks
    .create('example:high_pressure_large_steam_boiler')
    .controller(
        'modern_industrialization:high_pressure_large_steam_boiler'
    )
    .hatchCasing(
        'modern_industrialization:heatproof_machine_casing'
    )

    .layer([
        'AAA',
        'AAA',
        'A#A'
    ])

    .layer([
        'AAA',
        'A A',
        'AAA'
    ])

    .layer([
        'AAA',
        'AAA',
        'AAA'
    ])

    .mappingBlock(
        'A',
        'modern_industrialization:heatproof_machine_casing',
        [
            'modern_industrialization:item_input',
            'modern_industrialization:fluid_input',
            'modern_industrialization:fluid_output'
        ]
    )

    .register();

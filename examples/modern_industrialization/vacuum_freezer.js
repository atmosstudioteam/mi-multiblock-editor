MIMultiblocks
    .create('example:vacuum_freezer')
    .controller(
        'modern_industrialization:vacuum_freezer'
    )
    .hatchCasing(
        'modern_industrialization:frostproof_machine_casing'
    )

    .layer([
        'ABA',
        'A#A',
        'ACA'
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
        'modern_industrialization:frostproof_machine_casing',
        [
            'modern_industrialization:item_input',
            'modern_industrialization:item_output',
            'modern_industrialization:fluid_input',
            'modern_industrialization:fluid_output',
            'modern_industrialization:energy_input'
        ]
    )

    .mappingTag(
        'B',
        'minecraft:logs',
        'minecraft:oak_log',
        []
    )

    .mappingState(
        'C',
        'minecraft:oak_log[axis=y]',
        []
    )

    .register()

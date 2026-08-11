// kubejs/startup_scripts/basic_multiblock.js

MIMultiblocks
    .create('example:vacuum_freezer')
    .controller('modern_industrialization:vacuum_freezer')
    .hatchCasing('modern_industrialization:frostproof_machine_casing')
    .layer([
        'AAA',
        'A#A',
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
    .register();

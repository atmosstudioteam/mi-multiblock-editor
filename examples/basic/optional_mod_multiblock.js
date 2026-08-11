// kubejs/startup_scripts/optional_mod_multiblock.js
//
// This is a template showing how addon-specific definitions can be guarded.
// Replace the example IDs with IDs from the addon you are targeting.

MIMultiblocks
    .create('example:addon_machine')
    .requiresMod('example_addon')
    .controller('example_addon:multiblock_controller')
    .hatchCasing('modern_industrialization:steel')
    .layer([
        'AAA',
        'A#A',
        'AAA'
    ])
    .mappingBlock(
        'A',
        'modern_industrialization:steel_machine_casing',
        []
    )
    .register();

// kubejs/startup_scripts/variant_multiblock.js
//
// Electric Blast Furnace example. The variant key is the coil block ID.

function registerBlastFurnaceVariant(structureId, coilId) {
    MIMultiblocks
        .create(structureId)
        .controller('modern_industrialization:electric_blast_furnace')
        .variant(coilId)
        .hatchCasing('modern_industrialization:heatproof_machine_casing')
        .layer([
            'AAA',
            'A#A',
            'AAA'
        ])
        .layer([
            'AAA',
            'ABA',
            'AAA'
        ])
        .mappingBlock(
            'A',
            'modern_industrialization:heatproof_machine_casing',
            [
                'modern_industrialization:item_input',
                'modern_industrialization:item_output',
                'modern_industrialization:fluid_input',
                'modern_industrialization:fluid_output',
                'modern_industrialization:energy_input'
            ]
        )
        .mappingBlock('B', coilId, [])
        .register();
}

registerBlastFurnaceVariant(
    'example:ebf_cupronickel',
    'modern_industrialization:cupronickel_coil'
);

registerBlastFurnaceVariant(
    'example:ebf_kanthal',
    'modern_industrialization:kanthal_coil'
);

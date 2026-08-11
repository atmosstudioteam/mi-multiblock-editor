(function registerExampleElectricBlastFurnaceShapes() {
    function registerElectricBlastFurnaceShape(
        structureId,
        variantId,
        coilBlockId
    ) {
        MIMultiblocks
            .create(structureId)
            .controller(
                'modern_industrialization:electric_blast_furnace'
            )
            .variant(variantId)
            .hatchCasing(
                'modern_industrialization:heatproof_machine_casing'
            )

            .layer([
                'AAA',
                'BBB',
                'BBB',
                'AAA'
            ])

            .layer([
                'AAA',
                'B B',
                'B B',
                'A#A'
            ])

            .layer([
                'AAA',
                'BBB',
                'BBB',
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

            .mappingBlock(
                'B',
                coilBlockId,
                []
            )

            .register();
    }

    registerElectricBlastFurnaceShape(
        'example:electric_blast_furnace_cupronickel',
        'modern_industrialization:cupronickel_coil',
        'modern_industrialization:cupronickel_coil'
    );

    registerElectricBlastFurnaceShape(
        'example:electric_blast_furnace_kanthal',
        'modern_industrialization:kanthal_coil',
        'modern_industrialization:kanthal_coil'
    );

})();

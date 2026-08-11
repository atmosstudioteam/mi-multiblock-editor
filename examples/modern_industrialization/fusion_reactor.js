(function registerExampleFusionReactor() {
    MIMultiblocks
        .create('example:fusion_reactor')
        .controller(
            'modern_industrialization:fusion_reactor'
        )

        .hatchCasing(
            'modern_industrialization:ev'
        )

        .layer([
            'AAA',
            'A#A',
            'AAA'
        ])

        .layer([
            'ABA',
            'B B',
            'ABA'
        ])

        .layer([
            'AAA',
            'AAA',
            'AAA'
        ])

        .mappingBlock(
            'A',
            'modern_industrialization:'
                + 'highly_advanced_machine_hull',
            [
                'modern_industrialization:energy_input',
                'modern_industrialization:fluid_input',
                'modern_industrialization:fluid_output'
            ]
        )

        .mappingBlock(
            'B',
            'modern_industrialization:fusion_chamber',
            []
        )

        .register();

})();

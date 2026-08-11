(function registerExampleDistillationTowerShapes() {

    var maximumTowerHeight =
        MIMultiblocks.distillationTowerMaximumHeight();

    function registerTowerShape(height) {
        var towerBuilder = MIMultiblocks
            .create(
                'example:distillation_tower_height_'
                    + height
            )
            .controller(
                'modern_industrialization:distillation_tower'
            )
            .hatchCasing(
                'modern_industrialization:'
                    + 'clean_stainless_steel_machine_casing'
            );

        towerBuilder.layer([
            'AAA',
            'AAA',
            'A#A'
        ]);

        for (
            var level = 0;
            level < height;
            level++
        ) {
            towerBuilder.layer([
                'BBB',
                'BCB',
                'BBB'
            ]);
        }

        towerBuilder.mappingBlock(
            'A',
            'modern_industrialization:'
                + 'clean_stainless_steel_machine_casing',
            [
                'modern_industrialization:energy_input',
                'modern_industrialization:fluid_input'
            ]
        );

        towerBuilder.mappingBlock(
            'B',
            'modern_industrialization:'
                + 'clean_stainless_steel_machine_casing',
            [
                'modern_industrialization:fluid_output'
            ]
        );

        towerBuilder.mappingBlock(
            'C',
            'modern_industrialization:'
                + 'stainless_steel_machine_casing_pipe',
            []
        );

        towerBuilder.register();
    }

    for (
        var height = 1;
        height <= maximumTowerHeight;
        height++
    ) {
        registerTowerShape(height);
    }
})();

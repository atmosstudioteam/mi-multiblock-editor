(function registerExampleNuclearReactorShapes() {
    var reactorSizes = [
        5,
        7,
        9,
        11
    ];

    function getMinimumZ(
        shapeIndex,
        absoluteX
    ) {
        if (shapeIndex !== 3) {
            if (absoluteX === 0) {
                return 0;
            }

            return absoluteX - 1;
        }

        if (absoluteX <= 1) {
            return 0;
        }

        return absoluteX - 2;
    }

    function createLayer(
        shapeIndex,
        size,
        layerIndex
    ) {
        var rows = [];
        var radius =
            Math.floor(size / 2);

        for (
            var row = 0;
            row < size;
            row++
        ) {
            var z =
                size - 1 - row;

            var layerRow = '';

            for (
                var column = 0;
                column < size;
                column++
            ) {
                var x =
                    column - radius;

                var absoluteX =
                    Math.abs(x);

                var minimumZ =
                    getMinimumZ(
                        shapeIndex,
                        absoluteX
                    );

                var maximumZ =
                    size - 1 - minimumZ;

                var insideShape =
                    z >= minimumZ
                    && z <= maximumZ;

                if (!insideShape) {
                    layerRow += ' ';
                    continue;
                }

                var isController =
                    layerIndex === 1
                    && x === 0
                    && z === 0;

                if (isController) {
                    layerRow += '#';
                    continue;
                }

                var isBoundary =
                    z === minimumZ
                    || z === maximumZ
                    || absoluteX === radius;

                if (layerIndex === 0) {
                    layerRow += 'A';
                } else if (
                    layerIndex === 1
                    || layerIndex === 2
                    || layerIndex === 3
                ) {
                    layerRow += isBoundary
                        ? 'A'
                        : 'B';
                } else {
                    layerRow += isBoundary
                        ? 'A'
                        : 'T';
                }
            }

            rows.push(layerRow);
        }

        return rows;
    }

    function registerReactorShape(
        shapeIndex,
        size
    ) {
        var reactorBuilder =
            MIMultiblocks
                .create(
                    'example:nuclear_reactor_'
                        + size
                        + 'x'
                        + size
                )
                .controller(
                    'modern_industrialization:'
                        + 'nuclear_reactor'
                )
                .hatchCasing(
                    'modern_industrialization:'
                        + 'nuclear_casing'
                );

        for (
            var layerIndex = 0;
            layerIndex < 5;
            layerIndex++
        ) {
            reactorBuilder.layer(
                createLayer(
                    shapeIndex,
                    size,
                    layerIndex
                )
            );
        }

        reactorBuilder.mappingBlock(
            'A',
            'modern_industrialization:'
                + 'nuclear_casing',
            []
        );

        reactorBuilder.mappingBlock(
            'B',
            'modern_industrialization:'
                + 'nuclear_alloy_machine_casing_pipe',
            []
        );

        reactorBuilder.mappingBlock(
            'T',
            'modern_industrialization:'
                + 'nuclear_casing',
            [
                'modern_industrialization:nuclear_item',
                'modern_industrialization:nuclear_fluid'
            ]
        );

        reactorBuilder.register();
    }

    for (
        var shapeIndex = 0;
        shapeIndex < reactorSizes.length;
        shapeIndex++
    ) {
        registerReactorShape(
            shapeIndex,
            reactorSizes[shapeIndex]
        );
    }

})();

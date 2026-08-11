(function registerExampleLargeTankShapes() {
    var xSizes = [
        3,
        5,
        7
    ];

    var ySizes = [
        3,
        4,
        5,
        6,
        7
    ];

    var zSizes = [
        3,
        4,
        5,
        6,
        7
    ];

    function createLayer(
        sizeX,
        sizeY,
        sizeZ,
        layerIndex
    ) {
        var rows = [];

        for (
            var row = 0;
            row < sizeZ;
            row++
        ) {
            var layerRow = '';

            for (
                var column = 0;
                column < sizeX;
                column++
            ) {
                var isController =
                    layerIndex === 1
                    && row === sizeZ - 1
                    && column === Math.floor(sizeX / 2);

                if (isController) {
                    layerRow += '#';
                    continue;
                }

                var boundaryCount = 0;

                if (
                    column === 0
                    || column === sizeX - 1
                ) {
                    boundaryCount++;
                }

                if (
                    layerIndex === 0
                    || layerIndex === sizeY - 1
                ) {
                    boundaryCount++;
                }

                if (
                    row === 0
                    || row === sizeZ - 1
                ) {
                    boundaryCount++;
                }

                if (boundaryCount === 0) {
                    layerRow += ' ';
                } else if (boundaryCount === 1) {
                    layerRow += 'B';
                } else {
                    layerRow += 'A';
                }
            }

            rows.push(
                layerRow
            );
        }

        return rows;
    }

    function registerTankShape(
        sizeX,
        sizeY,
        sizeZ
    ) {
        var tankBuilder =
            MIMultiblocks
                .create(
                    'example:large_tank_'
                        + sizeX
                        + 'x'
                        + sizeY
                        + 'x'
                        + sizeZ
                )
                .controller(
                    'modern_industrialization:large_tank'
                )
                .hatchCasing(
                    'modern_industrialization:steel'
                );

        for (
            var layerIndex = 0;
            layerIndex < sizeY;
            layerIndex++
        ) {
            tankBuilder.layer(
                createLayer(
                    sizeX,
                    sizeY,
                    sizeZ,
                    layerIndex
                )
            );
        }

        tankBuilder.mappingBlock(
            'A',
            'modern_industrialization:'
                + 'steel_machine_casing',
            [
                'modern_industrialization:large_tank'
            ]
        );

        tankBuilder.mappingTag(
            'B',
            'c:glass_blocks',
            'minecraft:glass',
            [
                'modern_industrialization:large_tank'
            ]
        );

        tankBuilder.register();
    }

    for (
        var xIndex = 0;
        xIndex < xSizes.length;
        xIndex++
    ) {
        for (
            var yIndex = 0;
            yIndex < ySizes.length;
            yIndex++
        ) {
            for (
                var zIndex = 0;
                zIndex < zSizes.length;
                zIndex++
            ) {
                registerTankShape(
                    xSizes[xIndex],
                    ySizes[yIndex],
                    zSizes[zIndex]
                );
            }
        }
    }

})();

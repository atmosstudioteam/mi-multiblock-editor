(function registerExtendedIndustrializationSteamFarmer() {
    var CONTROLLER_ID =
        'extended_industrialization:steam_farmer';

    var STRUCTURE_PREFIX =
        'example:steam_farmer_';

    var BASE_BLOCK_ID =
        'modern_industrialization:bronze_plated_bricks';

    var PIPE_BLOCK_ID =
        'modern_industrialization:bronze_machine_casing_pipe';

    var HATCH_CASING_ID =
        'extended_industrialization:bronze_pipe';

    var FARMER_DIRT_MEMBER_TAG =
        'mi_multiblock_editor:extended_farmer_dirt';

    var HATCH_TYPES = [
        'modern_industrialization:item_input',
        'modern_industrialization:item_output',
        'modern_industrialization:fluid_input'
    ];

    var VARIANTS = [
        {
            name:
                'small',

            variantId:
                'mi_multiblock_editor:farmer_small',

            radius:
                5,

            maxDistanceSquared:
                20,

            towerHeight:
                4
        },
        {
            name:
                'medium',

            variantId:
                'mi_multiblock_editor:farmer_medium',

            radius:
                7,

            maxDistanceSquared:
                42,

            towerHeight:
                5
        }
    ];

    function getSymbol(
        variant,
        relativeX,
        relativeY,
        relativeZ
    ) {
        var bottomY =
            -variant.towerHeight + 1;

        var insideTower =
            relativeX >= -1
            && relativeX <= 1
            && relativeZ >= 0
            && relativeZ <= 2;

        if (insideTower) {
            var isBottom =
                relativeY == bottomY;

            var isTop =
                relativeY == 0;

            var isSecondFromTop =
                relativeY == -1;

            var isMiddle =
                !isBottom
                && !isTop
                && !isSecondFromTop;

            if (!isMiddle) {
                return 'B';
            }

            if (
                relativeX == 0
                && relativeZ == 1
            ) {
                return 'P';
            }

            return 'H';
        }

        if (relativeY != -1) {
            return ' ';
        }

        var sourceZ =
            relativeZ - 1;

        if (
            Math.abs(relativeX) <= 1
            && Math.abs(sourceZ) <= 1
        ) {
            return ' ';
        }

        var distanceSquared =
            relativeX * relativeX
            + sourceZ * sourceZ;

        if (
            distanceSquared
            < variant.maxDistanceSquared
        ) {
            return 'D';
        }

        return ' ';
    }

    function replaceCharacter(
        row,
        index,
        character
    ) {
        return row.substring(
            0,
            index
        )
            + character
            + row.substring(
                index + 1
            );
    }

    function createLayerRows(
        variant,
        relativeY,
        topLayer
    ) {
        var size =
            variant.radius * 2 + 1;

        var controllerGridX =
            variant.radius;

        var controllerGridZ =
            variant.radius + 1;

        var rows = [];

        for (
            var gridZ = 0;
            gridZ < size;
            gridZ++
        ) {
            var relativeZ =
                controllerGridZ - gridZ;

            var row = '';

            for (
                var gridX = 0;
                gridX < size;
                gridX++
            ) {
                var relativeX =
                    gridX - controllerGridX;

                row += getSymbol(
                    variant,
                    relativeX,
                    relativeY,
                    relativeZ
                );
            }

            if (
                topLayer
                && gridZ == controllerGridZ
            ) {
                row = replaceCharacter(
                    row,
                    controllerGridX,
                    '#'
                );
            }

            rows.push(
                row
            );
        }

        return rows;
    }

    function createShapeLayers(
        variant
    ) {
        var bottomY =
            -variant.towerHeight + 1;

        var layers = [];

        for (
            var relativeY = bottomY;
            relativeY < 0;
            relativeY++
        ) {
            layers.push(
                createLayerRows(
                    variant,
                    relativeY,
                    false
                )
            );
        }

        layers.push(
            createLayerRows(
                variant,
                0,
                true
            )
        );

        return layers;
    }

    function countControllers(
        layers
    ) {
        var count = 0;

        for (
            var layerIndex = 0;
            layerIndex < layers.length;
            layerIndex++
        ) {
            var layer =
                layers[layerIndex];

            for (
                var rowIndex = 0;
                rowIndex < layer.length;
                rowIndex++
            ) {
                var row =
                    '' + layer[rowIndex];

                var searchIndex =
                    row.indexOf('#');

                while (searchIndex >= 0) {
                    count++;

                    searchIndex =
                        row.indexOf(
                            '#',
                            searchIndex + 1
                        );
                }
            }
        }

        return count;
    }

    function validateShape(
        structureId,
        variant,
        layers
    ) {
        var expectedSize =
            variant.radius * 2 + 1;

        var expectedLayerCount =
            variant.towerHeight;

        if (
            layers.length
            != expectedLayerCount
        ) {
            throw new Error(
                'Steam Farmer structure '
                    + structureId
                    + ' generated '
                    + layers.length
                    + ' layers instead of '
                    + expectedLayerCount
            );
        }

        for (
            var layerIndex = 0;
            layerIndex < layers.length;
            layerIndex++
        ) {
            var layer =
                layers[layerIndex];

            if (
                layer.length
                != expectedSize
            ) {
                throw new Error(
                    'Steam Farmer structure '
                        + structureId
                        + ' has invalid depth at layer '
                        + layerIndex
                        + ': expected '
                        + expectedSize
                        + ', got '
                        + layer.length
                );
            }

            for (
                var rowIndex = 0;
                rowIndex < layer.length;
                rowIndex++
            ) {
                var row =
                    '' + layer[rowIndex];

                if (
                    row.length
                    != expectedSize
                ) {
                    throw new Error(
                        'Steam Farmer structure '
                            + structureId
                            + ' has invalid width at layer '
                            + layerIndex
                            + ', row '
                            + rowIndex
                            + ': expected '
                            + expectedSize
                            + ', got '
                            + row.length
                    );
                }
            }
        }

        var topLayer =
            layers[layers.length - 1];

        var controllerRow =
            '' + topLayer[
                variant.radius + 1
            ];

        var controllerCharacter =
            controllerRow.substring(
                variant.radius,
                variant.radius + 1
            );

        if (controllerCharacter != '#') {
            throw new Error(
                'Steam Farmer structure '
                    + structureId
                    + ' has no controller at the expected '
                    + 'position. Generated row: "'
                    + controllerRow
                    + '"'
            );
        }

        var controllerCount =
            countControllers(
                layers
            );

        if (controllerCount != 1) {
            throw new Error(
                'Steam Farmer structure '
                    + structureId
                    + ' generated '
                    + controllerCount
                    + ' controller characters'
            );
        }
    }

    function registerShape(
        variant
    ) {
        var structureId =
            STRUCTURE_PREFIX
            + variant.name;

        var layers =
            createShapeLayers(
                variant
            );

        validateShape(
            structureId,
            variant,
            layers
        );

        var builder =
            MIMultiblocks
                .create(
                    structureId
                )
                .requiresMod(
                    'extended_industrialization'
                )
                .controller(
                    CONTROLLER_ID
                )
                .variant(
                    variant.variantId
                )
                .hatchCasing(
                    HATCH_CASING_ID
                );

        for (
            var layerIndex = 0;
            layerIndex < layers.length;
            layerIndex++
        ) {
            builder.layer(
                layers[layerIndex]
            );
        }

        builder
            .mappingBlock(
                'B',
                BASE_BLOCK_ID,
                []
            )

            .mappingBlock(
                'P',
                PIPE_BLOCK_ID,
                []
            )

            .mappingBlock(
                'H',
                PIPE_BLOCK_ID,
                HATCH_TYPES
            )

            .mappingTag(
                'D',
                FARMER_DIRT_MEMBER_TAG,
                'minecraft:dirt',
                []
            )

            .register();

    }

    for (
        var variantIndex = 0;
        variantIndex < VARIANTS.length;
        variantIndex++
    ) {
        registerShape(
            VARIANTS[variantIndex]
        );
    }

})();

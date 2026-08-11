package dev.atmos_studio.mimultiblockeditor.adapter;

import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class LargeTankAdapterRules {
    private static final int[] X_SIZES = {
            3,
            5,
            7
    };

    private static final int[] Y_SIZES = {
            3,
            4,
            5,
            6,
            7
    };

    private static final int[] Z_SIZES = {
            3,
            4,
            5,
            6,
            7
    };

    public static final int SHAPE_COUNT =
            X_SIZES.length
                    * Y_SIZES.length
                    * Z_SIZES.length;

    private LargeTankAdapterRules() {
    }

    public static void validate(
            ResourceLocation controllerId,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (registrations.size() != SHAPE_COUNT) {
            throw new IllegalArgumentException(
                    "Large Tank controller "
                            + controllerId
                            + " requires exactly "
                            + SHAPE_COUNT
                            + " ordered KubeJS structure shapes, but "
                            + registrations.size()
                            + " were registered"
            );
        }

        for (int index = 0;
             index < registrations.size();
             index++) {

            KubeJSStructureRegistration registration =
                    registrations.get(index);

            int xIndex =
                    index / 25;

            int yIndex =
                    index % 25 / 5;

            int zIndex =
                    index % 5;

            int expectedWidth =
                    X_SIZES[xIndex];

            int expectedHeight =
                    Y_SIZES[yIndex];

            int expectedDepth =
                    Z_SIZES[zIndex];

            validateShape(
                    controllerId,
                    registration,
                    index,
                    expectedWidth,
                    expectedHeight,
                    expectedDepth
            );
        }
    }

    private static void validateShape(
            ResourceLocation controllerId,
            KubeJSStructureRegistration registration,
            int shapeIndex,
            int expectedWidth,
            int expectedHeight,
            int expectedDepth
    ) {
        List<List<String>> layers =
                registration.definition().layers();

        int actualHeight =
                layers.size();

        int actualDepth =
                layers.getFirst().size();

        int actualWidth =
                layers.getFirst()
                        .getFirst()
                        .length();

        if (actualWidth != expectedWidth
                || actualHeight != expectedHeight
                || actualDepth != expectedDepth) {

            throw new IllegalArgumentException(
                    "Large Tank shape "
                            + registration.structureId()
                            + " at index "
                            + shapeIndex
                            + " has dimensions "
                            + actualWidth
                            + "x"
                            + actualHeight
                            + "x"
                            + actualDepth
                            + ", but expected "
                            + expectedWidth
                            + "x"
                            + expectedHeight
                            + "x"
                            + expectedDepth
                            + " for controller "
                            + controllerId
            );
        }

        ControllerPosition controllerPosition =
                findController(
                        registration
                );

        int expectedControllerLayer = 1;
        int expectedControllerRow =
                expectedDepth - 1;
        int expectedControllerColumn =
                expectedWidth / 2;

        if (controllerPosition.layer()
                != expectedControllerLayer
                || controllerPosition.row()
                != expectedControllerRow
                || controllerPosition.column()
                != expectedControllerColumn) {

            throw new IllegalArgumentException(
                    "Large Tank shape "
                            + registration.structureId()
                            + " has controller at layer="
                            + controllerPosition.layer()
                            + ", row="
                            + controllerPosition.row()
                            + ", column="
                            + controllerPosition.column()
                            + ", but expected layer="
                            + expectedControllerLayer
                            + ", row="
                            + expectedControllerRow
                            + ", column="
                            + expectedControllerColumn
            );
        }
    }

    private static ControllerPosition findController(
            KubeJSStructureRegistration registration
    ) {
        List<List<String>> layers =
                registration.definition().layers();

        for (int layer = 0;
             layer < layers.size();
             layer++) {

            List<String> rows =
                    layers.get(layer);

            for (int row = 0;
                 row < rows.size();
                 row++) {

                int column =
                        rows.get(row)
                                .indexOf('#');

                if (column >= 0) {
                    return new ControllerPosition(
                            layer,
                            row,
                            column
                    );
                }
            }
        }

        throw new IllegalArgumentException(
                "Large Tank shape "
                        + registration.structureId()
                        + " has no controller character '#'"
        );
    }

    private record ControllerPosition(
            int layer,
            int row,
            int column
    ) {
    }
}
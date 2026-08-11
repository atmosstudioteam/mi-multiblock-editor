package dev.atmos_studio.mimultiblockeditor.adapter;

import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public final class NuclearReactorAdapterRules {
    private static final int[] GRID_SIZES = {
            5,
            7,
            9,
            11
    };

    private static final int STRUCTURE_HEIGHT = 5;

    public static final int SHAPE_COUNT =
            GRID_SIZES.length;

    private NuclearReactorAdapterRules() {
    }

    public static void validate(
            ResourceLocation controllerId,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (registrations.size() != SHAPE_COUNT) {
            throw new IllegalArgumentException(
                    "Nuclear Reactor controller "
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

            validateShape(
                    controllerId,
                    registrations.get(index),
                    index,
                    GRID_SIZES[index]
            );
        }
    }

    private static void validateShape(
            ResourceLocation controllerId,
            KubeJSStructureRegistration registration,
            int shapeIndex,
            int expectedSize
    ) {
        List<List<String>> layers =
                registration.definition().layers();

        if (layers.size() != STRUCTURE_HEIGHT) {
            throw new IllegalArgumentException(
                    "Nuclear Reactor shape "
                            + registration.structureId()
                            + " at index "
                            + shapeIndex
                            + " has height "
                            + layers.size()
                            + ", but expected "
                            + STRUCTURE_HEIGHT
            );
        }

        for (int layerIndex = 0;
             layerIndex < layers.size();
             layerIndex++) {

            List<String> rows =
                    layers.get(layerIndex);

            if (rows.size() != expectedSize) {
                throw new IllegalArgumentException(
                        "Nuclear Reactor shape "
                                + registration.structureId()
                                + " at index "
                                + shapeIndex
                                + ", layer "
                                + layerIndex
                                + " has depth "
                                + rows.size()
                                + ", but expected "
                                + expectedSize
                );
            }

            for (int rowIndex = 0;
                 rowIndex < rows.size();
                 rowIndex++) {

                int actualWidth =
                        rows.get(rowIndex).length();

                if (actualWidth != expectedSize) {
                    throw new IllegalArgumentException(
                            "Nuclear Reactor shape "
                                    + registration.structureId()
                                    + " at index "
                                    + shapeIndex
                                    + ", layer "
                                    + layerIndex
                                    + ", row "
                                    + rowIndex
                                    + " has width "
                                    + actualWidth
                                    + ", but expected "
                                    + expectedSize
                    );
                }
            }
        }

        ControllerPosition controllerPosition =
                findController(registration);

        int expectedControllerLayer = 1;
        int expectedControllerRow =
                expectedSize - 1;
        int expectedControllerColumn =
                expectedSize / 2;

        if (controllerPosition.layer()
                != expectedControllerLayer
                || controllerPosition.row()
                != expectedControllerRow
                || controllerPosition.column()
                != expectedControllerColumn) {

            throw new IllegalArgumentException(
                    "Nuclear Reactor shape "
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

        for (int layerIndex = 0;
             layerIndex < layers.size();
             layerIndex++) {

            List<String> rows =
                    layers.get(layerIndex);

            for (int rowIndex = 0;
                 rowIndex < rows.size();
                 rowIndex++) {

                int column =
                        rows.get(rowIndex).indexOf('#');

                if (column >= 0) {
                    return new ControllerPosition(
                            layerIndex,
                            rowIndex,
                            column
                    );
                }
            }
        }

        throw new IllegalArgumentException(
                "Nuclear Reactor shape "
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
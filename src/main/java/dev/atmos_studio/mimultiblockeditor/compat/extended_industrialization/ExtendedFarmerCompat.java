package dev.atmos_studio.mimultiblockeditor.compat.extended_industrialization;

import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterResolution;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockVariantOrderer;
import dev.atmos_studio.mimultiblockeditor.data.MultiblockStructureDefinition;
import dev.atmos_studio.mimultiblockeditor.data.StructureMemberDefinition;
import dev.atmos_studio.mimultiblockeditor.data.StructureMemberType;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistration;
import dev.atmos_studio.mimultiblockeditor.kubejs.KubeJSStructureRegistry;
import dev.atmos_studio.mimultiblockeditor.runtime.CompiledMultiblockOverride;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ExtendedFarmerCompat {
    public static final String MOD_ID =
            "extended_industrialization";

    public static final ResourceLocation
            STEAM_FARMER_CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "steam_farmer"
            );

    public static final ResourceLocation
            ELECTRIC_FARMER_CONTROLLER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MOD_ID,
                    "electric_farmer"
            );

    public static final ResourceLocation
            SMALL_VARIANT_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "mi_multiblock_editor",
                    "farmer_small"
            );

    public static final ResourceLocation
            MEDIUM_VARIANT_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "mi_multiblock_editor",
                    "farmer_medium"
            );

    public static final ResourceLocation
            LARGE_VARIANT_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "mi_multiblock_editor",
                    "farmer_large"
            );

    public static final ResourceLocation
            EXTREME_VARIANT_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "mi_multiblock_editor",
                    "farmer_extreme"
            );

    public static final ResourceLocation
            FARMER_DIRT_MEMBER_TAG_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "mi_multiblock_editor",
                    "extended_farmer_dirt"
            );

    private static final TagKey<Block>
            FARMER_DIRT_BLOCK_TAG =
            TagKey.create(
                    Registries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(
                            MOD_ID,
                            "farmer_dirt"
                    )
            );

    private static final String
            FARMER_BASE_CLASS_NAME =
            "net.swedz.extended_industrialization."
                    + "machines.blockentity.multiblock.farmer."
                    + "FarmerBlockEntity";

    private static final String
            STEAM_FARMER_CLASS_NAME =
            "net.swedz.extended_industrialization."
                    + "machines.blockentity.multiblock.farmer."
                    + "SteamFarmerBlockEntity";

    private static final String
            ELECTRIC_FARMER_CLASS_NAME =
            "net.swedz.extended_industrialization."
                    + "machines.blockentity.multiblock.farmer."
                    + "ElectricFarmerBlockEntity";

    private static final Map<ResourceLocation, Long>
            APPLIED_GENERATIONS =
            new ConcurrentHashMap<>();

    private static final SimpleMember
            FARMER_DIRT_MEMBER =
            new SimpleMember() {
                @Override
                public boolean matchesState(
                        BlockState state,
                        BlockEntity blockEntity
                ) {
                    return state.is(
                            FARMER_DIRT_BLOCK_TAG
                    )
                            || state.getFluidState().is(
                            Fluids.WATER
                    )
                            || state.getFluidState().is(
                            Fluids.FLOWING_WATER
                    );
                }

                @Override
                public BlockState getPreviewState() {
                    return Blocks.DIRT
                            .defaultBlockState();
                }
            };

    private ExtendedFarmerCompat() {
    }

    public static boolean isFarmerController(
            ResourceLocation controllerId
    ) {
        return STEAM_FARMER_CONTROLLER_ID.equals(
                controllerId
        ) || ELECTRIC_FARMER_CONTROLLER_ID.equals(
                controllerId
        );
    }

    public static boolean isFarmerClassName(
            String className
    ) {
        return STEAM_FARMER_CLASS_NAME.equals(
                className
        ) || ELECTRIC_FARMER_CLASS_NAME.equals(
                className
        );
    }

    public static String farmerBaseClassName() {
        return FARMER_BASE_CLASS_NAME;
    }

    public static SimpleMember farmerDirtMember() {
        return FARMER_DIRT_MEMBER;
    }

    public static boolean isFarmerDirtTag(
            ResourceLocation tagId
    ) {
        return FARMER_DIRT_MEMBER_TAG_ID.equals(
                tagId
        );
    }

    public static List<ResourceLocation>
    expectedVariants(
            ResourceLocation controllerId
    ) {
        if (STEAM_FARMER_CONTROLLER_ID.equals(
                controllerId
        )) {
            return List.of(
                    SMALL_VARIANT_ID,
                    MEDIUM_VARIANT_ID
            );
        }

        if (ELECTRIC_FARMER_CONTROLLER_ID.equals(
                controllerId
        )) {
            return List.of(
                    SMALL_VARIANT_ID,
                    MEDIUM_VARIANT_ID,
                    LARGE_VARIANT_ID,
                    EXTREME_VARIANT_ID
            );
        }

        throw new IllegalArgumentException(
                "Controller "
                        + controllerId
                        + " is not an Extended "
                        + "Industrialization farmer"
        );
    }

    public static List<KubeJSStructureRegistration>
    orderRegistrations(
            ResourceLocation controllerId,
            MultiblockAdapterResolution resolution,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (!isFarmerController(
                controllerId
        )) {
            throw new IllegalArgumentException(
                    "Controller "
                            + controllerId
                            + " is not an Extended "
                            + "Industrialization farmer"
            );
        }

        if (resolution.adapterType()
                != MultiblockAdapterType
                .EXTENDED_FARMER) {

            throw new IllegalArgumentException(
                    "Farmer controller "
                            + controllerId
                            + " was resolved to adapter "
                            + resolution.adapterType().id()
                            + " instead of "
                            + MultiblockAdapterType
                            .EXTENDED_FARMER
                            .id()
            );
        }

        validateRegistrations(
                controllerId,
                registrations
        );

        return MultiblockVariantOrderer
                .orderByVariantKeys(
                        controllerId,
                        resolution.adapterType(),
                        registrations,
                        expectedVariants(
                                controllerId
                        )
                );
    }

    public static void applyRuntimeOverride(
            Object rawBlockEntity
    ) {
        if (!(rawBlockEntity
                instanceof BlockEntity blockEntity)) {
            return;
        }

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        blockEntity
                                .getBlockState()
                                .getBlock()
                );

        if (!isFarmerController(
                controllerId
        )) {
            return;
        }

        MultiblockOverrideCache.ensureBuilt();

        long generation =
                MultiblockOverrideCache.generation();

        Long appliedGeneration =
                APPLIED_GENERATIONS.get(
                        controllerId
                );

        if (appliedGeneration != null
                && appliedGeneration == generation) {
            return;
        }

        synchronized (ExtendedFarmerCompat.class) {
            appliedGeneration =
                    APPLIED_GENERATIONS.get(
                            controllerId
                    );

            if (appliedGeneration != null
                    && appliedGeneration == generation) {
                return;
            }

            CompiledMultiblockOverride override =
                    MultiblockOverrideCache.get(
                            controllerId
                    );

            if (override == null) {
                return;
            }

            if (override.adapterType()
                    != MultiblockAdapterType
                    .EXTENDED_FARMER) {

                throw new IllegalStateException(
                        "Controller "
                                + controllerId
                                + " has compiled adapter "
                                + override.adapterType().id()
                                + " instead of "
                                + MultiblockAdapterType
                                .EXTENDED_FARMER
                                .id()
                );
            }

            List<KubeJSStructureRegistration>
                    orderedRegistrations =
                    getOrderedRegistrations(
                            controllerId
                    );

            List<List<BlockPos>>
                    dirtPositionsByShape =
                    collectDirtPositions(
                            controllerId,
                            orderedRegistrations
                    );

            applyToShapeWrapper(
                    rawBlockEntity,
                    controllerId,
                    override,
                    dirtPositionsByShape
            );

            APPLIED_GENERATIONS.put(
                    controllerId,
                    generation
            );

            MIMultiblockEditor.LOGGER.info(
                    "Applied {} farmer replacement shape(s) "
                            + "and working-area definition(s) "
                            + "to controller {}; generation {}",
                    override.size(),
                    controllerId,
                    generation
            );
        }
    }

    private static List<KubeJSStructureRegistration>
    getOrderedRegistrations(
            ResourceLocation controllerId
    ) {
        List<KubeJSStructureRegistration> registrations =
                KubeJSStructureRegistry
                        .byController()
                        .get(
                                controllerId
                        );

        if (registrations == null
                || registrations.isEmpty()) {
            throw new IllegalStateException(
                    "No KubeJS farmer registrations "
                            + "were found for controller "
                            + controllerId
            );
        }

        validateRegistrations(
                controllerId,
                registrations
        );

        return MultiblockVariantOrderer
                .orderByVariantKeys(
                        controllerId,
                        MultiblockAdapterType
                                .EXTENDED_FARMER,
                        registrations,
                        expectedVariants(
                                controllerId
                        )
                );
    }

    private static void validateRegistrations(
            ResourceLocation controllerId,
            List<KubeJSStructureRegistration> registrations
    ) {
        if (registrations == null
                || registrations.isEmpty()) {
            throw new IllegalArgumentException(
                    "Farmer controller "
                            + controllerId
                            + " has no registered shapes"
            );
        }

        for (
                KubeJSStructureRegistration registration
                : registrations
        ) {
            if (registration
                    .variantId()
                    .isEmpty()) {

                throw new IllegalArgumentException(
                        "Farmer structure "
                                + registration.structureId()
                                + " must define .variant(...)"
                );
            }

            boolean hasFarmerDirtMapping =
                    registration
                            .definition()
                            .mappings()
                            .values()
                            .stream()
                            .anyMatch(
                                    ExtendedFarmerCompat
                                            ::isFarmerDirtDefinition
                            );

            if (!hasFarmerDirtMapping) {
                throw new IllegalArgumentException(
                        "Farmer structure "
                                + registration.structureId()
                                + " has no mappingTag using "
                                + FARMER_DIRT_MEMBER_TAG_ID
                );
            }
        }
    }

    private static boolean isFarmerDirtDefinition(
            StructureMemberDefinition definition
    ) {
        if (definition == null
                || definition.type()
                != StructureMemberType.TAG) {
            return false;
        }

        return definition
                .tagId()
                .filter(
                        ExtendedFarmerCompat
                                ::isFarmerDirtTag
                )
                .isPresent();
    }

    private static List<List<BlockPos>>
    collectDirtPositions(
            ResourceLocation controllerId,
            List<KubeJSStructureRegistration> registrations
    ) {
        List<List<BlockPos>> result =
                new ArrayList<>(
                        registrations.size()
                );

        for (
                KubeJSStructureRegistration registration
                : registrations
        ) {
            List<BlockPos> positions =
                    collectDirtPositions(
                            registration.structureId(),
                            registration.definition()
                    );

            if (positions.isEmpty()) {
                throw new IllegalArgumentException(
                        "Farmer structure "
                                + registration.structureId()
                                + " contains no working dirt "
                                + "positions for controller "
                                + controllerId
                );
            }

            result.add(
                    positions
            );
        }

        return List.copyOf(
                result
        );
    }

    private static List<BlockPos>
    collectDirtPositions(
            ResourceLocation structureId,
            MultiblockStructureDefinition definition
    ) {
        ControllerPosition controller =
                findController(
                        structureId,
                        definition.layers()
                );

        Set<BlockPos> positions =
                new LinkedHashSet<>();

        List<List<String>> layers =
                definition.layers();

        for (int y = 0;
             y < layers.size();
             y++) {

            List<String> layer =
                    layers.get(y);

            for (int z = 0;
                 z < layer.size();
                 z++) {

                String row =
                        layer.get(z);

                for (int x = 0;
                     x < row.length();
                     x++) {

                    char symbol =
                            row.charAt(x);

                    if (symbol == '#'
                            || symbol == ' ') {
                        continue;
                    }

                    StructureMemberDefinition member =
                            definition
                                    .mappings()
                                    .get(symbol);

                    if (!isFarmerDirtDefinition(
                            member
                    )) {
                        continue;
                    }

                    int relativeX =
                            x - controller.x();

                    int relativeY =
                            y - controller.y();

                    int relativeZ =
                            -(z - controller.z());

                    positions.add(
                            new BlockPos(
                                    relativeX,
                                    relativeY,
                                    relativeZ
                            )
                    );
                }
            }
        }

        return List.copyOf(
                positions
        );
    }

    private static ControllerPosition findController(
            ResourceLocation structureId,
            List<List<String>> layers
    ) {
        ControllerPosition result = null;

        for (int y = 0;
             y < layers.size();
             y++) {

            List<String> layer =
                    layers.get(y);

            for (int z = 0;
                 z < layer.size();
                 z++) {

                String row =
                        layer.get(z);

                for (int x = 0;
                     x < row.length();
                     x++) {

                    if (row.charAt(x) != '#') {
                        continue;
                    }

                    if (result != null) {
                        throw new IllegalArgumentException(
                                "Structure "
                                        + structureId
                                        + " contains more than "
                                        + "one controller"
                        );
                    }

                    result =
                            new ControllerPosition(
                                    x,
                                    y,
                                    z
                            );
                }
            }
        }

        if (result == null) {
            throw new IllegalArgumentException(
                    "Structure "
                            + structureId
                            + " contains no controller"
            );
        }

        return result;
    }

    private static void applyToShapeWrapper(
            Object blockEntity,
            ResourceLocation controllerId,
            CompiledMultiblockOverride override,
            List<List<BlockPos>> dirtPositionsByShape
    ) {
        try {
            Object shapeWrapper =
                    readField(
                            blockEntity,
                            "shapes"
                    );

            Method shapeTemplatesMethod =
                    shapeWrapper
                            .getClass()
                            .getMethod(
                                    "shapeTemplates"
                            );

            Method dirtPositionsMethod =
                    shapeWrapper
                            .getClass()
                            .getMethod(
                                    "dirtPositions"
                            );

            Object rawWrapperShapes =
                    shapeTemplatesMethod.invoke(
                            shapeWrapper
                    );

            Object rawDirtPositions =
                    dirtPositionsMethod.invoke(
                            shapeWrapper
                    );

            if (!(rawWrapperShapes
                    instanceof ShapeTemplate[]
                    wrapperShapes)) {

                throw new IllegalStateException(
                        "Farmer ShapeWrapper.shapeTemplates() "
                                + "did not return ShapeTemplate[]"
                );
            }

            if (!(rawDirtPositions
                    instanceof Object[]
                    dirtPositionArray)) {

                throw new IllegalStateException(
                        "Farmer ShapeWrapper.dirtPositions() "
                                + "did not return an array"
                );
            }

            ShapeTemplate[] replacementShapes =
                    override.shapes();

            validateRuntimeArraySizes(
                    controllerId,
                    replacementShapes.length,
                    wrapperShapes.length,
                    dirtPositionArray.length,
                    dirtPositionsByShape.size()
            );

            for (int index = 0;
                 index < replacementShapes.length;
                 index++) {

                wrapperShapes[index] =
                        replacementShapes[index];

                Array.set(
                        dirtPositionArray,
                        index,
                        dirtPositionsByShape.get(
                                index
                        )
                );
            }

            Object activeShape =
                    readField(
                            blockEntity,
                            "activeShape"
                    );

            Object rawActiveShapes =
                    readField(
                            activeShape,
                            "shapeTemplates"
                    );

            if (!(rawActiveShapes
                    instanceof ShapeTemplate[]
                    activeShapes)) {

                throw new IllegalStateException(
                        "ActiveShapeComponent.shapeTemplates "
                                + "is not ShapeTemplate[]"
                );
            }

            if (activeShapes.length
                    != replacementShapes.length) {

                throw new IllegalStateException(
                        "Farmer controller "
                                + controllerId
                                + " has "
                                + activeShapes.length
                                + " ActiveShapeComponent shape(s), "
                                + "but "
                                + replacementShapes.length
                                + " replacements were compiled"
                );
            }

            for (int index = 0;
                 index < replacementShapes.length;
                 index++) {

                activeShapes[index] =
                        replacementShapes[index];
            }
        } catch (
                ReflectiveOperationException
                | LinkageError exception
        ) {
            throw new IllegalStateException(
                    "Failed to apply Extended "
                            + "Industrialization farmer override "
                            + "for controller "
                            + controllerId,
                    exception
            );
        }
    }

    private static void validateRuntimeArraySizes(
            ResourceLocation controllerId,
            int replacementShapeCount,
            int wrapperShapeCount,
            int dirtArrayCount,
            int replacementDirtCount
    ) {
        if (replacementShapeCount
                != wrapperShapeCount
                || replacementShapeCount
                != dirtArrayCount
                || replacementShapeCount
                != replacementDirtCount) {

            throw new IllegalStateException(
                    "Farmer runtime array size mismatch "
                            + "for controller "
                            + controllerId
                            + ": replacements="
                            + replacementShapeCount
                            + ", wrapperShapes="
                            + wrapperShapeCount
                            + ", wrapperDirtPositions="
                            + dirtArrayCount
                            + ", replacementDirtPositions="
                            + replacementDirtCount
            );
        }
    }

    private static Object readField(
            Object target,
            String fieldName
    ) throws ReflectiveOperationException {
        if (target == null) {
            throw new IllegalArgumentException(
                    "Cannot read field "
                            + fieldName
                            + " from null"
            );
        }

        Field field =
                findField(
                        target.getClass(),
                        fieldName
                );

        if (!field.trySetAccessible()) {
            throw new IllegalAccessException(
                    "Cannot access field "
                            + fieldName
                            + " in "
                            + target
                            .getClass()
                            .getName()
            );
        }

        return field.get(
                target
        );
    }

    private static Field findField(
            Class<?> type,
            String fieldName
    ) throws NoSuchFieldException {
        Class<?> currentClass =
                type;

        while (currentClass != null) {
            try {
                return currentClass
                        .getDeclaredField(
                                fieldName
                        );
            } catch (
                    NoSuchFieldException ignored
            ) {
                currentClass =
                        currentClass
                                .getSuperclass();
            }
        }

        throw new NoSuchFieldException(
                type.getName()
                        + "."
                        + fieldName
        );
    }

    private record ControllerPosition(
            int x,
            int y,
            int z
    ) {
    }
}
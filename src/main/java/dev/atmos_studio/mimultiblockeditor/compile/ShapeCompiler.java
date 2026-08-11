package dev.atmos_studio.mimultiblockeditor.compile;

import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineCasings;
import aztech.modern_industrialization.machines.multiblocks.HatchFlags;
import aztech.modern_industrialization.machines.multiblocks.HatchType;
import aztech.modern_industrialization.machines.multiblocks.HatchTypes;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import dev.atmos_studio.mimultiblockeditor.data.MultiblockStructureDefinition;
import dev.atmos_studio.mimultiblockeditor.data.StructureMemberDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ShapeCompiler {
    private ShapeCompiler() {
    }

    public static ShapeTemplate compile(
            ResourceLocation structureId,
            MultiblockStructureDefinition definition
    ) {
        try {
            MachineCasing hatchCasing =
                    MachineCasings.get(
                            definition.hatchCasingId()
                    );

            ControllerPosition controller =
                    findController(
                            structureId,
                            definition.layers()
                    );

            ShapeTemplate.Builder builder =
                    new ShapeTemplate.Builder(
                            hatchCasing
                    );

            Map<Character, SimpleMember>
                    compiledMembers =
                    new HashMap<>();

            Map<Character, HatchFlags>
                    compiledHatchFlags =
                    new HashMap<>();

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

                        if (symbol == ' '
                                || symbol == '#') {
                            continue;
                        }

                        StructureMemberDefinition
                                memberDefinition =
                                definition.mappings()
                                        .get(symbol);

                        if (memberDefinition == null) {
                            throw new ShapeCompilationException(
                                    "Structure "
                                            + structureId
                                            + " uses unmapped symbol '"
                                            + symbol
                                            + "'"
                            );
                        }

                        SimpleMember member =
                                compiledMembers.computeIfAbsent(
                                        symbol,
                                        ignored ->
                                                compileMember(
                                                        structureId,
                                                        symbol,
                                                        memberDefinition
                                                )
                                );

                        HatchFlags hatchFlags =
                                compiledHatchFlags
                                        .computeIfAbsent(
                                                symbol,
                                                ignored ->
                                                        compileHatchFlags(
                                                                structureId,
                                                                symbol,
                                                                memberDefinition
                                                                        .hatchTypes()
                                                        )
                                        );

                        int relativeX =
                                x - controller.x();

                        int relativeY =
                                y - controller.y();

                        int relativeZ =
                                -(z - controller.z());

                        builder.add(
                                relativeX,
                                relativeY,
                                relativeZ,
                                member,
                                hatchFlags
                        );
                    }
                }
            }

            return builder.build();
        } catch (ShapeCompilationException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ShapeCompilationException(
                    "Failed to compile structure "
                            + structureId
                            + ": "
                            + safeMessage(exception),
                    exception
            );
        }
    }

    private static SimpleMember compileMember(
            ResourceLocation structureId,
            char symbol,
            StructureMemberDefinition definition
    ) {
        return switch (definition.type()) {
            case BLOCK ->
                    compileBlockMember(
                            structureId,
                            symbol,
                            definition.blockId()
                    );

            case TAG ->
                    compileTagMember(
                            structureId,
                            symbol,
                            definition
                    );

            case STATE ->
                    compileStateMember(
                            structureId,
                            symbol,
                            definition
                    );
        };
    }

    private static SimpleMember compileBlockMember(
            ResourceLocation structureId,
            char symbol,
            ResourceLocation blockId
    ) {
        requireRegisteredBlock(
                structureId,
                symbol,
                blockId
        );

        return SimpleMember.forBlockId(
                blockId
        );
    }

    private static SimpleMember compileTagMember(
            ResourceLocation structureId,
            char symbol,
            StructureMemberDefinition definition
    ) {
        ResourceLocation previewBlockId =
                definition.blockId();

        requireRegisteredBlock(
                structureId,
                symbol,
                previewBlockId
        );

        ResourceLocation tagId =
                definition.tagId().orElseThrow(
                        () ->
                                new ShapeCompilationException(
                                        "TAG mapping '"
                                                + symbol
                                                + "' in structure "
                                                + structureId
                                                + " has no tag ID"
                                )
                );

        return SimpleMember.forBlockTagId(
                previewBlockId,
                tagId
        );
    }

    private static SimpleMember compileStateMember(
            ResourceLocation structureId,
            char symbol,
            StructureMemberDefinition definition
    ) {
        ResourceLocation blockId =
                definition.blockId();

        requireRegisteredBlock(
                structureId,
                symbol,
                blockId
        );

        Block block =
                BuiltInRegistries.BLOCK.get(
                        blockId
                );

        BlockState state =
                block.defaultBlockState();

        for (Map.Entry<String, String> propertyEntry
                : definition.stateProperties().entrySet()) {

            String propertyName =
                    propertyEntry.getKey();

            String propertyValue =
                    propertyEntry.getValue();

            Property<?> property =
                    block.getStateDefinition()
                            .getProperty(
                                    propertyName
                            );

            if (property == null) {
                throw new ShapeCompilationException(
                        "Block "
                                + blockId
                                + " has no property '"
                                + propertyName
                                + "' for symbol '"
                                + symbol
                                + "' in structure "
                                + structureId
                );
            }

            state = applyProperty(
                    structureId,
                    symbol,
                    blockId,
                    state,
                    property,
                    propertyValue
            );
        }

        return SimpleMember.forBlockState(
                state
        );
    }

    private static HatchFlags compileHatchFlags(
            ResourceLocation structureId,
            char symbol,
            List<ResourceLocation> hatchTypeIds
    ) {
        if (hatchTypeIds.isEmpty()) {
            return HatchFlags.NO_HATCH;
        }

        Set<HatchType> hatchTypes =
                new HashSet<>();

        for (ResourceLocation hatchTypeId
                : hatchTypeIds) {

            try {
                hatchTypes.add(
                        HatchTypes.get(
                                hatchTypeId
                        )
                );
            } catch (RuntimeException exception) {
                throw new ShapeCompilationException(
                        "Unknown hatch type "
                                + hatchTypeId
                                + " for symbol '"
                                + symbol
                                + "' in structure "
                                + structureId,
                        exception
                );
            }
        }

        return new HatchFlags(
                hatchTypes
        );
    }

    private static void requireRegisteredBlock(
            ResourceLocation structureId,
            char symbol,
            ResourceLocation blockId
    ) {
        if (!BuiltInRegistries.BLOCK.containsKey(
                blockId
        )) {
            throw new ShapeCompilationException(
                    "Unknown block "
                            + blockId
                            + " for symbol '"
                            + symbol
                            + "' in structure "
                            + structureId
            );
        }
    }

    private static <T extends Comparable<T>>
    BlockState applyProperty(
            ResourceLocation structureId,
            char symbol,
            ResourceLocation blockId,
            BlockState state,
            Property<T> property,
            String value
    ) {

        Optional<T> parsedValue =
                property.getValue(value);

        if (parsedValue.isEmpty()) {
            throw new ShapeCompilationException(
                    "Invalid value '"
                            + value
                            + "' for property '"
                            + property.getName()
                            + "' of block "
                            + blockId
                            + " for symbol '"
                            + symbol
                            + "' in structure "
                            + structureId
            );
        }

        return state.setValue(
                property,
                parsedValue.get()
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
                        throw new ShapeCompilationException(
                                "Structure "
                                        + structureId
                                        + " contains more than "
                                        + "one controller '#'"
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
            throw new ShapeCompilationException(
                    "Structure "
                            + structureId
                            + " contains no controller '#'"
            );
        }

        return result;
    }

    private static String safeMessage(
            Throwable throwable
    ) {
        String message =
                throwable.getMessage();

        return message == null
                || message.isBlank()
                ? "<no message>"
                : message;
    }

    private record ControllerPosition(
            int x,
            int y,
            int z
    ) {
    }
}
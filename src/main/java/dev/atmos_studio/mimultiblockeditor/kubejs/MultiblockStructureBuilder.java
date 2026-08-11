package dev.atmos_studio.mimultiblockeditor.kubejs;

import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.data.MultiblockStructureDefinition;
import dev.atmos_studio.mimultiblockeditor.data.StructureMemberDefinition;
import dev.atmos_studio.mimultiblockeditor.data.StructureMemberType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class MultiblockStructureBuilder {
    private final ResourceLocation structureId;

    private ResourceLocation controllerId;
    private ResourceLocation variantId;
    private ResourceLocation hatchCasingId;

    private final Set<String> requiredModIds =
            new LinkedHashSet<>();

    private final List<List<String>> layers =
            new ArrayList<>();

    private final Map<Character, StructureMemberDefinition> mappings =
            new LinkedHashMap<>();

    private boolean registered;

    MultiblockStructureBuilder(
            ResourceLocation structureId
    ) {
        if (structureId == null) {
            throw new IllegalArgumentException(
                    "Multiblock structure ID cannot be null"
            );
        }

        this.structureId = structureId;
    }
    public MultiblockStructureBuilder requiresMod(
            String modId
    ) {
        ensureNotRegistered();

        if (modId == null
                || modId.isBlank()) {
            throw new IllegalArgumentException(
                    "Required mod ID cannot be empty "
                            + "for structure "
                            + structureId
            );
        }

        String parsedModId =
                modId.trim();

        if (!parsedModId.matches(
                "[a-z][a-z0-9_]{1,63}"
        )) {
            throw new IllegalArgumentException(
                    "Invalid required mod ID '"
                            + parsedModId
                            + "' for structure "
                            + structureId
            );
        }

        if (!requiredModIds.add(
                parsedModId
        )) {
            throw new IllegalArgumentException(
                    "Required mod "
                            + parsedModId
                            + " was already added to structure "
                            + structureId
            );
        }

        return this;
    }

    public MultiblockStructureBuilder controller(
            String controllerId
    ) {
        ensureNotRegistered();

        if (controllerId == null
                || controllerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Multiblock controller ID cannot be empty"
            );
        }

        if (this.controllerId != null) {
            throw new IllegalStateException(
                    "Controller was already assigned to structure "
                            + structureId
                            + ": "
                            + this.controllerId
            );
        }

        this.controllerId =
                parseResourceLocation(
                        controllerId,
                        "controller ID"
                );

        return this;
    }

    public MultiblockStructureBuilder variant(
            String variantId
    ) {
        ensureNotRegistered();

        if (variantId == null
                || variantId.isBlank()) {
            throw new IllegalArgumentException(
                    "Multiblock variant ID cannot be empty"
            );
        }

        if (this.variantId != null) {
            throw new IllegalStateException(
                    "Variant was already assigned to structure "
                            + structureId
                            + ": "
                            + this.variantId
            );
        }

        this.variantId =
                parseResourceLocation(
                        variantId,
                        "variant ID"
                );

        return this;
    }

    public MultiblockStructureBuilder hatchCasing(
            String hatchCasingId
    ) {
        ensureNotRegistered();

        if (hatchCasingId == null
                || hatchCasingId.isBlank()) {
            throw new IllegalArgumentException(
                    "Multiblock hatch casing ID cannot be empty"
            );
        }

        if (this.hatchCasingId != null) {
            throw new IllegalStateException(
                    "Hatch casing was already assigned to structure "
                            + structureId
                            + ": "
                            + this.hatchCasingId
            );
        }

        this.hatchCasingId =
                parseResourceLocation(
                        hatchCasingId,
                        "hatch casing ID"
                );

        return this;
    }

    public MultiblockStructureBuilder layer(
            List<?> rows
    ) {
        ensureNotRegistered();

        if (rows == null
                || rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "Layer for structure "
                            + structureId
                            + " cannot be empty"
            );
        }

        List<String> parsedRows =
                new ArrayList<>(rows.size());

        int expectedWidth = -1;

        for (int rowIndex = 0;
             rowIndex < rows.size();
             rowIndex++) {

            Object rowObject =
                    rows.get(rowIndex);

            if (rowObject == null) {
                throw new IllegalArgumentException(
                        "Layer row "
                                + rowIndex
                                + " for structure "
                                + structureId
                                + " cannot be null"
                );
            }

            String row =
                    String.valueOf(rowObject);

            if (row.isEmpty()) {
                throw new IllegalArgumentException(
                        "Layer row "
                                + rowIndex
                                + " for structure "
                                + structureId
                                + " cannot be empty"
                );
            }

            if (expectedWidth < 0) {
                expectedWidth = row.length();
            } else if (row.length()
                    != expectedWidth) {
                throw new IllegalArgumentException(
                        "All rows in one layer must have the same width "
                                + "for structure "
                                + structureId
                                + ". Expected "
                                + expectedWidth
                                + " characters, but row "
                                + rowIndex
                                + " has "
                                + row.length()
                );
            }

            parsedRows.add(row);
        }

        layers.add(
                List.copyOf(parsedRows)
        );

        return this;
    }

    public MultiblockStructureBuilder mapping(
            String symbol,
            String blockId,
            List<?> hatchTypes
    ) {
        return mappingBlock(
                symbol,
                blockId,
                hatchTypes
        );
    }

    public MultiblockStructureBuilder mappingBlock(
            String symbol,
            String blockId,
            List<?> hatchTypes
    ) {
        ensureNotRegistered();

        char parsedSymbol =
                parseMappingSymbol(symbol);

        ResourceLocation parsedBlockId =
                parseResourceLocation(
                        blockId,
                        "block ID for symbol '"
                                + parsedSymbol
                                + "'"
                );

        StructureMemberDefinition definition =
                new StructureMemberDefinition(
                        StructureMemberType.BLOCK,
                        parsedBlockId,
                        Optional.empty(),
                        Map.of(),
                        parseHatchTypes(
                                parsedSymbol,
                                hatchTypes
                        )
                );

        addMapping(
                parsedSymbol,
                definition
        );

        return this;
    }

    public MultiblockStructureBuilder mappingTag(
            String symbol,
            String tagId,
            String previewBlockId,
            List<?> hatchTypes
    ) {
        ensureNotRegistered();

        char parsedSymbol =
                parseMappingSymbol(symbol);

        ResourceLocation parsedTagId =
                parseResourceLocation(
                        tagId,
                        "block tag ID for symbol '"
                                + parsedSymbol
                                + "'"
                );

        ResourceLocation parsedPreviewBlockId =
                parseResourceLocation(
                        previewBlockId,
                        "preview block ID for symbol '"
                                + parsedSymbol
                                + "'"
                );

        StructureMemberDefinition definition =
                new StructureMemberDefinition(
                        StructureMemberType.TAG,
                        parsedPreviewBlockId,
                        Optional.of(parsedTagId),
                        Map.of(),
                        parseHatchTypes(
                                parsedSymbol,
                                hatchTypes
                        )
                );

        addMapping(
                parsedSymbol,
                definition
        );

        return this;
    }

    public MultiblockStructureBuilder mappingState(
            String symbol,
            String blockState,
            List<?> hatchTypes
    ) {
        ensureNotRegistered();

        char parsedSymbol =
                parseMappingSymbol(symbol);

        ParsedBlockState parsedState =
                parseBlockState(
                        parsedSymbol,
                        blockState
                );

        StructureMemberDefinition definition =
                new StructureMemberDefinition(
                        StructureMemberType.STATE,
                        parsedState.blockId(),
                        Optional.empty(),
                        parsedState.properties(),
                        parseHatchTypes(
                                parsedSymbol,
                                hatchTypes
                        )
                );

        addMapping(
                parsedSymbol,
                definition
        );

        return this;
    }

    public void register() {
        ensureNotRegistered();

        List<String> missingModIds =
                requiredModIds
                        .stream()
                        .filter(
                                modId ->
                                        !ModList
                                                .get()
                                                .isLoaded(modId)
                        )
                        .toList();

        if (!missingModIds.isEmpty()) {
            MIMultiblockEditor.LOGGER.info(
                    "Skipping KubeJS multiblock structure {} "
                            + "because required mod(s) are not loaded: {}",
                    structureId,
                    missingModIds
            );

            registered = true;
            return;
        }

        if (controllerId == null) {
            throw new IllegalStateException(
                    "Structure "
                            + structureId
                            + " has no controller. "
                            + "Call .controller(...) before .register()"
            );
        }

        if (hatchCasingId == null) {
            throw new IllegalStateException(
                    "Structure "
                            + structureId
                            + " has no hatch casing. "
                            + "Call .hatchCasing(...) before .register()"
            );
        }

        if (layers.isEmpty()) {
            throw new IllegalStateException(
                    "Structure "
                            + structureId
                            + " has no layers"
            );
        }

        validateLayerDimensions();
        validateControllerPosition();
        validateUsedSymbols();

        MultiblockStructureDefinition definition =
                new MultiblockStructureDefinition(
                        hatchCasingId,
                        List.copyOf(layers),
                        Map.copyOf(mappings)
                );

        KubeJSStructureRegistry.register(
                new KubeJSStructureRegistration(
                        structureId,
                        controllerId,
                        variantId,
                        definition
                )
        );

        registered = true;
    }

    private void addMapping(
            char symbol,
            StructureMemberDefinition definition
    ) {
        StructureMemberDefinition previous =
                mappings.putIfAbsent(
                        symbol,
                        definition
                );

        if (previous != null) {
            throw new IllegalArgumentException(
                    "Symbol '"
                            + symbol
                            + "' already has a mapping "
                            + "in structure "
                            + structureId
            );
        }
    }

    private void validateLayerDimensions() {
        List<String> firstLayer =
                layers.getFirst();

        int expectedDepth =
                firstLayer.size();

        int expectedWidth =
                firstLayer.getFirst().length();

        for (int layerIndex = 0;
             layerIndex < layers.size();
             layerIndex++) {

            List<String> layer =
                    layers.get(layerIndex);

            if (layer.size() != expectedDepth) {
                throw new IllegalArgumentException(
                        "All layers must have the same depth "
                                + "for structure "
                                + structureId
                                + ". Expected "
                                + expectedDepth
                                + " rows, but layer "
                                + layerIndex
                                + " has "
                                + layer.size()
                );
            }

            for (int rowIndex = 0;
                 rowIndex < layer.size();
                 rowIndex++) {

                int actualWidth =
                        layer.get(rowIndex).length();

                if (actualWidth != expectedWidth) {
                    throw new IllegalArgumentException(
                            "All layers must have the same width "
                                    + "for structure "
                                    + structureId
                                    + ". Expected "
                                    + expectedWidth
                                    + " characters, but layer "
                                    + layerIndex
                                    + ", row "
                                    + rowIndex
                                    + " has "
                                    + actualWidth
                    );
                }
            }
        }
    }

    private void validateControllerPosition() {
        int controllerCount = 0;

        for (List<String> layer : layers) {
            for (String row : layer) {
                for (int index = 0;
                     index < row.length();
                     index++) {

                    if (row.charAt(index) == '#') {
                        controllerCount++;
                    }
                }
            }
        }

        if (controllerCount != 1) {
            throw new IllegalArgumentException(
                    "Structure "
                            + structureId
                            + " must contain exactly one controller "
                            + "character '#', but found "
                            + controllerCount
            );
        }
    }

    private void validateUsedSymbols() {
        Set<Character> usedSymbols =
                new LinkedHashSet<>();

        for (List<String> layer : layers) {
            for (String row : layer) {
                for (int index = 0;
                     index < row.length();
                     index++) {

                    char symbol =
                            row.charAt(index);

                    if (symbol == '#'
                            || symbol == ' ') {
                        continue;
                    }

                    usedSymbols.add(symbol);
                }
            }
        }

        Set<Character> missingMappings =
                new LinkedHashSet<>(
                        usedSymbols
                );

        missingMappings.removeAll(
                mappings.keySet()
        );

        if (!missingMappings.isEmpty()) {
            throw new IllegalArgumentException(
                    "Structure "
                            + structureId
                            + " uses symbols without mappings: "
                            + missingMappings
            );
        }

        Set<Character> unusedMappings =
                new LinkedHashSet<>(
                        mappings.keySet()
                );

        unusedMappings.removeAll(
                usedSymbols
        );

        if (!unusedMappings.isEmpty()) {
            throw new IllegalArgumentException(
                    "Structure "
                            + structureId
                            + " defines mappings for unused symbols: "
                            + unusedMappings
            );
        }
    }

    private List<ResourceLocation> parseHatchTypes(
            char symbol,
            List<?> hatchTypes
    ) {
        if (hatchTypes == null
                || hatchTypes.isEmpty()) {
            return List.of();
        }

        List<ResourceLocation> parsedHatchTypes =
                new ArrayList<>(
                        hatchTypes.size()
                );

        Set<ResourceLocation> uniqueHatchTypes =
                new LinkedHashSet<>();

        for (int index = 0;
             index < hatchTypes.size();
             index++) {

            Object hatchTypeObject =
                    hatchTypes.get(index);

            if (hatchTypeObject == null) {
                throw new IllegalArgumentException(
                        "Hatch type at index "
                                + index
                                + " for symbol '"
                                + symbol
                                + "' in structure "
                                + structureId
                                + " cannot be null"
                );
            }

            String hatchTypeString =
                    String.valueOf(
                            hatchTypeObject
                    );

            ResourceLocation hatchTypeId =
                    parseResourceLocation(
                            hatchTypeString,
                            "hatch type for symbol '"
                                    + symbol
                                    + "'"
                    );

            if (!uniqueHatchTypes.add(
                    hatchTypeId
            )) {
                throw new IllegalArgumentException(
                        "Duplicate hatch type "
                                + hatchTypeId
                                + " for symbol '"
                                + symbol
                                + "' in structure "
                                + structureId
                );
            }

            parsedHatchTypes.add(
                    hatchTypeId
            );
        }

        return List.copyOf(
                parsedHatchTypes
        );
    }

    private ParsedBlockState parseBlockState(
            char symbol,
            String blockState
    ) {
        if (blockState == null
                || blockState.isBlank()) {
            throw new IllegalArgumentException(
                    "Block state for symbol '"
                            + symbol
                            + "' cannot be empty"
            );
        }

        String value =
                blockState.trim();

        int openingBracket =
                value.indexOf('[');

        if (openingBracket < 0) {
            return new ParsedBlockState(
                    parseResourceLocation(
                            value,
                            "block state block ID for symbol '"
                                    + symbol
                                    + "'"
                    ),
                    Map.of()
            );
        }

        if (!value.endsWith("]")) {
            throw new IllegalArgumentException(
                    "Invalid block state '"
                            + value
                            + "' for symbol '"
                            + symbol
                            + "' in structure "
                            + structureId
                            + ": missing closing ']'"
            );
        }

        if (value.indexOf(
                '[',
                openingBracket + 1
        ) >= 0) {
            throw new IllegalArgumentException(
                    "Invalid block state '"
                            + value
                            + "' for symbol '"
                            + symbol
                            + "' in structure "
                            + structureId
                            + ": multiple '[' characters"
            );
        }

        String blockIdString =
                value.substring(
                        0,
                        openingBracket
                ).trim();

        ResourceLocation blockId =
                parseResourceLocation(
                        blockIdString,
                        "block state block ID for symbol '"
                                + symbol
                                + "'"
                );

        String propertiesString =
                value.substring(
                        openingBracket + 1,
                        value.length() - 1
                ).trim();

        if (propertiesString.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid block state '"
                            + value
                            + "' for symbol '"
                            + symbol
                            + "' in structure "
                            + structureId
                            + ": property list cannot be empty"
            );
        }

        Map<String, String> properties =
                new LinkedHashMap<>();

        String[] propertyEntries =
                propertiesString.split(",");

        for (String propertyEntry
                : propertyEntries) {

            String entry =
                    propertyEntry.trim();

            int equalsIndex =
                    entry.indexOf('=');

            if (equalsIndex <= 0
                    || equalsIndex
                    == entry.length() - 1) {
                throw new IllegalArgumentException(
                        "Invalid property '"
                                + entry
                                + "' in block state '"
                                + value
                                + "' for structure "
                                + structureId
                                + ". Expected property=value"
                );
            }

            String propertyName =
                    entry.substring(
                            0,
                            equalsIndex
                    ).trim();

            String propertyValue =
                    entry.substring(
                            equalsIndex + 1
                    ).trim();

            if (propertyName.isEmpty()
                    || propertyValue.isEmpty()) {
                throw new IllegalArgumentException(
                        "Invalid property '"
                                + entry
                                + "' in block state '"
                                + value
                                + "' for structure "
                                + structureId
                );
            }

            String previous =
                    properties.putIfAbsent(
                            propertyName,
                            propertyValue
                    );

            if (previous != null) {
                throw new IllegalArgumentException(
                        "Duplicate property '"
                                + propertyName
                                + "' in block state '"
                                + value
                                + "' for structure "
                                + structureId
                );
            }
        }

        return new ParsedBlockState(
                blockId,
                Map.copyOf(properties)
        );
    }

    private char parseMappingSymbol(
            String symbol
    ) {
        if (symbol == null
                || symbol.length() != 1) {
            throw new IllegalArgumentException(
                    "Mapping symbol for structure "
                            + structureId
                            + " must contain exactly one character"
            );
        }

        char parsedSymbol =
                symbol.charAt(0);

        if (parsedSymbol == '#') {
            throw new IllegalArgumentException(
                    "Character '#' is reserved for the controller "
                            + "and cannot have a mapping in structure "
                            + structureId
            );
        }

        if (parsedSymbol == ' ') {
            throw new IllegalArgumentException(
                    "Space is reserved for ignored positions "
                            + "and cannot have a mapping in structure "
                            + structureId
            );
        }

        return parsedSymbol;
    }

    private ResourceLocation parseResourceLocation(
            String value,
            String description
    ) {
        if (value == null
                || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid "
                            + description
                            + " in structure "
                            + structureId
                            + ": value cannot be empty"
            );
        }

        try {
            return ResourceLocation.parse(
                    value.trim()
            );
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(
                    "Invalid "
                            + description
                            + " '"
                            + value
                            + "' in structure "
                            + structureId,
                    exception
            );
        }
    }

    private void ensureNotRegistered() {
        if (registered) {
            throw new IllegalStateException(
                    "Structure "
                            + structureId
                            + " was already registered"
            );
        }
    }

    private record ParsedBlockState(
            ResourceLocation blockId,
            Map<String, String> properties
    ) {
    }
}
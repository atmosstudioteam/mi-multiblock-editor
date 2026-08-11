# KubeJS API

MI Multiblock Editor exposes one global binding to **KubeJS startup scripts**:

```js
MIMultiblocks
```

The usual flow is:

```js
MIMultiblocks
    .create('namespace:structure_id')
    .controller('namespace:controller_block')
    .hatchCasing('namespace:machine_casing_model')
    .layer([
        'AAA',
        'A#A',
        'AAA'
    ])
    .mappingBlock('A', 'minecraft:bricks', [])
    .register();
```

## `MIMultiblocks.create(structureId)`

Starts a new structure definition.

```js
MIMultiblocks.create('mypack:vacuum_freezer');
```

`structureId` is an identifier for the override definition itself. It does not replace the controller ID.

## `.requiresMod(modId)`

Marks the definition as dependent on an optional mod.

```js
.requiresMod('yet_another_industrialization')
```

When the mod is absent, the definition is skipped before controller, block, casing, and mapping validation. The method can be called multiple times for structures that require more than one optional mod.

## `.controller(controllerId)`

Selects the existing multiblock controller whose structure is being replaced.

```js
.controller('modern_industrialization:vacuum_freezer')
```

Exactly one controller must be assigned to a structure definition.

## `.variant(variantId)`

Assigns a stable logical key to one form of a multi-form machine.

```js
.variant('modern_industrialization:cupronickel_coil')
```

Some special adapters require variants because the machine internally associates a shape index with a tier, coil, soil, winding, casing, or another stable key. For ordinary single-shape machines, `.variant(...)` is not needed.

## `.hatchCasing(casingId)`

Selects the MI `MachineCasing` model used for hatch rendering in this shape.

```js
.hatchCasing('modern_industrialization:frostproof_machine_casing')
```

This is a **machine casing model ID**, which is not necessarily the same thing as the physical block used by a mapping.

## `.layer(rows)`

Adds one horizontal layer.

```js
.layer([
    'AAA',
    'A#A',
    'AAA'
])
```

Coordinate order:

1. `.layer()` calls go from bottom to top on **Y**.
2. Rows go from front to back on **Z**.
3. Characters go from left to right on **X**.

Special characters:

- `#` — controller position;
- space (` `) — ignored position.

Rules:

- the complete structure must contain exactly one `#`;
- all rows in one layer must have the same width;
- all layers must have the same depth and width;
- every used symbol other than `#` and space must have a mapping;
- mappings for unused symbols are rejected.

## `.mapping(symbol, blockId, hatchTypes)`

Alias for `.mappingBlock(...)`.

```js
.mapping('A', 'minecraft:bricks', [])
```

## `.mappingBlock(symbol, blockId, hatchTypes)`

Requires one exact block at every position using the symbol.

```js
.mappingBlock(
    'A',
    'modern_industrialization:steel_machine_casing',
    [
        'modern_industrialization:item_input',
        'modern_industrialization:item_output'
    ]
)
```

The `hatchTypes` array lists the hatch types that may replace the mapped block at those positions. Use `[]` to allow no hatches.

## `.mappingTag(symbol, tagId, previewBlockId, hatchTypes)`

Accepts any block in a block tag.

```js
.mappingTag(
    'G',
    'c:glass_blocks',
    'minecraft:glass',
    []
)
```

`previewBlockId` is the representative block used when the structure is displayed by a recipe viewer.

## `.mappingState(symbol, blockState, hatchTypes)`

Requires an exact block state.

```js
.mappingState(
    'L',
    'minecraft:oak_log[axis=y]',
    []
)
```

Properties omitted from the string are taken from the block's default state and still participate in the exact state comparison. This API does not provide wildcard state matching or NBT matching.

## `.register()`

Validates and registers the completed structure definition.

```js
.register();
```

Registration checks include controller presence, hatch casing presence, layer dimensions, the controller count, and symbol mappings.

## Hatch type IDs

Hatch type IDs are resource locations registered by MI or an addon. Common MI examples include:

```text
modern_industrialization:item_input
modern_industrialization:item_output
modern_industrialization:fluid_input
modern_industrialization:fluid_output
modern_industrialization:energy_input
modern_industrialization:energy_output
```

Special machines and addons may define additional hatch types.

## Distillation Tower helper

The binding also exposes:

```js
MIMultiblocks.distillationTowerMaximumHeight()
```

It returns the maximum number of Distillation Tower shape definitions that a startup script should prepare. The runtime cache later limits the compiled forms to the height selected by the loaded MI startup configuration.

## Full example

```js
MIMultiblocks
    .create('mypack:example_machine')
    .requiresMod('example_addon')
    .controller('example_addon:controller')
    .variant('example_addon:tier_one')
    .hatchCasing('modern_industrialization:steel')
    .layer([
        'AAA',
        'A#A',
        'AAA'
    ])
    .layer([
        'AGA',
        'G G',
        'AGA'
    ])
    .mappingBlock(
        'A',
        'modern_industrialization:steel_machine_casing',
        [
            'modern_industrialization:item_input',
            'modern_industrialization:item_output',
            'modern_industrialization:energy_input'
        ]
    )
    .mappingTag(
        'G',
        'c:glass_blocks',
        'minecraft:glass',
        []
    )
    .register();
```

Because this API is registered only for KubeJS **STARTUP** scripts, changing a structure requires a full restart rather than `/reload`.

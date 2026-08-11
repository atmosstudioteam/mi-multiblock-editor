# MI Multiblock Editor Examples

This directory contains complete KubeJS startup-script examples for supported
Modern Industrialization multiblocks and compatible addon machines.

These examples are intended as templates for modpack developers. Copy the
required file into:

`kubejs/startup_scripts/`

Then modify the structure, mappings and `example:` namespace as needed.

> MI Multiblock Editor is registered during the KubeJS startup phase.
> Changes to these scripts require a full game restart.

## Basic examples

The `basic/` directory contains small examples demonstrating the public API
without focusing on a specific special multiblock.

- [`basic_multiblock.js`](basic/basic_multiblock.js) — basic structure replacement.
- [`optional_mod_multiblock.js`](basic/optional_mod_multiblock.js) — optional mod handling with `.requiresMod(...)`.
- [`variant_multiblock.js`](basic/variant_multiblock.js) — multiple structure variants with `.variant(...)`.

## Modern Industrialization

### Vacuum Freezer

[`modern_industrialization/vacuum_freezer.js`](modern_industrialization/vacuum_freezer.js)

A simple example of replacing a standard Modern Industrialization crafting
multiblock.

### Electric Blast Furnace

[`modern_industrialization/electric_blast_furnace.js`](modern_industrialization/electric_blast_furnace.js)

The Electric Blast Furnace supports multiple coil-based structure variants.

The example demonstrates how `.variant(...)` is used to associate each KubeJS
structure with the corresponding MI coil tier.

### Distillation Tower

[`modern_industrialization/distillation_tower.js`](modern_industrialization/distillation_tower.js)

The Distillation Tower has a variable-height structure.

The example registers the required structure forms so the runtime structure can
match the configured tower height.

### Large Tank

[`modern_industrialization/large_tank.js`](modern_industrialization/large_tank.js)

The Large Tank supports multiple valid sizes.

The example contains the structure forms used by the Large Tank adapter.

### Nuclear Reactor

[`modern_industrialization/nuclear_reactor.js`](modern_industrialization/nuclear_reactor.js)

Demonstrates the multiple structure forms used by the Modern Industrialization
Nuclear Reactor.

### Fusion Reactor

[`modern_industrialization/fusion_reactor.js`](modern_industrialization/fusion_reactor.js)

Example replacement for the Fusion Reactor structure.

### Steam Boilers

- [`large_steam_boiler.js`](modern_industrialization/large_steam_boiler.js)
- [`advanced_large_steam_boiler.js`](modern_industrialization/advanced_large_steam_boiler.js)
- [`high_pressure_large_steam_boiler.js`](modern_industrialization/high_pressure_large_steam_boiler.js)
- [`high_pressure_advanced_large_steam_boiler.js`](modern_industrialization/high_pressure_advanced_large_steam_boiler.js)

Each Steam Boiler has its own controller and therefore its own example file.

## Extended Industrialization

Extended Industrialization is optional.

The examples in this directory use `.requiresMod(...)` where appropriate so
definitions can safely be skipped when the addon is not installed.

### Steam Farmer

[`extended_industrialization/steam_farmer.js`](extended_industrialization/steam_farmer.js)

Contains the supported Steam Farmer structure variants.

### Electric Farmer

[`extended_industrialization/electric_farmer.js`](extended_industrialization/electric_farmer.js)

Contains the supported Electric Farmer structure variants.

### Large Electric Furnace

[`extended_industrialization/large_electric_furnace.js`](extended_industrialization/large_electric_furnace.js)

Uses `.variant(...)` to associate structures with the furnace coil tiers.

### Processing Array

[`extended_industrialization/processing_array.js`](extended_industrialization/processing_array.js)

Contains the supported Processing Array forms.

### Tesla Tower

[`extended_industrialization/tesla_tower.js`](extended_industrialization/tesla_tower.js)

The Tesla Tower uses multiple winding tiers.

Each structure must keep the correct `.variant(...)` key so MI Multiblock Editor
can associate it with the matching tower tier.

## Industrialization Overdrive

Industrialization Overdrive is optional.

### Multi Processing Array

[`industrialization_overdrive/multi_processing_array.js`](industrialization_overdrive/multi_processing_array.js)

Contains the supported Multi Processing Array forms.

### Pyrolyse Oven

[`industrialization_overdrive/pyrolyse_oven.js`](industrialization_overdrive/pyrolyse_oven.js)

Uses coil-based `.variant(...)` keys for the supported Pyrolyse Oven tiers.

## Yet Another Industrialization

Yet Another Industrialization is optional.

### Arboreous Greenhouse

[`yet_another_industrialization/arboreous_greenhouse.js`](yet_another_industrialization/arboreous_greenhouse.js)

Contains the soil-based variants used by the Arboreous Greenhouse.

### Flight Pylon

[`yet_another_industrialization/flight_pylon.js`](yet_another_industrialization/flight_pylon.js)

Contains the tiered Flight Pylon structure variants.

### Large Storage Unit

[`yet_another_industrialization/large_storage_unit.js`](yet_another_industrialization/large_storage_unit.js)

Contains the battery-tier structure variants used by the Large Storage Unit.

The `.variant(...)` values are important because they determine which custom
structure belongs to each storage tier.

### Nuclear Rod Irradiator

[`yet_another_industrialization/nuclear_rod_irradiator.js`](yet_another_industrialization/nuclear_rod_irradiator.js)

Example replacement for the Nuclear Rod Irradiator structure.

## Structure conventions

Inside `.layer([...])`:

- `.layer()` calls are ordered from bottom to top on Y.
- Rows are ordered from front to back on Z.
- Characters are ordered from left to right on X.
- `#` is the controller.
- A space is an ignored position.
- Every other symbol used by a structure must have exactly one mapping.

The structure must contain exactly one controller and all layers must have the
same dimensions.

## Namespace

The examples use:

`example:`

for custom structure IDs.

This namespace is not required. Replace it with the namespace used by your
modpack or project.

For example:

```js
.create('my_modpack:vacuum_freezer')
```

## API documentation

For the complete KubeJS API reference, see:

[`../docs/KUBEJS_API.md`](../docs/KUBEJS_API.md)
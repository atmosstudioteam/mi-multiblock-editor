# MI Multiblock Editor

MI Multiblock Editor is a NeoForge addon for **Modern Industrialization** that lets modpack developers replace existing multiblock structures from **KubeJS startup scripts**.

It is intended for packs that want to redesign machine structures without reimplementing the machines themselves.

## Requirements

- Minecraft **1.21.1**
- NeoForge **21.1.228+**
- Modern Industrialization **2.5.5–2.5.x**
- KubeJS **2101.7.2+**
- Java **21**

## Features

- Replace the structure of an existing Modern Industrialization multiblock controller.
- Define structures layer by layer from KubeJS.
- Map structure symbols to exact blocks, block tags, or exact block states.
- Allow MI hatch types on selected structure positions.
- Use stable variant keys for machines with multiple forms or tiers.
- Skip addon-specific definitions safely with `.requiresMod(...)`.
- Keep supported multiblock recipe-viewer structure displays in sync with the runtime override.
- Dedicated support for special multiblocks whose structure logic cannot be replaced by the generic MI adapter.
- Tested on both the client and a dedicated server.

## Quick example

Create a file in `kubejs/startup_scripts/`, for example `vacuum_freezer.js`:

```js
MIMultiblocks
    .create('example:vacuum_freezer')
    .controller('modern_industrialization:vacuum_freezer')
    .hatchCasing('modern_industrialization:frostproof_machine_casing')
    .layer([
        'AAA',
        'A#A',
        'AAA'
    ])
    .mappingBlock(
        'A',
        'modern_industrialization:frostproof_machine_casing',
        [
            'modern_industrialization:item_input',
            'modern_industrialization:item_output',
            'modern_industrialization:fluid_input',
            'modern_industrialization:fluid_output',
            'modern_industrialization:energy_input'
        ]
    )
    .register();
```

`MIMultiblocks` is available only in **KubeJS startup scripts**, so structure changes require a full game restart.

## Examples

Complete, working KubeJS examples are available in:

[`examples/README.md`](examples/README.md)

The examples include standard Modern Industrialization machines as well as special multiblocks that require multiple structure forms or stable `.variant(...)` keys.

Dedicated examples are also provided for supported machines from:

- Extended Industrialization
- Industrialization Overdrive
- Yet Another Industrialization

The example scripts use the neutral `example:` namespace. Replace it with the namespace used by your own modpack or project.

## Structure coordinates

For every `.layer([...])` call:

- calls to `.layer()` go **bottom to top** on Y;
- rows inside one layer go **front to back** on Z;
- characters inside a row go **left to right** on X;
- `#` is the controller position;
- a space is ignored and does not require a mapping.

Every structure must contain exactly one `#`, all layers must have the same dimensions, and every non-space/non-controller symbol must have exactly one mapping.

## KubeJS API

The complete API reference is in [`docs/KUBEJS_API.md`](docs/KUBEJS_API.md).

## Special compatibility

The generic adapters cover normal MI crafting/generator multiblocks. Additional handling is included for special cases used by MI and several optional addons, including:

### Modern Industrialization

- Electric Blast Furnace
- Distillation Tower
- Large Steam Boilers
- Large Tank
- Nuclear Reactor

### Extended Industrialization

- Steam Farmer
- Electric Farmer
- Large Electric Furnace
- Tesla Tower

### Industrialization Overdrive

- Multi Processing Array
- Pyrolyse Oven

### Yet Another Industrialization

- Arboreous Greenhouse
- Flight Pylon
- Large Storage Unit
- Nuclear Rod Irradiator

Optional addon compatibility does not make those addons mandatory dependencies.

## Tested configurations

The 0.1.0 codebase has been tested with:

- Modern Industrialization without the optional addons;
- Modern Industrialization + Extended Industrialization;
- Modern Industrialization + Industrialization Overdrive;
- Modern Industrialization + Yet Another Industrialization;
- all supported addons together;
- a dedicated NeoForge server.

## License

MI Multiblock Editor is licensed under the [MIT License](LICENSE).

The repository also retains the NeoForge MDK template license where applicable.

Modern Industrialization and the optional supported addons are separate projects and are not part of this repository.
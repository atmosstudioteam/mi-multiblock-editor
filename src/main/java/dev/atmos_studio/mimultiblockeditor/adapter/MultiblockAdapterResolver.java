package dev.atmos_studio.mimultiblockeditor.adapter;

import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.AbstractCraftingMultiblockBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.DistillationTowerBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.ElectricBlastFurnaceBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.FusionReactorBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.GeneratorMultiblockBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.LargeTankMultiblockBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.NuclearReactorMultiblockBlockEntity;
import aztech.modern_industrialization.machines.blockentities.multiblocks.SteamBoilerMultiblockBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class MultiblockAdapterResolver {

    private static final String
            TESSERACT_MULTIPLIED_CRAFTING_CLASS =
            "net.swedz.tesseract.neoforge.compat.mi.machine."
                    + "blockentity.multiblock.multiplied."
                    + "AbstractMultipliedCraftingMultiblockBlockEntity";

    private MultiblockAdapterResolver() {
    }

    public static MultiblockAdapterResolution resolve(
            ResourceLocation controllerId
    ) {

        if (!BuiltInRegistries.BLOCK.containsKey(
                controllerId
        )) {
            return new MultiblockAdapterResolution(
                    controllerId,
                    MultiblockAdapterType.UNKNOWN_CONTROLLER,
                    "<missing>",
                    "<missing>",
                    "The controller block is not registered"
            );
        }

        Block block =
                BuiltInRegistries.BLOCK.get(
                        controllerId
                );

        String blockClassName =
                block.getClass().getName();

        if (!(block instanceof MachineBlock machineBlock)) {
            return new MultiblockAdapterResolution(
                    controllerId,
                    MultiblockAdapterType.NOT_MACHINE_BLOCK,
                    blockClassName,
                    "<unavailable>",
                    "The controller is not an MI MachineBlock"
            );
        }

        final MachineBlockEntity blockEntity;

        try {
            blockEntity =
                    machineBlock.getBlockEntityInstance();
        } catch (RuntimeException | LinkageError exception) {
            return new MultiblockAdapterResolution(
                    controllerId,
                    MultiblockAdapterType.RESOLUTION_ERROR,
                    blockClassName,
                    "<failed to instantiate>",
                    exception.getClass().getName()
                            + ": "
                            + safeMessage(exception)
            );
        }

        String blockEntityClassName =
                blockEntity.getClass().getName();

        if (blockEntity
                instanceof ElectricBlastFurnaceBlockEntity) {

            return resolution(
                    controllerId,
                    MultiblockAdapterType
                            .ELECTRIC_BLAST_FURNACE,
                    blockClassName,
                    blockEntityClassName,
                    "Requires a coil-tier adapter"
            );
        }

        if (blockEntity
                instanceof DistillationTowerBlockEntity) {

            return resolution(
                    controllerId,
                    MultiblockAdapterType
                            .DISTILLATION_TOWER,
                    blockClassName,
                    blockEntityClassName,
                    "Requires a height and output adapter"
            );
        }

        if (blockEntity
                instanceof SteamBoilerMultiblockBlockEntity) {

            return resolution(
                    controllerId,
                    MultiblockAdapterType.STEAM_BOILER,
                    blockClassName,
                    blockEntityClassName,
                    "Requires a steam boiler adapter"
            );
        }

        if (blockEntity
                instanceof LargeTankMultiblockBlockEntity) {

            return resolution(
                    controllerId,
                    MultiblockAdapterType.LARGE_TANK,
                    blockClassName,
                    blockEntityClassName,
                    "Requires a capacity adapter"
            );
        }

        if (blockEntity
                instanceof NuclearReactorMultiblockBlockEntity) {

            return resolution(
                    controllerId,
                    MultiblockAdapterType.NUCLEAR_REACTOR,
                    blockClassName,
                    blockEntityClassName,
                    "Requires a nuclear grid adapter"
            );
        }

        if (inheritsFromNamedClass(
                blockEntity.getClass(),
                TESSERACT_MULTIPLIED_CRAFTING_CLASS
        )) {
            return resolution(
                    controllerId,
                    MultiblockAdapterType
                            .TESSERACT_MULTIPLIED_CRAFTING,
                    blockClassName,
                    blockEntityClassName,
                    "Supported by the Tesseract multiplied "
                            + "crafting adapter"
            );
        }

        if (blockEntity
                instanceof GeneratorMultiblockBlockEntity) {

            return resolution(
                    controllerId,
                    MultiblockAdapterType.GENERATOR,
                    blockClassName,
                    blockEntityClassName,
                    "Supported by the generator adapter"
            );
        }

        if (blockEntity
                instanceof AbstractCraftingMultiblockBlockEntity) {

            return resolution(
                    controllerId,
                    MultiblockAdapterType.STANDARD_CRAFTING,
                    blockClassName,
                    blockEntityClassName,
                    "Supported by the standard crafting adapter"
            );
        }

        if (blockEntity
                instanceof MultiblockMachineBlockEntity) {

            return resolution(
                    controllerId,
                    MultiblockAdapterType.UNKNOWN_MULTIBLOCK,
                    blockClassName,
                    blockEntityClassName,
                    "The multiblock class has no registered adapter"
            );
        }

        return resolution(
                controllerId,
                MultiblockAdapterType.NOT_MULTIBLOCK,
                blockClassName,
                blockEntityClassName,
                "The machine block entity is not a multiblock"
        );
    }

    private static MultiblockAdapterResolution resolution(
            ResourceLocation controllerId,
            MultiblockAdapterType adapterType,
            String blockClassName,
            String blockEntityClassName,
            String details
    ) {
        return new MultiblockAdapterResolution(
                controllerId,
                adapterType,
                blockClassName,
                blockEntityClassName,
                details
        );
    }

    private static boolean inheritsFromNamedClass(
            Class<?> actualClass,
            String expectedClassName
    ) {
        Class<?> currentClass = actualClass;

        while (currentClass != null) {
            if (currentClass.getName().equals(
                    expectedClassName
            )) {
                return true;
            }

            currentClass =
                    currentClass.getSuperclass();
        }

        return false;
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
}
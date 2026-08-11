package dev.atmos_studio.mimultiblockeditor.mixin.compat.yet_another_industrialization.nuclear_rod_irradiator;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization.YAINuclearRodIrradiatorCompat;
import dev.atmos_studio.mimultiblockeditor.runtime.CompiledMultiblockOverride;
import dev.atmos_studio.mimultiblockeditor.runtime.MultiblockOverrideCache;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets =
                "me.luligabi.yet_another_industrialization."
                        + "common.block.machine."
                        + "nuclear_rod_irradiator."
                        + "NuclearRodIrradiatorBlockEntity",
        remap = false
)
public abstract class NuclearRodIrradiatorBlockEntityMixin {

    @Unique
    private long mme$appliedOverrideGeneration =
            Long.MIN_VALUE;

    @Inject(
            method = "getActiveShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementActiveShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        MultiblockMachineBlockEntity self =
                (MultiblockMachineBlockEntity)
                        (Object) this;

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        self.getBlockState().getBlock()
                );

        if (!YAINuclearRodIrradiatorCompat
                .isController(
                        controllerId
                )) {
            return;
        }

        CompiledMultiblockOverride override =
                MultiblockOverrideCache.get(
                        controllerId
                );

        if (override == null) {
            return;
        }

        if (
                override.adapterType()
                        != MultiblockAdapterType
                        .YAI_NUCLEAR_ROD_IRRADIATOR
        ) {
            return;
        }

        callback.setReturnValue(
                override.shapeAt(0)
        );
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void mme$applyNuclearRodIrradiatorOverride(
            CallbackInfo callback
    ) {
        MultiblockMachineBlockEntity self =
                (MultiblockMachineBlockEntity)
                        (Object) this;

        Level level =
                self.getLevel();

        if (level == null) {
            return;
        }

        long currentGeneration =
                MultiblockOverrideCache.generation();

        if (
                mme$appliedOverrideGeneration
                        == currentGeneration
        ) {
            return;
        }

        mme$appliedOverrideGeneration =
                currentGeneration;

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        self.getBlockState().getBlock()
                );

        if (!YAINuclearRodIrradiatorCompat
                .isController(
                        controllerId
                )) {
            return;
        }

        CompiledMultiblockOverride override =
                MultiblockOverrideCache.get(
                        controllerId
                );

        if (
                override == null
                        || override.adapterType()
                        != MultiblockAdapterType
                        .YAI_NUCLEAR_ROD_IRRADIATOR
        ) {
            return;
        }

        if (!level.isClientSide) {
            self.unlink();
            self.setChanged();
            self.sync(false);

            MIMultiblockEditor.LOGGER.info(
                    "Applied Nuclear Rod Irradiator "
                            + "replacement shape to controller "
                            + "{} at {}",
                    controllerId,
                    self.getBlockPos()
            );
        }
    }
}
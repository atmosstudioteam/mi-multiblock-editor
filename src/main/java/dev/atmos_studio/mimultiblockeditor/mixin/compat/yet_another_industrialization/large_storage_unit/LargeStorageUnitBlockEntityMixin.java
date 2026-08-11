package dev.atmos_studio.mimultiblockeditor.mixin.compat.yet_another_industrialization.large_storage_unit;

import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import dev.atmos_studio.mimultiblockeditor.MIMultiblockEditor;
import dev.atmos_studio.mimultiblockeditor.adapter.MultiblockAdapterType;
import dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization.YAILargeStorageUnitCompat;
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
                        + "common.block.machine.large_storage_unit."
                        + "LargeStorageUnitBlockEntity",
        remap = false
)
public abstract class LargeStorageUnitBlockEntityMixin {

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
        CompiledMultiblockOverride override =
                mme$getLargeStorageUnitOverride();

        if (override == null) {
            return;
        }

        int activeTierIndex =
                YAILargeStorageUnitCompat
                        .getActiveTierIndex(
                                this
                        );

        callback.setReturnValue(
                override.shapeAt(
                        activeTierIndex
                )
        );
    }

    @Inject(
            method = "getBigShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mme$getReplacementBigShape(
            CallbackInfoReturnable<ShapeTemplate> callback
    ) {
        CompiledMultiblockOverride override =
                mme$getLargeStorageUnitOverride();

        if (override == null) {
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
    private void mme$applyLargeStorageUnitOverride(
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

        CompiledMultiblockOverride override =
                mme$getLargeStorageUnitOverride();

        if (override == null) {
            return;
        }

        if (!level.isClientSide) {
            self.unlink();
            self.setChanged();
            self.sync(false);

            MIMultiblockEditor.LOGGER.info(
                    "Applied Large Storage Unit replacement "
                            + "shapes to controller {} at {}",
                    YAILargeStorageUnitCompat.CONTROLLER_ID,
                    self.getBlockPos()
            );
        }
    }

    @Unique
    private CompiledMultiblockOverride
    mme$getLargeStorageUnitOverride() {
        MultiblockMachineBlockEntity self =
                (MultiblockMachineBlockEntity)
                        (Object) this;

        ResourceLocation controllerId =
                BuiltInRegistries.BLOCK.getKey(
                        self.getBlockState().getBlock()
                );

        if (!YAILargeStorageUnitCompat
                .isController(
                        controllerId
                )) {
            return null;
        }

        CompiledMultiblockOverride override =
                MultiblockOverrideCache.get(
                        controllerId
                );

        if (override == null) {
            return null;
        }

        if (
                override.adapterType()
                        != MultiblockAdapterType
                        .YAI_LARGE_STORAGE_UNIT
        ) {
            return null;
        }

        return override;
    }
}
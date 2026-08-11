package dev.atmos_studio.mimultiblockeditor.mixin.compat.extended_industrialization.farmer;

import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import dev.atmos_studio.mimultiblockeditor.compat.extended_industrialization.ExtendedFarmerCompat;
import dev.atmos_studio.mimultiblockeditor.compile.ShapeCompiler;
import dev.atmos_studio.mimultiblockeditor.data.StructureMemberDefinition;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        value = ShapeCompiler.class,
        remap = false
)
public abstract class FarmerShapeCompilerMixin {

    @Inject(
            method = "compileTagMember",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void mme$compileFarmerDirt(
            ResourceLocation structureId,
            char symbol,
            StructureMemberDefinition definition,
            CallbackInfoReturnable<SimpleMember> callback
    ) {
        ResourceLocation tagId =
                definition
                        .tagId()
                        .orElse(null);

        if (!ExtendedFarmerCompat
                .isFarmerDirtTag(
                        tagId
                )) {
            return;
        }

        callback.setReturnValue(
                ExtendedFarmerCompat
                        .farmerDirtMember()
        );
    }
}
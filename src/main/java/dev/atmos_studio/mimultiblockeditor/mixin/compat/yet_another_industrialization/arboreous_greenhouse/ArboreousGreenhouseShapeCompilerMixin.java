package dev.atmos_studio.mimultiblockeditor.mixin.compat.yet_another_industrialization.arboreous_greenhouse;

import aztech.modern_industrialization.machines.multiblocks.SimpleMember;
import dev.atmos_studio.mimultiblockeditor.compat.yet_another_industrialization.YAIArboreousGreenhouseCompat;
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
public abstract class ArboreousGreenhouseShapeCompilerMixin {

    @Inject(
            method = "compileTagMember",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void mme$compileSpecialTagMember(
            ResourceLocation structureId,
            char symbol,
            StructureMemberDefinition definition,
            CallbackInfoReturnable<SimpleMember> callback
    ) {
        ResourceLocation tagId =
                definition
                        .tagId()
                        .orElse(null);

        if (tagId == null) {
            return;
        }

        if (YAIArboreousGreenhouseCompat
                .HANGING_LANTERN_TAG_ID
                .equals(
                        tagId
                )) {

            callback.setReturnValue(
                    YAIArboreousGreenhouseCompat
                            .getHangingLanternMember()
            );

            return;
        }

        if (!YAIArboreousGreenhouseCompat
                .isDynamicSoilTag(
                        tagId
                )) {
            return;
        }

        ResourceLocation tierId =
                YAIArboreousGreenhouseCompat
                        .getSoilTierId(
                                tagId
                        );

        callback.setReturnValue(
                YAIArboreousGreenhouseCompat
                        .createSoilMember(
                                tierId
                        )
        );
    }
}
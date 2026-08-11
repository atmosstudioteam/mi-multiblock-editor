package dev.atmos_studio.mimultiblockeditor.mixin;

import aztech.modern_industrialization.machines.components.ActiveShapeComponent;
import aztech.modern_industrialization.machines.multiblocks.ShapeTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ActiveShapeComponent.class, remap = false)
public interface ActiveShapeComponentAccessor {
    @Accessor("shapeTemplates")
    ShapeTemplate[] mme$getShapeTemplates();

    @Mutable
    @Accessor("shapeTemplates")
    void mme$setShapeTemplates(ShapeTemplate[] shapes);

    @Accessor("activeShape")
    int mme$getActiveShape();

    @Accessor("activeShape")
    void mme$setActiveShape(int index);
}
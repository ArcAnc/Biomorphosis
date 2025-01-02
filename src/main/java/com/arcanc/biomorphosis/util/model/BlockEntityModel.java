/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class BlockEntityModel<T extends BlockEntityRenderState> extends Model
{
    public static final float MODEL_Y_OFFSET = -1.501F;

    protected BlockEntityModel(ModelPart root)
    {
        this(root, RenderType::entityCutoutNoCull);
    }

    protected BlockEntityModel(ModelPart root, Function<ResourceLocation, RenderType> typeFunction)
    {
        super(root, typeFunction);
    }

    public void setupAnim(T renderState)
    {
        this.resetPose();
    }
}

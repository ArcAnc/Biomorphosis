/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.wings.client.model;

import com.arcanc.biomorphosis.content.mutations.wings.client.animation.WingAnimator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Base wing model was taken from {@link <a href="https://github.com/Fuzss/fantastic-wings">Fantastic Wings</a>}
 * <p>
 * Permission was obtained directly from @Fuzs
 */
public abstract class WingModel
{
	protected static void setAngles(ModelPart left, ModelPart right, Vec3 angles)
	{
		right.xRot = left.xRot = (float) angles.x * Mth.DEG_TO_RAD;
		right.yRot = -(left.yRot = (float) angles.y * Mth.DEG_TO_RAD);
		right.zRot = -(left.zRot = (float) angles.z * Mth.DEG_TO_RAD);
	}

	public abstract void render(WingAnimator animator, float partialTick, PoseStack poseStack,
	                            VertexConsumer consumer, int light, int overlay, int color);
}

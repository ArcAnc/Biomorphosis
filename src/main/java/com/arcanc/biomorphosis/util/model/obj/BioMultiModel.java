/**
 * @author ArcAnc
 * Created at: 08.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.model.obj;


import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BioMultiModel extends BioObjModel
{
	private final List<BioObjModel> children = new ArrayList<>();
	
	public static @NotNull BioMultiModel newModel(ResourceLocation texture)
	{
		return new BioMultiModel(texture, false);
	}
	
	public static @NotNull BioMultiModel newModel(ResourceLocation texture, boolean flipUV)
	{
		return new BioMultiModel(texture, flipUV);
	}
	
	public BioMultiModel(ResourceLocation texture, boolean flipUV)
	{
		super(texture, flipUV);
	}
	
	public BioMultiModel addChild(BioObjModel child)
	{
		Preconditions.checkNotNull(child);
		this.children.add(child);
		return this;
	}
	
	public List<BioObjModel> getChildren()
	{
		return Collections.unmodifiableList(this.children);
	}
	
	//FIXME: temporary method, must be removed
	protected void fallbackRender(@NotNull PoseStack mStack, @NotNull Function<ResourceLocation, RenderType> type, @NotNull MultiBufferSource bufferSource, int overlay, int light, int color)
	{
		super.render(mStack, type, bufferSource, overlay, light, color);
	}
	
	@Override
	public void render(@NotNull PoseStack mStack, @NotNull Function<ResourceLocation, RenderType> type, @NotNull MultiBufferSource bufferSource, int overlay, int light, int color)
	{
		mStack.pushPose();
		super.render(mStack, type, bufferSource, overlay, light, color);
		renderChild(mStack, type, bufferSource, overlay, light, color);
		mStack.popPose();
	}
	
	public void renderChild(@NotNull PoseStack mStack, @NotNull Function<ResourceLocation, RenderType> type, @NotNull MultiBufferSource bufferSource, int overlay, int light, int color)
	{
		for (BioObjModel child : this.children)
			child.render(mStack, type, bufferSource, overlay, light, color);
	}
}

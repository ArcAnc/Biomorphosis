/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer.srf;


import com.arcanc.biomorphosis.content.entity.renderer.srf.model.SergeantModel;
import com.arcanc.biomorphosis.content.entity.renderer.srf.model.renderState.SergeantRenderState;
import com.arcanc.biomorphosis.content.entity.srf.Sergeant;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SergeantRenderer extends LivingEntityRenderer<Sergeant, SergeantRenderState, SergeantModel>
{
	private static final ResourceLocation TEXTURE = Database.rl("textures/entity/srf/sergeant.png");
	
	public SergeantRenderer(EntityRendererProvider.Context context)
	{
		super(context, new SergeantModel(context.bakeLayer(SergeantModel.LAYER_LOCATION)), 0.5f);
	}
	
	@Override
	protected boolean shouldShowName(@NotNull Sergeant mob, double distance)
	{
		return false;
	}
	
	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull SergeantRenderState renderState)
	{
		return TEXTURE;
	}
	
	@Override
	public @NotNull SergeantRenderState createRenderState()
	{
		return new SergeantRenderState();
	}
}

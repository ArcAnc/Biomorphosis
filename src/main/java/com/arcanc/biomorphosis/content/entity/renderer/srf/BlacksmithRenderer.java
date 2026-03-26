/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer.srf;


import com.arcanc.biomorphosis.content.entity.renderer.srf.model.BlacksmithModel;
import com.arcanc.biomorphosis.content.entity.srf.Blacksmith;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class BlacksmithRenderer extends LivingEntityRenderer<Blacksmith, BlacksmithModel>
{
	private static final ResourceLocation TEXTURE = Database.rl("textures/entity/srf/blacksmith.png");
	
	public BlacksmithRenderer(EntityRendererProvider.Context context)
	{
		super(context, new BlacksmithModel(context.bakeLayer(BlacksmithModel.LAYER_LOCATION)), 0.5f);
	}
	
	@Override
	protected boolean shouldShowName(Blacksmith entity)
	{
		return false;
	}
	
	@Override
	public ResourceLocation getTextureLocation(Blacksmith entity)
	{
		return TEXTURE;
	}
}
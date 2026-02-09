/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer.srf;


import com.arcanc.biomorphosis.content.entity.renderer.srf.model.CaptainModel;
import com.arcanc.biomorphosis.content.entity.srf.Captain;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CaptainRenderer extends LivingEntityRenderer<Captain, CaptainModel>
{
	private static final ResourceLocation TEXTURE = Database.rl("textures/entity/srf/captain.png");
	
	public CaptainRenderer(EntityRendererProvider.Context context)
	{
		super(context, new CaptainModel(context.bakeLayer(CaptainModel.LAYER_LOCATION)), 0.5f);
	}
	
	@Override
	protected boolean shouldShowName(@NotNull Captain entity)
	{
		return false;
	}
	
	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull Captain entity)
	{
		return TEXTURE;
	}
}

/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer.srf;


import com.arcanc.biomorphosis.content.entity.renderer.srf.model.SoldierModel;
import com.arcanc.biomorphosis.content.entity.renderer.srf.model.renderState.SoldierRenderState;
import com.arcanc.biomorphosis.content.entity.srf.Soldier;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SoldierRenderer extends LivingEntityRenderer<Soldier, SoldierRenderState, SoldierModel>
{
	private static final ResourceLocation TEXTURE = Database.rl("textures/entity/srf/soldier.png");
	
	public SoldierRenderer(EntityRendererProvider.Context context)
	{
		super(context, new SoldierModel(context.bakeLayer(SoldierModel.LAYER_LOCATION)), 0.5f);
	}
	
	@Override
	protected boolean shouldShowName(@NotNull Soldier mob, double distance)
	{
		return false;
	}
	
	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull SoldierRenderState renderState)
	{
		return TEXTURE;
	}
	
	@Override
	public @NotNull SoldierRenderState createRenderState()
	{
		return new SoldierRenderState();
	}
}

/**
 * @author ArcAnc
 * Created at: 13.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity.renderer;


import com.arcanc.metamorphosis.content.entity.Worker;
import com.arcanc.metamorphosis.util.Database;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WorkerRenderer extends GeoEntityRenderer<Worker>
{
	public WorkerRenderer(EntityRendererProvider.Context ctx)
	{
		super(ctx, new DefaultedEntityGeoModel<>(Database.rl("worker"), true));
	}

	@Override
	public @Nullable RenderType getRenderType(Worker animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
	{
		return RenderType.entityCutout(texture);
	}
}

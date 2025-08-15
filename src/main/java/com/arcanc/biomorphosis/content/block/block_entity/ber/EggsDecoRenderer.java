/**
 * @author ArcAnc
 * Created at: 19.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;


import com.arcanc.biomorphosis.content.block.block_entity.EggsDeco;
import com.arcanc.biomorphosis.content.block.block_entity.HiveDeco;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EggsDecoRenderer extends GeoBlockRenderer<EggsDeco>
{
	public EggsDecoRenderer(final BlockEntityRendererProvider.Context ctx)
	{
		super(new DefaultedBlockGeoModel<>(Database.rl("eggs")));
	}

	@Override
	public @Nullable RenderType getRenderType(EggsDeco animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
	{
		return RenderType.entityTranslucent(texture);
	}
}

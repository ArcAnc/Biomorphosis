/**
 * @author ArcAnc
 * Created at: 20.02.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;


import com.arcanc.biomorphosis.content.item.renderer.MultiblockMorpherSpecialRenderer;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class MultiblockMorpherBlockItem extends BioBaseBlockItem
{
	public MultiblockMorpherBlockItem(Block block, Properties properties, boolean addToCreative)
	{
		super(block, properties, addToCreative);
	}
	
	public IClientItemExtensions registerMorpherExtension()
	{
		return new IClientItemExtensions()
		{
			private final MultiblockMorpherSpecialRenderer renderer = new MultiblockMorpherSpecialRenderer
					(
							RenderHelper.mc().getBlockEntityRenderDispatcher(),
							RenderHelper.mc().getEntityModels()
					);
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer()
			{
				return this.renderer;
			}
		};
	}
}

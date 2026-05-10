/**
 * @author ArcAnc
 * Created at: 20.02.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;


import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.PItemAnimatable;
import com.arcanc.pulselib.content.animatable.singleton.SingletonAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.content.renderer.PItemRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class MultiblockMorpherBlockItem extends BioBaseBlockItem implements PItemAnimatable<MultiblockMorpherBlockItem>
{
	private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
	
	public MultiblockMorpherBlockItem(Block block, Properties properties, boolean addToCreative)
	{
		super(block, properties, addToCreative);
	}
	
	@Override
	public IClientItemExtensions registerClientExtension()
	{
		return new IClientItemExtensions()
		{
			private final MultiblockMorpherBlockItem.Renderer renderer = new MultiblockMorpherBlockItem.Renderer(
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
	
	@Override
	public PAnimationManager<MultiblockMorpherBlockItem> getAnimationManager(AnimManagerKey key)
	{
		return SingletonAnimationManager.getManager(key, this);
	}
	
	@Override
	public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<MultiblockMorpherBlockItem> registrar)
	{
		registrar.add(() -> state ->
		{
			state.controller().play(IDLE);
			return state.controller().getState();
		});
	}
	
	private static class Renderer extends PItemRenderer<MultiblockMorpherBlockItem>
	{
		private Renderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher,
		                 EntityModelSet entityModelSet)
		{
			super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("morpher")).
					addTexture(Database.rl("0")).build(), PRenderTypes.RenderTypeProvider :: trianglesSolid, blockEntityRenderDispatcher, entityModelSet);
		}
	}
}

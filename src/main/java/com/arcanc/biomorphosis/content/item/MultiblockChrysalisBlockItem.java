/**
 * @author ArcAnc
 * Created at: 15.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;


import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class MultiblockChrysalisBlockItem extends BioBaseBlockItem implements GeoItem
{
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	
	public MultiblockChrysalisBlockItem(Block block, Properties properties, boolean addToCreative)
	{
		super(block, properties, addToCreative);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}
	
	@Override
	public void createGeoRenderer(@NotNull Consumer<GeoRenderProvider> consumer)
	{
		consumer.accept(new GeoRenderProvider()
		{
			private MultiblockChrysalisBlockItem.Renderer renderer = null;
			
			@Override
			public @NotNull GeoItemRenderer<MultiblockChrysalisBlockItem> getGeoItemRenderer()
			{
				if (this.renderer == null)
					this.renderer = new MultiblockChrysalisBlockItem.Renderer();
				return this.renderer;
			}
		});
	}
	
	@Override
	public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
	{
		controllers.add(new AnimationController<>(this, "controller", 0, state -> state.
				setAndContinue(DefaultAnimations.IDLE)));
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.cache;
	}
	
	public static class Renderer extends GeoItemRenderer<MultiblockChrysalisBlockItem>
	{
		public Renderer()
		{
			super(new DefaultedBlockGeoModel<>(Database.rl("chrysalis")));
		}
		
		@Override
		public @Nullable RenderType getRenderType(MultiblockChrysalisBlockItem animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
		{
			return RenderType.entityTranslucent(texture);
		}
	}
}

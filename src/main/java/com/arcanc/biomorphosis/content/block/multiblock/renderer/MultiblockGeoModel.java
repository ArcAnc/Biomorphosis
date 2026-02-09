/**
 * @author ArcAnc
 * Created at: 19.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.renderer;


import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class MultiblockGeoModel<T extends StaticMultiblockPart & GeoAnimatable> extends DefaultedBlockGeoModel<T>
{
	private final Info morphed;
	private final Info morphing;
	private final Info disassembled;
	
	public MultiblockGeoModel(ResourceLocation morphed, ResourceLocation morphing, ResourceLocation disassembled)
	{
		super(morphed);
		this.morphed = new Info(buildFormattedModelPath(morphed), buildFormattedTexturePath(morphed), buildFormattedAnimationPath(morphed));
		this.morphing = new Info(buildFormattedModelPath(morphing), buildFormattedTexturePath(morphing), buildFormattedAnimationPath(morphing));
		this.disassembled = new Info(buildFormattedModelPath(disassembled), buildFormattedTexturePath(disassembled), buildFormattedAnimationPath(disassembled));
	}
	
	@Override
	public ResourceLocation getModelResource(@NotNull T animatable, GeoRenderer<T> renderer)
	{
		MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
		return state == MultiblockState.FORMED ? this.morphed.model() : state == MultiblockState.MORPHING ? this.morphing.model() : this.disassembled.model();
	}
	
	@Override
	public ResourceLocation getTextureResource(@NotNull T animatable, GeoRenderer<T> renderer)
	{
		MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
		return state == MultiblockState.FORMED ? this.morphed.texture() : state == MultiblockState.MORPHING ? this.morphing.texture() : this.disassembled.texture();
	}
	
	@Override
	public ResourceLocation getAnimationResource(@NotNull T animatable)
	{
		MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
		return state == MultiblockState.FORMED ? this.morphed.animation() : state == MultiblockState.MORPHING ? this.morphing.animation() : this.disassembled.animation();
	}
	
	@Override
	public @Nullable RenderType getRenderType(T animatable, ResourceLocation texture)
	{
		return RenderType.entityTranslucent(texture, false);
	}
	
	public record Info(ResourceLocation model, ResourceLocation texture, ResourceLocation animation)
	{}
}

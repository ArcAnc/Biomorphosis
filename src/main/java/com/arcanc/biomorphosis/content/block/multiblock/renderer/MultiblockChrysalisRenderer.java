/**
 * @author ArcAnc
 * Created at: 16.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.renderer;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChrysalis;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MultiblockChrysalisRenderer extends GeoBlockRenderer<MultiblockChrysalis>
{
	public MultiblockChrysalisRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(new MultiblockGeoModel<>(Database.rl("chrysalis"), Database.rl("chrysalis"), Database.rl("chrysalis")));
	}
	
	@Override
	public @NotNull AABB getRenderBoundingBox(@NotNull MultiblockChrysalis blockEntity)
	{
		BlockPos size = blockEntity.getDefinition().map(IMultiblockDefinition :: size).orElse(BlockPos.ZERO);
		return blockEntity.isMaster() ? new AABB(Vec3.atCenterOf(blockEntity.getBlockPos().subtract(size)), Vec3.atCenterOf(blockEntity.getBlockPos().offset(size))) : super.getRenderBoundingBox(blockEntity);
	}
	
	@Override
	public boolean shouldRender(@NotNull MultiblockChrysalis blockEntity, @NotNull Vec3 cameraPos)
	{
		return blockEntity.isMaster() && blockEntity.getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED && super.shouldRender(blockEntity, cameraPos);
	}
}

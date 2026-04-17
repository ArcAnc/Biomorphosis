/**
 * @author ArcAnc
 * Created at: 17.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.renderer;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockTurret;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.animatable.PAnimationController;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedModel;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class MultiblockTurretRenderer extends PBlockRenderer<MultiblockTurret>
{
	private static final ResourceLocation TEXTURE = Database.rl("block/turret/0");
	
	private static final PModelData MORPHED = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("turret")).build();
	private static final PModelData MORPHING = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("turret")).build();
	private static final PModelData DISASSEMBLED = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("turret")).build();
	
	public MultiblockTurretRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(new PModelData.Builder(Database.rl("turret"), "block").build(),
				PRenderTypes.RenderTypeProvider :: trianglesSolid);
	}
	
	@Override
	public @Nullable PBakedModel getModel(MultiblockTurret animatable)
	{
		return this.getModelData(animatable).getModel();
	}
	
	@Override
	public PModelData getModelData(MultiblockTurret animatable)
	{
		MultiblockState state = animatable.getBlockState().getValue(MultiblockPartBlock.STATE);
		return state == MultiblockState.FORMED ? MORPHED : state == MultiblockState.MORPHING ? MORPHING : DISASSEMBLED;
	}
	
	@Override
	protected void perBoneSubmit(MultiblockTurret animatable, PoseStack poseStack, PBakedBone bone, Collection<PAnimationController<MultiblockTurret>> pAnimationControllers, Function<ResourceLocation, RenderType> renderType, int packedColor, int packedLight, int packedOverlay, float partialTick)
	{
		if (bone.name().equals("projectile"))
		{
			renderType = PRenderTypes.RenderTypeProvider :: trianglesTranslucent;
			packedColor = animatable.getShootEffect().getColor();
		}
		super.perBoneSubmit(animatable, poseStack, bone, pAnimationControllers, renderType, packedColor, packedLight, packedOverlay, partialTick);
	}
	
	@Override
	public AABB getRenderBoundingBox(MultiblockTurret blockEntity)
	{
		BlockPos size = blockEntity.getDefinition().map(IMultiblockDefinition :: size).orElse(BlockPos.ZERO);
		return blockEntity.isMaster() ? new AABB(Vec3.atCenterOf(blockEntity.getBlockPos().subtract(size)), Vec3.atCenterOf(blockEntity.getBlockPos().offset(size))) : super.getRenderBoundingBox(blockEntity);
	}
	
	@Override
	public boolean shouldRender(MultiblockTurret blockEntity, Vec3 cameraPos)
	{
		return blockEntity.isMaster() && blockEntity.getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED && super.shouldRender(blockEntity, cameraPos);
	}
	
	public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}

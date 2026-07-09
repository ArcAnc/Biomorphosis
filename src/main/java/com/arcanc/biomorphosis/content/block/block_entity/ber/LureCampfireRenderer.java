/**
 * @author ArcAnc
 * Created at: 08.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.LureCampfireBlock;
import com.arcanc.biomorphosis.content.block.block_entity.LureCampfireBE;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.pulselib.content.animatable.PAnimationController;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.function.Function;

public class LureCampfireRenderer extends PBlockRenderer<LureCampfireBE>
{
    private static final ResourceLocation MAIN = Database.rl("block/lure_campfire/main");
    private static final ResourceLocation FIRE = Database.rl("block/lure_campfire/fire");
    
    public LureCampfireRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("lure_campfire")).
                        build(),
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
    
    @Override
    protected void perBoneSubmit(LureCampfireBE animatable, PoseStack poseStack, PBakedBone bone, Collection<PAnimationController<LureCampfireBE>> pAnimationControllers, Function<ResourceLocation, RenderType> renderType, int packedColor, int packedLight, int packedOverlay, float partialTick)
    {
        if (bone.name().equals("fire"))
            renderType = PRenderTypes.RenderTypeProvider :: trianglesTranslucent;
        super.perBoneSubmit(animatable, poseStack, bone, pAnimationControllers, renderType, packedColor, packedLight, packedOverlay, partialTick);
    }
    
    @Override
    public void postSubmit(PoseStack poseStack, LureCampfireBE animatable, Function<ResourceLocation, RenderType> renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick, @Nullable Object... additionalData)
    {
        LureCampfireBE.LureCampfireStackHandler inventory = animatable.getInventory();
        if (ItemHelper.isEmpty(inventory))
            return;
        Minecraft mc = RenderHelper.mc();
        // FIXME: исправить угол поворота. Мне не нравится, как оно сейчас работает.
        // 10 - animation length in ticks
        int currentTick = Math.toIntExact(mc.level.getGameTime() % 10) * 10;
        float angle = 360 % (currentTick + partialTick);
        for (int q = 0; q < inventory.getSlots(); q++)
        {
            ItemStack stack = inventory.getStackInSlot(q);
            if (!stack.isEmpty())
            {
                poseStack.pushPose();
                Direction dir = animatable.getBlockState().getValue(LureCampfireBlock.HORIZONTAL_FACING);
                poseStack.mulPose(Axis.YP.rotationDegrees(dir.toYRot()));
                poseStack.translate(dir.getStepZ() * (-0.3f + (q * 0.15f)), 0.9f, dir.getStepX() * (-0.3f + (q * 0.15f)));
                poseStack.scale(0.4f, 0.4f, 0.4f);
                
                Vector3f vec = Vec3.atLowerCornerOf(dir.getCounterClockWise().getNormal()).toVector3f();
                poseStack.mulPose(Axis.of(vec).rotationDegrees(angle));
                poseStack.translate(0.0f, -0.179f, 0.0f);
                
                RenderHelper.renderItem().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, animatable.getLevel(), 0);
                poseStack.popPose();
            }
        }
    }
    
    public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
    {
        event.addTextureLocation(MAIN);
        event.addTextureLocation(FIRE);
    }
}

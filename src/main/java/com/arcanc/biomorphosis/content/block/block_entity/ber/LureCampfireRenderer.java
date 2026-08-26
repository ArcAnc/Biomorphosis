/**
 * @author ArcAnc
 * Created at: 08.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.block_entity.LureCampfireBE;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.ItemHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.animation.PPose;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedMesh;
import com.arcanc.pulselib.content.model.baked.PBakedModel;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.model.textures.PAlphaMode;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
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
    public void postSubmit(PoseStack poseStack, LureCampfireBE animatable, Function<ResourceLocation, RenderType> renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick, @Nullable Object... additionalData)
    {
        LureCampfireBE.LureCampfireStackHandler inventory = animatable.getInventory();
        if (ItemHelper.isEmpty(inventory))
            return;
        PBakedModel model = this.getModel(animatable);
        if (model == null)
            return;

        int shaftIndex = model.boneIndex("shaft");
        if (shaftIndex < 0)
            return;

        PPose pose = model.evaluate(
                animatable.getAnimationManager(AnimManagerKey.of(animatable)).getControllers().values(),
                Map.of(),
                partialTick);

        poseStack.pushPose();
        poseStack.translate(pose.translation(shaftIndex).x(), pose.translation(shaftIndex).y(), pose.translation(shaftIndex).z());
        poseStack.mulPose(pose.rotation(shaftIndex));
        poseStack.scale(pose.scale(shaftIndex).x(), pose.scale(shaftIndex).y(), pose.scale(shaftIndex).z());
        for (int q = 0; q < inventory.getSlots(); q++)
        {
            ItemStack stack = inventory.getStackInSlot(q);
            if (!stack.isEmpty())
            {
                poseStack.pushPose();
                poseStack.translate(-0.3f + (q * 0.15f), 0.0f, 0.0f);
                poseStack.scale(0.4f, 0.4f, 0.4f);
                poseStack.translate(0.0f, -0.179f, 0.0f);
                RenderHelper.renderItem().renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, animatable.getLevel(), 0);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }
	
	@Override
	protected PMeshRenderContext resolveMeshRender(LureCampfireBE animatable, PBakedBone bone, PBakedMesh mesh, PMeshRenderContext inherited, float partialTick)
	{
		if (!bone.name().equals("fire"))
			return inherited;
		return new PMeshRenderContext(PRenderTypes.RenderTypeProvider :: trianglesTranslucent,
				inherited.color(),
				inherited.packedLight(),
				inherited.packedOverlay(),
				inherited.deformation(),
				inherited.texture(),
				true,
				PAlphaMode.TRANSLUCENT);
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
    {
        event.addTextureLocation(MAIN);
        event.addTextureLocation(FIRE);
    }
}

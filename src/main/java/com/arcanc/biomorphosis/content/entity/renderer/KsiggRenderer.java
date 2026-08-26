/**
 * @author ArcAnc
 * Created at: 10.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;

import com.arcanc.biomorphosis.content.entity.Ksigg;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedMesh;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.model.textures.PAlphaMode;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultEntityModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class KsiggRenderer extends PEntityRenderer<Ksigg>
{
    private static final ResourceLocation TEXTURE = Database.rl("entity/ksigg/0");
    public KsiggRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DefaultEntityModelData.DefaultEntityModelDataBuilder(Database.rl("ksigg")).
                addTexture(Database.rl("0")).build(),
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
    
    @Override
    public void trueSubmit(PoseStack poseStack, Ksigg animatable, Function<ResourceLocation, RenderType> renderType, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float partialTick, @Nullable Object... additionalData)
    {
        if (!animatable.isBaby())
        {
            poseStack.pushPose();
            poseStack.scale(1.5f, 1.5f, 1.5f);
            super.trueSubmit(poseStack, animatable, renderType, bufferSource, packedLight, packedOverlay, partialTick, additionalData);
            poseStack.popPose();
        }
        else
            super.trueSubmit(poseStack, animatable, renderType, bufferSource, packedLight, packedOverlay, partialTick, additionalData);
    }
	
	@Override
	protected PMeshRenderContext resolveMeshRender(Ksigg animatable, PBakedBone bone, PBakedMesh mesh, PMeshRenderContext inherited, float partialTick)
	{
		if (!bone.name().equals("boob"))
			return inherited;
		return new PMeshRenderContext(
				PRenderTypes.RenderTypeProvider :: trianglesTranslucent,
				inherited.color(),
				inherited.packedLight(),
				inherited.packedOverlay(),
				inherited.deformation(),
				inherited.texture(),
				inherited.emissive(),
				PAlphaMode.TRANSLUCENT);
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
    {
        event.addTextureLocation(TEXTURE);
    }
}

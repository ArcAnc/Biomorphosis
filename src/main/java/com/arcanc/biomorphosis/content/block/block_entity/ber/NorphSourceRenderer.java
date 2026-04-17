/**
 * @author ArcAnc
 * Created at: 16.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.norph.source.NorphSource;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.animatable.PAnimationController;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Function;

public class NorphSourceRenderer extends PBlockRenderer<NorphSource>
{
    private static final ResourceLocation MAIN = Database.rl("block/norph_source/main");
    private static final ResourceLocation FLUID = Database.rl("block/norph_source/fluid");
    
    public NorphSourceRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("norph_source")).
                        build(),
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
    
    @Override
    protected void perBoneSubmit(NorphSource animatable, PoseStack poseStack, PBakedBone bone, Collection<PAnimationController<NorphSource>> pAnimationControllers, Function<ResourceLocation, RenderType> renderType, int packedColor, int packedLight, int packedOverlay, float partialTick)
    {
        if (bone.name().equals("center") || bone.name().equals("fluid_left") || bone.name().equals("fluid_right"))
            renderType = PRenderTypes.RenderTypeProvider :: trianglesTranslucent;
        super.perBoneSubmit(animatable, poseStack, bone, pAnimationControllers, renderType, packedColor, packedLight, packedOverlay, partialTick);
    }
    
    public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
    {
        event.addTextureLocation(MAIN);
        event.addTextureLocation(FLUID);
    }
}

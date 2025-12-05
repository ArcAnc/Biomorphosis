/**
 * @author ArcAnc
 * Created at: 16.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.block_entity.ber;

import com.arcanc.metamorphosis.content.block.norph.source.NorphSource;
import com.arcanc.metamorphosis.util.Database;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class NorphSourceRenderer extends GeoBlockRenderer<NorphSource>
{
    public NorphSourceRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new DefaultedBlockGeoModel<>(Database.rl("norph_source")));
    }

    @Override
    public @Nullable RenderType getRenderType(NorphSource animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick)
    {
        return RenderType.entityTranslucent(texture);
    }
}

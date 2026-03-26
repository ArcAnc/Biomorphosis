/**
 * @author ArcAnc
 * Created at: 03.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.BioForgeBlock;
import com.arcanc.biomorphosis.content.block.block_entity.BioForge;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BioForgeRenderer extends PBlockRenderer<BioForge>
{
    private static final ResourceLocation TEXTURE = Database.rl("block/forge/0");
    
    private static final PModelData FORGE = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("forge")).build();
    
    private static final PModelData DOUBLE_FORGE = new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("forge_double")).
            addTexture("0", TEXTURE).build();
    
    public BioForgeRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(FORGE, PRenderTypes.RenderTypeProvider :: trianglesTranslucent);
    }
    
    @Override
    public PModelData getModelData(BioForge animatable)
    {
        return animatable.getBlockState().getValue(BioForgeBlock.DOUBLE) ? DOUBLE_FORGE : FORGE;
    }
    
    public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
    {
        event.addTextureLocation(TEXTURE);
    }
}

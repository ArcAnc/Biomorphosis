/**
 * @author ArcAnc
 * Created at: 28.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.block_entity.BioCrusher;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BioCrusherRenderer extends PBlockRenderer<BioCrusher>
{
    public static final ResourceLocation TEXTURE = Database.rl("block/crusher/0");
    
    public BioCrusherRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("crusher")).
                    build(),
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
    
    public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
    {
        event.addTextureLocation(TEXTURE);
    }
}

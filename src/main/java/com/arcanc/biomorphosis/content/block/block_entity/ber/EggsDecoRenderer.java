/**
 * @author ArcAnc
 * Created at: 19.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;


import com.arcanc.biomorphosis.content.block.block_entity.EggsDeco;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EggsDecoRenderer extends PBlockRenderer<EggsDeco>
{
	private static final ResourceLocation TEXTURE = Database.rl("block/egges/0");
	
	public EggsDecoRenderer(final BlockEntityRendererProvider.Context ctx)
	{
		super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("eggs")).
						build(),
				PRenderTypes.RenderTypeProvider :: trianglesTranslucent);
	}
	
	public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}

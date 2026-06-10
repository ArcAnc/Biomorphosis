/**
 * @author ArcAnc
 * Created at: 19.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;


import com.arcanc.biomorphosis.content.block.block_entity.HiveDeco;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class HiveDecoRenderer extends PBlockRenderer<HiveDeco>
{
	private static final ResourceLocation TEXTURE = Database.rl("block/hive_deco/0");
	
	public HiveDecoRenderer(final BlockEntityRendererProvider.Context ctx)
	{
		super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("hive_deco")).
						build(),
				PRenderTypes.RenderTypeProvider :: trianglesCutout);
	}
	
	public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}

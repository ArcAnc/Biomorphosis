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
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EggsDecoRenderer extends PBlockRenderer<EggsDeco>
{
	private static final PModelData MODEL_DATA = PModelData.block(Database.rl("eggs"));
	private static final ResourceLocation TEXTURE = Database.rl("block/eggs/0");
	
	public EggsDecoRenderer(final BlockEntityRendererProvider.Context ctx)
	{
		super(MODEL_DATA,
				PRenderTypes.RenderTypeProvider :: trianglesTranslucent);
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterResourceEvent event)
	{
		event.model(MODEL_DATA).
				texture("0", TEXTURE);
	}
}

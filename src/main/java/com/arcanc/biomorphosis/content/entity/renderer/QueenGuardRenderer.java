/**
 * @author ArcAnc
 * Created at: 12.08.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;


import com.arcanc.biomorphosis.content.entity.QueenGuard;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.CustomEvents;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultEntityModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class QueenGuardRenderer extends PEntityRenderer<QueenGuard>
{
	private static final ResourceLocation TEXTURE = Database.rl("entity/guard/0");
	public QueenGuardRenderer(EntityRendererProvider.Context ctx)
	{
		super(ctx, new DefaultEntityModelData.DefaultEntityModelDataBuilder(Database.rl("guard")).
						build(),
				PRenderTypes.RenderTypeProvider :: trianglesCutout);
	}
	
	public static void registerTextures(final CustomEvents.PLibRegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}

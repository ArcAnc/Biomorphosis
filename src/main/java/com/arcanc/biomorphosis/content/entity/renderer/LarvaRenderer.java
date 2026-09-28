/**
 * @author ArcAnc
 * Created at: 10.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;

import com.arcanc.biomorphosis.content.entity.Larva;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LarvaRenderer extends PEntityRenderer<Larva>
{
	private static final PModelData MODEL_DATA = PModelData.entity(Database.rl("larva"));
    private static final ResourceLocation TEXTURE = Database.rl("entity/larva/0");
    public LarvaRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, MODEL_DATA,
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
    
    public static void registerTextures(final PulseLibEvents.RegisterResourceEvent event)
    {
        event.model(MODEL_DATA).
		        texture("0", TEXTURE);
    }
}

/**
 * @author ArcAnc
 * Created at: 12.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;

import com.arcanc.biomorphosis.content.entity.Ziris;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.model.textures.PAlphaMode;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultEntityModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ZirisRenderer extends PEntityRenderer<Ziris>
{
    private static final ResourceLocation TEXTURE = Database.rl("entity/ziris/0");
    
    public ZirisRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DefaultEntityModelData.DefaultEntityModelDataBuilder(Database.rl("ziris")).
                        build(),
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
    
    public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
    {
        event.addTextureLocation(TEXTURE);
    }
	
	@Override
	protected PMeshRenderContext resolveBoneRender(Ziris animatable, PBakedBone bone, PMeshRenderContext inherited, float partialTick)
	{
		if (!bone.name().equals("wingleft") &&
				!bone.name().equals("wingright"))
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
}

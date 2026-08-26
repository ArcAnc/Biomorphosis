/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;

import com.arcanc.biomorphosis.content.entity.Queen;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedMesh;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.model.textures.PAlphaMode;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultEntityModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class QueenRenderer extends PEntityRenderer<Queen>
{
    private static final ResourceLocation TEXTURE = Database.rl("entity/queen/0");
    public QueenRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DefaultEntityModelData.DefaultEntityModelDataBuilder(Database.rl("queen")).
                        build(),
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
	
	@Override
	protected PMeshRenderContext resolveMeshRender(Queen animatable, PBakedBone bone, PBakedMesh mesh, PMeshRenderContext inherited, float partialTick)
	{
		if (!bone.name().equals("head") &&
				!bone.name().equals("wing_left") &&
				!bone.name().equals("wing_right"))
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
	
	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
    {
        event.addTextureLocation(TEXTURE);
    }
}

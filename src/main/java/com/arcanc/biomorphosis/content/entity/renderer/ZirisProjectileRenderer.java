/**
 * @author ArcAnc
 * Created at: 26.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;

import com.arcanc.biomorphosis.content.entity.ZirisProjectile;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedMesh;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultEntityModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ZirisProjectileRenderer extends PEntityRenderer<ZirisProjectile>
{
	private static final int YELLOW = MathHelper.ColorHelper.color(255, 255, 225, 0);

	public ZirisProjectileRenderer(EntityRendererProvider.Context context)
	{
		super(context, new DefaultEntityModelData.DefaultEntityModelDataBuilder(Database.rl("projectile_turret")).build(),
				PRenderTypes.RenderTypeProvider :: trianglesTranslucent);
	}
	
	@Override
	protected PMeshRenderContext resolveMeshRender(ZirisProjectile animatable, PBakedBone bone, PBakedMesh mesh, PMeshRenderContext inherited, float partialTick)
	{
		return new PMeshRenderContext(
				inherited.renderType(),
				YELLOW,
				inherited.packedLight(),
				inherited.packedOverlay(),
				inherited.deformation(),
				inherited.texture(),
				inherited.emissive(),
				inherited.alphaModeOverride());
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
	{
		//event.addTextureLocation(TEXTURE);
	}
}

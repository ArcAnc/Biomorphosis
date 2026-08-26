/**
 * @author ArcAnc
 * Created at: 24.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.renderer;


import com.arcanc.biomorphosis.content.entity.TurretProjectile;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedMesh;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultEntityModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TurretProjectileRenderer extends PEntityRenderer<TurretProjectile>
{
	private static final ResourceLocation TEXTURE = Database.rl("entity/projectile_turret/0");
	
	public TurretProjectileRenderer(EntityRendererProvider.Context context)
	{
		super(context, new DefaultEntityModelData.DefaultEntityModelDataBuilder(Database.rl("projectile_turret")).
						build(),
				PRenderTypes.RenderTypeProvider :: trianglesTranslucent);
	}
	
	@Override
	protected PMeshRenderContext resolveMeshRender(TurretProjectile animatable, PBakedBone bone, PBakedMesh mesh, PMeshRenderContext inherited, float partialTick)
	{
		if (animatable.getEffect() == null)
			return inherited;
		return new PMeshRenderContext(
				inherited.renderType(),
				animatable.getEffect().getColor(),
				inherited.packedLight(),
				inherited.packedOverlay(),
				inherited.deformation(),
				inherited.texture(),
				inherited.emissive(),
				inherited.alphaModeOverride());
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterTextureEvent event)
	{
		event.addTextureLocation(TEXTURE);
	}
}

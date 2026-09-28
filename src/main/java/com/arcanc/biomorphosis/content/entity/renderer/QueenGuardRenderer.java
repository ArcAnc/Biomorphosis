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
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedMesh;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.renderer.PEntityRenderer;
import com.arcanc.pulselib.content.renderer.modelData.PModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class QueenGuardRenderer extends PEntityRenderer<QueenGuard>
{
	private static final PModelData MODEL_DATA = PModelData.entity(Database.rl("guard"));
	private static final ResourceLocation TEXTURE = Database.rl("entity/guard/0");
	private static final int BERSERK_TINT = MathHelper.ColorHelper.color(255, 255, 85, 85);

	public QueenGuardRenderer(EntityRendererProvider.Context ctx)
	{
		super(ctx, MODEL_DATA,
				PRenderTypes.RenderTypeProvider :: trianglesCutout);
	}
	
	@Override
	protected PMeshRenderContext resolveMeshRender(QueenGuard animatable, PBakedBone bone, PBakedMesh mesh, PMeshRenderContext inherited, float partialTick)
	{
		return !animatable.isBerserk() ?
				inherited :
				new PMeshRenderContext(
						inherited.renderType(),
						MathHelper.ColorHelper.multiply(inherited.color(), BERSERK_TINT),
						inherited.packedLight(),
						inherited.packedOverlay());
	}
	
	public static void registerTextures(final PulseLibEvents.RegisterResourceEvent event)
	{
		event.model(MODEL_DATA).
				texture("0", TEXTURE);
	}
}

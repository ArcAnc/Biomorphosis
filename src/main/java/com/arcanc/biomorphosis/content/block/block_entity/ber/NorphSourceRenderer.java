/**
 * @author ArcAnc
 * Created at: 16.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;

import com.arcanc.biomorphosis.content.block.norph.source.NorphSource;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.pulselib.content.event.PulseLibEvents;
import com.arcanc.pulselib.content.model.baked.PBakedBone;
import com.arcanc.pulselib.content.model.baked.PBakedMesh;
import com.arcanc.pulselib.content.model.baked.PMeshRenderContext;
import com.arcanc.pulselib.content.model.textures.PAlphaMode;
import com.arcanc.pulselib.content.renderer.PBlockRenderer;
import com.arcanc.pulselib.content.renderer.modelData.DefaultBlockModelData;
import com.arcanc.pulselib.util.PRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NorphSourceRenderer extends PBlockRenderer<NorphSource>
{
    private static final ResourceLocation MAIN = Database.rl("block/norph_source/main");
    private static final ResourceLocation FLUID = Database.rl("block/norph_source/fluid");
    
    public NorphSourceRenderer(final BlockEntityRendererProvider.Context ctx)
    {
        super(new DefaultBlockModelData.DefaultBlockModelDataBuilder(Database.rl("norph_source")).
                        build(),
                PRenderTypes.RenderTypeProvider :: trianglesSolid);
    }
	
	@Override
	protected PMeshRenderContext resolveMeshRender(NorphSource animatable, PBakedBone bone, PBakedMesh mesh, PMeshRenderContext inherited, float partialTick)
	{
		if (!bone.name().equals("center") &&
			!bone.name().equals("fluid_left") &&
			!bone.name().equals("fluid_right"))
			return inherited;
		return new PMeshRenderContext(PRenderTypes.RenderTypeProvider :: trianglesTranslucent,
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
        event.addTextureLocation(MAIN);
        event.addTextureLocation(FLUID);
    }
}

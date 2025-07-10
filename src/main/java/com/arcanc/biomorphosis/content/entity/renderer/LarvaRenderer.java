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
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LarvaRenderer extends GeoEntityRenderer<Larva>
{
    public LarvaRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DefaultedEntityGeoModel<>(Database.rl("larva"), true));
    }
}

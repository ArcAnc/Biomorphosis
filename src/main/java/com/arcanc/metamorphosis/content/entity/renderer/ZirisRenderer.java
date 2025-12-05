/**
 * @author ArcAnc
 * Created at: 12.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.entity.renderer;

import com.arcanc.metamorphosis.content.entity.Ziris;
import com.arcanc.metamorphosis.util.Database;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ZirisRenderer extends GeoEntityRenderer<Ziris>
{
    public ZirisRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new DefaultedEntityGeoModel<>(Database.rl("ziris"), true));
    }
}

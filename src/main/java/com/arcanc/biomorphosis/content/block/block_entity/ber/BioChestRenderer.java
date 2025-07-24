/**
 * @author ArcAnc
 * Created at: 20.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.ber;


import com.arcanc.biomorphosis.content.block.block_entity.BioChest;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BioChestRenderer extends GeoBlockRenderer<BioChest>
{

	public BioChestRenderer(final BlockEntityRendererProvider.Context ctx)
	{
		super(new DefaultedBlockGeoModel<>(Database.rl("swarm_chest")));
	}

}

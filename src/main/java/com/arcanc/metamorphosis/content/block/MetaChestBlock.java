/**
 * @author ArcAnc
 * Created at: 20.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block;


import com.arcanc.metamorphosis.content.block.block_entity.MetaChest;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MetaChestBlock extends MetaBaseEntityBlock<MetaChest>
{
	public static final MapCodec<MetaChestBlock> CODEC = simpleCodec(MetaChestBlock :: new);

	public MetaChestBlock(Properties props)
	{
		super(Registration.BETypeReg.BE_CHEST, props);
	}

	@Override
	protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
	{
		return RenderShape.INVISIBLE;
	}

	@Override
	protected @NotNull MapCodec<MetaChestBlock> codec()
	{
		return CODEC;
	}
}

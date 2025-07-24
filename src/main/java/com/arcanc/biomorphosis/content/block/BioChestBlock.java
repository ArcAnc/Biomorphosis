/**
 * @author ArcAnc
 * Created at: 20.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;


import com.arcanc.biomorphosis.content.block.block_entity.BioChest;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BioChestBlock extends BioBaseEntityBlock<BioChest>
{
	public static final MapCodec<BioChestBlock> CODEC = simpleCodec(BioChestBlock :: new);

	public BioChestBlock(Properties props)
	{
		super(Registration.BETypeReg.BE_CHEST, props);
	}

	@Override
	protected @NotNull RenderShape getRenderShape(@NotNull BlockState state)
	{
		return RenderShape.INVISIBLE;
	}

	@Override
	protected @NotNull MapCodec<BioChestBlock> codec()
	{
		return CODEC;
	}
}

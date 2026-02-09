/**
 * @author ArcAnc
 * Created at: 13.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import org.jetbrains.annotations.NotNull;

public class GlowMossBlock extends MultifaceBlock implements BlockInterfaces.IWrencheable
{
    public static final MapCodec<GlowMossBlock> CODEC = simpleCodec(GlowMossBlock :: new);
    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    public GlowMossBlock(Properties props)
    {
        super(props);
    }

    @Override
    public InteractionResult onUsed(@NotNull ItemStack stack, UseOnContext ctx)
    {
        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull MapCodec<GlowMossBlock> codec()
    {
        return CODEC;
    }
    
    @Override
    public @NotNull MultifaceSpreader getSpreader()
    {
        return this.spreader;
    }
}

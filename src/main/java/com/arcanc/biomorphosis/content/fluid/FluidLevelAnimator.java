/**
 * @author ArcAnc
 * Created at: 24.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.fluid;

import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FluidLevelAnimator
{
    private static final float ANIMATION_SPEED = 0.1f;
    private static final Set<GlobalPos> BLOCK_ENTITIES = new HashSet<>();

    public static void registerBlockEntity(@NotNull Level level, BlockPos pos)
    {
        if (level.isClientSide() && FluidHelper.isFluidHandler(level, pos))
            BLOCK_ENTITIES.add(new GlobalPos(level.dimension(), pos));
    }

    public static void removeBlockEntity(@NotNull Level level, BlockPos pos)
    {
        if (level.isClientSide())
            BLOCK_ENTITIES.remove(new GlobalPos(level.dimension(), pos));
    }

    public static void renderFrame(@NotNull final RenderFrameEvent.Pre event)
    {
        Minecraft mc = RenderHelper.mc();
        Level level = mc.level;
        if (level == null)
            return;
        for (GlobalPos globalPos : BLOCK_ENTITIES)
        {
            if (!globalPos.dimension().equals(level.dimension()))
                continue;
            if (!level.isAreaLoaded(globalPos.pos(), 1))
                continue;
            Optional<IFluidHandler> fluidHandler = FluidHelper.getFluidHandler(level, globalPos.pos());
            if (fluidHandler.isEmpty())
                continue;

            IFluidHandler handler = fluidHandler.get();
            if (!(handler instanceof FluidSidedStorage fluidSidedStorage))
                continue;

            for (int q = 0; q < handler.getTanks(); q++)
            {
                int realFluidAmount = fluidSidedStorage.getFluidInTank(q).getAmount();
                int clientFluidAmount = fluidSidedStorage.getClientFluidAmountInTank(q);
                if (clientFluidAmount != realFluidAmount)
                    fluidSidedStorage.setClientFluidAmountInTank(q, Mth.lerpDiscrete(ANIMATION_SPEED, clientFluidAmount, realFluidAmount));
            }
        }
    }
}

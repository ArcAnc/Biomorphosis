/**
 * @author ArcAnc
 * Created at: 18.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.fluid;

import net.minecraft.Util;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This code was taken from <a href="https://github.com/BluSunrize/ImmersiveEngineering/tree/1.21.1">Immersive Engineering</a>
 * <p>BluSunrize is original author. Yes, bro, you are not forgotten</p>
 * <p>Modified by ArcAnc</p>
 */

public abstract class BioBaseFluid extends BaseFlowingFluid
{
    public static BioBaseFluid makeFluid(@NotNull Function<Properties, ? extends BioBaseFluid> make, Properties properties, Consumer<Properties> props)
    {
        return make.apply(Util.make(properties, props));
    }

    protected BioBaseFluid(Properties properties)
    {
        super(properties);
    }

    public static class BioFluidSource extends BioBaseFluid
    {

        public BioFluidSource(Properties properties)
        {
            super(properties);
        }

        @Override
        public int getAmount(@NotNull FluidState state)
        {
            return 8;
        }

        @Override
        public boolean isSource(@NotNull FluidState state)
        {
            return true;
        }
    }

    public static class BioFluidFlowing extends BioBaseFluid
    {

        public BioFluidFlowing(Properties properties)
        {
            super(properties);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        @Override
        public int getAmount(@NotNull FluidState state)
        {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(@NotNull FluidState state)
        {
            return false;
        }

        @Override
        protected void createFluidStateDefinition(@NotNull StateDefinition.Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

    }

    public interface FluidPropsGetter
    {
        BaseFlowingFluid.Properties get(Supplier<? extends FluidType> fluidType, Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing);
    }
}

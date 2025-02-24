/**
 * @author ArcAnc
 * Created at: 24.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.capabilities;

import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class Capabilities
{
    public static final class Fluid
    {
        public static final BlockCapability<IFluidHandler, @NotNull CapabilityAccess> BLOCK = BlockCapability.create(Database.rl("fluid_handler"), IFluidHandler.class, CapabilityAccess.class);

        public record CapabilityAccess (DirectionHelper.RelativeFace direction, FluidStackHolder.HolderType type){}
    }


}

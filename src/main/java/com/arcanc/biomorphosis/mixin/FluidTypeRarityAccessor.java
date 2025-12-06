/**
 * @author ArcAnc
 * Created at: 21.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.mixin;

import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidType.Properties.class)
public interface FluidTypeRarityAccessor
{
    @Accessor(remap = false)
    Rarity getRarity();
}

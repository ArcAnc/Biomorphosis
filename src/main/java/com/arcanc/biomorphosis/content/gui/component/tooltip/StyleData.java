/**
 * @author ArcAnc
 * Created at: 23.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.tooltip;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public record StyleData(boolean isCustom, BiFunction<Player, ItemStack, TooltipData> tooltip)
{
    public StyleData(boolean isCustom, TooltipData data)
    {
        this(isCustom, (player, stack) -> data);
    }
}

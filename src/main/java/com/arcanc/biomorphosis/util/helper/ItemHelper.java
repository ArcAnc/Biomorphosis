/**
 * @author ArcAnc
 * Created at: 11.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class ItemHelper
{

    public static boolean isEmpty(@NotNull IItemHandler inv)
    {
        for (int q = 0; q < inv.getSlots(); q++)
        {
            if (!inv.getStackInSlot(q).isEmpty())
                return false;
        }
        return true;
    }
}

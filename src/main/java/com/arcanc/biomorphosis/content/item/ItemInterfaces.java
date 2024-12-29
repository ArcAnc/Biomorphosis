/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;

public final class ItemInterfaces
{
    public interface IMustAddToCreativeTab
    {
        default boolean addSelfToCreativeTab()
        {
            return true;
        }
    }
}

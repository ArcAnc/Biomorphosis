/**
 * @author ArcAnc
 * Created at: 23.06.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity.tick;


/*This code was taken from Immersive Engineering. Thanks BluSunrize, it's perfect*/
public interface TickableBase
{
    default boolean canTickAny()
    {
        return true;
    }
}

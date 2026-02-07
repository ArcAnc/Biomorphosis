/**
 * @author ArcAnc
 * Created at: 10.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface LazyDataLoader<T extends BlockEntity>
{
    void loadData(T blockEntity);
}

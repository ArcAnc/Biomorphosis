/**
 * @author ArcAnc
 * Created at: 07.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.multiblock.definition;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;

public interface IMultiblockDefinition
{
    Codec<IMultiblockDefinition> CODEC = MultiblockType.CODEC.dispatch(IMultiblockDefinition :: type, MultiblockType :: codecForType);

    ResourceLocation getId();

    MultiblockType type();

    BlockStateMap getStructure(BlockGetter level, BlockPos origin);

    BlockPos size();
}

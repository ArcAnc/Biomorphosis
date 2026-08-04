/**
 * @author ArcAnc
 * Created at: 04.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.srf;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.loot.BioChestLoot;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class SRFChestLootProcessor extends StructureProcessor
{
    public static final SRFChestLootProcessor INSTANCE = new SRFChestLootProcessor();
    public static final MapCodec<SRFChestLootProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    private SRFChestLootProcessor()
    {
    }

    @Override
    protected StructureProcessorType<?> getType()
    {
        return Registration.StructureProcessorReg.SRF_CHEST_LOOT.get();
    }

    @Override
    public @Nullable StructureTemplate.StructureBlockInfo processBlock(LevelReader level,
                                                                        BlockPos offset,
                                                                        BlockPos pos,
                                                                        StructureTemplate.StructureBlockInfo originalBlockInfo,
                                                                        StructureTemplate.StructureBlockInfo currentBlockInfo,
                                                                        StructurePlaceSettings settings)
    {
        if (!currentBlockInfo.state().is(Blocks.CHEST))
            return currentBlockInfo;

        CompoundTag tag = currentBlockInfo.nbt() == null ? new CompoundTag() : currentBlockInfo.nbt().copy();
        tag.putString("LootTable", BioChestLoot.SRF.location().toString());
        return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), currentBlockInfo.state(), tag);
    }
}

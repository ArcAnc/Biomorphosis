/**
 * @author ArcAnc
 * Created at: 14.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.base.type;

import com.arcanc.biomorphosis.content.block.multiblock.base.BioMultiblockPart;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.base.role.MasterRoleBehavior;
import com.arcanc.biomorphosis.content.block.multiblock.base.role.SlaveRoleBehavior;
import com.arcanc.biomorphosis.content.block.multiblock.definition.BlockStateMap;
import com.arcanc.biomorphosis.content.block.multiblock.definition.DynamicMultiblockDefinition;
import com.arcanc.biomorphosis.content.block.multiblock.definition.MultiblockType;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.ZoneHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DynamicMultiblockPart extends BioMultiblockPart
{
    public DynamicMultiblockPart(BlockEntityType<?> type, BlockPos pos, BlockState blockState)
    {
        super(type, pos, blockState);
    }

    public void onPlace(@NotNull ServerLevel level, BlockPos pos, @NotNull BlockState state)
    {
        /*FIXME: надо проверить механизм создания мультиблока. Сейчас он слегка кривой, потому что можно слить 2 мультиблока в 1, как так и надо. + размер определяется слегка не верно*/

        if (state.hasProperty(MultiblockPartBlock.STATE) && state.getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED)
            return;

        this.definition = level.registryAccess().lookup(Registration.MultiblockReg.DEFINITION_KEY).
                flatMap(registry -> registry.filterElements(definition ->
                {
                    if (definition.type() == MultiblockType.STATIC)
                        return false;
                    DynamicMultiblockDefinition dynDef = (DynamicMultiblockDefinition) definition;
                    return dynDef.getAllowedBlockType().getBlock().equals(this.getBlockState().getBlock());
                }).
                listElements().
                findFirst().
                map(Holder.Reference::value)).
                orElse(null);
  
		if (!(this.definition instanceof DynamicMultiblockDefinition dynDefinition))
            return;

        BlockStateMap map = dynDefinition.getStructure(level, pos);

        if (map.getStates().isEmpty())
            return;

        /*It's MASTER!!*/
        if (map.getStates().size() == 1)
            markAsPartOfMultiblock(getBlockPos());
        else
            for (Map.Entry<BlockPos, BlockState> entry : map.getStates().entrySet())
            {
                BlockPos targetRealPos = getBlockPos().offset(entry.getKey());
                if (entry.getKey().equals(BlockPos.ZERO))
                    continue;
                BlockPos masterPos = BlockHelper.castTileEntity(level, targetRealPos, this.getClass()).
                        map(part -> part.isMaster() ? targetRealPos : null).orElse(null);
                if (masterPos == null)
                    continue;
                BlockPos multiblockSize = dynDefinition.size();
                boolean markAsMaster = ZoneHelper.getPoses(masterPos, ZoneHelper.RadiusOptions.of(ZoneHelper.ZoneType.SQUARE, multiblockSize.getX(), multiblockSize.getY(), multiblockSize.getZ())).
                        noneMatch(checkPos -> checkPos.equals(getBlockPos()));
                if (markAsMaster)
                    markAsPartOfMultiblock(getBlockPos());
                else
                {
                    markAsPartOfMultiblock(masterPos);
                    BlockHelper.castTileEntity(level, masterPos, this.getClass()).
                            ifPresent(DynamicMultiblockPart :: updateCapabilities);
                    break;
                }
            }
    }

    public void onRemove(ServerLevel level, BlockPos pos, BlockState state)
    {
        if (!isMaster())
            getMasterPos().
                flatMap(master -> BlockHelper.castTileEntity(level, master, this.getClass())).
                ifPresent(DynamicMultiblockPart :: updateCapabilities);
        else
        {
            Set<BlockPos> poses = new HashSet<>();
            for (Direction dir : Direction.values())
            {
                BlockPos toCheckPos = pos.relative(dir);
                BlockState toCheckState = level.getBlockState(toCheckPos);
                if (state.is(toCheckState.getBlock()))
                    poses.add(toCheckPos);
            }
            if (poses.isEmpty())
                return;
            BlockPos startPos = poses.stream().findAny().get();
            BlockStateMap map = this.definition.getStructure(level, startPos);
            BlockPos newMasterPos = map.getStates().keySet().
                    stream().
                    findAny().
                    orElse(null);
            if (newMasterPos == null)
                return;
            BlockPos realMasterPos = startPos.offset(newMasterPos);
            BlockHelper.castTileEntity(level, realMasterPos, this.getClass()).
                    ifPresent(newMaster ->
                    {
                        newMaster.changeRoleBehavior(new MasterRoleBehavior(newMaster));
                        this.transferRequiredData(newMaster);
                        newMaster.updateCapabilities();
                    });
            map.getStates().keySet().stream().
                    filter(entry -> !entry.equals(newMasterPos)).
                    forEach(slavePos ->
                            BlockHelper.castTileEntity(level, startPos.offset(slavePos), this.getClass()).
                                ifPresent(part -> part.changeRoleBehavior(new SlaveRoleBehavior(part).setMasterPos(realMasterPos))));
        }
    }

    protected abstract void transferRequiredData(DynamicMultiblockPart target);

    @Override
    protected void tryFormMultiblock(Level level)
    {
    }

    @Override
    protected boolean isMultiblockStillValid()
    {
        return true;
    }

    @Override
    public void onDisassemble()
    {

    }

    protected abstract void updateCapabilities();

}

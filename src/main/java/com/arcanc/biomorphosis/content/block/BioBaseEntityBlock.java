/**
 * @author ArcAnc
 * Created at: 31.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block;

import com.arcanc.biomorphosis.content.block.block_entity.tick.ClientTickableBE;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * This code was partially taken from <a href="https://github.com/BluSunrize/ImmersiveEngineering/tree/1.21.1">Immersive Engineering</a>
 * <p>BluSunrize is original author. Yes, bro, you are not forgotten</p>
 * <p>Modified by ArcAnc</p>
 */

public class BioBaseEntityBlock<T extends BlockEntity> extends BioBaseBlock implements EntityBlock
{
    private final BiFunction<BlockPos, BlockState, T> makeEntity;
    private BEClassInspectedData classData;

    public BioBaseEntityBlock(BiFunction<BlockPos, BlockState, T> makeEntity, Properties props)
    {
        super(props);
        this.makeEntity = makeEntity;
    }

    public BioBaseEntityBlock(Supplier<BlockEntityType<T>> tileType, Properties blockProps)
    {
        this((bp, state) -> tileType.get().create(bp, state), blockProps);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
    {
        return makeEntity.apply(pos, state);
    }

    @Nullable
    @Override
    public <T2 extends BlockEntity>
    BlockEntityTicker<T2> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T2> type)
    {
        return getClassData().makeBaseTicker(world.isClientSide());
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int eventID, int eventParam)
    {
        super.triggerEvent(state, level, pos, eventID, eventParam);
        return BlockHelper.getTileEntity(level, pos).map(blockEntity -> blockEntity.triggerEvent(eventID, eventParam)).orElse(false);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                        @NotNull Level level,
                                                        @NotNull BlockPos pos,
                                                        @NotNull Player player,
                                                        @NotNull BlockHitResult hitResult)
    {
        BlockEntity tile = level.getBlockEntity(pos);
        if(tile instanceof MenuProvider menuProvider && !player.isShiftKeyDown())
        {
            if(player instanceof ServerPlayer serverPlayer)
            {
                if(menuProvider instanceof BlockInterfaces.IInteractionObject<?> interaction)
                {
                    interaction = interaction.getGuiMaster();
                    if(interaction != null && interaction.canUseGui(player))
                    {
                        BlockPos masterPos = interaction.getGuiMaster().getBlockPos();
                        serverPlayer.openMenu(interaction, byteBuf -> byteBuf.writeBlockPos(masterPos));
                        return InteractionResult.SUCCESS_SERVER;
                    }
                }
                else
                {
                    serverPlayer.openMenu(menuProvider);
                    return InteractionResult.SUCCESS_SERVER;
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    private BEClassInspectedData getClassData()
    {
        if(this.classData==null)
        {
            T tempBE = makeEntity.apply(BlockPos.ZERO, getInitDefaultState());
            this.classData = new BEClassInspectedData(
                    tempBE instanceof ServerTickableBE,
                    tempBE instanceof ClientTickableBE
            );
        }
        return this.classData;
    }

    private record BEClassInspectedData(
            boolean serverTicking,
            boolean clientTicking
    )
    {
        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> makeBaseTicker(boolean isClient)
        {
            if(serverTicking && !isClient)
                return ServerTickableBE.makeTicker();
            else if(clientTicking && isClient)
                return ClientTickableBE.makeTicker();
            else
                return null;
        }
    }
}

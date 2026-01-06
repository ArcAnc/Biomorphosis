/**
 * @author ArcAnc
 * Created at: 16.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.DynamicMultiblockPart;
import com.arcanc.biomorphosis.content.fluid.FluidLevelAnimator;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiblockFluidStorage extends DynamicMultiblockPart implements BlockInterfaces.IWrencheable
{

    private final FluidSidedStorage handler;

    public MultiblockFluidStorage(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_FLUID_STORAGE.get(), pos, blockState);

        this.handler = new FluidSidedStorage().
                addHolder(FluidStackHolder.newBuilder().
                                setCallback(hold -> this.markDirty()).
                                setValidator(stack -> true).
                                setCapacity(10000).
                                build(),
                        BasicSidedStorage.FaceMode.ALL);
    }

    public static @Nullable FluidSidedStorage getHandler(@NotNull MultiblockFluidStorage be, Direction ctx)
    {
		return be.isMaster() ? be.handler : be.getMasterPos().
                flatMap(pos -> BlockHelper.
                        castTileEntity(be.getLevel(), pos, MultiblockFluidStorage.class).
                        map(master -> master.handler)).
                orElse(null);
    }

    @Override
    protected void transferRequiredData(@NotNull DynamicMultiblockPart target)
    {
        CompoundTag tag = new CompoundTag();
        this.writeCustomTag(tag, getLevel().registryAccess(), false);
        target.readCustomTag(tag, getLevel().registryAccess(), false);
    }

    @Override
    protected void updateCapabilities()
    {
        this.handler.setTankCapacity(0, 10000 * this.definition.getStructure(getLevel(), getBlockPos()).getParts().size());
    }

    @Override
    protected void firstTick()
    {
        if (this.level != null && this.level.isClientSide())
            FluidLevelAnimator.registerBlockEntity(this.level, getBlockPos());
    }

    @Override
    public void setRemoved()
    {
        if (this.level != null && this.level.isClientSide())
            FluidLevelAnimator.removeBlockEntity(this.level, getBlockPos());
        super.setRemoved();
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
        if (this.isMaster())
            this.handler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Fluids.HANDLER));
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        if (this.isMaster())
            tag.put(Database.Capabilities.Fluids.HANDLER, this.handler.serializeNBT(registries));
    }

    @Override
    public InteractionResult onUsed(@NotNull ItemStack stack, @NotNull UseOnContext ctx)
    {
        Player player = ctx.getPlayer();

        if (player != null)
        {
            if (player.isCrouching())
            {
                Level level = ctx.getLevel();
                if (level.isClientSide())
                    return InteractionResult.SUCCESS;

                List<Vec3> positions = stack.has(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA)
                        ? stack.get(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA)
                        : new ArrayList<>();
                if (positions == null)
                    positions = new ArrayList<>();
                while (positions.size() <= 1)
                    positions.add(Vec3.ZERO);

                if (positions.getFirst() == Vec3.ZERO)
                    positions.set(0, getMasterPos().orElse(BlockPos.ZERO).getBottomCenter());
                else
                    positions.set(1, getMasterPos().orElse(BlockPos.ZERO).getBottomCenter());
                stack.set(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA, positions);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}

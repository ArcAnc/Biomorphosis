/**
 * @author ArcAnc
 * Created at: 28.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;

import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockPartBlock;
import com.arcanc.biomorphosis.content.block.multiblock.base.MultiblockState;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPart;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackHolder;
import com.arcanc.biomorphosis.util.inventory.item.ItemStackSidedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MultiblockChamber extends StaticMultiblockPart implements GeoBlockEntity, BlockInterfaces.IInteractionObject<MultiblockChamber>
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final ItemStackSidedStorage itemHandler;
    private int process;

    public MultiblockChamber(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_MULTIBLOCK_CHAMBER.get(), pos, blockState);
        this.itemHandler = new ItemStackSidedStorage().
                addHolder(ItemStackHolder.newBuilder().
                                setCallback(holder -> this.markDirty()).
                                setValidator(stack -> false).
                                setCapacity(64).
                                build(),
                        BasicSidedStorage.FaceMode.OUTPUT);
        for (int q = 0; q < 12; q++)
            this.itemHandler.addHolder(ItemStackHolder.newBuilder().
                            setCallback(holder -> this.markDirty()).
                            setValidator(stack -> true).
                            setCapacity(64).
                            build(),
                    BasicSidedStorage.FaceMode.ALL);
    }

    public void tryStart()
    {

    }

    @Override
    protected void multiblockServerTick()
    {
        Level level = getLevel();
        if (level == null)
            return;
    }

    @Override
    protected void firstTick()
    {

    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.writeCustomTag(tag, registries, descrPacket);
        if (this.isMaster())
            tag.put(Database.Capabilities.Items.HANDLER, this.itemHandler.serializeNBT(registries));
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        super.readCustomTag(tag, registries, descrPacket);
        if (this.isMaster())
            this.itemHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Items.HANDLER));
    }

    @Override
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, state ->
        {
            if (!isMaster())
                return PlayState.STOP;
            if (getBlockState().getValue(MultiblockPartBlock.STATE) == MultiblockState.FORMED)
                return state.setAndContinue(DefaultAnimations.IDLE);
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    public static @Nullable ItemStackSidedStorage getItemHandler(@NotNull MultiblockChamber be, Direction ctx)
    {
        return be.isMaster() ? be.itemHandler : be.getMasterPos().
                flatMap(pos -> BlockHelper.
                        castTileEntity(be.getLevel(), pos, MultiblockChamber.class).
                        map(master -> master.itemHandler)).
                orElse(null);
    }

    @Override
    public @Nullable MultiblockChamber getGuiMaster()
    {
        return isMaster() ? this : getMasterPos().flatMap(pos ->
                BlockHelper.castTileEntity(getLevel(), pos, MultiblockChamber.class)).
                orElse(null);
    }

    @Override
    public Registration.MenuTypeReg.ArgContainer<? super MultiblockChamber, ?> getContainerType()
    {
        return Registration.MenuTypeReg.CHAMBER;
    }

    @Override
    public boolean canUseGui(@NotNull Player player)
    {
        return getMasterPos().map(pos -> player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 16).orElse(false);
    }
}

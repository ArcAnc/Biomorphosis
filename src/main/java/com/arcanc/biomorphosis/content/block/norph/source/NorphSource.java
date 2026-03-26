/**
 * @author ArcAnc
 * Created at: 16.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.norph.source;

import com.arcanc.biomorphosis.content.block.block_entity.BioBaseBlockEntity;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.instance.ControllerState;
import com.arcanc.pulselib.content.animatable.instance.PAnimationController;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class NorphSource extends BioBaseBlockEntity implements PAnimatable<NorphSource>
{
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();

    private final PAnimationManager<NorphSource> manager = PLibHelper.createManager(this);

    public NorphSource(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_NORPH_SOURCE.get(), pos, blockState);
    }

    @Override
    protected void firstTick()
    {

    }

    @Override
    public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {

    }

    @Override
    public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {

    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<NorphSource> registrar)
    {
        registrar.add(new PAnimationController<>(state ->
        {
            state.controller().play(IDLE);
            return ControllerState.PLAY;
        }));
    }
    
    @Override
    public PAnimationManager<NorphSource> getAnimationManager()
    {
        return this.manager;
    }
}

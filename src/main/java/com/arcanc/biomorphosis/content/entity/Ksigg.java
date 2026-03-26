/**
 * @author ArcAnc
 * Created at: 09.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioItemTags;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.instance.PAnimationController;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/*Like a cow, but FOR THE SWARM!*/
public class Ksigg extends Animal implements PAnimatable<Ksigg>
{
    /*FIXME: заменить молоко на другую жижу*/
    private static final EntityDimensions BABY_DIMENSIONS = Registration.EntityReg.MOB_KSIGG.getEntityHolder().get().getDimensions().scale(0.65f).withEyeHeight(0.6f);

    private final PAnimationManager<Ksigg> manager = PLibHelper.createManager(this);
    
    private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
    private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").build();
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
    
    private static final PRawAnimation DEATH = PRawAnimation.begin().thenHold("death").build();
    
    public Ksigg(EntityType<? extends Animal> type, Level level)
    {
        super(type, level);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, stack ->  stack.is(BioItemTags.KSIGG_FOOD), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(Items.BUCKET) && !this.isBaby())
        {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, itemstack1);
            return InteractionResult.SUCCESS;
        }
        else
        {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.is(BioItemTags.KSIGG_FOOD);
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.35f;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return Registration.EntityReg.MOB_KSIGG.getSounds().getIdleSound().get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_KSIGG.getSounds().getHurtSound().get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_KSIGG.getSounds().getDeathSound().get();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent)
    {
        return Registration.EntityReg.MOB_KSIGG.getEntityHolder().get().create(level);
    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<Ksigg> registrar)
    {
        registrar.add(new PAnimationController<>("animController",state ->
        {
            Ksigg animatable = state.animatable();
            if (animatable.swinging)
                state.controller().play(ATTACK);
            else
            {
                if (animatable.walkAnimation.isMoving())
                    state.controller().play(WALK);
                else
                    state.controller().play(IDLE);
            }
            return state.controller().getState();
        })).
        add(new PAnimationController<>("death", state ->
        {
            if (state.animatable().isDeadOrDying())
                state.controller().play(DEATH);
            else
                state.controller().stop();
            return  state.controller().getState();
        }));
    }
    
    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose)
    {
        return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
    }
    
    @Override
    public PAnimationManager<Ksigg> getAnimationManager()
    {
        return this.manager;
    }
}

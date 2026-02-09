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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

/*Like a cow, but FOR THE SWARM!*/
public class Ksigg extends Animal implements GeoEntity
{
    /*FIXME: заменить молоко на другую жижу*/
    private static final EntityDimensions BABY_DIMENSIONS = Registration.EntityReg.MOB_KSIGG.getEntityHolder().get().getDimensions().scale(0.65f).withEyeHeight(0.6f);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

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
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand)
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
    public boolean isFood(@NotNull ItemStack stack)
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
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource)
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
    public AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent)
    {
        return Registration.EntityReg.MOB_KSIGG.getEntityHolder().get().create(level);
    }

    @Override
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "animController", 0, state ->
        {
            if (this.swinging)
                return state.setAndContinue(DefaultAnimations.ATTACK_SWING);
            return state.setAndContinue(this.walkAnimation.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
        }),
                DefaultAnimations.genericDeathController(this));
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(@NotNull Pose pose)
    {
        return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return this.cache;
    }
}

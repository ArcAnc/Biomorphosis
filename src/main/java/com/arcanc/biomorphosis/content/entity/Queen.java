/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.arcanc.biomorphosis.content.entity.ai.goals.MoveToLureGoal;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.tags.base.BioEntityTags;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.animatable.instance.ControllerState;
import com.arcanc.pulselib.content.animatable.instance.PAnimationController;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Queen extends Monster implements PAnimatable<Queen>
{
    private final PAnimationManager<Queen> manager = PLibHelper.createManager(this);

    private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
    private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").build();
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
    private static final PRawAnimation UNBURROW = PRawAnimation.begin().thenHold("unburrow").build();
    private static final PRawAnimation BURROW = PRawAnimation.begin().thenHold("burrow").build();
    private static final PRawAnimation DEATH = PRawAnimation.begin().thenHold("death").build();

    private @Nullable BlockPos lurePos;
    private BlockPos spawnPos;
    private boolean findLure = false;
    private BurrowState burrowState;
    private int unburrowTimer = 0;
    private int burrowTimer = 0;

    public Queen(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
        if (spawnPos == null)
            this.spawnPos = BlockPos.ZERO;
        if (this.lurePos == null)
            this.lurePos = BlockPos.ZERO;
        this.burrowState = BurrowState.UNBURROWING;
    }

    public Queen (Level level, Vec3 position)
    {
        this(Registration.EntityReg.MOB_QUEEN.getEntityHolder().get(), level);
        this.setPos(position);
        this.spawnPos = BlockPos.containing(position);
    }

    public Queen(Level level, Vec3 position, BlockPos lurePos)
    {
        this(level, position);
        this.lurePos = lurePos;
    }

    public boolean isFindLure()
    {
        return this.findLure;
    }

    public BlockPos getLurePos()
    {
        return this.lurePos;
    }

    public BlockPos getSpawnPos()
    {
        return this.spawnPos;
    }

    public void setLurePos(BlockPos pos)
    {
        this.lurePos = pos;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, entity -> !entity.getType().is(BioEntityTags.SWARM)));

        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(2, new MoveToLureGoal(this, 1.0));

        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public boolean isUnderGround()
    {
        return this.burrowState == BurrowState.UNDER_GROUND;
    }

    public boolean isUnburrowing()
    {
        return this.burrowState == BurrowState.UNBURROWING;
    }

    public boolean isOnGround()
    {
        return this.burrowState == BurrowState.ON_GROUND;
    }

    public boolean isBurrowing()
    {
        return this.burrowState == BurrowState.BURROWING;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.isUnburrowing())
            this.unburrowTimer++;
        if (this.isBurrowing())
            this.burrowTimer++;

        if (this.unburrowTimer >= 1.5f * 20 && this.isUnburrowing())
        {
            this.unburrowTimer = 0;
            this.burrowState = BurrowState.ON_GROUND;
        }

        if (this.burrowTimer >= 5f * 20 && this.isBurrowing())
        {
            this.burrowTimer = 0;
            this.burrowState = BurrowState.UNDER_GROUND;
        }

        if (this.lurePos != null && !this.findLure && this.isOnGround())
        {
            if (this.blockPosition().closerToCenterThan(Vec3.atCenterOf(this.lurePos), 2.0))
                sniffBait();
        }

        if (this.findLure && this.lurePos != null)
        {
            double distance = this.blockPosition().distManhattan(this.lurePos);
            if (distance > 32)
            {
                if (this.isOnGround())
                    this.burrowState = BurrowState.BURROWING;
                if (this.isUnderGround())
                {
                    this.discard();
                    this.level().getEntitiesOfClass(QueenGuard.class, this.getBoundingBox().inflate(16))
                            .forEach(Entity :: discard);
				}
            }
        }
    }

    private void sniffBait()
    {
        this.findLure = true;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.lurePos = TagHelper.readBlockPos(compound, "lure_pos");
        this.spawnPos = TagHelper.readBlockPos(compound, "spawn_pos");
        this.findLure = compound.getBoolean("find_lure");
        this.burrowState = BurrowState.values()[compound.getInt("burrow_state")];
        this.burrowTimer = compound.getInt("burrow_timer");
        this.unburrowTimer = compound.getInt("unburrow_timer");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        TagHelper.writeBlockPos(this.lurePos, compound, "lure_pos");
        TagHelper.writeBlockPos(this.spawnPos, compound, "spawn_pos");
        compound.putBoolean("find_lure", this.findLure);
        compound.putInt("burrow_state", this.burrowState.ordinal());
        compound.putInt("burrow_timer", this.burrowTimer);
        compound.putInt("unburrow_timer", this.unburrowTimer);
    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<Queen> registrar)
    {
        registrar.add(new PAnimationController<>("animControl", state ->
        {
            Queen animatable = state.animatable();
            return switch (animatable.burrowState)
            {
                case BURROWING -> {
                    state.controller().play(BURROW);
                    yield ControllerState.PLAY;
                }
                case ON_GROUND ->
                {
                    if (animatable.swinging)
                    {
                        state.controller().play(ATTACK);
                    }
                    else
                    {
                        if (animatable.walkAnimation.isMoving())
                            state.controller().play(WALK);
                        else
                            state.controller().play(IDLE);
                    }
	                yield ControllerState.PLAY;
                }
                case UNBURROWING -> {
                    state.controller().play(UNBURROW);
                    yield ControllerState.PLAY;
                }
                case UNDER_GROUND ->
                {
                    state.controller().pause();
                    yield ControllerState.STOP;
                }
            };
        })).
        add(new PAnimationController<>("death", state ->
        {
            if (!state.animatable().isDeadOrDying())
                return ControllerState.STOP;
            state.controller().play(DEATH);
            return ControllerState.PLAY;
        }));
    }
    @Override
    public int getCurrentSwingDuration()
    {
        return 30;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound()
    {
        return Registration.EntityReg.MOB_QUEEN.getSounds().getIdleSound().get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_QUEEN.getSounds().getDeathSound().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_QUEEN.getSounds().getHurtSound().get();
    }
    
    @Override
    public PAnimationManager<Queen> getAnimationManager()
    {
        return this.manager;
    }
    
    private enum BurrowState
    {
        UNDER_GROUND, UNBURROWING, ON_GROUND, BURROWING;
    }
    
    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return false;
    }
	
	@Override
	public boolean isPersistenceRequired()
	{
		return true;
	}
}

/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity;

import com.arcanc.biomorphosis.content.entity.ai.brain.GuardBrain;
import com.arcanc.biomorphosis.content.entity.ai.targeting.SwarmTargeting;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.arcanc.pulselib.content.animatable.AnimManagerKey;
import com.arcanc.pulselib.content.animatable.ControllerState;
import com.arcanc.pulselib.content.animatable.PAnimatable;
import com.arcanc.pulselib.content.animatable.PAnimationManager;
import com.arcanc.pulselib.content.model.animation.PRawAnimation;
import com.arcanc.pulselib.util.helpers.PLibHelper;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class QueenGuard extends Monster implements PAnimatable<QueenGuard>
{

    /*FIXME: переписать ИИ на брейн работу. Добавить оружие и щит в руки гвардосу. Дописать реакцию на атаку квины*/

    private final PAnimationManager<QueenGuard> manager = PLibHelper.createManager(this);
    private static final PRawAnimation IDLE = PRawAnimation.begin().thenLoop("idle").build();
    private static final PRawAnimation WALK = PRawAnimation.begin().thenLoop("walk").build();
    private static final PRawAnimation ATTACK = PRawAnimation.begin().thenPlay("attack").build();
    private static final PRawAnimation DEATH = PRawAnimation.begin().thenPlay("death").build();
    private static final ResourceLocation BERSERK_DAMAGE_MODIFIER = Database.rl("queen_guard_berserk_damage");
    private static final double BERSERK_DAMAGE_BONUS = 2.0d;
    private static final EntityDataAccessor<Boolean> BERSERK = SynchedEntityData.defineId(QueenGuard.class, EntityDataSerializers.BOOLEAN);
    
    private @Nullable UUID queen;
    private @Nullable BlockPos patrolPos;

    public QueenGuard(EntityType<? extends Monster> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    protected void registerGoals()
    {
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                                  DifficultyInstance difficulty,
                                                  MobSpawnType spawnType,
                                                  @Nullable SpawnGroupData spawnGroupData)
    {
        this.patrolPos = this.blockPosition();
        syncBrainMemories();
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Nullable
    public Queen getQueen()
    {
        if (this.queen == null)
            return null;
        if (this.level() instanceof ServerLevel serverLevel)
        {
            Entity ent = serverLevel.getEntity(this.queen);
            if (ent instanceof Queen mobQueen && mobQueen.isAlive())
                return mobQueen;
        }
		return null;
	}

    public boolean hasQueenId()
    {
        return this.queen != null;
    }

    public boolean isGuarding(Queen queen)
    {
        return this.queen != null && this.queen.equals(queen.getUUID());
    }

    public void setQueen(Queen queen)
    {
        this.queen = queen.getUUID();
        this.setBerserk(false);
        syncBrainMemories();
    }

    public BlockPos getPatrolPos()
    {
        if (this.patrolPos == null)
            this.patrolPos = this.blockPosition();
        return this.patrolPos;
    }

    public boolean isBerserk()
    {
        return this.entityData.get(BERSERK);
    }

    private void setBerserk(boolean value)
    {
        this.entityData.set(BERSERK, value);
        updateBerserkDamageModifier();
    }

    public void queenDied(Queen queen)
    {
        if (!isGuarding(queen))
            return;

        this.setBerserk(true);
        LivingEntity attacker = queen.getLastHurtByMob();
        if (SwarmTargeting.isValidSwarmEnemy(this, attacker))
        {
            this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, attacker);
            this.setTarget(attacker);
        }
    }

    private void updateBerserkDamageModifier()
    {
        AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage == null)
            return;

        damage.removeModifier(BERSERK_DAMAGE_MODIFIER);
        if (this.isBerserk())
            damage.addTransientModifier(new AttributeModifier(
                    BERSERK_DAMAGE_MODIFIER,
                    BERSERK_DAMAGE_BONUS,
                    AttributeModifier.Operation.ADD_VALUE));
    }

    public boolean canAttackInCurrentMode(@Nullable LivingEntity target)
    {
        if (!SwarmTargeting.isValidSwarmEnemy(this, target))
            return false;
        if (target == this.getLastHurtByMob() || didTargetHurtNearbySwarm(target))
            return true;
        if (this.isBerserk())
            return true;

        Queen queen = this.getQueen();
        if (queen != null)
        {
            if (target == queen.getLastHurtByMob())
                return true;
            return target.blockPosition().closerToCenterThan(queen.position(), GuardBrain.QUEEN_DEFENSE_RADIUS);
        }

        if (this.hasQueenId())
            return false;

        return target.blockPosition().closerToCenterThan(Vec3.atCenterOf(this.getPatrolPos()), GuardBrain.PATROL_RADIUS);
    }

    private boolean didTargetHurtNearbySwarm(LivingEntity target)
    {
        return this.level().getEntitiesOfClass(
                        LivingEntity.class,
                        this.getBoundingBox().inflate(GuardBrain.RESPONSE_RADIUS),
                        SwarmTargeting :: isSwarmMember).
                stream().
                anyMatch(ally -> ally.getLastHurtByMob() == target);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return GuardBrain.makeBrain(dynamic);
    }

    @Override
    public @Nullable LivingEntity getTarget()
    {
        return this.getTargetFromBrain();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<QueenGuard> getBrain()
    {
        return (Brain<QueenGuard>) super.getBrain();
    }

    @Override
    protected void customServerAiStep()
    {
        if (this.level() instanceof ServerLevel serverLevel)
        {
            syncBrainMemories();
            serverLevel.getProfiler().push("queenGuardBrain");
            this.getBrain().tick(serverLevel, this);
            serverLevel.getProfiler().pop();
        }
        super.customServerAiStep();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(BERSERK, false);
    }

    private void syncBrainMemories()
    {
        if (this.queen != null)
            this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), this.queen);
        else
            this.getBrain().eraseMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get());

        this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_PATROL_POS.get(), this.getPatrolPos());
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getDeathSound().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getHurtSound().get();
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return Registration.EntityReg.MOB_QUEEN_GUARD.getSounds().getIdleSound().get();
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.35f;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);

        if (compound.hasUUID("queen"))
            this.queen = compound.getUUID("queen");
        else
            this.queen = null;

        this.patrolPos = TagHelper.readBlockPos(compound, "patrol_pos");
        this.setBerserk(compound.getBoolean("berserk"));
        syncBrainMemories();

        /*if (compound.hasUUID("queen"))
            this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get(), compound.getUUID("queen"));
        else
            this.getBrain().eraseMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get());

        this.getBrain().setMemory(Registration.AIReg.QUEEN_GUARD_PATROL_POS.get(), TagHelper.readBlockPos(compound, "patrol"));*/
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);

        if (this.queen != null)
            compound.putUUID("queen", this.queen);
        TagHelper.writeBlockPos(this.getPatrolPos(), compound, "patrol_pos");
        compound.putBoolean("berserk", this.isBerserk());

        /*this.getBrain().getMemory(Registration.AIReg.QUEEN_GUARD_QUEEN_UUID.get()).
            ifPresent(uuid -> compound.putUUID("queen", uuid));

        this.getBrain().getMemory(Registration.AIReg.QUEEN_GUARD_PATROL_POS.get()).
                ifPresent(pos -> TagHelper.writeBlockPos(pos, compound, "patrol"))*/;
    }
    
    @Override
    public void registerAnimationControllers(PAnimationManager.PAnimationRegistrar<QueenGuard> registrar)
    {
        registrar.add("walk/idle/attack", () -> state ->
                {
                    QueenGuard animatable = state.animatable();
                    if (animatable.swinging)
                    {
                        state.controller().play(ATTACK);
                        return ControllerState.PLAY;
                    }
                    if (animatable.walkAnimation.isMoving())
                        state.controller().play(WALK);
                    else
                        state.controller().play(IDLE);
                    return ControllerState.PLAY;
                }).
        add("death", () -> state ->
        {
            if (!state.animatable().isDeadOrDying())
                return ControllerState.STOP;
            state.controller().play(DEATH);
            return ControllerState.PLAY;
        });
    }
    
    @Override
    public PAnimationManager<QueenGuard> getAnimationManager(AnimManagerKey key)
    {
        return this.manager;
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

/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.srf;


import com.arcanc.biomorphosis.content.entity.ai.goals.ReturnGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Captain extends AbstractPalladin
{
	public Captain(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}
	
	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Creaking.class, 8.0F, 1.0, 1.2));
		this.goalSelector.addGoal(2, new ReturnGoal(this, 1.2d));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2d, true));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0f));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, AbstractPalladin.class).setAlertOthers());
		this.targetSelector.addGoal(
				2,
				new NearestAttackableTargetGoal<>(
						this,
						Monster.class,
						5,
						true,
						false,
						(entity, level) ->
								!(entity instanceof Creeper) &&
										!(entity instanceof AbstractVillager)
				)
		);
	}
}

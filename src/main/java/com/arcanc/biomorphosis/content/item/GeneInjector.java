/**
 * @author ArcAnc
 * Created at: 01.02.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;


import com.arcanc.biomorphosis.util.Database;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneInjector extends BioBaseItem
{
	public static final ItemAbility INJECTOR_INJECT = ItemAbility.get(Database.rl("injector_inject").toString());
	public static final Set<ItemAbility> INJECTOR_ACTIONS = Stream.of(INJECTOR_INJECT).collect(Collectors.toCollection(Sets :: newIdentityHashSet));
	
	public GeneInjector(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean canAttackBlock(BlockState state,
	                              Level level,
	                              BlockPos pos,
	                              Player player)
	{
		return !player.isCreative();
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		return true;
	}
	
	@Override
	public void postHurtEnemy(ItemStack stack,
	                          LivingEntity enemy,
	                          LivingEntity attacker)
	{
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
	}
	
	@Override
	public boolean canPerformAction(ItemStack stack,
	                                net.neoforged.neoforge.common.ItemAbility itemAbility)
	{
		return INJECTOR_ACTIONS.contains(itemAbility);
	}
}

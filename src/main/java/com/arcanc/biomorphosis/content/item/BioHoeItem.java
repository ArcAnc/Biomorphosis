/**
 * @author ArcAnc
 * Created at: 17.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;


import com.arcanc.biomorphosis.content.gui.component.tooltip.ICustomTooltip;
import com.arcanc.biomorphosis.content.gui.component.tooltip.StyleData;
import com.arcanc.biomorphosis.content.gui.component.tooltip.TooltipData;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BioHoeItem extends HoeItem implements ItemInterfaces.IMustAddToCreativeTab, ICustomTooltip
{
	public static final ItemAbility BIO_HOE_TILL = ItemAbility.get(Database.rlStr("bio_till"));
	public static final Set<ItemAbility> BIO_HOE_ACTIONS = Stream.of(ItemAbilities.HOE_DIG, ItemAbilities.HOE_TILL, BIO_HOE_TILL).
			collect(Collectors.toCollection(Sets :: newIdentityHashSet));
	
	private final StyleData style = new StyleData(true, (player, stack) -> new TooltipData(
			true,
			Database.GUI.Textures.Tooltip.TOOLTIP_BACKGROUND,
			Database.GUI.Textures.Tooltip.TOOLTIP_DECORATIONS,
			true));
	
	public BioHoeItem(Tier tier, Properties props)
	{
		super(tier, props);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		BlockState state = level.getBlockState(blockpos);
		BlockState toolModifiedState = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
		if (toolModifiedState == null || state.equals(toolModifiedState))
			toolModifiedState = state.getToolModifiedState(context, BIO_HOE_TILL, false);
		Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = toolModifiedState == null ? null : Pair.of(ctx -> true, changeIntoState(toolModifiedState));
		if (pair == null) {
			return InteractionResult.PASS;
		} else {
			Predicate<UseOnContext> predicate = pair.getFirst();
			Consumer<UseOnContext> consumer = pair.getSecond();
			if (predicate.test(context)) {
				Player player = context.getPlayer();
				level.playSound(player, blockpos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				if (!level.isClientSide) {
					consumer.accept(context);
					if (player != null) {
						context.getItemInHand().hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
					}
				}
				
				return InteractionResult.sidedSuccess(level.isClientSide);
			} else {
				return InteractionResult.PASS;
			}
		}
	}
	
	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility)
	{
		return BIO_HOE_ACTIONS.contains(itemAbility);
	}
	
	@Override
	public StyleData getStyle()
	{
		return this.style;
	}
}

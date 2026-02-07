/**
 * @author ArcAnc
 * Created at: 02.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.entity.srf;


import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AbstractPalladin extends PathfinderMob
{
	public static final int MAX_AGGRO_RADIUS = 16;
	public static final int MAX_AGGRO_RADIUS_SQR = MAX_AGGRO_RADIUS * MAX_AGGRO_RADIUS;
	
	@Nullable
	private BlockPos guardPos;
	
	protected AbstractPalladin(EntityType<? extends PathfinderMob> type, Level level)
	{
		super(type, level);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public @Nullable SpawnGroupData finalizeSpawn(
			@NotNull ServerLevelAccessor level,
			@NotNull DifficultyInstance difficulty,
			@NotNull EntitySpawnReason spawnReason,
			@Nullable SpawnGroupData spawnGroupData)
	{
		SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
		
		if (this.guardPos == null)
			this.guardPos = this.blockPosition();
		
		return data;
	}
	
	public BlockPos getGuardPos()
	{
		return this.guardPos != null ? this.guardPos : this.blockPosition();
	}
	
	public boolean isOutAggroRadius()
	{
		return this.distanceToSqr(Vec3.atCenterOf(getGuardPos())) > MAX_AGGRO_RADIUS_SQR;
	}
	
	public boolean isNotOnGuardPos()
	{
		return this.distanceToSqr(Vec3.atBottomCenterOf(getGuardPos())) > 2;
	}
	
	protected void addOffersFromItemListings(MerchantOffers givenMerchantOffers, VillagerTrades.ItemListing[] newTrades, int maxNumbers)
	{
		ArrayList<VillagerTrades.ItemListing> arraylist = Lists.newArrayList(newTrades);
		int i = 0;
		
		while (i < maxNumbers && !arraylist.isEmpty())
		{
			MerchantOffer merchantoffer = arraylist.remove(this.random.nextInt(arraylist.size())).getOffer(this, this.random);
			if (merchantoffer != null)
			{
				givenMerchantOffers.add(merchantoffer);
				i++;
			}
		}
	}
	
	protected void rewardTradeXp(@NotNull MerchantOffer offer)
	{
		if (!offer.shouldRewardExp())
			return;
		int i = 3 + this.random.nextInt(4);
		this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), i));
	}
	
	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		
		if (this.guardPos == null)
			return;
		TagHelper.writeBlockPos(this.guardPos, compound, "guard_pos");
	}
	
	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		
		if (compound.contains("guard_pos"))
			this.guardPos = TagHelper.readBlockPos(compound, "guard_pos");
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

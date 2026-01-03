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
import com.arcanc.biomorphosis.content.entity.trades.Trades;
import com.arcanc.biomorphosis.content.worldgen.srf.orders.PalladinOrders;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Blacksmith extends AbstractPalladin implements Merchant
{
	@Nullable
	private Player tradingPlayer;
	@Nullable
	protected MerchantOffers offers;
	
	public Blacksmith(EntityType<? extends PathfinderMob> type, Level level)
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
	
	@Override
	public void setTradingPlayer(@Nullable Player tradingPlayer)
	{
		this.tradingPlayer = tradingPlayer;
	}
	
	@Override
	public @Nullable Player getTradingPlayer()
	{
		return this.tradingPlayer;
	}
	
	@Override
	public @NotNull MerchantOffers getOffers()
	{
		if (this.level().isClientSide)
		{
			throw new IllegalStateException("Cannot load Villager offers on the client");
		}
		else
		{
			if (this.offers == null)
			{
				this.offers = new MerchantOffers();
				this.updateTrades();
			}
			
			return this.offers;
		}
	}
	
	private void updateTrades()
	{
		VillagerTrades.ItemListing[] listing = Trades.getOffers(PalladinOrders.BASE_ORDER, Trades.PalladinTraderType.BLACKSMITH);
		if (listing == null)
			return;
		MerchantOffers merchantOffers = this.getOffers();
		this.addOffersFromItemListings(merchantOffers, listing, 6);
	}
	
	@Override
	public void overrideOffers(@NotNull MerchantOffers offers)
	{ }
	
	@Override
	public void notifyTrade(@NotNull MerchantOffer offer)
	{
		offer.increaseUses();
		this.ambientSoundTime = -this.getAmbientSoundInterval();
		this.rewardTradeXp(offer);
	}
	
	@Override
	public void notifyTradeUpdated(@NotNull ItemStack stack)
	{ }
	
	@Override
	public int getVillagerXp()
	{
		return 0;
	}
	
	@Override
	public void overrideXp(int xp)
	{
	}
	
	@Override
	public boolean showProgressBar()
	{
		return false;
	}
	
	@Override
	public @NotNull SoundEvent getNotifyTradeSound()
	{
		return SoundEvents.VILLAGER_YES;
	}
	
	@Override
	public boolean isClientSide()
	{
		return this.level().isClientSide();
	}
	
	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return this.getTradingPlayer() == player && this.isAlive() && player.canInteractWithEntity(this, 4.0);
	}
	
	@Override
	protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand)
	{
		if (this.tradingPlayer != null)
			return super.mobInteract(player, hand);
		if (!this.level().isClientSide())
		{
			if (this.getOffers().isEmpty())
				return InteractionResult.CONSUME;
			else
			{
				this.setTradingPlayer(player);
				this.openTradingScreen(player, this.getDisplayName(), 1);
			}
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(player, hand);
	}
	
	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag compound)
	{
		super.addAdditionalSaveData(compound);
		if (this.level().isClientSide())
			return;
		MerchantOffers merchantOffers = this.getOffers();
		if (merchantOffers.isEmpty())
			return;
		compound.put("offers", MerchantOffers.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), merchantOffers).getOrThrow());
	}
	
	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag compound)
	{
		super.readAdditionalSaveData(compound);
		if (compound.contains("offers"))
			MerchantOffers.CODEC.
					parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("offers")).
					resultOrPartial(s -> Database.LOGGER.warn("Can't read trade offers: {}", s)).
					ifPresent(merchantOffers -> this.offers = merchantOffers);
	}
}

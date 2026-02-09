/**
 * @author ArcAnc
 * Created at: 15.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock;


import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.content.block.multiblock.base.type.StaticMultiblockPart;
import com.arcanc.biomorphosis.content.gui.container_menu.ChrysalisMenu;
import com.arcanc.biomorphosis.content.mutations.GenomeEffectsHolder;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CGenomeSync;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.DamageHelper;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class MultiblockChrysalis extends StaticMultiblockPart implements GeoBlockEntity, BlockInterfaces.IInteractionObject<MultiblockChrysalis>
{
	private static final RawAnimation WAVE_ANIMATION = RawAnimation.begin().thenPlay("wave");
	private static final int MIN_ANIM_PERIOD = 4 * 20;
	private static final int MAX_ANIM_PERIOD = 10 * 20;
	
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	
	private final ServerBossEvent progressBar = new ServerBossEvent(
			Component.translatable(Database.GUI.Chrysalis.PROGRESS_BAR, 0),
			BossEvent.BossBarColor.GREEN,
			BossEvent.BossBarOverlay.PROGRESS);
	private final FluidSidedStorage fluidHandler;
	
	@Nullable
	private UUID playerUUID = null;
	@Nullable
	private GenomeInstance toGenome = null;
	@Nullable
	private GenomeInstance fromGenome = null;
	private int timer = -1;
	private int mutationTime = -1;
	
	private int animationTimer = -1;
	private int currentAnimationPeriod = -1;
	
	public MultiblockChrysalis(BlockPos pos, BlockState blockState)
	{
		super(Registration.BETypeReg.BE_MULTIBLOCK_CHRYSALIS.get(), pos, blockState);
		this.fluidHandler = new FluidSidedStorage().
				addHolder(FluidStackHolder.newBuilder().
						setCallback(hold -> this.markDirty()).
						setValidator(stack -> stack.is(Registration.FluidReg.BIOMASS.type().get())).
						setCapacity(10000).
						build(),
				BasicSidedStorage.FaceMode.ALL).
				addHolder(FluidStackHolder.newBuilder().
						setCallback(hold -> this.markDirty()).
						setValidator(stack -> stack.is(Registration.FluidReg.ACID.type().get())).
						setCapacity(5000).
						build(),
				BasicSidedStorage.FaceMode.ALL).
				addHolder(FluidStackHolder.newBuilder().
						setCallback(hold -> this.markDirty()).
						setValidator(stack -> stack.is(Registration.FluidReg.ADRENALINE.type().get())).
						setCapacity(5000).
						build(),
				BasicSidedStorage.FaceMode.ALL);
	}
	
	@Override
	protected void multiblockServerTick()
	{
		Level level = getLevel();
		if (!(level instanceof ServerLevel serverLevel))
			return;
		
		if (!this.isMaster())
			return;
		
		if (this.animationTimer-- <= 0)
			this.updateAnimationPeriod(serverLevel);
		
		if (this.timer < 0)
			return;
		
		ServerPlayer serverPlayer = serverLevel.getServer().getPlayerList().getPlayer(this.playerUUID);
		
		if (serverPlayer == null || !serverPlayer.isAlive())
		{
			reset();
			return;
		}
		
		this.timer--;
		float progress = 1f - ((float)this.timer / this.mutationTime);
		this.progressBar.setName(Component.translatable(Database.GUI.Chrysalis.PROGRESS_BAR, String.format("%.1f", progress * 100)));
		this.progressBar.setProgress(progress);
		
		if (this.timer <= 0)
			completeMutation(serverPlayer);
		
		setChanged();
	}
	
	private void completeMutation(ServerPlayer serverPlayer)
	{
		this.progressBar.removePlayer(serverPlayer);
		if (GenomeHelper.calculateStability(this.toGenome, this.getLevel()) < 0)
		{
			this.toGenome = GenomeInstance.EMPTY;
			serverPlayer.setData(Registration.DataAttachmentsReg.GENOME.get(), this.toGenome);
			NetworkEngine.sendToPlayer(serverPlayer, new S2CGenomeSync(serverPlayer.getUUID(), this.toGenome));
			reset();
			DamageHelper.mutationDamage(Integer.MAX_VALUE, serverPlayer);
			return;
		}
		serverPlayer.setData(Registration.DataAttachmentsReg.GENOME.get(), this.toGenome);
		NetworkEngine.sendToPlayer(serverPlayer, new S2CGenomeSync(serverPlayer.getUUID(), this.toGenome));
		
		serverPlayer.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
		serverPlayer.removeEffect(MobEffects.DAMAGE_RESISTANCE);
		serverPlayer.removeEffect(MobEffects.WEAKNESS);
		
		BlockState state = this.getBlockState();
		Direction facing = state.getValue(BlockHelper.BlockProperties.HORIZONTAL_FACING);
		BlockPos pos = this.getMasterPos().map(blockPos -> blockPos.relative(facing, 2)).
				orElse(BlockPos.ZERO);
		serverPlayer.teleportTo(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
		if (serverPlayer instanceof GenomeEffectsHolder holder)
			holder.biomorphosis$rebuildEffects();
		reset();
	}
	
	private void reset()
	{
		this.timer = -1;
		this.mutationTime = -1;
		this.toGenome = null;
		this.fromGenome = null;
		this.playerUUID = null;
		this.setChanged();
	}
	
	public void tryStartMutation(ServerPlayer player, GenomeInstance toGenome)
	{
		if (this.playerUUID == null || !this.playerUUID.equals(player.getUUID()))
			return;
		
		if (this.timer >= 0)
			return;
		
		GenomeInstance currentGenome = GenomeHelper.getGenome(player);
		int diff = currentGenome.calculateDiff(toGenome);
		if (diff <= 0)
			return;
		if (!consumeResources(diff))
			return;
		player.closeContainer();
		this.playerUUID = player.getUUID();
		this.fromGenome = currentGenome.copy();
		this.toGenome = toGenome;
		this.mutationTime = diff * 10 * 20; // 10 sec 20 ticks
		this.timer = this.mutationTime;
		
		lockPlayer(player);
		
		this.progressBar.setProgress(0);
		this.progressBar.addPlayer(player);
		
		setChanged();
	}
	
	private boolean consumeResources(int diff)
	{
		FluidStack[] toCheckStacks = new FluidStack[]
				{
						new FluidStack(Registration.FluidReg.BIOMASS.still(), diff * 1000),
						new FluidStack(Registration.FluidReg.ACID.still(), diff * 100),
						new FluidStack(Registration.FluidReg.ADRENALINE.still(), diff * 100)
				};
		
		FluidStack biomass = this.fluidHandler.extract(null, toCheckStacks[0], true);
		if (biomass.getAmount() < toCheckStacks[0].getAmount())
			return false;
		FluidStack acid = this.fluidHandler.extract(null, toCheckStacks[1], true);
		if (acid.getAmount() < toCheckStacks[1].getAmount())
			return false;
		FluidStack adrenaline = this.fluidHandler.extract(null, toCheckStacks[2], true);
		if (adrenaline.getAmount() < toCheckStacks[2].getAmount())
			return false;
		
		this.fluidHandler.extract(null, toCheckStacks[0], false);
		this.fluidHandler.extract(null, toCheckStacks[1], false);
		this.fluidHandler.extract(null, toCheckStacks[2], false);
		
		return true;
	}
	
	private void lockPlayer(@NotNull ServerPlayer player)
	{
		BlockPos center = getMasterPos().orElse(getBlockPos());
		player.teleportTo(center.getX() + 0.5f,
					center.getY() + 0.5f,
					center.getZ() + 0.5f);
		
		player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, this.mutationTime, 255, true, false));
		player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, this.mutationTime, 255, true, false));
		player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, this.mutationTime, 255, true, false));
	}
	
	public void onInteractionStarted(Player player)
	{
		if (this.playerUUID != null || !(this.level instanceof ServerLevel))
			return;
		this.playerUUID = player.getUUID();
		this.setChanged();
	}
	
	public void onInteractionEnd(Player player)
	{
		if (this.playerUUID == null || !this.playerUUID.equals(player.getUUID()))
			return;
		this.playerUUID = null;
		this.setChanged();
	}
	
	@Override
	protected void firstTick()
	{
	
	}
	
	@Override
	public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{
		super.readCustomTag(tag, registries, descrPacket);
		if (!isMaster())
			return;
		this.fluidHandler.deserializeNBT(registries, tag.getCompound(Database.Capabilities.Fluids.HANDLER));
		this.timer = tag.getInt("timer");
		this.mutationTime = tag.getInt("mutation_timer");
		if (tag.contains("player_uuid"))
			this.playerUUID = tag.getUUID("player_uuid");
		if (tag.contains("from_genome"))
			this.fromGenome = GenomeInstance.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("from_genome")).
					getOrThrow();
		if (tag.contains("to_genome"))
			this.toGenome = GenomeInstance.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("to_genome")).
					getOrThrow();
	}
	
	@Override
	public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
	{
		super.writeCustomTag(tag, registries, descrPacket);
		if (!isMaster())
			return;
		tag.put(Database.Capabilities.Fluids.HANDLER, this.fluidHandler.serializeNBT(registries));
		if(this.playerUUID != null)
			tag.putUUID("player_uuid", this.playerUUID);
		tag.putInt("timer", this.timer);
		tag.putInt("mutation_time", this.mutationTime);
		if (this.fromGenome != null)
			GenomeInstance.CODEC.encodeStart(NbtOps.INSTANCE, this.fromGenome).
					map(written -> tag.put("from_genome", written));
		if (this.toGenome != null)
			GenomeInstance.CODEC.encodeStart(NbtOps.INSTANCE, this.toGenome).
					map(written -> tag.put("to_genome", written));
	}
	
	@Override
	public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllers)
	{
		controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.STOP).
				triggerableAnim("wave", WAVE_ANIMATION));
	}
	
	@Override
	public @Nullable MultiblockChrysalis getGuiMaster()
	{
		return isMaster() ? this : getMasterPos().flatMap(pos ->
						BlockHelper.castTileEntity(getLevel(), pos, MultiblockChrysalis.class)).
				orElse(null);
	}
	
	@Override
	public Registration.MenuTypeReg.ArgContainer<MultiblockChrysalis, ChrysalisMenu> getContainerType()
	{
		return Registration.MenuTypeReg.CHRYSALIS;
	}
	
	@Override
	public boolean canUseGui(Player player)
	{
		return getMasterPos().map(pos -> (player.
						distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= 64) &&
						(this.playerUUID == player.getUUID() || this.playerUUID == null) &&
						this.timer == -1).
				orElse(false);
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return this.cache;
	}
	
	public static @Nullable FluidSidedStorage getFluidHandler(@NotNull MultiblockChrysalis be, Direction ctx)
	{
		return be.isMaster() ? be.fluidHandler : be.getMasterPos().
				flatMap(pos -> BlockHelper.
				castTileEntity(be.getLevel(), pos, MultiblockChrysalis.class).
				map(master -> master.fluidHandler)).
				orElse(null);
	}
	
	private void updateAnimationPeriod(@NotNull ServerLevel level)
	{
		this.currentAnimationPeriod = level.random.nextIntBetweenInclusive(MIN_ANIM_PERIOD, MAX_ANIM_PERIOD);
		this.animationTimer = this.currentAnimationPeriod;
		this.triggerAnim("controller", "wave");
	}
}

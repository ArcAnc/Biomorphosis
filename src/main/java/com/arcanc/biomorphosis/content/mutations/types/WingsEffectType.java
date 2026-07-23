/**
 * @author ArcAnc
 * Created at: 21.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.types;

import com.arcanc.biomorphosis.content.mutations.AttributeParams;
import com.arcanc.biomorphosis.content.mutations.GenomeEffectsHolder;
import com.arcanc.biomorphosis.content.mutations.wings.WingsFlightData;
import com.arcanc.biomorphosis.content.mutations.wings.WingsFlightInput;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.mixin.LivingEntityAccessor;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


/**
 * Flight mechanic was taken from {@link <a href="https://github.com/Fuzss/fantastic-wings">Fantastic Wings</a>}
 * <p>
 * Permission was obtained directly from @Fuzs
 */
public class WingsEffectType implements IGeneEffectType<WingsEffectType>
{
	public static final MapCodec<WingsEffectType> CODEC = MapCodec.unit(WingsEffectType :: new);
	private static final float MIN_SPEED = 0.03f;
	private static final float MANUAL_Y_BOOST = 0.06f;
	private static final float FALL_REDUCTION = 0.9f;
	private static final double HOVER_DAMPING = 0.6d;
	private static final double HOVER_STOP_SPEED_SQR = 0.01d;

	@Override
	public MapCodec<WingsEffectType> mapCodec()
	{
		return CODEC;
	}

	@Override
	public ResourceLocation getId()
	{
		return Database.GUI.Genome.WINGS.id();
	}

	@Override
	public void remove(LivingEntity entity, AttributeParams params, CompoundTag data)
	{
		if (entity instanceof Player player)
		{
			setFlightData(player, getFlightData(player).withFlying(false));
			clearFlightInput(player);
			restoreGravity(player);
			player.refreshDimensions();
		}
	}

	@Override
	public void tick(LivingEntity entity, AttributeParams params, CompoundTag data)
	{
		if (entity instanceof Player player)
			tickFlight(player, params);
	}

	public static boolean hasWings(LivingEntity entity)
	{
		return findEffect(entity) != null;
	}

	public static boolean isFlying(LivingEntity entity)
	{
		return entity instanceof Player player && getFlightData(player).flying();
	}

	public static boolean setFlying(Player player, boolean flying)
	{
		GenomeEffectsHolder.GeneEffectInstance effect = findEffect(player);
		if (effect == null)
			return false;
		setFlightData(player, getFlightData(player).withFlying(flying));
		clearFlightInput(player);
		if (flying && player.isPassenger())
			player.stopRiding();
		if (!flying)
			restoreGravity(player);
		player.refreshDimensions();
		return true;
	}
	
	public static void syncFlying(Player player, boolean flying)
	{
		setFlightData(player, getFlightData(player).withFlying(flying));
		clearFlightInput(player);
		if (!flying)
			restoreGravity(player);
		player.refreshDimensions();
	}

	public static boolean isHovering(Player player)
	{
		return isFlying(player) && !getFlightInput(player).hasInput() &&
				player.getDeltaMovement().lengthSqr() <= HOVER_STOP_SPEED_SQR;
	}
	
	public static WingsFlightInput getLocalFlightInput(Player player)
	{
		return new WingsFlightInput(player.xxa, player.zza,
				((LivingEntityAccessor) player).biomorphosis$isJumping(), player.isShiftKeyDown());
	}
	
	public static void setFlightInput(Player player, float strafe, float forward, boolean jumping, boolean descending)
	{
		if (!isFlying(player))
		{
			clearFlightInput(player);
			return;
		}
		player.setData(Registration.DataAttachmentsReg.WINGS_FLIGHT_INPUT,
				new WingsFlightInput(clampInput(strafe), clampInput(forward), jumping, descending));
	}

	public static void tickClient(Player player)
	{
		GenomeEffectsHolder.GeneEffectInstance effect = findEffect(player);
		if (effect != null)
			tickFlight(player, effect.entry().params(), getLocalFlightInput(player));
	}

	private static void tickFlight(Player player, AttributeParams params)
	{
		tickFlight(player, params, getFlightInput(player));
	}

	private static void tickFlight(Player player, AttributeParams params, WingsFlightInput input)
	{
		WingsFlightData flightData = getFlightData(player);
		boolean flying = flightData.flying();
		if (flying && (player.isSpectator() || player.isFallFlying()))
		{
			setFlightData(player, flightData.withFlying(false));
			clearFlightInput(player);
			flying = false;
		}
		if (flying)
			disableGravity(player);
		else
			restoreGravity(player);

		if (flying && !player.isInWaterOrBubble())
		{
			boolean jumping = input.jumping();
			boolean descending = input.descending();
			boolean hasFlightInput = input.hasInput();
			
			if (!hasFlightInput)
			{
				Vec3 movement = player.getDeltaMovement();
				player.setDeltaMovement(movement.lengthSqr() <= HOVER_STOP_SPEED_SQR ? Vec3.ZERO :
						movement.scale(HOVER_DAMPING));
			}

			if (input.forward() > 0.0F)
			{
				float maxSpeed = params.getInt("speed", 72) / 1000.0F;
				float speed = Mth.clampedLerp(MIN_SPEED, maxSpeed, input.forward());
				float pitch = -player.getXRot() * Mth.DEG_TO_RAD;
				float yaw = -player.getYRot() * Mth.DEG_TO_RAD - Mth.PI;
				float horizontal = -Mth.cos(pitch);
				player.setDeltaMovement(player.getDeltaMovement().add(
						Mth.sin(yaw) * horizontal * speed,
						Mth.sin(pitch) * speed,
						Mth.cos(yaw) * horizontal * speed));
			}
			if (jumping)
				player.setDeltaMovement(player.getDeltaMovement().add(0.0, MANUAL_Y_BOOST / 2.0F, 0.0));
			else if (descending)
				player.setDeltaMovement(player.getDeltaMovement().add(0.0, -MANUAL_Y_BOOST, 0.0));
		}

		if (!flying && !input.descending())
		{
			Vec3 movement = player.getDeltaMovement();
			if (movement.y() < 0.0)
				player.setDeltaMovement(movement.multiply(1.0, FALL_REDUCTION, 1.0));
		}
		if (flying)
			player.fallDistance = 0.0F;

		refreshFlightHitbox(player);
	}

	private static void refreshFlightHitbox(Player player)
	{
		float expectedHeight = player.getDimensions(player.getPose()).height();
		if (Math.abs(player.getBbHeight() - expectedHeight) > 1.0E-4F)
			player.refreshDimensions();
	}

	private static void disableGravity(Player player)
	{
		WingsFlightData data = getFlightData(player);
		if (!data.hasPreviousNoGravity())
			setFlightData(player, data.withPreviousNoGravity(player.isNoGravity()));
		player.setNoGravity(true);
	}

	private static void restoreGravity(Player player)
	{
		WingsFlightData data = getFlightData(player);
		if (!data.hasPreviousNoGravity())
			return;
		player.setNoGravity(data.previousNoGravity());
		setFlightData(player, data.withoutPreviousNoGravity());
	}

	private static WingsFlightData getFlightData(Player player)
	{
		return player.hasData(Registration.DataAttachmentsReg.WINGS_FLIGHT) ?
				player.getData(Registration.DataAttachmentsReg.WINGS_FLIGHT) : WingsFlightData.EMPTY;
	}

	private static void setFlightData(Player player, WingsFlightData data)
	{
		player.setData(Registration.DataAttachmentsReg.WINGS_FLIGHT, data);
	}

	private static WingsFlightInput getFlightInput(Player player)
	{
		if (player.level().isClientSide() && player.isLocalPlayer())
			return getLocalFlightInput(player);
		return player.hasData(Registration.DataAttachmentsReg.WINGS_FLIGHT_INPUT) ?
				player.getData(Registration.DataAttachmentsReg.WINGS_FLIGHT_INPUT) : WingsFlightInput.EMPTY;
	}

	private static void clearFlightInput(Player player)
	{
		if (player.hasData(Registration.DataAttachmentsReg.WINGS_FLIGHT_INPUT) &&
				!player.getData(Registration.DataAttachmentsReg.WINGS_FLIGHT_INPUT).equals(WingsFlightInput.EMPTY))
			player.setData(Registration.DataAttachmentsReg.WINGS_FLIGHT_INPUT, WingsFlightInput.EMPTY);
	}

	private static float clampInput(float input)
	{
		return Float.isFinite(input) ? Mth.clamp(input, -1.0F, 1.0F) : 0.0F;
	}

	private static @Nullable GenomeEffectsHolder.GeneEffectInstance findEffect(LivingEntity entity)
	{
		if (!(entity instanceof GenomeEffectsHolder holder))
			return null;
		for (GenomeEffectsHolder.GeneEffectInstance effect : holder.biomorphosis$getGeneEffects())
			if (effect.entry().type() instanceof WingsEffectType)
				return effect;
		return null;
	}
}

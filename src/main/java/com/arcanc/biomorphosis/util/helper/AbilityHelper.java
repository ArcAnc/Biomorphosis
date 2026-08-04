/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import com.arcanc.biomorphosis.content.ability.*;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CAbilityCast;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class AbilityHelper
{
	private AbilityHelper()
	{
	}

	public static AbilityLoadout getLoadout(Player player)
	{
		return player.getData(Registration.DataAttachmentsReg.ABILITY_LOADOUT);
	}

	public static Optional<IAbility> getAbility(ResourceLocation id)
	{
		return Optional.ofNullable(Registration.AbilityReg.ABILITY_REGISTRY.get(id));
	}

	public static Optional<IAbility> getAbility(Player player, int slot)
	{
		if (slot < 0 || slot >= AbilityLoadout.SLOT_COUNT)
			return Optional.empty();

		return getLoadout(player).getSlot(slot).flatMap(AbilityHelper :: getAbility);
	}
	
	public static Set<ResourceLocation> getInnateAbilities(LivingEntity entity)
	{
		ResourceKey<EntityAbilityDefinition> definitionKey = ResourceKey.create(
				Registration.AbilityReg.ENTITY_ABILITY_KEY, EntityType.getKey(entity.getType()));
		return entity.registryAccess().lookup(Registration.AbilityReg.ENTITY_ABILITY_KEY).
				flatMap(registry -> registry.get(definitionKey)).
				map(Holder.Reference :: value).
				map(definition -> definition.abilities().stream().
						map(ResourceKey :: location).
						filter(abilityId -> Registration.AbilityReg.ABILITY_REGISTRY.containsKey(abilityId)).
						collect(Collectors.toUnmodifiableSet())).
				orElseGet(Set :: of);
	}

	public static <A extends IAbility> Optional<A> getInnateAbility(LivingEntity entity, ResourceLocation abilityId, Class<A> abilityClass)
	{
		if (!getInnateAbilities(entity).contains(abilityId))
			return Optional.empty();

		return getAbility(abilityId).filter(abilityClass :: isInstance).map(abilityClass :: cast);
	}

	public static void setAbility(Player player, int slot, ResourceLocation abilityId)
	{
		if (!assignAbility(player, slot, abilityId))
			throw new IllegalArgumentException("Unknown ability or invalid slot: " + abilityId);
	}
	
	public static boolean assignAbility(Player player, int slot, ResourceLocation abilityId)
	{
		if (slot < 0 || slot >= AbilityLoadout.SLOT_COUNT || getAbility(abilityId).isEmpty())
			return false;

		AbilityLoadout loadout = getLoadout(player);
		if (!loadout.hasAbility(abilityId))
			return false;
		Map<Integer, ResourceLocation> slots = new HashMap<>(loadout.slots());
		slots.entrySet().removeIf(entry -> entry.getValue().equals(abilityId));
		slots.put(slot, abilityId);
		player.setData(Registration.DataAttachmentsReg.ABILITY_LOADOUT,
				new AbilityLoadout(slots, loadout.cooldownEnds(), loadout.unlockedAbilities()));
		return true;
	}

	public static void clearAbility(Player player, int slot)
	{
		player.setData(Registration.DataAttachmentsReg.ABILITY_LOADOUT, getLoadout(player).withoutSlot(slot));
	}

	public static boolean tryActivate(ServerPlayer player, int slot)
	{
		if (slot < 0 || slot >= AbilityLoadout.SLOT_COUNT)
			return false;
		if (player.getData(Registration.DataAttachmentsReg.ABILITY_CASTING).isCasting())
			return false;

		AbilityLoadout loadout = getLoadout(player);
		Optional<ResourceLocation> abilityId = loadout.getSlot(slot);
		if (abilityId.isEmpty())
			return false;

		long gameTime = player.level().getGameTime();
		if (loadout.isOnCooldown(abilityId.get(), gameTime))
			return false;

		Optional<IAbility> ability = getAbility(abilityId.get());
		if (ability.isEmpty())
			return false;

		AbilityActivationContext context = new AbilityActivationContext(player, slot, ability.get());
		if (!ability.get().getType().value().canActivate(context))
			return false;

		int castTimeTicks = ability.get().getCastTimeTicks();
		if (castTimeTicks <= 0)
			return activate(player, abilityId.get(), ability.get(), context);

		AbilityCastingState state = AbilityCastingState.start(abilityId.get(), slot, gameTime, castTimeTicks);
		player.setData(Registration.DataAttachmentsReg.ABILITY_CASTING, state);
		sendCastState(player, state, true);
		ability.get().getCastSound().ifPresent(sound ->
				player.serverLevel().playSound(null, player, sound, SoundSource.PLAYERS, 1F, 1f + player.getRandom().nextFloat() * 0.1F));
		return true;
	}

	public static void completeCast(ServerPlayer player, AbilityCastingState state, IAbility ability)
	{
		player.setData(Registration.DataAttachmentsReg.ABILITY_CASTING, AbilityCastingState.EMPTY);
		AbilityActivationContext context = new AbilityActivationContext(player, state.slot(), ability);
		activate(player, state.abilityId().orElseThrow(), ability, context);
		sendCastState(player, state, false);
	}

	public static void cancelCast(ServerPlayer player, AbilityCastingState state)
	{
		player.setData(Registration.DataAttachmentsReg.ABILITY_CASTING, AbilityCastingState.EMPTY);
		sendCastState(player, state, false);
	}

	private static boolean activate(ServerPlayer player, ResourceLocation abilityId, IAbility ability, AbilityActivationContext context)
	{
		if (!ability.getType().value().tryActivate(context))
			return false;

		AbilityLoadout loadout = getLoadout(player);
		long cooldownEnd = player.level().getGameTime() + ability.getCooldownTicks();
		player.setData(Registration.DataAttachmentsReg.ABILITY_LOADOUT, loadout.withCooldown(abilityId, cooldownEnd));
		return true;
	}

	private static void sendCastState(ServerPlayer player, AbilityCastingState state, boolean active)
	{
		state.abilityId().ifPresent(abilityId ->
		{
			ResourceLocation animationId = getAbility(abilityId).
					map(IAbility :: getCastAnimationId).
					orElse(AbilityCastAnimations.NONE);
			NetworkEngine.sendToPlayerNear(player.serverLevel(), null, player.position(), 64.0,
					new S2CAbilityCast(player.getUUID(), abilityId, animationId, state.startedAt(),
							(int)Math.min(Integer.MAX_VALUE, state.completesAt() - state.startedAt()), active));
		});
	}
}

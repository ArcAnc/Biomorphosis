/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record AbilityLoadout(Map<Integer, ResourceLocation> slots, Map<ResourceLocation, Long> cooldownEnds,
							 Set<ResourceLocation> unlockedAbilities)
{
	public static final int SLOT_COUNT = 6;
	public static final AbilityLoadout EMPTY = empty();
	private static final Codec<Integer> SLOT_KEY_CODEC = Codec.STRING.comapFlatMap(slotName ->
	{
		try
		{
			int slot = Integer.parseInt(slotName);
			if (slot >= 0 && slot < SLOT_COUNT)
				return DataResult.success(slot);
		}
		catch (NumberFormatException ignored)
		{
		}
		return DataResult.error(() -> "Ability slot key must be between 0 and " + (SLOT_COUNT - 1) + ": " + slotName);
	}, Object :: toString);

	public AbilityLoadout
	{
		if (slots.keySet().stream().anyMatch(slot -> slot < 0 || slot >= SLOT_COUNT))
			throw new IllegalArgumentException("Ability slot must be between 0 and " + (SLOT_COUNT - 1));

		slots = Map.copyOf(slots);
		cooldownEnds = Map.copyOf(cooldownEnds);
		Set<ResourceLocation> allUnlockedAbilities = new HashSet<>(unlockedAbilities);
		allUnlockedAbilities.addAll(slots.values());
		unlockedAbilities = Set.copyOf(allUnlockedAbilities);
	}

	public static final Codec<AbilityLoadout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(SLOT_KEY_CODEC, ResourceLocation.CODEC).
					fieldOf("slots").forGetter(AbilityLoadout :: slots),
			Codec.unboundedMap(ResourceLocation.CODEC, Codec.LONG).
					fieldOf("cooldowns").forGetter(AbilityLoadout :: cooldownEnds),
			ResourceLocation.CODEC.listOf().xmap(Set :: copyOf, java.util.List :: copyOf).
					optionalFieldOf("unlocked", Set.of()).forGetter(AbilityLoadout :: unlockedAbilities)
	).apply(instance, AbilityLoadout :: new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AbilityLoadout> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.<RegistryFriendlyByteBuf, Integer, ResourceLocation, Map<Integer, ResourceLocation>>map(
					HashMap :: new, ByteBufCodecs.VAR_INT, ResourceLocation.STREAM_CODEC),
			AbilityLoadout :: slots,
			ByteBufCodecs.<RegistryFriendlyByteBuf, ResourceLocation, Long, Map<ResourceLocation, Long>>map(
					HashMap :: new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.VAR_LONG),
			AbilityLoadout :: cooldownEnds,
			ByteBufCodecs.collection(HashSet :: new, ResourceLocation.STREAM_CODEC),
			AbilityLoadout :: unlockedAbilities,
			AbilityLoadout :: new);

	public static AbilityLoadout empty()
	{
		return new AbilityLoadout(Map.of(), Map.of(), Set.of());
	}

	public Optional<ResourceLocation> getSlot(int slot)
	{
		validateSlot(slot);
		return Optional.ofNullable(this.slots.get(slot));
	}

	public long getRemainingCooldownTicks(ResourceLocation abilityId, long gameTime)
	{
		return Math.max(0L, this.cooldownEnds.getOrDefault(abilityId, 0L) - gameTime);
	}

	public boolean isOnCooldown(ResourceLocation abilityId, long gameTime)
	{
		return this.getRemainingCooldownTicks(abilityId, gameTime) > 0;
	}

	public boolean hasAbility(ResourceLocation abilityId)
	{
		return this.unlockedAbilities.contains(abilityId);
	}

	public AbilityLoadout withSlot(int slot, ResourceLocation abilityId)
	{
		validateSlot(slot);
		Map<Integer, ResourceLocation> updatedSlots = new HashMap<>(this.slots);
		updatedSlots.put(slot, abilityId);
		return new AbilityLoadout(updatedSlots, this.cooldownEnds, this.unlockedAbilities);
	}

	public AbilityLoadout withoutSlot(int slot)
	{
		validateSlot(slot);
		Map<Integer, ResourceLocation> updatedSlots = new HashMap<>(this.slots);
		updatedSlots.remove(slot);
		return new AbilityLoadout(updatedSlots, this.cooldownEnds, this.unlockedAbilities);
	}

	public AbilityLoadout withCooldown(ResourceLocation abilityId, long cooldownEnd)
	{
		Map<ResourceLocation, Long> updatedCooldowns = new HashMap<>(this.cooldownEnds);
		updatedCooldowns.put(abilityId, cooldownEnd);
		return new AbilityLoadout(this.slots, updatedCooldowns, this.unlockedAbilities);
	}

	public AbilityLoadout withUnlockedAbility(ResourceLocation abilityId)
	{
		if (this.hasAbility(abilityId))
			return this;

		Set<ResourceLocation> updatedAbilities = new HashSet<>(this.unlockedAbilities);
		updatedAbilities.add(abilityId);
		return new AbilityLoadout(this.slots, this.cooldownEnds, updatedAbilities);
	}

	private static void validateSlot(int slot)
	{
		if (slot < 0 || slot >= SLOT_COUNT)
			throw new IllegalArgumentException("Ability slot must be between 0 and " + (SLOT_COUNT - 1));
	}
}

/**
 * @author ArcAnc
 * Created at: 21.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.organic_armor;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OrganicArmorEffectHandler
{
	private static final int TICK_PERIOD = 5 * 20;
	private static final int FLUID_CONSUME_AMOUNT = 1;
	private static final int EFFECT_DURATION_TICKS = 6 * 20;
	private static final Object2ObjectLinkedOpenHashMap<UUID, QueuedOrganicArmorEffects> EFFECTS_MAP = new Object2ObjectLinkedOpenHashMap<>();
	private static final Object2ObjectLinkedOpenHashMap<UUID, ActiveOrganicArmorEffects> ACTIVE_EFFECTS = new Object2ObjectLinkedOpenHashMap<>();

	public static boolean hasFluidEffect(HolderLookup.Provider registries, OrganicArmorState.Piece piece, FluidStack fluid)
	{
		return findEffectDefinition(registries, piece, fluid).isPresent();
	}

	public static Optional<ResourceLocation> getEffectId(HolderLookup.Provider registries, OrganicArmorState.Piece piece, FluidStack fluid)
	{
		return findEffectDefinition(registries, piece, fluid).map(holder -> holder.value().effect().location());
	}

	public static Optional<Holder.Reference<OrganicArmorFluidEffect>> findEffectDefinition(HolderLookup.Provider registries, OrganicArmorState.Piece piece, FluidStack fluid)
	{
		if (fluid.isEmpty())
			return Optional.empty();

		ResourceLocation fluidTypeId = NeoForgeRegistries.FLUID_TYPES.getKey(fluid.getFluidType());
		if (fluidTypeId == null)
			return Optional.empty();

		ResourceKey<OrganicArmorType> organicArmorKey = ResourceKey.create(Registration.OrganicArmorReg.TYPE_KEY, piece.typeId());
		boolean hasSlotParams = registries.lookup(Registration.OrganicArmorReg.TYPE_KEY).
				flatMap(registry -> registry.get(organicArmorKey)).
				map(Holder.Reference :: value).
				flatMap(type -> type.get(piece.slot())).
				isPresent();
		if (!hasSlotParams)
			return Optional.empty();

		ResourceKey<FluidType> fluidTypeKey = ResourceKey.create(NeoForgeRegistries.Keys.FLUID_TYPES, fluidTypeId);
		return registries.lookup(Registration.OrganicArmorReg.EFFECT_DATA_KEY).
				flatMap(registry -> registry.
						listElements().
						filter(holder -> matches(holder.value(), organicArmorKey, fluidTypeKey)).
						findFirst());
	}

	private static boolean matches(OrganicArmorFluidEffect entry, ResourceKey<OrganicArmorType> organicArmor, ResourceKey<FluidType> fluidType)
	{
		return entry.organicArmor().equals(organicArmor) &&
				entry.fluidType().equals(fluidType);
	}

	public static void tickArmor(LivingEntity entity)
	{
		if (!(entity.level() instanceof ServerLevel))
			return;
		if (entity.level().getGameTime() % TICK_PERIOD != 0)
			return;

		OrganicArmorState state = OrganicArmorHelper.getState(entity);
		if (state.isEmpty())
			return;

		OrganicArmorState updated = state;
		boolean changed = false;
		for (OrganicArmorState.Piece piece : state.pieces())
		{
			FluidStack fluid = piece.fluid();
			if (fluid.isEmpty() || fluid.getAmount() < FLUID_CONSUME_AMOUNT)
			{
				removeEffectData(entity, piece.slot());
				continue;
			}

			Optional<Holder.Reference<OrganicArmorFluidEffect>> effect = findEffectDefinition(entity.level().registryAccess(), piece, fluid);
			if (effect.isEmpty())
			{
				removeEffectData(entity, piece.slot());
				continue;
			}

			FluidStack drained = fluid.copy();
			drained.shrink(FLUID_CONSUME_AMOUNT);
			updated = updated.with(new OrganicArmorState.Piece(piece.slot(), piece.typeId(), drained));
			changed = true;
			addEffectData(entity, piece.slot(), effect.get().key().location());
		}

		if (changed)
		{
			entity.setData(Registration.DataAttachmentsReg.ORGANIC_ARMOR, updated);
			OrganicArmorHelper.rebuildArmor(entity);
			if (entity instanceof ServerPlayer serverPlayer)
				OrganicArmorHelper.sync(serverPlayer, serverPlayer);
		}
	}

	public static void tick(MinecraftServer server)
	{
		long gameTime = server.overworld().getGameTime();
		EFFECTS_MAP.forEach((uuid, queued) ->
		{
			LivingEntity wearer = findLivingEntity(server, uuid);
			if (wearer == null)
				return;

			ActiveOrganicArmorEffects active = ACTIVE_EFFECTS.computeIfAbsent(uuid, id -> new ActiveOrganicArmorEffects());
			Map<ResourceLocation, Integer> previousCounts = countEffects(active.effects());
			active.effects().clear();
			active.effects().putAll(queued.effects());
			active.setExpireAt(gameTime + EFFECT_DURATION_TICKS);

			Map<ResourceLocation, Integer> counts = countEffects(active.effects());
			rebuildAttributeEffects(wearer, previousCounts, counts);
			counts.forEach((effectDataId, count) ->
			{
				IOrganicArmorEffect effect = getRuntimeEffect(wearer.level().registryAccess(), effectDataId).orElse(null);
				if (effect != null)
					effect.applyTick(wearer, effectDataId, count, EFFECT_DURATION_TICKS);
			});
		});
		EFFECTS_MAP.clear();
		EFFECTS_MAP.trim();
		clearExpiredEffects(server, gameTime);
	}

	public static void addEffectData(UUID uuid, EquipmentSlot slot, ResourceLocation effectDataId)
	{
		EFFECTS_MAP.computeIfAbsent(uuid, id -> new QueuedOrganicArmorEffects()).
				effects().
				put(slot, effectDataId);
	}

	public static void addEffectData(LivingEntity entity, EquipmentSlot slot, ResourceLocation effectDataId)
	{
		addEffectData(entity.getUUID(), slot, effectDataId);
	}

	public static void removeEffectData(LivingEntity entity, EquipmentSlot slot)
	{
		QueuedOrganicArmorEffects queued = EFFECTS_MAP.get(entity.getUUID());
		if (queued != null && queued.effects().remove(slot) != null && queued.effects().isEmpty())
			EFFECTS_MAP.remove(entity.getUUID());

		ActiveOrganicArmorEffects active = ACTIVE_EFFECTS.get(entity.getUUID());
		if (active == null)
			return;

		Map<ResourceLocation, Integer> previousCounts = countEffects(active.effects());
		if (active.effects().remove(slot) != null)
			rebuildAttributeEffects(entity, previousCounts, countEffects(active.effects()));

		if (active.effects().isEmpty())
			ACTIVE_EFFECTS.remove(entity.getUUID());
	}

	public static void handleIncomingDamage(LivingDamageEvent.Pre event)
	{
		LivingEntity wearer = event.getEntity();
		for (Map.Entry<ResourceLocation, Integer> entry : countActiveEffects(wearer.getUUID()).entrySet())
		{
			IOrganicArmorEffect effect = getRuntimeEffect(wearer.level().registryAccess(), entry.getKey()).orElse(null);
			if (effect != null)
				effect.onIncomingDamage(wearer, event, entry.getKey(), entry.getValue());
		}
	}

	public static void handleDamagePost(LivingDamageEvent.Post event)
	{
		LivingEntity wearer = event.getEntity();
		DamageSource source = event.getSource();
		if (!source.isDirect())
			return;

		Entity sourceEntity = source.getEntity();
		if (!(sourceEntity instanceof LivingEntity attacker) || attacker == wearer)
			return;

		for (Map.Entry<ResourceLocation, Integer> entry : countActiveEffects(wearer.getUUID()).entrySet())
		{
			IOrganicArmorEffect effect = getRuntimeEffect(wearer.level().registryAccess(), entry.getKey()).orElse(null);
			if (effect != null)
				effect.onMeleeAttacked(wearer, attacker, source, event.getNewDamage(), entry.getKey(), entry.getValue());
		}
	}

	private static void clearExpiredEffects(MinecraftServer server, long gameTime)
	{
		ACTIVE_EFFECTS.entrySet().removeIf(entry ->
		{
			if (entry.getValue().expireAt() > gameTime)
				return false;

			LivingEntity wearer = findLivingEntity(server, entry.getKey());
			if (wearer != null)
				removeAttributeEffects(wearer, countEffects(entry.getValue().effects()));
			return true;
		});
	}

	private static void rebuildAttributeEffects(LivingEntity wearer, Map<ResourceLocation, Integer> previousCounts, Map<ResourceLocation, Integer> counts)
	{
		removeAttributeEffects(wearer, previousCounts);
		counts.forEach((effectDataId, count) ->
		{
			IOrganicArmorEffect effect = getRuntimeEffect(wearer.level().registryAccess(), effectDataId).orElse(null);
			if (effect != null)
				effect.applyAttributes(wearer, effectDataId, count);
		});
	}

	private static void removeAttributeEffects(LivingEntity wearer, Map<ResourceLocation, Integer> counts)
	{
		counts.keySet().forEach(effectDataId ->
				getRuntimeEffect(wearer.level().registryAccess(), effectDataId).
						ifPresent(effect -> effect.removeAttributes(wearer, effectDataId)));
	}

	private static Map<ResourceLocation, Integer> countActiveEffects(UUID uuid)
	{
		ActiveOrganicArmorEffects active = ACTIVE_EFFECTS.get(uuid);
		return active == null ? Map.of() : countEffects(active.effects());
	}

	private static Map<ResourceLocation, Integer> countEffects(EnumMap<EquipmentSlot, ResourceLocation> effects)
	{
		if (effects.isEmpty())
			return Map.of();

		Map<ResourceLocation, Integer> counts = new LinkedHashMap<>();
		effects.values().forEach(fluidTypeId -> counts.merge(fluidTypeId, 1, Integer :: sum));
		return counts;
	}

	private static @Nullable LivingEntity findLivingEntity(MinecraftServer server, UUID uuid)
	{
		for (ServerLevel level : server.getAllLevels())
		{
			Entity entity = level.getEntity(uuid);
			if (entity instanceof LivingEntity livingEntity)
				return livingEntity;
		}
		return null;
	}

	private static Optional<OrganicArmorFluidEffect> getEffectDefinition(HolderLookup.Provider registries, ResourceLocation effectDataId)
	{
		return registries.lookup(Registration.OrganicArmorReg.EFFECT_DATA_KEY).
				flatMap(registry -> registry.get(ResourceKey.create(Registration.OrganicArmorReg.EFFECT_DATA_KEY, effectDataId))).
				map(Holder.Reference :: value);
	}

	private static Optional<IOrganicArmorEffect> getRuntimeEffect(HolderLookup.Provider registries, ResourceLocation effectDataId)
	{
		return getEffectDefinition(registries, effectDataId).
				map(OrganicArmorFluidEffect :: effect).
				map(ResourceKey :: location).
				map(Registration.OrganicArmorReg.EFFECT_REGISTRY :: get);
	}

	static void replaceModifier(LivingEntity entity, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation)
	{
		AttributeInstance instance = entity.getAttribute(attribute);
		if (instance == null)
			return;

		instance.removeModifier(id);
		if (amount != 0)
			instance.addTransientModifier(new AttributeModifier(id, amount, operation));
	}

	static ResourceLocation modifierId(ResourceLocation effectDataId, String name)
	{
		String effectPath = effectDataId.getNamespace() + "_" + effectDataId.getPath().replace('/', '_');
		return Database.rl("organic_armor").withSuffix("_" + effectPath + "_" + name);
	}

	private record QueuedOrganicArmorEffects(EnumMap<EquipmentSlot, ResourceLocation> effects)
	{
		private QueuedOrganicArmorEffects()
		{
			this(new EnumMap<>(EquipmentSlot.class));
		}
	}

	private static class ActiveOrganicArmorEffects
	{
		private final EnumMap<EquipmentSlot, ResourceLocation> effects = new EnumMap<>(EquipmentSlot.class);
		private long expireAt;

		private EnumMap<EquipmentSlot, ResourceLocation> effects()
		{
			return this.effects;
		}

		private long expireAt()
		{
			return this.expireAt;
		}

		private void setExpireAt(long expireAt)
		{
			this.expireAt = expireAt;
		}
	}
}

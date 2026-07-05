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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class OrganicArmorEffectHandler
{
	private static final int TICK_PERIOD = 5 * 20;
	private static final int FLUID_CONSUME_AMOUNT = 1;
	private static final int EFFECT_DURATION_TICKS = 6 * 20;
	private static final ResourceLocation ACID = Database.rl("acid");
	private static final ResourceLocation ADRENALINE = Database.rl("adrenaline");
	private static final ResourceLocation BIOMASS = Database.rl("biomass");
	private static final Object2ObjectLinkedOpenHashMap<ResourceLocation, FluidArmorEffect> EFFECTS_BY_FLUID = new Object2ObjectLinkedOpenHashMap<>();
	private static final Object2ObjectLinkedOpenHashMap<UUID, QueuedOrganicArmorEffects> EFFECTS_MAP = new Object2ObjectLinkedOpenHashMap<>();
	private static final Object2ObjectLinkedOpenHashMap<UUID, ActiveOrganicArmorEffects> ACTIVE_EFFECTS = new Object2ObjectLinkedOpenHashMap<>();

	static
	{
		registerFluidEffect(ACID, new AcidArmorEffect());
		registerFluidEffect(ADRENALINE, new AdrenalineArmorEffect());
		registerFluidEffect(BIOMASS, new BiomassArmorEffect());
	}

	public static void registerFluidEffect(ResourceLocation fluidTypeId, FluidArmorEffect effect)
	{
		EFFECTS_BY_FLUID.put(fluidTypeId, effect);
	}

	public static boolean hasFluidEffect(FluidStack fluid)
	{
		if (fluid.isEmpty())
			return false;

		ResourceLocation fluidTypeId = getFluidTypeId(fluid.getFluidType());
		return fluidTypeId != null && EFFECTS_BY_FLUID.containsKey(fluidTypeId);
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

			FluidStack drained = fluid.copy();
			drained.shrink(FLUID_CONSUME_AMOUNT);
			updated = updated.with(new OrganicArmorState.Piece(piece.slot(), piece.typeId(), drained));
			changed = true;
			addEffectData(entity, piece.slot(), fluid.getFluidType());
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
			active.effects().clear();
			active.effects().putAll(queued.effects());
			active.setExpireAt(gameTime + EFFECT_DURATION_TICKS);

			Map<ResourceLocation, Integer> counts = countEffects(active.effects());
			rebuildAttributeEffects(wearer, counts);
			counts.forEach((fluidTypeId, count) ->
			{
				FluidArmorEffect effect = EFFECTS_BY_FLUID.get(fluidTypeId);
				if (effect != null)
					effect.applyTick(wearer, fluidTypeId, count, EFFECT_DURATION_TICKS);
			});
		});
		EFFECTS_MAP.clear();
		EFFECTS_MAP.trim();
		clearExpiredEffects(server, gameTime);
	}

	public static void addEffectData(UUID uuid, EquipmentSlot slot, FluidType fluidType)
	{
		ResourceLocation fluidTypeId = getFluidTypeId(fluidType);
		if (fluidTypeId == null)
			return;

		EFFECTS_MAP.computeIfAbsent(uuid, id -> new QueuedOrganicArmorEffects()).
				effects().
				put(slot, fluidTypeId);
	}

	public static void addEffectData(LivingEntity entity, EquipmentSlot slot, FluidType fluidType)
	{
		addEffectData(entity.getUUID(), slot, fluidType);
	}

	public static void removeEffectData(LivingEntity entity, EquipmentSlot slot)
	{
		QueuedOrganicArmorEffects queued = EFFECTS_MAP.get(entity.getUUID());
		if (queued != null && queued.effects().remove(slot) != null && queued.effects().isEmpty())
			EFFECTS_MAP.remove(entity.getUUID());

		ActiveOrganicArmorEffects active = ACTIVE_EFFECTS.get(entity.getUUID());
		if (active == null)
			return;

		if (active.effects().remove(slot) != null)
			rebuildAttributeEffects(entity, countEffects(active.effects()));

		if (active.effects().isEmpty())
			ACTIVE_EFFECTS.remove(entity.getUUID());
	}

	public static void handleIncomingDamage(LivingDamageEvent.Pre event)
	{
		LivingEntity wearer = event.getEntity();
		for (Map.Entry<ResourceLocation, Integer> entry : countActiveEffects(wearer.getUUID()).entrySet())
		{
			FluidArmorEffect effect = EFFECTS_BY_FLUID.get(entry.getKey());
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
			FluidArmorEffect effect = EFFECTS_BY_FLUID.get(entry.getKey());
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
				removeAttributeEffects(wearer);
			return true;
		});
	}

	private static void rebuildAttributeEffects(LivingEntity wearer, Map<ResourceLocation, Integer> counts)
	{
		removeAttributeEffects(wearer);
		counts.forEach((fluidTypeId, count) ->
		{
			FluidArmorEffect effect = EFFECTS_BY_FLUID.get(fluidTypeId);
			if (effect != null)
				effect.applyAttributes(wearer, fluidTypeId, count);
		});
	}

	private static void removeAttributeEffects(LivingEntity wearer)
	{
		EFFECTS_BY_FLUID.forEach((fluidTypeId, effect) -> effect.removeAttributes(wearer, fluidTypeId));
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

	private static @Nullable ResourceLocation getFluidTypeId(FluidType fluidType)
	{
		return NeoForgeRegistries.FLUID_TYPES.getKey(fluidType);
	}

	private static void replaceModifier(LivingEntity entity, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation)
	{
		AttributeInstance instance = entity.getAttribute(attribute);
		if (instance == null)
			return;

		instance.removeModifier(id);
		if (amount != 0)
			instance.addTransientModifier(new AttributeModifier(id, amount, operation));
	}

	private static ResourceLocation modifierId(ResourceLocation fluidTypeId, String name)
	{
		String fluidPath = fluidTypeId.getNamespace() + "_" + fluidTypeId.getPath().replace('/', '_');
		return Database.rl("organic_armor").withSuffix("_" + fluidPath + "_" + name);
	}

	public interface FluidArmorEffect
	{
		default void applyTick(LivingEntity wearer, ResourceLocation fluidTypeId, int count, int duration)
		{
		}

		default void applyAttributes(LivingEntity wearer, ResourceLocation fluidTypeId, int count)
		{
		}

		default void removeAttributes(LivingEntity wearer, ResourceLocation fluidTypeId)
		{
		}

		default void onIncomingDamage(LivingEntity wearer, LivingDamageEvent.Pre event, ResourceLocation fluidTypeId, int count)
		{
		}

		default void onMeleeAttacked(LivingEntity wearer, LivingEntity attacker, DamageSource source, float damage, ResourceLocation fluidTypeId, int count)
		{
		}
	}

	private static class AcidArmorEffect implements FluidArmorEffect
	{
		@Override
		public void onMeleeAttacked(LivingEntity wearer, LivingEntity attacker, DamageSource source, float damage, ResourceLocation fluidTypeId, int count)
		{
			int amplifier = Math.min(count - 1, 3);
			int duration = 60 + count * 20;
			attacker.addEffect(new MobEffectInstance(MobEffects.POISON, duration, amplifier));
			attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier));
		}
	}

	private static class AdrenalineArmorEffect implements FluidArmorEffect
	{
		@Override
		public void applyAttributes(LivingEntity wearer, ResourceLocation fluidTypeId, int count)
		{
			replaceModifier(wearer, Attributes.MOVEMENT_SPEED, modifierId(fluidTypeId, "movement_speed"), 0.04d * count, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
			replaceModifier(wearer, Attributes.ATTACK_SPEED, modifierId(fluidTypeId, "attack_speed"), 0.06d * count, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		}

		@Override
		public void removeAttributes(LivingEntity wearer, ResourceLocation fluidTypeId)
		{
			replaceModifier(wearer, Attributes.MOVEMENT_SPEED, modifierId(fluidTypeId, "movement_speed"), 0, AttributeModifier.Operation.ADD_VALUE);
			replaceModifier(wearer, Attributes.ATTACK_SPEED, modifierId(fluidTypeId, "attack_speed"), 0, AttributeModifier.Operation.ADD_VALUE);
		}
	}

	private static class BiomassArmorEffect implements FluidArmorEffect
	{
		@Override
		public void applyTick(LivingEntity wearer, ResourceLocation fluidTypeId, int count, int duration)
		{
			wearer.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, Math.min(count - 1, 3), true, false));
		}

		@Override
		public void applyAttributes(LivingEntity wearer, ResourceLocation fluidTypeId, int count)
		{
			replaceModifier(wearer, Attributes.KNOCKBACK_RESISTANCE, modifierId(fluidTypeId, "knockback_resistance"), Math.min(0.1d * count, 0.6d), AttributeModifier.Operation.ADD_VALUE);
			replaceModifier(wearer, Attributes.ARMOR_TOUGHNESS, modifierId(fluidTypeId, "armor_toughness"), 0.75d * count, AttributeModifier.Operation.ADD_VALUE);
		}

		@Override
		public void removeAttributes(LivingEntity wearer, ResourceLocation fluidTypeId)
		{
			replaceModifier(wearer, Attributes.KNOCKBACK_RESISTANCE, modifierId(fluidTypeId, "knockback_resistance"), 0, AttributeModifier.Operation.ADD_VALUE);
			replaceModifier(wearer, Attributes.ARMOR_TOUGHNESS, modifierId(fluidTypeId, "armor_toughness"), 0, AttributeModifier.Operation.ADD_VALUE);
		}

		@Override
		public void onIncomingDamage(LivingEntity wearer, LivingDamageEvent.Pre event, ResourceLocation fluidTypeId, int count)
		{
			float reduction = Math.min(0.06f * count, 0.3f);
			event.setNewDamage(event.getNewDamage() * (1f - reduction));
		}
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

/**
 * @author ArcAnc
 * Created at: 31.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.ability.IAbility;
import com.arcanc.biomorphosis.content.event.OverlayRenderHandler;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.AbilityHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.ArrayList;

public record S2CUnlockedAbilities(List<ResourceLocation> abilities) implements IPacket
{
	private static final ResourceLocation MISSING_ABILITY_ICON = ResourceLocation.withDefaultNamespace("textures/item/barrier.png");
	public static final Type<S2CUnlockedAbilities> TYPE = new Type<>(Database.rl("s2c_unlocked_abilities"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CUnlockedAbilities> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.collection(ArrayList :: new, ResourceLocation.STREAM_CODEC),
			S2CUnlockedAbilities :: abilities,
			S2CUnlockedAbilities :: new);

	public S2CUnlockedAbilities
	{
		abilities = List.copyOf(abilities);
	}

	@Override
	public void process(IPayloadContext context)
	{
		context.enqueueWork(() ->
			this.abilities.forEach(abilityId -> AbilityHelper.getAbility(abilityId).ifPresent(ability ->
					OverlayRenderHandler.AdvancementOverlays.addAdvancement(new AbilityAdvancement(abilityId, ability)))));
	}

	@Override
	public Type<S2CUnlockedAbilities> type()
	{
		return TYPE;
	}

	private record AbilityAdvancement(ResourceLocation abilityId, IAbility ability)
			implements OverlayRenderHandler.AdvancementOverlays.AdvancementRenderable
	{
		@Override
		public ResourceLocation getImageLocation()
		{
			return this.ability.getIcon().orElse(MISSING_ABILITY_ICON);
		}

		@Override
		public Component getName()
		{
			String path = this.abilityId.getPath().replace('_', ' ');
			return Component.literal(Character.toUpperCase(path.charAt(0)) + path.substring(1));
		}

		@Override
		public Component getAdditionalInfo()
		{
			return Component.translatable(Database.GUI.Overlays.Advancements.RECEIVE_ABILITY).append(getName());
		}
	}
}

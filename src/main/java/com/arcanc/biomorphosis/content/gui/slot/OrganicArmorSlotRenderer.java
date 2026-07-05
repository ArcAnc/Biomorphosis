/**
 * @author ArcAnc
 * Created at: 19.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.slot;

import com.arcanc.biomorphosis.content.gui.component.tooltip.TooltipBorderHandler;
import com.arcanc.biomorphosis.content.gui.component.tooltip.TooltipData;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorHelper;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorState;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorType;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganicArmorSlotRenderer
{
	private static final int SLOT_SIZE = 16;
	private static final int BAR_WIDTH = 12;
	private static final int BAR_HEIGHT = 2;
	private static final int BAR_BACKGROUND = MathHelper.ColorHelper.color(220, 18, 12, 22);
	private static final int EMPTY_BAR = MathHelper.ColorHelper.color(255, 66, 42, 76);
	private static final TooltipData TOOLTIP_STYLE = new TooltipData(
			true,
			Database.GUI.Textures.Tooltip.TOOLTIP_BACKGROUND,
			Database.GUI.Textures.Tooltip.TOOLTIP_DECORATIONS,
			true);

	public static boolean shouldReplace(Slot slot)
	{
		OrganicArmorSlotReplacement organicSlot = getOrganicArmorSlot(slot);
		return organicSlot != null && hasOrganicArmor(organicSlot);
	}

	public static void render(GuiGraphics guiGraphics, Slot slot)
	{
		OrganicArmorSlotReplacement organicSlot = getOrganicArmorSlot(slot);
		if (organicSlot == null)
			return;

		int x = slot.x;
		int y = slot.y;
		OrganicArmorData data = resolveData(organicSlot);

		guiGraphics.blit(x, y, 0, SLOT_SIZE, SLOT_SIZE, RenderHelper.mc().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(OrganicArmorSlotReplacement.BLOCKED_SLOT));
		renderFluidBar(guiGraphics, data.tank(), x + 2, y + 13);
	}

	public static List<Component> tooltip(Slot slot)
	{
		OrganicArmorSlotReplacement organicSlot = getOrganicArmorSlot(slot);
		if (organicSlot == null)
			return List.of();

		OrganicArmorData data = resolveData(organicSlot);
		List<Component> tooltip = new ArrayList<>();

		tooltip.add(Component.translatable(Database.GUI.OrganicArmor.NAME.apply(data.typeId())).
				withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN.getColor())));
		tooltip.add(Component.translatable(
				Database.GUI.OrganicArmor.Tooltip.STATS,
				Component.literal(Integer.toString(data.armor())).withStyle(ChatFormatting.GREEN),
				Component.literal(Integer.toString(data.capacity())).withStyle(ChatFormatting.AQUA)).
				withStyle(ChatFormatting.GRAY));

		FluidTank tank = data.tank();
		FluidStack stack = tank.getFluid();
		if (stack.isEmpty())
		{
			tooltip.add(Component.translatable(Database.GUI.OrganicArmor.Tooltip.EMPTY).withStyle(ChatFormatting.DARK_GRAY));
			return tooltip;
		}

		IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(stack.getFluid());
		Style fluidStyle = Style.EMPTY.withColor(renderProps.getTintColor());
		tooltip.add(Component.empty());
		tooltip.add(Component.translatable(stack.getDescriptionId()).withStyle(fluidStyle));
		tooltip.add(Component.translatable(
				Database.GUI.InfoArea.FluidArea.Tooltip.NORMAL_EXTENDED_TOOLTIP,
				Component.literal(Integer.toString(stack.getAmount())).withStyle(fluidStyle),
				Component.literal(Integer.toString(tank.getCapacity())).withStyle(fluidStyle)).
				withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(Database.GUI.OrganicArmor.Tooltip.EFFECTS).withStyle(ChatFormatting.DARK_GREEN));
		tooltip.add(Component.translatable(Database.GUI.OrganicArmor.Tooltip.FLUID_EFFECT.apply(getFluidId(stack))).withStyle(ChatFormatting.GRAY));
		return tooltip;
	}

	public static void renderTooltip(GuiGraphics guiGraphics, Font font, Slot slot, int x, int y)
	{
		TooltipBorderHandler.pushTooltipStyle(TOOLTIP_STYLE);
		try
		{
			guiGraphics.renderTooltip(font, tooltip(slot), Optional.empty(), x, y);
		}
		finally
		{
			TooltipBorderHandler.popTooltipStyle();
		}
	}

	private static void renderFluidBar(GuiGraphics guiGraphics, FluidTank tank, int x, int y)
	{
		guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, BAR_BACKGROUND);

		FluidStack stack = tank.getFluid();
		if (stack.isEmpty() || tank.getCapacity() <= 0)
		{
			guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, EMPTY_BAR);
			return;
		}

		int width = Math.max(1, Math.round(BAR_WIDTH * (stack.getAmount() / (float)tank.getCapacity())));
		IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(stack.getFluid());
		TextureAtlasSprite still = RenderHelper.mc().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).
				apply(renderProps.getStillTexture());
		int color = MathHelper.ColorHelper.opaque(renderProps.getTintColor());
		guiGraphics.blit(
				x,
				y,
				0,
				width,
				BAR_HEIGHT,
				still,
				MathHelper.ColorHelper.red(color) / 255f,
				MathHelper.ColorHelper.green(color) / 255f,
				MathHelper.ColorHelper.blue(color) / 255f,
				MathHelper.ColorHelper.alpha(color) / 255f);
	}

	private static boolean hasOrganicArmor(OrganicArmorSlotReplacement slot)
	{
		return OrganicArmorHelper.hasArmor(slot.getOwner(), slot.getEquipmentSlot());
	}

	private static @Nullable OrganicArmorSlotReplacement getOrganicArmorSlot(Slot slot)
	{
		if (slot instanceof OrganicArmorSlotReplacement organicSlot)
			return organicSlot;
		if (slot instanceof CreativeModeInventoryScreen.SlotWrapper wrapper && wrapper.target instanceof OrganicArmorSlotReplacement organicSlot)
			return organicSlot;
		return null;
	}

	private static OrganicArmorData resolveData(OrganicArmorSlotReplacement slot)
	{
		LivingEntity owner = slot.getOwner();
		EquipmentSlot equipmentSlot = slot.getEquipmentSlot();
		OrganicArmorState.Piece piece = OrganicArmorHelper.getPiece(owner, equipmentSlot).orElse(null);
		if (piece == null)
			return OrganicArmorData.empty();

		OrganicArmorType type = findOrganicArmorType(piece);
		if (type == null)
			return new OrganicArmorData(piece.typeId(), 0, 0, new FluidTank(0));

		FluidTank tank = new FluidTank(type.capacity());
		tank.fill(piece.fluid(), IFluidHandler.FluidAction.EXECUTE);
		return new OrganicArmorData(piece.typeId(), piece.hasFluid() ? type.armor() : type.drainedArmor(), type.capacity(), tank);
	}

	private static @Nullable OrganicArmorType findOrganicArmorType(OrganicArmorState.Piece piece)
	{
		Minecraft minecraft = RenderHelper.mc();
		ClientPacketListener listener = minecraft.getConnection();
		if (listener == null)
			return null;

		return listener.registryAccess().
				lookup(Registration.OrganicArmorReg.TYPE_KEY).
				flatMap(registry -> registry.get(ResourceKey.create(Registration.OrganicArmorReg.TYPE_KEY, piece.typeId()))).
				map(net.minecraft.core.Holder.Reference :: value).
				orElse(null);
	}

	private static ResourceLocation getFluidId(FluidStack stack)
	{
		ResourceLocation id = net.neoforged.neoforge.registries.NeoForgeRegistries.FLUID_TYPES.getKey(stack.getFluid().getFluidType());
		return id == null ? ResourceLocation.withDefaultNamespace("empty") : id;
	}

	private record OrganicArmorData(ResourceLocation typeId, int armor, int capacity, FluidTank tank)
	{
		private static OrganicArmorData empty()
		{
			return new OrganicArmorData(Database.rl("empty"), 0, 0, new FluidTank(0));
		}
	}
}

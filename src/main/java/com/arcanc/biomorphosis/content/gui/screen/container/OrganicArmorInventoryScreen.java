/**
 * @author ArcAnc
 * Created at: 27.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen.container;


import com.arcanc.biomorphosis.content.gui.component.FittingMultiLineText;
import com.arcanc.biomorphosis.content.gui.container_menu.OrganicArmorInventoryMenu;
import com.arcanc.biomorphosis.content.gui.slot.OrganicArmorSlotRenderer;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorHelper;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorState;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorType;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrganicArmorInventoryScreen extends BioContainerScreen<OrganicArmorInventoryMenu>
{
	private static final ResourceLocation SLIME_BACKGROUND = Database.rl("textures/gui/slime_background.png");
	private static final int BACKGROUND_TEXTURE_SIZE = 512;
	private static final int BACKGROUND_SOURCE_SIZE = 448;
	private static final int BACKGROUND_OVERDRAW_X = 18;
	private static final int BACKGROUND_OVERDRAW_Y = 12;
	private static final float BACKGROUND_PARALLAX_X = 10f;
	private static final float BACKGROUND_PARALLAX_Y = 7f;
	
	private static final int MODEL_X = 73;
	private static final int MODEL_Y = 15;
	private static final int MODEL_WIDTH = 58;
	private static final int MODEL_HEIGHT = 76;
	private static final int EFFECT_X = 190;
	private static final int EFFECT_Y = 17;
	private static final int EFFECT_WIDTH = 130;
	private static final int EFFECT_HEIGHT = 150;
	private static final int PANEL = MathHelper.ColorHelper.color(190, 14, 18, 15);
	private static final int PANEL_BORDER = MathHelper.ColorHelper.color(255, 66, 103, 73);
	private static final int TEXT = MathHelper.ColorHelper.color(255, 218, 232, 214);
	private static final int MUTED_TEXT = MathHelper.ColorHelper.color(255, 119, 139, 122);
	private @Nullable FittingMultiLineText effectsText;

	public OrganicArmorInventoryScreen(OrganicArmorInventoryMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		this.imageWidth = 326;
		this.imageHeight = 184;
		this.inventoryLabelY = 84;
	}
	
	@Override
	protected void init()
	{
		super.init();
		this.effectsText = addRenderableWidget(new FittingMultiLineText(
				this.getGuiLeft() + EFFECT_X + 6,
				this.getGuiTop() + EFFECT_Y + 22,
				EFFECT_WIDTH - 12,
				EFFECT_HEIGHT - 28,
				Component.empty(),
				this.font).setColor(MUTED_TEXT));
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		updateEffectsText();
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (this.effectsText != null && this.effectsText.mouseClicked(mouseX, mouseY, button))
		{
			this.setFocused(this.effectsText);
			if (button == 0)
				this.setDragging(true);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
	{
		if (this.effectsText != null && this.effectsText.isFocused() && this.effectsText.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
	{
		renderMovingBackground(guiGraphics, mouseX, mouseY);

		int left = this.getGuiLeft();
		int top = this.getGuiTop();
		renderPanel(guiGraphics, left + MODEL_X, top + MODEL_Y, MODEL_WIDTH, MODEL_HEIGHT);
		renderPanel(guiGraphics, left + EFFECT_X, top + EFFECT_Y, EFFECT_WIDTH, EFFECT_HEIGHT);
		InventoryScreen.renderEntityInInventoryFollowsMouse(
				guiGraphics,
				left + MODEL_X,
				top + MODEL_Y,
				left + MODEL_X + MODEL_WIDTH,
				top + MODEL_Y + MODEL_HEIGHT,
				30,
				0.0625F,
				mouseX,
				mouseY,
				this.minecraft.player);
	}

	private void renderMovingBackground(GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
		int left = this.getGuiLeft();
		int top = this.getGuiTop();
		float centerX = left + getXSize() / 2f;
		float centerY = top + getYSize() / 2f;
		float mouseOffsetX = Mth.clamp((mouseX - centerX) / (getXSize() / 2f), -1f, 1f);
		float mouseOffsetY = Mth.clamp((mouseY - centerY) / (getYSize() / 2f), -1f, 1f);

		float targetWidth = getXSize() + BACKGROUND_OVERDRAW_X * 2f;
		float targetHeight = getYSize() + BACKGROUND_OVERDRAW_Y * 2f;
		float coverSize = Math.max(targetWidth, targetHeight);
		float x = left + (getXSize() - coverSize) / 2f + mouseOffsetX * BACKGROUND_PARALLAX_X;
		float y = top + (getYSize() - coverSize) / 2f + mouseOffsetY * BACKGROUND_PARALLAX_Y;
		float sourceOffset = (BACKGROUND_TEXTURE_SIZE - BACKGROUND_SOURCE_SIZE) / 2f;

		RenderHelper.mc().getTextureManager().getTexture(SLIME_BACKGROUND).setFilter(true, false);
		guiGraphics.enableScissor(left, top, left + getXSize(), top + getYSize());
		RenderHelper.blit(guiGraphics,
				SLIME_BACKGROUND,
				x, y,
				sourceOffset, sourceOffset,
				coverSize, coverSize,
				0,
				BACKGROUND_SOURCE_SIZE, BACKGROUND_SOURCE_SIZE,
				BACKGROUND_TEXTURE_SIZE, BACKGROUND_TEXTURE_SIZE);
		guiGraphics.disableScissor();
	}
	
	@Override
	protected void renderSlot(GuiGraphics guiGraphics, Slot slot)
	{
		if (OrganicArmorSlotRenderer.shouldReplace(slot))
		{
			OrganicArmorSlotRenderer.render(guiGraphics, slot);
			return;
		}
		super.renderSlot(guiGraphics, slot);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
		guiGraphics.drawString(
				this.font,
				Component.translatable(Database.GUI.OrganicArmorInventory.EFFECTS),
				EFFECT_X + 8,
				EFFECT_Y + 8,
				TEXT,
				false);
	}

	@Override
	public void onClose()
	{
		super.onClose();
		if (this.minecraft != null && this.minecraft.player != null)
			this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
	}

	private void updateEffectsText()
	{
		if (this.effectsText == null)
			return;
		this.effectsText.setMessage(buildEffectsText());
	}

	private MutableComponent buildEffectsText()
	{
		List<OrganicArmorState.Piece> pieces = new ArrayList<>(OrganicArmorHelper.getState(this.minecraft.player).pieces());
		pieces.sort(Comparator.comparingInt(piece -> armorOrder(piece.slot())));
		if (pieces.isEmpty())
			return Component.translatable(Database.GUI.OrganicArmorInventory.NO_ARMOR).withColor(MUTED_TEXT);

		MutableComponent text = Component.empty();
		for (int q = 0; q < pieces.size(); q++)
		{
			OrganicArmorState.Piece piece = pieces.get(q);

			OrganicArmorType type = findOrganicArmorType(piece);
			int armor = type == null ? 0 : OrganicArmorHelper.getEffectiveArmor(this.minecraft.player.level(), piece);
			text.append(Component.translatable(Database.GUI.OrganicArmor.NAME.apply(piece.typeId())).withColor(TEXT)).
					append("\n").
					append(Component.translatable(Database.GUI.OrganicArmorInventory.ARMOR, armor).withColor(MUTED_TEXT)).
					append("\n");

			FluidStack fluid = piece.fluid();
			if (fluid.isEmpty())
			{
				text.append(Component.translatable(Database.GUI.OrganicArmor.Tooltip.EMPTY).withColor(MUTED_TEXT));
			}
			else
			{
				int fluidColor = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor();
				text.append(Component.translatable(fluid.getDescriptionId()).withColor(fluidColor)).
						append("\n").
						append(Component.translatable(Database.GUI.OrganicArmor.Tooltip.FLUID_EFFECT.apply(getFluidId(fluid))).withColor(MUTED_TEXT));
			}

			if (q < pieces.size() - 1)
				text.append("\n\n");
		}
		return text;
	}

	private static void renderPanel(GuiGraphics guiGraphics, int x, int y, int width, int height)
	{
		guiGraphics.fill(x, y, x + width, y + height, PANEL);
		guiGraphics.renderOutline(x, y, width, height, PANEL_BORDER);
	}

	private @Nullable OrganicArmorType findOrganicArmorType(OrganicArmorState.Piece piece)
	{
		ClientPacketListener listener = this.minecraft.getConnection();
		if (listener == null)
			return null;

		return listener.registryAccess().
				lookup(Registration.OrganicArmorReg.TYPE_KEY).
				flatMap(registry -> registry.get(ResourceKey.create(Registration.OrganicArmorReg.TYPE_KEY, piece.typeId()))).
				map(Holder.Reference :: value).
				orElse(null);
	}

	private static ResourceLocation getFluidId(FluidStack stack)
	{
		ResourceLocation id = NeoForgeRegistries.FLUID_TYPES.getKey(stack.getFluid().getFluidType());
		return id == null ? ResourceLocation.withDefaultNamespace("empty") : id;
	}

	private static int armorOrder(EquipmentSlot slot)
	{
		return switch (slot)
		{
			case HEAD -> 0;
			case CHEST -> 1;
			case LEGS -> 2;
			case FEET -> 3;
			default -> 4;
		};
	}
}

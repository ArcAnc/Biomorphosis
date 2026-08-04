/**
 * @author ArcAnc
 * Created at: 16.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen.container;


import com.arcanc.biomorphosis.content.ability.AbilityLoadout;
import com.arcanc.biomorphosis.content.ability.IAbility;
import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChrysalis;
import com.arcanc.biomorphosis.content.gui.component.*;
import com.arcanc.biomorphosis.content.gui.component.info.ErrorInfoArea;
import com.arcanc.biomorphosis.content.gui.component.info.FluidInfoArea;
import com.arcanc.biomorphosis.content.gui.component.info.GenomeStabilityInfoArea;
import com.arcanc.biomorphosis.content.gui.container_menu.ChrysalisMenu;
import com.arcanc.biomorphosis.content.gui.slot.OrganicArmorSlotRenderer;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorHelper;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.AbilityHelper;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;

public class ChrysalisScreen extends BioContainerScreen<ChrysalisMenu>
{
	private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	private Tab activeTab = Tab.MUTATION;
	private final Player player;
	private final Button[] armorActionButtons = new Button[ARMOR_SLOTS.length];
	
	private GeneChooser chooser;
	private OwnedGeneList ownedGeneList;
	private OwnedRarityList ownedRarityList;
	private GenomeStabilityInfoArea stabilityInfoArea;
	private ErrorInfoArea errorInfoArea;
	private AbilityLoadout abilityLoadout;
	private int selectedAbilitySlot = -1;
	
	public ChrysalisScreen(ChrysalisMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		this.imageHeight = 184;
		this.imageWidth = 250;
		this.player = playerInventory.player;
	}
	
	@Override
	protected void init()
	{
		super.init();
		addTabButtons();
		this.menu.setOrganicArmorTabActive(this.activeTab == Tab.ORGANIC_ARMOR);
		if (this.activeTab == Tab.ORGANIC_ARMOR)
		{
			initOrganicArmorTab();
			return;
		}
		else if (this.activeTab == Tab.ABILITIES)
		{
			initAbilitiesTab();
			return;
		}

		addInfoArea(this.errorInfoArea = new ErrorInfoArea(new Rect2i(
				this.getGuiLeft() + 11,
				this.getGuiTop() + 75,
				125,
				20)));
		addRenderableWidget(this.chooser = new GeneChooser(
				this.getGuiLeft() + 9,
				this.getGuiTop() + 26,
				156,
				110,
				this.player, this.errorInfoArea));
		addInfoArea(this.stabilityInfoArea = new GenomeStabilityInfoArea(new Rect2i(
				this.getGuiLeft() + 9,
				this.getGuiTop() + 10,
				62,
				10), this.chooser));
		addRenderableWidget(this.ownedRarityList = new OwnedRarityList(this.minecraft,
				this.getGuiLeft() + 191,
				this.getGuiTop() + 85,
				50,
				82,
				18));
		addRenderableWidget(this.ownedGeneList = new OwnedGeneList(this.minecraft,
				this.getGuiLeft() + 138,
				this.getGuiTop() + 85,
				50,
				82,
				18,
				this.ownedRarityList));
		
		addRenderableWidget(new TexturedButton(
				this.getGuiLeft() + 66,
				this.getGuiTop() + 151,
				16, 16,
				new WidgetSprites(
						Database.rl("textures/gui/elements/buttons/add.png"),
						Database.rl("textures/gui/elements/buttons/add_disabled.png")),
				button ->
				{
					OwnedGeneList.OwnedGeneEntry entry = this.ownedGeneList.getSelected();
					if (entry == null)
						return;
					
					ResourceLocation id = entry.getValue();
					OwnedRarityList.OwnedRarityEntry rarityEntry = this.ownedRarityList.getSelected();
					if (rarityEntry == null)
						return;
					
					GeneInstance gene = new GeneInstance(id, rarityEntry.getValue());
					
					if (this.chooser.canAddGene(gene))
						this.chooser.addGene(gene);
				},
				Tooltip.create(Component.literal("Add Gene"))));
		
		addRenderableWidget(new TexturedButton(
				this.getGuiLeft() + 36,
				this.getGuiTop() + 151,
				16, 16,
				new WidgetSprites(
						Database.rl("textures/gui/elements/buttons/remove.png"),
						Database.rl("textures/gui/elements/buttons/remove_disabled.png")),
						button -> this.chooser.removeGene(),
				Tooltip.create(Component.literal("Remove gene")))
		);
		
			addRenderableWidget(new TexturedButton(
					this.getGuiLeft() + 95,
					this.getGuiTop() + 151,
					16, 16,
					new WidgetSprites(
							Database.rl("textures/gui/elements/buttons/ok.png"),
							Database.rl("textures/gui/elements/buttons/ok_disabled.png")),
					button ->
					{
						if (!this.chooser.canUseGenome())
							return;
						sendUpdateToServer(tag -> GenomeInstance.CODEC.
								encodeStart(NbtOps.INSTANCE, this.chooser.getGenome()).
								map(written -> tag.put("genome", written)));
					},
					Tooltip.create(Component.literal("Start Mutation"))));
		
		BlockHelper.castTileEntity(this.minecraft.level, this.menu.getBlockPos(), MultiblockChrysalis.class).ifPresent(chrysalis ->
		{
			FluidSidedStorage handler = MultiblockChrysalis.getFluidHandler(chrysalis, null);
			
			if (handler == null)
				return;
			handler.getHolderAt(null, 0).ifPresent(biomass ->
					addInfoArea(new FluidInfoArea(biomass, new Rect2i(this.getGuiLeft() + 171, this.getGuiTop() + 17, 21, 46))));
			handler.getHolderAt(null, 1).ifPresent(acid ->
					addInfoArea(new FluidInfoArea(acid, new Rect2i(this.getGuiLeft() + 196, this.getGuiTop() + 17, 21, 46))));
			handler.getHolderAt(null, 2).ifPresent(adrenaline ->
					addInfoArea(new FluidInfoArea(adrenaline, new Rect2i(this.getGuiLeft() + 220, this.getGuiTop() + 17, 21, 46))));
		});
	}

	private void addTabButtons()
	{
		addRenderableWidget(Button.builder(Component.literal("Mutation"), button ->
		{
			this.activeTab = Tab.MUTATION;
			this.rebuildWidgets();
		}).bounds(this.getGuiLeft() + 8, this.getGuiTop() - 18, 70, 18).build());
		addRenderableWidget(Button.builder(Component.literal("Armor"), button ->
		{
			this.activeTab = Tab.ORGANIC_ARMOR;
			this.rebuildWidgets();
		}).bounds(this.getGuiLeft() + 80, this.getGuiTop() - 18, 58, 18).build());
		addRenderableWidget(Button.builder(Component.literal("Abilities"), button ->
		{
			this.activeTab = Tab.ABILITIES;
			this.selectedAbilitySlot = -1;
			this.rebuildWidgets();
		}).bounds(this.getGuiLeft() + 140, this.getGuiTop() - 18, 70, 18).build());
	}

	private void initAbilitiesTab()
	{
		if (this.abilityLoadout == null)
			this.abilityLoadout = AbilityHelper.getLoadout(this.player);

		int left = this.getGuiLeft();
		int top = this.getGuiTop();
		if (this.selectedAbilitySlot >= 0)
		{
			initAbilitySelection(left, top);
			return;
		}

		for (int slot = 0; slot < AbilityLoadout.SLOT_COUNT; slot++)
		{
			final int abilitySlot = slot;
			addRenderableWidget(Button.builder(getAbilitySlotLabel(abilitySlot), button ->
			{
				this.selectedAbilitySlot = this.selectedAbilitySlot == abilitySlot ? -1 : abilitySlot;
				this.rebuildWidgets();
			}).bounds(left + 10, top + 20 + slot * 22, 112, 20).build());
		}

	}

	private void initAbilitySelection(int left, int top)
	{
		List<ResourceLocation> availableAbilities = this.abilityLoadout.unlockedAbilities().stream().
				filter(abilityId -> Registration.AbilityReg.ABILITY_REGISTRY.containsKey(abilityId)).
				sorted(Comparator.comparing(ResourceLocation :: toString)).toList();
		addRenderableWidget(Button.builder(Component.literal("Back"), button ->
		{
			this.selectedAbilitySlot = -1;
			this.rebuildWidgets();
		}).bounds(left + 10, top + 20, 112, 20).build());
		addRenderableWidget(new AbilitySelectionList(this.minecraft, left + 10, top + 45, 112,
				Math.min(72, Math.max(18, availableAbilities.size() * 18)), 18, availableAbilities,
				this :: selectAbility));
	}

	private Component getAbilitySlotLabel(int slot)
	{
		return this.abilityLoadout.getSlot(slot).
				map(AbilitySelectionList :: formatAbilityName).
				map(name -> Component.literal((slot + 1) + ":  " + name)).
				orElseGet(() -> Component.literal((slot + 1) + ": Empty"));
	}

	private void selectAbility(ResourceLocation abilityId)
	{
		this.abilityLoadout = moveAbilityToSlot(this.abilityLoadout, this.selectedAbilitySlot, abilityId);
		sendUpdateToServer(tag ->
		{
			tag.putInt("ability_slot", this.selectedAbilitySlot);
			tag.putString("ability_id", abilityId.toString());
		});
		this.selectedAbilitySlot = -1;
		this.rebuildWidgets();
	}

	private static AbilityLoadout moveAbilityToSlot(AbilityLoadout loadout, int slot, ResourceLocation abilityId)
	{
		AbilityLoadout updated = loadout;
		for (int currentSlot = 0; currentSlot < AbilityLoadout.SLOT_COUNT; currentSlot++)
			if (updated.getSlot(currentSlot).filter(abilityId :: equals).isPresent())
				updated = updated.withoutSlot(currentSlot);

		return updated.withSlot(slot, abilityId);
	}

	private void initOrganicArmorTab()
	{
		int left = this.getGuiLeft();
		int top = this.getGuiTop();
		int[] buttonX = {20, 20, 186, 186};
		int[] buttonY = {33, 72, 33, 72};
		for (int q = 0; q < ARMOR_SLOTS.length; q++)
		{
			EquipmentSlot slot = ARMOR_SLOTS[q];
			Button button = Button.builder(Component.empty(), btn ->
					sendUpdateToServer(tag ->
					{
						tag.putString("organic_armor_slot", slot.getName());
						tag.putString("organic_armor_action", (OrganicArmorHelper.hasArmor(this.player, slot) ?
								MultiblockChrysalis.ArmorAction.UNEQUIP :
								MultiblockChrysalis.ArmorAction.EQUIP).name());
					})).
					bounds(left + buttonX[q], top + buttonY[q], 44, 16).
					build();
			this.armorActionButtons[q] = button;
			addRenderableWidget(button);
		}
		updateOrganicArmorButtons();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		if (this.activeTab == Tab.ORGANIC_ARMOR)
			updateOrganicArmorButtons();
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	private void updateOrganicArmorButtons()
	{
		for (int q = 0; q < ARMOR_SLOTS.length; q++)
		{
			Button button = this.armorActionButtons[q];
			if (button == null)
				continue;

			EquipmentSlot slot = ARMOR_SLOTS[q];
			ItemStack stack = this.player.getItemBySlot(slot);
			boolean installed = OrganicArmorHelper.hasArmor(this.player, slot);
			button.setMessage(Component.literal(installed ? "Remove" : "Equip"));
			button.active = installed || !stack.isEmpty();
			button.setTooltip(Tooltip.create(installed ?
					Component.literal("Remove organic armor") :
					stack.isEmpty() ? Component.literal("No armor equipped") : stack.getHoverName()));
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
	{
		super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
		if (this.activeTab == Tab.ABILITIES)
		{
			renderAbilityPlayer(guiGraphics, mouseX, mouseY);
			return;
		}
		else if (this.activeTab != Tab.ORGANIC_ARMOR)
			return;

		int left = this.getGuiLeft();
		int top = this.getGuiTop();
		InventoryScreen.renderEntityInInventoryFollowsMouse(
				guiGraphics,
				left + 84,
				top + 15,
				left + 166,
				top + 97,
				30,
				0.0625F,
				mouseX,
				mouseY,
				this.player);
	}

	private void renderAbilityPlayer(GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
		int left = this.getGuiLeft();
		int top = this.getGuiTop();
		InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics,
				left + 141, top + 15, left + 239, top + 130, 42, 0.0625F, mouseX, mouseY, this.player);

		int barX = left + 132;
		int barY = top + 139;
		int barWidth = 108;
		int filled = Math.round(barWidth * this.player.experienceProgress);
		guiGraphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + 7, 0xFF1D4B26);
		guiGraphics.fill(barX, barY, barX + barWidth, barY + 6, 0xFF162018);
		guiGraphics.fill(barX, barY, barX + filled, barY + 6, 0xFF38B541);
		guiGraphics.drawCenteredString(this.minecraft.font, Component.literal("Level " + this.player.experienceLevel),
				barX + barWidth / 2, barY + 11, 0xFFFFFFFF);
	}
	
	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
		if (this.activeTab != Tab.MUTATION)
			return;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(138, 72, 0);
		guiGraphics.pose().scale(0.7f, 0.7f, 1);
		guiGraphics.drawString(
				this.minecraft.font,
				Component.literal("Owned Genes:"),
				0,
				0,
				-1,
				false);
		guiGraphics.pose().popPose();
		
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(191, 72, 0);
		guiGraphics.pose().scale(0.7f, 0.7f, 1);
		guiGraphics.drawString(
				this.minecraft.font,
				Component.literal("Owned Rarities:"),
				0,
				0,
				-1,
				false);
		guiGraphics.pose().popPose();
	}

	@Override
	protected void renderBeforeTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		super.renderBeforeTooltips(guiGraphics, mouseX, mouseY, partialTick);
		if (this.activeTab != Tab.ABILITIES || this.selectedAbilitySlot >= 0)
			return;

		int left = this.getGuiLeft();
		int top = this.getGuiTop();
		for (int slot = 0; slot < AbilityLoadout.SLOT_COUNT; slot++)
		{
			final int iconY = top + 22 + slot * 22;
			this.abilityLoadout.getSlot(slot).
					flatMap(AbilityHelper :: getAbility).
					flatMap(IAbility :: getIcon).
					ifPresent(icon -> RenderHelper.blit(guiGraphics, icon,
							left + 12, iconY, 0, 0, 16, 16, 0, 16, 16, 16, 16));
		}
	}
	
	@Override
	protected void renderSlot(GuiGraphics guiGraphics, Slot slot)
	{
		super.renderSlot(guiGraphics, slot);
		if (OrganicArmorSlotRenderer.shouldReplace(slot))
			OrganicArmorSlotRenderer.render(guiGraphics, slot);
	}
	
	private enum Tab
	{
		MUTATION,
		ORGANIC_ARMOR,
		ABILITIES
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double p_364436_, double p_364417_)
	{
		return super.mouseScrolled(mouseX, mouseY, p_364436_, p_364417_) ||
				this.getChildAt(mouseX, mouseY).filter(p_293596_ -> p_293596_.mouseScrolled(mouseX, mouseY, p_364436_, p_364417_)).isPresent();
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
	{
		return this.getChildAt(mouseX, mouseY).filter(child -> child.mouseDragged(mouseX, mouseY, button, dragX, dragY)).isPresent() ||
				super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}
}

/**
 * @author ArcAnc
 * Created at: 16.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen.container;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockChrysalis;
import com.arcanc.biomorphosis.content.gui.component.GeneChooser;
import com.arcanc.biomorphosis.content.gui.component.OwnedGeneList;
import com.arcanc.biomorphosis.content.gui.component.OwnedRarityList;
import com.arcanc.biomorphosis.content.gui.component.TexturedButton;
import com.arcanc.biomorphosis.content.gui.component.info.ErrorInfoArea;
import com.arcanc.biomorphosis.content.gui.component.info.FluidInfoArea;
import com.arcanc.biomorphosis.content.gui.component.info.GenomeStabilityInfoArea;
import com.arcanc.biomorphosis.content.gui.container_menu.ChrysalisMenu;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.organic_armor.OrganicArmorHelper;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ChrysalisScreen extends BioContainerScreen<ChrysalisMenu>
{
	private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	private Tab activeTab = Tab.MUTATION;
	private final Player player;
	
	private GeneChooser chooser;
	private OwnedGeneList ownedGeneList;
	private OwnedRarityList ownedRarityList;
	private GenomeStabilityInfoArea stabilityInfoArea;
	private ErrorInfoArea errorInfoArea;
	
	public ChrysalisScreen(ChrysalisMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		this.imageHeight = 176;
		this.imageWidth = 250;
		this.player = playerInventory.player;
	}
	
	@Override
	protected void init()
	{
		super.init();
		addTabButtons();
		if (this.activeTab == Tab.ORGANIC_ARMOR)
		{
			initOrganicArmorTab();
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
	}

	private void initOrganicArmorTab()
	{
		int x = this.getGuiLeft() + 26;
		int y = this.getGuiTop() + 34;
		for (int q = 0; q < ARMOR_SLOTS.length; q++)
		{
			EquipmentSlot slot = ARMOR_SLOTS[q];
			ItemStack stack = this.player.getItemBySlot(slot);
			boolean installed = OrganicArmorHelper.hasArmor(this.player, slot);
			boolean canInstall = !installed && !stack.isEmpty();
			MultiblockChrysalis.ArmorAction action = installed ?
					MultiblockChrysalis.ArmorAction.UNEQUIP :
					MultiblockChrysalis.ArmorAction.EQUIP;
			Button button = Button.builder(Component.literal(slot.getName()), btn ->
					sendUpdateToServer(tag ->
					{
						tag.putString("organic_armor_slot", slot.getName());
						tag.putString("organic_armor_action", action.name());
					})).
					bounds(x, y + q * 24, 96, 20).
					build();
			button.active = installed || canInstall;
			button.setTooltip(Tooltip.create(installed ?
					Component.literal("Remove organic armor") :
					stack.isEmpty() ? Component.literal("No armor equipped") : stack.getHoverName()));
			addRenderableWidget(button);
		}
	}
	
	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
		if (this.activeTab == Tab.ORGANIC_ARMOR)
		{
			guiGraphics.drawString(this.minecraft.font, Component.literal("Organic Armor"), 26, 16, -1, false);
			for (int q = 0; q < ARMOR_SLOTS.length; q++)
			{
				EquipmentSlot slot = ARMOR_SLOTS[q];
				ItemStack stack = this.player.getItemBySlot(slot);
				Component status = OrganicArmorHelper.hasArmor(this.player, slot) ?
						Component.literal("installed") :
						stack.isEmpty() ? Component.literal("empty") : stack.getHoverName();
				guiGraphics.drawString(this.minecraft.font, status, 130, 39 + q * 24, -1, false);
			}
			return;
		}

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

	private enum Tab
	{
		MUTATION,
		ORGANIC_ARMOR
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

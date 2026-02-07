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
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ChrysalisScreen extends BioContainerScreen<ChrysalisMenu>
{
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
						sendUpdateToServer(tag -> GenomeInstance.CODEC.
								encodeStart(NbtOps.INSTANCE, this.chooser.getGenome()).
								map(written -> tag.put("genome", written))),
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
	
	@Override
	protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
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

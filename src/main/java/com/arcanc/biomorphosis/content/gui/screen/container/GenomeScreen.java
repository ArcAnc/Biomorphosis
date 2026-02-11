/**
 * @author ArcAnc
 * Created at: 08.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen.container;


import com.arcanc.biomorphosis.content.gui.component.GeneChooser;
import com.arcanc.biomorphosis.content.gui.component.OwnedGeneList;
import com.arcanc.biomorphosis.content.gui.component.OwnedRarityList;
import com.arcanc.biomorphosis.content.gui.component.info.ErrorInfoArea;
import com.arcanc.biomorphosis.content.gui.component.info.GenomeStabilityInfoArea;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.C2SAddUnlockedGenes;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenomeScreen extends Screen
{
	private final LivingEntity entity;
	private final Player player;
	
	private GeneChooser chooser;
	private OwnedGeneList ownedGeneList;
	private OwnedRarityList ownedRarityList;
	private GenomeStabilityInfoArea stabilityInfoArea;
	private ErrorInfoArea errorInfoArea;
	public GenomeScreen(Player player, LivingEntity entity)
	{
		super(Component.empty());
		this.entity = entity;
		this.player = player;
	}
	
	@Override
	protected void init()
	{
		this.errorInfoArea = new ErrorInfoArea(new Rect2i(20, 130, 250, 20));
		addRenderableWidget(this.chooser = new GeneChooser(20, 20, 250, 100, this.entity, this.errorInfoArea));
		this.stabilityInfoArea = new GenomeStabilityInfoArea(new Rect2i(20, 8, 60, 10), this.chooser);
		addRenderableWidget(this.ownedRarityList = new OwnedRarityList(minecraft, 355, 20, 50, 150, 20));
		this.ownedGeneList = new OwnedGeneList(this.minecraft, 275, 20, 75, 150, 40, this.ownedRarityList);
		this.ownedGeneList.setSelected(this.ownedGeneList.getFirstElement());
		addRenderableWidget(this.ownedGeneList);
		
		addRenderableWidget(Button.builder(Component.literal("Add"), button ->
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
				}).
				pos(50, 200).
				size(30, 30).
				build());
		addRenderableWidget(Button.builder(Component.literal("Remove"), button ->
				{
					this.chooser.removeGene();
				}).
				pos(90, 200).
				size(30, 30).
				build());
		
		GenomeInstance genome = this.entity.getData(Registration.DataAttachmentsReg.GENOME.get());
		if (genome.geneInstances().isEmpty())
			return;
		NetworkEngine.sendToServer(new C2SAddUnlockedGenes(this.player.getUUID(), genome));
	}
	
	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
	{
		//FIXME: юзать метод, который в меню прописан. Избавиться от этого бесполезного куска кода
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		
		this.stabilityInfoArea.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.errorInfoArea.render(guiGraphics, mouseX, mouseY, partialTicks);
		List<Component> tooltipList = new ArrayList<>();
		this.stabilityInfoArea.fillTooltip(mouseX, mouseY, tooltipList);
		this.errorInfoArea.fillTooltip(mouseX, mouseY, tooltipList);
		if (!tooltipList.isEmpty())
			guiGraphics.renderTooltip(this.minecraft.font, tooltipList,Optional.empty(), mouseX, mouseY);
	}
	
	@Override
	protected void renderMenuBackground(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height)
	{
		super.renderMenuBackground(guiGraphics, x, y, width, height);
		
		RenderHelper.blit(guiGraphics,
				BioContainerScreen.BACKGROUND,
				x, y,
				0, 0,
				width, height,
				0,
				256, 256,
				256, 256);
	}
}

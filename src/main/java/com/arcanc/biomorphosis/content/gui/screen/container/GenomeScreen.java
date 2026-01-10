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
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GeneRarity;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GenomeScreen extends Screen
{
	private final LivingEntity entity;
	
	private GeneChooser chooser;
	private OwnedGeneList ownedGeneList;
	private OwnedRarityList ownedRarityList;
	
	public GenomeScreen(LivingEntity entity)
	{
		super(Component.empty());
		this.entity = entity;
	}
	
	@Override
	protected void init()
	{
		addRenderableWidget(this.chooser = new GeneChooser(20, 20, 250, 100, this.entity));
		addRenderableWidget(this.ownedRarityList = new OwnedRarityList(minecraft, 355, 20, 50, 150, 20));
		this.ownedGeneList = new OwnedGeneList(this.minecraft, 275, 20, 75, 150, 40, this.ownedRarityList);
		this.ownedGeneList.setSelectedIndex(0);
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
	}
	
	@Override
	protected void renderMenuBackground(@NotNull GuiGraphics guiGraphics, int x, int y, int width, int height)
	{
		super.renderMenuBackground(guiGraphics, x, y, width, height);
		
		RenderHelper.blit(guiGraphics,
				RenderType :: guiTextured,
				BioContainerScreen.BACKGROUND,
				x, y,
				0, 0,
				width, height,
				256, 256,
				256, 256);
	}
}

/**
 * @author ArcAnc
 * Created at: 09.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;


import com.arcanc.biomorphosis.content.gui.screen.container.BioContainerScreen;
import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.arcanc.biomorphosis.util.helper.RenderHelper.Rect2d;

public class GeneChooser extends AbstractWidget
{
	private final LivingEntity livingEntity;
	private final GenomeInstance genome;
	private final List<Rect2d> genePositions = new ArrayList<>();
	private int chosenGene = -1;
	
	public GeneChooser(int x, int y, int width, int height, @NotNull LivingEntity entity)
	{
		super(x, y, width, height, Component.empty());
		this.livingEntity = entity;
		this.genome = new GenomeInstance(new ArrayList<>(entity.getData(Registration.DataAttachmentsReg.GENOME).geneInstances()));
	}
	
	@Override
	protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		GenomeInstance genome = this.genome;
		Minecraft mc = RenderHelper.mc();
		Font font = mc.font;
		
		RenderHelper.blit(guiGraphics,
				RenderType :: guiTextured,
				BioContainerScreen.BACKGROUND,
				this.getX(), this.getY(),
				0, 0,
				getWidth(), getHeight(),
				256, 256,
				256, 256);
		
		if (genome.geneInstances().isEmpty())
		{
			guiGraphics.drawCenteredString(
					font,
					Component.translatable(Database.GUI.Genome.Translations.NO_GENES).
							withStyle(ChatFormatting.RED),
					this.getX() + this.getWidth() / 2,
					this.getY() + this.getHeight() / 2 - font.lineHeight / 2,
					-1);
			return;
		}
		
		float time = (Util.getMillis() % 10000) / 1000f;
		int geneAmount = genome.geneInstances().size();
		float reservedTop = this.getHeight() * 0.20f;
		float reservedRight = this.getWidth() * 0.6f;
		
		float availableWidth = this.getWidth() - reservedRight;
		float availableHeight = this.getHeight() - reservedTop;
		float geneSize = Math.min(availableHeight, availableWidth / geneAmount);
		
		float totalGeneWidth = geneSize * geneAmount;
		float startX = this.getX() + (availableWidth - totalGeneWidth) / 2;
		float centerY = this.getY() + reservedTop + availableHeight / 2;
		this.genePositions.clear();
		
		guiGraphics.drawCenteredString(font,
				livingEntity.getName(),
				(int) (this.getX() + availableWidth / 2),
				this.getY() + 3,
				-1);
		
		for (int q = 0; q < geneAmount; q++)
		{
			GeneInstance gene = genome.geneInstances().get(q);
			float x = startX + q * geneSize;
			float y = centerY;
			
			Rect2d bounds = new Rect2d(x, y - geneSize / 2f, geneSize, geneSize);
			
			this.genePositions.add(bounds);
			RenderHelper.GenomeRenderer.renderGeneInGui(gene, guiGraphics, bounds);
		}
		
		if (this.chosenGene == -1)
			return;
		GeneInstance gene = genome.geneInstances().get(this.chosenGene);
		Rect2d sizes = this.genePositions.get(this.chosenGene);
		
		guiGraphics.fill((int)sizes.x(), (int)sizes.y(), (int)(sizes.x() + sizes.width()), (int)(sizes.y() + sizes.height()), -1);
		mc.getConnection().registryAccess().
				lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY).
				getOptional(gene.id()).
				ifPresent(geneDefinition ->
				{
					GeneDefinition.RarityData data = geneDefinition.rarityData().get(gene.rarity());
					MutableComponent component = Component.empty();
					
					component.append(Component.translatable(Database.GUI.Genome.Translations.GENE_NAME.
									apply(geneDefinition.id())).
							withColor(gene.rarity().getColor())).
							append(":").
							append("\n").
							append(Component.translatable(Database.GUI.Genome.Translations.GENE_RARITY,
											Component.translatable(gene.rarity().getSerializedName()).
													withColor(gene.rarity().getColor()))).
							append("\n").
							append(Component.translatable(Database.GUI.Genome.Translations.GENE_INSTABILITY,
											Component.literal(String.valueOf(data.destabilizationAmount())).
															withColor(gene.rarity().getColor())));
					
					
					if (data.effects().isEmpty())
						component.append("\n • ").append(Component.translatable(Database.GUI.Genome.Translations.NO_GENE_EFFECT));
					
					for (int q = 0; q < data.effects().size(); q++)
					{
						GeneDefinition.GeneEffectEntry entry = data.effects().get(q);
						List<Object> values = GenomeHelper.getAllEffectData(entry);
						List<Component> stringifies = new ArrayList<>();
						
						String address = Database.GUI.Genome.Translations.GENE_EFFECT_DESCRIPTION.apply(entry.type().getId());
						for (Object value : values)
							stringifies.add(Component.literal(value.toString()).
									withColor(gene.rarity().getColor()));
						component.append("\n • ").append(Component.translatable(address, stringifies.toArray()));
					}
					
					guiGraphics.drawWordWrap(
							font,
							component,
							(int)(this.getX() + availableWidth + 3),
							this.getY() + 3,
							(int)reservedRight,
							-1,
							false);
				});
	}
	
	@Override
	public void onClick(double mouseX, double mouseY, int button)
	{
		if (this.genePositions.isEmpty())
		{
			this.chosenGene = -1;
			return;
		}
		
		int index = -1;
		for (int q = 0; q < this.genePositions.size(); q++)
		{
			Rect2d genePos = this.genePositions.get(q);
			if (mouseX >= genePos.x() &&
					mouseX < genePos.x() + genePos.width() &&
					mouseY >= genePos.y() &&
					mouseY < genePos.y() + genePos.height())
				index = q;
		}
		
		this.chosenGene = index;
	}
	
	public @Nullable GeneInstance getChosenGene()
	{
		return this.genome.geneInstances().get(this.chosenGene);
	}
	
	public GenomeInstance getGenome()
	{
		return this.genome;
	}
	
	public boolean canAddGene(@NotNull GeneInstance geneInstance)
	{
		Minecraft mc = RenderHelper.mc();
		GeneDefinition inputDefinition = mc.getConnection().registryAccess().lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY).
				getValue(geneInstance.id());
		
		if (inputDefinition == null)
			return false;
		
		GeneDefinition.RarityData inputGeneRarityData = inputDefinition.rarityData().get(geneInstance.rarity());
		
		boolean canBeAdded = true;
		for (int q = 0; q < this.genome.geneInstances().size(); q++)
		{
			GeneInstance checkGene = this.genome.geneInstances().get(q);
			GeneDefinition checkGeneDefinition = mc.getConnection().registryAccess().lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY).
					getValue(checkGene.id());
			if (checkGeneDefinition == null)
			{
				canBeAdded = false;
				break;
			}
			
			GeneDefinition.RarityData checkGeneRarityData = checkGeneDefinition.rarityData().get(checkGene.rarity());
			
			if (inputGeneRarityData.incompatibilities().contains(checkGene.id()) ||
					checkGeneRarityData.incompatibilities().contains(geneInstance.id()) ||
					(geneInstance.id().equals(checkGene.id()) &&
							geneInstance.rarity().ordinal() <= checkGene.rarity().ordinal()))
				canBeAdded = false;
		}
		
		return canBeAdded;
	}
	
	public void addGene(GeneInstance geneInstance)
	{
		removeSameGenesIfNeeded(geneInstance);
		this.genome.geneInstances().add(geneInstance);
	}
	
	public void removeGene()
	{
		if (this.chosenGene == -1)
			return;
		this.genome.geneInstances().remove(this.chosenGene);
		this.chosenGene = -1;
	}
	
	private void removeSameGenesIfNeeded(GeneInstance geneInstance)
	{
		int index = -1;
		for (int q = 0; q < this.genome.geneInstances().size(); q++)
		{
			GeneInstance checkGene = this.genome.geneInstances().get(q);
			if (checkGene.id().equals(geneInstance.id()))
				index = q;
		}
		
		if (index == -1)
			return;
		this.genome.geneInstances().remove(index);
		this.chosenGene = -1;
	}
	
	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
	{
	}
}

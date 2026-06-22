/**
 * @author ArcAnc
 * Created at: 09.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;


import com.arcanc.biomorphosis.content.gui.component.info.ErrorInfoArea;
import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.mutations.UnlockedGenome;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.GenomeHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.arcanc.biomorphosis.util.helper.RenderHelper.Rect2d;

public class GeneChooser extends AbstractWidget
{
	private final LivingEntity livingEntity;
	private GenomeInstance genome;
	private final List<Rect2d> genePositions = new ArrayList<>();
	private int chosenGene = -1;
	private final ErrorInfoArea error;
	private final FittingMultiLineText text;

	public GeneChooser(int x, int y, int width, int height, LivingEntity entity, ErrorInfoArea errorInfoArea)
	{
		super(x, y, width, height, Component.empty());
		this.error = errorInfoArea;
		this.livingEntity = entity;
		this.genome = new GenomeInstance(new ArrayList<>(GenomeHelper.getGenome(entity).geneInstances()));

		int textWidth = (int) (width * 0.75f);
		int textHeight = (int) (height * 0.35f);

		this.text = new FittingMultiLineText(x + 1,
				y + this.height - textHeight + 3,
				textWidth,
				textHeight,
				Component.empty(),
				RenderHelper.mc().font);
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		GenomeInstance genome = this.genome;
		Minecraft mc = RenderHelper.mc();
		Font font = mc.font;

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

		int geneAmount = genome.geneInstances().size();

		float availableWidth = this.getWidth();
		float availableHeight = this.getHeight() * 0.34f;
		float geneSize = Math.min(availableHeight, availableWidth / geneAmount);

		float totalGeneWidth = geneSize * geneAmount;
		float startX = this.getX() + (availableWidth - totalGeneWidth) / 2;
		float centerY = this.getY() + availableHeight / 2;
		this.genePositions.clear();
		for (int q = 0; q < geneAmount; q++)
		{
			GeneInstance gene = genome.geneInstances().get(q);
			float x = startX + q * geneSize;
			float y = centerY;

			Rect2d bounds = new Rect2d(x, y - geneSize / 2f, geneSize, geneSize);

			this.genePositions.add(bounds);
			RenderHelper.GenomeRenderer.renderGeneInGui(gene, guiGraphics, bounds);
		}

		if (this.chosenGene == -1 || this.chosenGene >= genome.geneInstances().size())
			return;

		GeneInstance gene = genome.geneInstances().get(this.chosenGene);
		Rect2d sizes = this.genePositions.get(this.chosenGene);

		guiGraphics.fill((int)sizes.x(), (int)sizes.y(), (int)(sizes.x() + sizes.width()), (int)(sizes.y() + sizes.height()), -1);

		mc.getConnection().registryAccess().
				lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY).
				get(ResourceKey.create(Registration.GenomeReg.DEFINITION_KEY, gene.id())).
				ifPresent(geneDefinition ->
				{
					GeneDefinition definition = geneDefinition.value();
					GeneDefinition.RarityData data = definition.rarityData().get(gene.rarity());
					MutableComponent component = Component.empty();

					component.append(Component.translatable(Database.GUI.Genome.Translations.GENE_NAME.
									apply(definition.id())).
							withColor(gene.rarity().getColor())).
							append(":").
							append("\n").
							append(Component.translatable(Database.GUI.Genome.Translations.GENE_INSTABILITY,
											Component.literal(String.valueOf(data.destabilizationAmount())).
															withColor(gene.rarity().getColor()))).
							append("\n");

					if (!data.incompatibilities().isEmpty())
					{
						MutableComponent incompatibilities = Component.empty();
						for (ResourceLocation inc : data.incompatibilities())
							incompatibilities.append("\n • ").
									append(Component.translatable(Database.GUI.Genome.Translations.GENE_NAME.apply(inc)));

						component.append(Component.translatable(Database.GUI.Genome.Translations.GENE_INCOMPATIBILITIES, incompatibilities)).
								append("\n");
					}

					MutableComponent effects = Component.empty();
					if (data.effects().isEmpty())
						effects.append("\n • ").append(Component.translatable(Database.GUI.Genome.Translations.NO_GENE_EFFECT));
					else
						for (int q = 0; q < data.effects().size(); q++)
						{
							GeneDefinition.GeneEffectEntry entry = data.effects().get(q);
							List<Object> values = GenomeHelper.getAllEffectData(entry);
							List<Component> stringifies = new ArrayList<>();

							String address = Database.GUI.Genome.Translations.GENE_EFFECT_DESCRIPTION.apply(entry.type().getId());
							for (Object value : values)
								stringifies.add(Component.literal(value.toString()).
										withColor(gene.rarity().getColor()));
							effects.append("\n • ").append(Component.translatable(address, stringifies.toArray()));
						}
					component.append(Component.translatable(Database.GUI.Genome.Translations.GENE_EFFECTS, effects));


					this.text.setMessage(component);
					this.text.render(guiGraphics, mouseX, mouseY, partialTick);
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
		if (this.chosenGene < 0 || this.chosenGene >= this.genome.geneInstances().size())
			return null;
		return this.genome.geneInstances().get(this.chosenGene);
	}

	public GenomeInstance getGenome()
	{
		return this.genome;
	}

	public boolean canAddGene(GeneInstance geneInstance)
	{
		Minecraft mc = RenderHelper.mc();
		if (mc.level == null || mc.player == null)
			return false;

		UnlockedGenome unlockedGenome = GenomeHelper.getUnlockedGenome(mc.player);
		GenomeHelper.GenomeValidationResult result = GenomeHelper.validateGeneCanBeAdded(this.genome, geneInstance, mc.level, unlockedGenome);
		if (result.valid())
			return true;
		result.firstError().ifPresent(this :: showValidationError);
		return false;
	}

	public boolean canUseGenome()
	{
		Minecraft mc = RenderHelper.mc();
		if (mc.level == null)
			return false;

		GenomeHelper.GenomeValidationResult result = this.livingEntity instanceof Player player ?
				GenomeHelper.validateMutation(player, this.genome) :
				GenomeHelper.validateGenome(this.genome, mc.level);
		if (result.valid())
			return true;
		result.firstError().ifPresent(this :: showValidationError);
		return false;
	}

	public void addGene(GeneInstance geneInstance)
	{
		this.genome = GenomeHelper.addOrReplaceGene(this.genome, geneInstance);
		this.chosenGene = -1;
	}

	public void removeGene()
	{
		GeneInstance gene = getChosenGene();
		if (gene == null)
			return;
		this.genome = GenomeHelper.removeGene(this.genome, gene);
		this.chosenGene = -1;
	}

	private void showValidationError(GenomeHelper.GenomeValidationError validationError)
	{
		GeneInstance gene = validationError.gene();
		GeneInstance otherGene = validationError.otherGene();

		switch (validationError.type())
		{
			case UNKNOWN_DEFINITION -> this.error.updateError(Database.GUI.InfoArea.ErrorInfoArea.UNKNOWN_GENE, geneName(gene), rarityName(gene));
			case UNKNOWN_RARITY -> this.error.updateError(Database.GUI.InfoArea.ErrorInfoArea.UNKNOWN_RARITY_DATA, geneName(gene), rarityName(gene));
			case LOCKED_GENE -> this.error.updateError(Database.GUI.InfoArea.ErrorInfoArea.LOCKED_GENE, geneName(gene), rarityName(gene));
			case INCOMPATIBLE_GENES -> this.error.updateError(Database.GUI.InfoArea.ErrorInfoArea.INCOMPATIBLE_GENES, geneName(gene), geneName(otherGene));
			case DUPLICATE_GENE -> this.error.updateError(Database.GUI.InfoArea.ErrorInfoArea.DUPLICATE_GENE, geneName(gene));
			case MORE_POWERFUL_GENE -> this.error.updateError(Database.GUI.InfoArea.ErrorInfoArea.MORE_POWERFUL_GENE, geneName(gene), geneName(otherGene));
			case LOW_STABILITY -> this.error.updateError(Database.GUI.InfoArea.GenomeStabilityInfoArea.LOW_STABILITY);
		}
	}

	private Component geneName(@Nullable GeneInstance gene)
	{
		if (gene == null)
			return Component.empty();
		return Component.translatable(Database.GUI.Genome.Translations.GENE_NAME.apply(gene.id())).
				withColor(gene.rarity().getColor());
	}

	private Component rarityName(@Nullable GeneInstance gene)
	{
		if (gene == null)
			return Component.empty();
		return Component.literal(gene.rarity().getSerializedName()).
				withColor(gene.rarity().getColor());
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
	{
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY)
	{
		return isMouseOver(mouseX, mouseY);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		if (this.genePositions.isEmpty())
			return false;
		for (Rect2d rect : this.genePositions)
		{
			if (mouseX >= rect.x() &&
				mouseX < rect.x() + rect.width() &&
				mouseY >= rect.y() &&
				mouseY < rect.y() + rect.height())
				return true;
		}
		return this.text.isMouseOver(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		return this.text.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
	{
		return this.text.mouseDragged(mouseX, mouseY, button, dragX, dragY) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
	{
		return this.text.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}
}

/**
 * @author ArcAnc
 * Created at: 06.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.page.component;


import com.arcanc.biomorphosis.content.block.multiblock.definition.IMultiblockDefinition;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiblockPageComponent extends AbstractPageComponent
{
	private static final int SLOT_SIZE = 20;
	private static final int MAX_PER_ROW = 5;
	
	private final ResourceLocation multiblockAddress;
	private final IMultiblockDefinition definition;
	
	public MultiblockPageComponent(ResourceLocation multiblockAddress)
	{
		super(0, 0, 45, 0, Component.empty());
		this.multiblockAddress = multiblockAddress;
		Minecraft mc = RenderHelper.mc();
		this.definition = mc.getConnection().registryAccess().
				lookupOrThrow(Registration.MultiblockReg.DEFINITION_KEY).
				getOrThrow(ResourceKey.create(Registration.MultiblockReg.DEFINITION_KEY, this.multiblockAddress)).
				value();
		
		if (this.definition == null)
			return;
		
		int size = this.definition.getStructure(mc.level, null).getStructure().size();
		
		this.setHeight(SLOT_SIZE * (size % MAX_PER_ROW + 1));
	}
	
	@Override
	protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
	{
		if (this.definition == null)
			return;
		Minecraft mc = RenderHelper.mc();
		List<IngredientWithSize> ingredients = this.definition.getStructure(mc.level, null).getStructure();
		
		int rows = (ingredients.size() + MAX_PER_ROW - 1) / MAX_PER_ROW;
		int centerX = this.getX() + this.getWidth() / 2;
		for (int row = 0; row < rows; row++)
		{
			int start = row * MAX_PER_ROW;
			int end = Math.min(start + MAX_PER_ROW, ingredients.size());
			int countInRow = end - start;
			
			int rowWidth = countInRow * SLOT_SIZE;
			int startX = centerX - rowWidth / 2;
			int y = getY() + row * SLOT_SIZE;
			
			for (int q = 0; q < countInRow; q++)
			{
				int x = startX + q * SLOT_SIZE;
				ItemStack stack = RenderHelper.getStackAtCurrentTime(ingredients.get(start + q));
				
				guiGraphics.renderItem(stack, x, y);
				guiGraphics.renderItemDecorations(mc.font, stack, x, y);
				
				if (mouseX >= x && mouseX < x + SLOT_SIZE &&
						mouseY >= y && mouseY < y + SLOT_SIZE)
					guiGraphics.renderTooltip(mc.font, stack, mouseX, mouseY);
			}
		}
	}
	
	@Override
	protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
	{
	
	}
}

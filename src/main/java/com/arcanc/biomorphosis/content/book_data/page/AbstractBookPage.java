/**
 * @author ArcAnc
 * Created at: 29.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.page;

import com.arcanc.biomorphosis.content.book_data.BookPageData;
import com.arcanc.biomorphosis.content.book_data.chapter.AbstractBookChapter;
import com.arcanc.biomorphosis.content.book_data.page.component.*;
import com.arcanc.biomorphosis.content.book_data.page.component.recipes.AbstractRecipeComponent;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractBookPage extends AbstractWidget
{
    private static final Pattern PATTERN = Pattern.compile(
            "</(item|block);([\\w:]+)/>" + "|" +
                    "</tag;(item|block|fluid);([\\w:]+)/>" + "|" +
                    "</recipe;([\\w:]+);([^>]+)/>" + "|" +
                    "</entity;([\\w:]+)/>"
    );

    private final BookPageData data;
    private final List<AbstractPageComponent> components;
    protected final Map<Integer, List<AbstractPageComponent>> dividedComponents = new HashMap<>();

    public AbstractBookPage(@NotNull BookPageData data)
    {
        super(0, 0, 0, 0, Component.translatable(data.title()));
        this.data = data;
        this.components = data.text().isBlank() ? List.of() : parseComponents(Component.translatable(data.text()).getString());
    }

    private @NotNull List<AbstractPageComponent> parseComponents(String string)
    {
        List<AbstractPageComponent> components = new ArrayList<>();

        Matcher matcher = PATTERN.matcher(string);

        int lastEnd = 0;
        while (matcher.find())
        {
            if (matcher.start() > lastEnd)
            {
                components.add(new TextPageComponent(Component.literal(string.substring(lastEnd, matcher.start()))));
            }

            if (matcher.group(1) != null)
            { // Item or Block
                if (matcher.group(1).equals("item"))
                    components.add(new ItemPageComponent(ResourceLocation.parse(matcher.group(2))));
                else
                    components.add(new BlockPageComponent(ResourceLocation.parse(matcher.group(2))));
            }
            else if (matcher.group(3) != null)
                // Tag
                components.add(new TagPageComponent(matcher.group(3), ResourceLocation.parse(matcher.group(4))));
            else if (matcher.group(5) != null)
            { // Recipe
                components.add(AbstractRecipeComponent.createRecipeComponent(ResourceLocation.parse(matcher.group(5)), ResourceLocation.parse(matcher.group(6))));
            }
            else if (matcher.group(7) != null)
                // Entity
                components.add(new EntityPageComponent(ResourceLocation.parse(matcher.group(7))));

            lastEnd = matcher.end();
        }

        if (lastEnd < string.length())
        {
            components.add(new TextPageComponent(Component.literal(string.substring(lastEnd))));
        }
        return components;
    }

    public BookPageData getData()
    {
        return data;
    }

    public void reCalcPositions()
    {
        int subPage = 0;
        Rect2i zone = AbstractBookChapter.getPageZones().get(subPage);
        int currentY = zone.getY();
		
        for (int q = 0; q < this.components.size(); q++)
        {
            AbstractPageComponent component = this.components.get(q);
            component.reCalcShiftX(subPage % 2);
            component.setOwningSubpage(subPage);
				
			LayoutState state = new LayoutState(component, q, subPage, currentY);
			
            if (component instanceof TextPageComponent)
	            state = splitTextComponentIfNeeded(state);
			
			component = state.lastComponent();
			currentY = state.currentY();
			subPage = state.subPage();
	        zone = AbstractBookChapter.getPageZones().get(subPage % 2);
			q = state.currentIndex();
			
            int componentHeight = component.getHeight();
            if (currentY + componentHeight > zone.getY() + zone.getHeight())
            {
                subPage++;
                zone = AbstractBookChapter.getPageZones().get(subPage % 2);
                currentY = zone.getY();

                component.reCalcShiftX(subPage % 2);
                component.setOwningSubpage(subPage);
            }

            component.setPosition(zone.getX() + component.getShiftX(), currentY);
            currentY += componentHeight;
        }

        this.dividedComponents.clear();
        for (AbstractPageComponent component : this.components)
            this.dividedComponents.computeIfAbsent(component.getOwningSubpage(), k -> new ArrayList<>()).add(component);
    }
	
    private @NotNull LayoutState splitTextComponentIfNeeded(@NotNull LayoutState state)
    {
        if (!(state.lastComponent() instanceof TextPageComponent component))
			return state;
		Minecraft mc = RenderHelper.mc();
        Font font = mc.font;
        int subPage = state.subPage;
	    Rect2i zone = AbstractBookChapter.getPageZones().get(subPage % 2);
		int currentY = state.currentY;
		int currentIndex = state.currentIndex;

        int availableHeight = zone.getY() + zone.getHeight() - currentY;
        List<FormattedText> lines = font.getSplitter().splitLines(component.getMessage(), component.getWidth(), Style.EMPTY);
        int lineHeight = font.lineHeight;
		
		int maxLines = availableHeight / lineHeight;
        if (lines.size() > maxLines)
        {
            List<FormattedText> currentLines = lines.subList(0, maxLines);
            List<FormattedText> remainingLines = lines.subList(maxLines, lines.size());

            MutableComponent currentText = Component.empty();
            for (FormattedText text : currentLines)
                currentText = currentText.append(text.getString()).append(" ");
			
			currentText = Component.literal(currentText.getString().trim());
			
			component.setMessage(currentText);
            component.setHeight(currentLines.size() * lineHeight);
			component.setPosition(zone.getX(), currentY);
			MutableComponent remainingText = Component.empty();
            for (FormattedText text : remainingLines)
                remainingText = remainingText.append(text.getString()).append(" ");
			
			remainingText = Component.literal(remainingText.getString().trim());
            TextPageComponent nextComponent = new TextPageComponent(remainingText);
            int nextComponentHeight = remainingLines.size() * lineHeight;
			int remainingHeight = zone.getY() + zone.getHeight() - (currentY + component.getHeight());
            if (nextComponentHeight > remainingHeight)
            {
                subPage++;
                zone = AbstractBookChapter.getPageZones().get(subPage % 2);
                currentY = zone.getY();
            }

            nextComponent.setOwningSubpage(subPage);
            nextComponent.reCalcShiftX(subPage % 2);
            nextComponent.setHeight(nextComponentHeight);
			nextComponent.setPosition(zone.getX(), zone.getY());

            components.add(currentIndex + 1, nextComponent);
            return splitTextComponentIfNeeded(new LayoutState(nextComponent, currentIndex + 1, subPage, currentY));
        }
        else
            component.setHeight(lines.size() * lineHeight);
		return state;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.active && this.visible)
        {
            if (this.isValidClickButton(button))
            {
                if (this.isMouseOver(mouseX, mouseY))
                {
                    this.onClick(mouseX, mouseY, button);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        for (Rect2i zone : AbstractBookChapter.getPageZones())
            if (isMouseInZone(zone, mouseX, mouseY))
                return true;
        return false;
    }

    private boolean isMouseInZone(@NotNull Rect2i zone, double mouseX, double mouseY)
    {
        return mouseX >= zone.getX() && mouseX < zone.getX() + zone.getWidth() &&
                mouseY >= zone.getY() && mouseY < zone.getY() + zone.getHeight();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        renderPageContent(guiGraphics, mouseX, mouseY, partialTicks);
        renderNavigationButtons(guiGraphics, mouseX, mouseY, partialTicks);
    }

    protected abstract void renderPageContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);
    protected abstract void renderNavigationButtons(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks);
	
	private record LayoutState(AbstractPageComponent lastComponent, int currentIndex, int subPage, int currentY){}
}

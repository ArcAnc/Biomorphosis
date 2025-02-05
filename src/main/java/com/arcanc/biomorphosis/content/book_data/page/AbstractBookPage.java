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
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractBookPage extends AbstractWidget
{
    private static final Pattern PATTERN = Pattern.compile(
            "</(item|block);([\\w:]+)/>" + "|" +
                    "</tag;(item|block|fluid);([\\w:]+)/>" + "|" +
                    "</recipe;([\\w:]+);([\\w:]+)/>" + "|" +
                    "</entity;([\\w:]+)/>"
    );

    private final BookPageData data;
    protected final List<AbstractPageComponent> components;

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
                //Дописать рассчет высоты текста
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
                components.add(new AbstractRecipeComponent(matcher.group(5), ResourceLocation.parse(matcher.group(6))));
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

        ListIterator<AbstractPageComponent> iterator = components.listIterator();
        while (iterator.hasNext())
        {
            AbstractPageComponent component = iterator.next();
            component.reCalcShiftX(subPage % 2);
            component.setOwningSubpage(subPage);

            if (component instanceof TextPageComponent textComponent)
                splitTextComponentIfNeeded(textComponent, iterator, subPage, currentY);

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
    }

    private void splitTextComponentIfNeeded(@NotNull TextPageComponent component, ListIterator<AbstractPageComponent> iterator, int subPage, int currentY)
    {
        Minecraft mc = RenderHelper.mc();
        Font font = mc.font;
        Rect2i zone = AbstractBookChapter.getPageZones().get(subPage % 2);

        int availableHeight = zone.getY() + zone.getHeight() - currentY;
        List<FormattedCharSequence> lines = font.split(component.getMessage(), component.getWidth());

        int lineHeight = font.lineHeight;
        int maxLines = availableHeight / lineHeight;

        if (lines.size() > maxLines)
        {
            List<FormattedCharSequence> currentLines = lines.subList(0, maxLines);
            List<FormattedCharSequence> remainingLines = lines.subList(maxLines, lines.size());

            MutableComponent currentText = Component.empty();
            for (FormattedCharSequence seq : currentLines)
                /*FIXME: is seq.toString is right way?*/
                currentText = currentText.append(seq.toString()).append("\n");
            component.setMessage(currentText);
            component.setHeight(currentLines.size() * lineHeight);

            MutableComponent remainingText = Component.empty();
            for (FormattedCharSequence seq : remainingLines)
                /*FIXME: is seq.toString is right way?*/
                remainingText = remainingText.append(seq.toString()).append("\n");
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

            iterator.add(nextComponent);

            splitTextComponentIfNeeded(nextComponent, iterator, subPage, currentY + nextComponentHeight);
        }
        else
            component.setHeight(lines.size() * lineHeight);
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
}

/**
 * @author ArcAnc
 * Created at: 01.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.gui.component.icon;

import com.google.common.primitives.Ints;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Icon
{
    void render(GuiGraphics graphics, int xPos, int yPos, int width, int height);

    class IconParser
    {
        public static @NotNull List<Object> parse(String text)
        {
            List<Object> result = new ArrayList<>();
            int start = 0;
            Pattern pattern = Pattern.compile("</(item|block|image);(.*?)/>");
            Matcher matcher = pattern.matcher(text);

            while (matcher.find())
            {
                if (matcher.start() > start)
                    result.add(text.substring(start, matcher.start()));

                String type = matcher.group(1);
                String data = matcher.group(2);
                switch (type)
                {
                    case "item":
                        Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(data));
                        result.add(new ItemIcon(item));
                        break;
                    case "block":
                        Block block = BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(data));
                        result.add(new BlockIcon(block));
                        break;
                    case "image":
                        String[] parts = data.split(";");
                        if (parts.length != 3)
                            throw new IllegalArgumentException("Invalid image format: " + data);
                        int width = Optional.ofNullable(parts[0]).map(Ints :: tryParse).orElse(16);
                        int height = Optional.ofNullable(parts[1]).map(Ints :: tryParse).orElse(16);
                        ResourceLocation image = ResourceLocation.parse(parts[2]);
                        result.add(new ImageIcon(image, width, height));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid icon type: " + type);
                }
                start = matcher.end();
            }
            if (start < text.length())
                result.add(text.substring(start));

            return result;
        }
    }
}
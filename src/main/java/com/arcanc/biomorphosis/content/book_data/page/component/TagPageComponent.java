/**
 * @author ArcAnc
 * Created at: 02.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.page.component;

import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TagPageComponent extends AbstractPageComponent
{
    private final TagKey<?> tag;
    private final List<ItemStack> toRender;

    @SuppressWarnings("unchecked")
    public TagPageComponent(@NotNull String tagType, @NotNull ResourceLocation location)
    {
        super(0, 0, 18, 18, Component.empty());

        tag = switch (tagType)
        {
            case "item" -> BuiltInRegistries.ITEM.getTags().map(HolderSet.Named :: key).filter(key -> key.location().equals(location)).findFirst().orElseThrow();
            case "block" -> BuiltInRegistries.BLOCK.getTags().map(HolderSet.Named :: key).filter(key -> key.location().equals(location)).findFirst().orElseThrow();
            case "fluid" -> BuiltInRegistries.FLUID.getTags().map(HolderSet.Named :: key).filter(key -> key.location().equals(location)).findFirst().orElseThrow();
            default -> null;
        };

        if (tag != null)
            toRender = switch (tagType)
            {
                case "item" -> BuiltInRegistries.ITEM.get((TagKey<Item>) tag).orElseThrow().stream().map(ItemStack :: new).toList();
                case "block" -> BuiltInRegistries.BLOCK.get((TagKey<Block>) tag).orElseThrow().stream().map(blockHolder -> blockHolder.value().asItem()).map(ItemStack :: new).toList();
                case "fluid" -> BuiltInRegistries.FLUID.get((TagKey<Fluid>) tag).orElseThrow().stream().map(fluidHolder -> new FluidStack(fluidHolder, 1)).map(FluidUtil :: getFilledBucket).toList();
                default -> new ArrayList<>();
            };
        else
            toRender = new ArrayList<>();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if (!toRender.isEmpty())
        {
            ItemStack stack = RenderHelper.getStackAtCurrentTime(toRender);
            guiGraphics.renderItem(stack, getX() + 1, getY() + 1);
            if (isHovered())
                guiGraphics.renderTooltip(RenderHelper.mc().font, stack, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}

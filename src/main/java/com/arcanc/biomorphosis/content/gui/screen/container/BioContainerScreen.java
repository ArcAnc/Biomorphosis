/**
 * @author ArcAnc
 * Created at: 30.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen.container;

import com.arcanc.biomorphosis.content.gui.BioSlot;
import com.arcanc.biomorphosis.content.gui.component.info.InfoArea;
import com.arcanc.biomorphosis.content.gui.sync.IGuiContextInfoProvider;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.C2SGuiData;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.google.common.base.Preconditions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class BioContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T>
{
    private final List<InfoArea> infoAreas = new ArrayList<>();
    protected static final ResourceLocation BACKGROUND = Database.rl("textures/gui/background.png");

    public BioContainerScreen(T menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (InfoArea area : this.infoAreas)
            area.render(guiGraphics, mouseX, mouseY, partialTick);

        List<Component> resultedTooltip = new ArrayList<>();
        for (InfoArea area : this.infoAreas)
            area.fillTooltip(mouseX, mouseY, resultedTooltip);

        if (resultedTooltip.isEmpty())
            renderTooltip(guiGraphics, mouseX, mouseY);
        else
            guiGraphics.renderTooltip(getFont(), resultedTooltip, Optional.empty(), mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        RenderHelper.blit(guiGraphics,
                RenderType :: guiTextured,
                BACKGROUND,
                this.getGuiLeft(), this.getGuiTop(),
                0, 0,
                getXSize(), getYSize(),
                256, 256,
                256, 256);
    }

    protected void addInfoArea(InfoArea info)
    {
        Preconditions.checkNotNull(info);
        this.infoAreas.add(info);
    }

    @Override
    protected void renderSlot(@NotNull GuiGraphics guiGraphics, @NotNull Slot slot)
    {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemstack = slot.getItem();
        boolean flag = false;
        boolean flag1 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemstack1 = this.menu.getCarried();
        String s = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty())
            itemstack = itemstack.copyWithCount(itemstack.getCount() / 2);
        else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemstack1.isEmpty())
        {
            if (this.quickCraftSlots.size() == 1)
                return;

            if (AbstractContainerMenu.canItemQuickReplace(slot, itemstack1, true) && this.menu.canDragTo(slot))
            {
                flag = true;
                int k = Math.min(itemstack1.getMaxStackSize(), slot.getMaxStackSize(itemstack1));
                int l = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
                int i1 = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, itemstack1) + l;
                if (i1 > k)
                {
                    i1 = k;
                    s = ChatFormatting.YELLOW.toString() + k;
                }

                itemstack = itemstack1.copyWithCount(i1);
            }
            else
            {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining();
            }
        }

        guiGraphics.pose().pushPose();

        if (slot instanceof BioSlot bioSlot)
        {
            guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.FRAME, i - 1, j - 1, 18, 18);
            guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.MASK, i - 1, j - 1, 18, 18, MathHelper.ColorHelper.color(bioSlot.getSlotColor().div(255f, new Vector4f())));
        }
        else
        {
            guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.FRAME, i - 1, j - 1, 18, 18);
            guiGraphics.blitSprite(RenderType :: guiTextured, BioSlot.MASK, i - 1, j - 1, 18, 18, MathHelper.ColorHelper.color(BioSlot.NORMAL_SLOT_COLOR.div(255f, new Vector4f())));
        }

        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        if (itemstack.isEmpty() && slot.isActive())
        {
            ResourceLocation resourcelocation = slot.getNoItemIcon();
            if (resourcelocation != null)
            {
                guiGraphics.blitSprite(RenderType::guiTextured, resourcelocation, i, j, 16, 16);
                flag1 = true;
            }
        }
        if (!flag1)
        {
            if (flag)
                guiGraphics.fill(i, j, i + 16, j + 16, -2130706433);

            renderSlotContents(guiGraphics, itemstack, slot, s);
        }

        guiGraphics.pose().popPose();
    }

    @Override
    protected void clearWidgets()
    {
        super.clearWidgets();
        this.infoAreas.clear();
    }

    protected void sendUpdateToServer(@NotNull Consumer<CompoundTag> additionalInfo)
    {
        CompoundTag tag = new CompoundTag();

        additionalInfo.accept(tag);
        addTypeInfo(tag);
        NetworkEngine.sendToServer(new C2SGuiData(this.menu.containerId, tag));
    }

    private void addTypeInfo(CompoundTag tag)
    {
        if (this.menu instanceof IGuiContextInfoProvider provider)
            provider.writeContextInfo(tag);
    }
}

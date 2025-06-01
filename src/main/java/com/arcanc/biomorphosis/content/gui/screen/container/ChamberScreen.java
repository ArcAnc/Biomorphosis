/**
 * @author ArcAnc
 * Created at: 30.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen.container;

import com.arcanc.biomorphosis.content.gui.component.info.ProgressInfoArea;
import com.arcanc.biomorphosis.content.gui.container_menu.ChamberMenu;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ChamberScreen extends BioContainerScreen<ChamberMenu>
{
    private int current = 0;
    private int max = 1000;

    public ChamberScreen(ChamberMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init()
    {
        super.init();
        addInfoArea(new ProgressInfoArea(new Rect2i(20, 20, 60, 40), new ProgressInfoArea.ProgressInfo(() -> current, () -> max)));
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY)
    {

    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {

    }

    @Override
    protected void containerTick()
    {
        current++;
        if (current == max)
        {
            current = 0;
            max = RenderHelper.mc().level.random.nextInt(5000);
        }
    }
}

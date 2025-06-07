/**
 * @author ArcAnc
 * Created at: 30.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen.container;

import com.arcanc.biomorphosis.content.gui.container_menu.ChamberMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ChamberScreen extends BioContainerScreen<ChamberMenu>
{

    public ChamberScreen(ChamberMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
        this.imageHeight = 176;
    }

    @Override
    protected void init()
    {
        super.init();
    }

    @Override
    protected void containerTick()
    {
        super.containerTick();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY)
    {
    }
}

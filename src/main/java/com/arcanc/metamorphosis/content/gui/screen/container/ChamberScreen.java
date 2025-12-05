/**
 * @author ArcAnc
 * Created at: 30.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.gui.screen.container;

import com.arcanc.metamorphosis.content.block.multiblock.MultiblockChamber;
import com.arcanc.metamorphosis.content.gui.component.ChamberButtonStart;
import com.arcanc.metamorphosis.content.gui.component.info.ChamberProgressArea;
import com.arcanc.metamorphosis.content.gui.component.info.ProgressInfoArea;
import com.arcanc.metamorphosis.content.gui.container_menu.ChamberMenu;
import com.arcanc.metamorphosis.util.helper.BlockHelper;
import com.arcanc.metamorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ChamberScreen extends MetaContainerScreen<ChamberMenu>
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

        BlockHelper.castTileEntity(RenderHelper.mc().level, this.menu.getBlockPos(), MultiblockChamber.class).ifPresent(chamber ->
        {
            addInfoArea(new ChamberProgressArea(new Rect2i(this.getGuiLeft() + 110, this.getGuiTop() + 15, 45, 13), new ProgressInfoArea.ProgressInfo(chamber :: getWorkedTime, chamber :: getMaxWorkedTime)));
            addRenderableWidget(new ChamberButtonStart(this.getGuiLeft() + 40, this.getGuiTop() + 10, 20, 20, button ->
                    sendUpdateToServer(tag -> {}),
                    new ChamberButtonStart.ClickLimitation(chamber :: getWorkedTime, chamber :: getMaxWorkedTime)));
        });
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

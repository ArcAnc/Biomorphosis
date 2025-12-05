/**
 * @author ArcAnc
 * Created at: 20.07.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.gui.screen.container;


import com.arcanc.metamorphosis.content.gui.container_menu.ChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ChestScreen extends MetaContainerScreen<ChestMenu>
{
	public ChestScreen(ChestMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		this.imageHeight = 176;
		this.imageWidth = 186;
	}

	@Override
	protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
	}
}

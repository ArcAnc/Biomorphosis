/**
 * @author ArcAnc
 * Created at: 28.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.client;

import com.arcanc.biomorphosis.content.ability.AbilityLoadout;
import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.C2SActivateAbility;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

public final class AbilityWheelClient
{
	public static final KeyMapping OPEN_WHEEL = new KeyMapping(Database.HotKeys.ABILITY_WHEEL,
			KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, Database.HotKeys.CATEGORY);

	private static boolean wheelOpen;
	private static int selectedSlot;

	private AbilityWheelClient()
	{
	}

	public static void init(IEventBus modEventBus)
	{
		modEventBus.addListener(AbilityWheelClient :: registerKeyMapping);
		NeoForge.EVENT_BUS.addListener(AbilityWheelClient :: keyInput);
		NeoForge.EVENT_BUS.addListener(AbilityWheelClient :: mouseButtonInput);
		NeoForge.EVENT_BUS.addListener(AbilityWheelClient :: screenOpening);
	}

	private static void registerKeyMapping(RegisterKeyMappingsEvent event)
	{
		event.register(OPEN_WHEEL);
	}

	private static void screenOpening(ScreenEvent.Opening event)
	{
		if (wheelOpen)
			closeWheel(RenderHelper.mc(), false);
	}

	private static void keyInput(InputEvent.Key event)
	{
		if (OPEN_WHEEL.matches(event.getKey(), event.getScanCode()))
			handleWheelInput(event.getAction());
	}

	private static void mouseButtonInput(InputEvent.MouseButton.Pre event)
	{
		if (OPEN_WHEEL.matchesMouse(event.getButton()))
			handleWheelInput(event.getAction());
	}

	private static void handleWheelInput(int action)
	{
		Minecraft minecraft = RenderHelper.mc();
		if (action == InputConstants.PRESS)
		{
			if (!wheelOpen && minecraft.player != null && minecraft.screen == null)
				openWheel(minecraft);
		}
		else if (action == InputConstants.RELEASE && wheelOpen)
			closeWheel(minecraft, true);
	}

	private static void openWheel(Minecraft minecraft)
	{
		wheelOpen = true;
		minecraft.mouseHandler.releaseMouse();
		updateSelectedSlot(minecraft);
	}

	private static void closeWheel(Minecraft minecraft, boolean activate)
	{
		if (!wheelOpen)
			return;

		if (activate)
		{
			updateSelectedSlot(minecraft);
			NetworkEngine.sendToServer(new C2SActivateAbility(selectedSlot));
		}

		wheelOpen = false;
		minecraft.mouseHandler.grabMouse();
	}

	private static int findSelectedSlot(Minecraft minecraft)
	{
		double mouseX = minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
		double mouseY = minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight();
		double deltaX = mouseX - minecraft.getWindow().getGuiScaledWidth() / 2.0;
		double deltaY = mouseY - minecraft.getWindow().getGuiScaledHeight() / 2.0;
		double degrees = Math.toDegrees(Math.atan2(deltaY, deltaX));
		return Math.floorMod((int)Math.floor((degrees + 120.0) / 60.0), AbilityLoadout.SLOT_COUNT);
	}

	public static boolean isWheelOpen()
	{
		return wheelOpen;
	}

	public static int getSelectedSlot()
	{
		return selectedSlot;
	}

	public static void updateSelectedSlot(Minecraft minecraft)
	{
		selectedSlot = findSelectedSlot(minecraft);
	}
}

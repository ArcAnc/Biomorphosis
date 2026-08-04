/**
 * @author ArcAnc
 * Created at: 01.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability.client;


import com.arcanc.biomorphosis.content.ability.hook.client.HookClientHandler;
import com.arcanc.biomorphosis.content.ability.spike.client.SpikeBarrageClientHandler;
import com.arcanc.biomorphosis.content.ability.wave.client.WaveClientHandler;
import net.neoforged.bus.api.IEventBus;

public class ClientAbilitiesHandler
{
	public static void register(final IEventBus modEventBus)
	{
		AbilityWheelClient.init(modEventBus);
		
		WaveClientHandler.init(modEventBus);
		HookClientHandler.init(modEventBus);
		SpikeBarrageClientHandler.init(modEventBus);
	}
}

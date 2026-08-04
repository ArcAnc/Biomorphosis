/**
 * @author ArcAnc
 * Created at: 01.08.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;


import com.arcanc.biomorphosis.content.ability.hook.HookHandler;
import com.arcanc.biomorphosis.content.ability.spike.SpikeBarrageHandler;
import com.arcanc.biomorphosis.content.ability.wave.WaveHandler;
import net.neoforged.bus.api.IEventBus;

public class CommonAbilitiesHandler
{
	public static void register(final IEventBus modEventBus)
	{
		AbilityAcquisitionHandler.register();
		AbilityCastingHandler.register();
		
		WaveHandler.register();
		HookHandler.register();
		SpikeBarrageHandler.register();
	}
}

/**
 * @author ArcAnc
 * Created at: 22.06.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.effect;

import com.arcanc.biomorphosis.content.entity.Infestor;
import com.arcanc.biomorphosis.content.registration.Registration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

public class InfestationHandler
{
	public static void register(IEventBus modEventBus)
	{
		NeoForge.EVENT_BUS.addListener(InfestationHandler :: onTargetChanged);
	}

	private static void onTargetChanged(final LivingChangeTargetEvent event)
	{
		if (!(event.getNewAboutToBeSetTarget() instanceof Infestor))
			return;
		if (!event.getEntity().hasEffect(Registration.EffectReg.INFESTATION))
			return;
		event.setCanceled(true);
	}
}

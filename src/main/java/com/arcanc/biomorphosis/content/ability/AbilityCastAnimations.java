/**
 * @author ArcAnc
 * Created at: 29.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.ability;

import com.arcanc.biomorphosis.util.Database;
import net.minecraft.resources.ResourceLocation;

public final class AbilityCastAnimations
{
	public static final ResourceLocation NONE = Database.rl("none");
	public static final ResourceLocation WAVE = Database.rl("wave");
	public static final ResourceLocation HOOK = Database.rl("hook");
	public static final ResourceLocation SPIKE_BARRAGE = Database.rl("spike_barrage");

	private AbilityCastAnimations()
	{
	}
}

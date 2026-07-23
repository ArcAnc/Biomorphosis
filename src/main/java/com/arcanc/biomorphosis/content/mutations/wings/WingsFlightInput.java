/**
 * @author ArcAnc
 * Created at: 23.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.mutations.wings;

public record WingsFlightInput(float strafe, float forward, boolean jumping, boolean descending)
{
	public static final WingsFlightInput EMPTY = new WingsFlightInput(0.0F, 0.0F, false, false);

	public boolean hasInput()
	{
		return this.strafe != 0.0F || this.forward != 0.0F || this.jumping || this.descending;
	}
}

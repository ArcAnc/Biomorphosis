/**
 * @author ArcAnc
 * Created at: 23.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component.tooltip;

import net.minecraft.resources.ResourceLocation;

public record TooltipData(boolean isTextured, ResourceLocation background, ResourceLocation decorations, boolean isInterpolated)
{

}

/**
 * @author ArcAnc
 * Created at: 08.04.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;

import com.arcanc.biomorphosis.content.gui.component.tooltip.ICustomTooltip;
import com.arcanc.biomorphosis.content.gui.component.tooltip.StyleData;
import com.arcanc.biomorphosis.content.gui.component.tooltip.TooltipData;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;

public class BioSpawnEgg extends SpawnEggItem implements ItemInterfaces.IMustAddToCreativeTab, ICustomTooltip
{
    private final StyleData style = new StyleData(true, (player, stack) -> new TooltipData(
            true,
            Database.GUI.Textures.Tooltip.TOOLTIP_BACKGROUND,
            Database.GUI.Textures.Tooltip.TOOLTIP_DECORATIONS,
            true));

    public BioSpawnEgg(EntityType<? extends Mob> defaultType, Properties properties)
    {
        super(defaultType, properties);
    }

    @Override
    public StyleData getStyle()
    {
        return this.style;
    }
}

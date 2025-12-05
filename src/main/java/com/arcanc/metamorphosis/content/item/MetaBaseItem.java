/**
 * @author ArcAnc
 * Created at: 28.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.item;

import com.arcanc.metamorphosis.content.gui.component.tooltip.ICustomTooltip;
import com.arcanc.metamorphosis.content.gui.component.tooltip.StyleData;
import com.arcanc.metamorphosis.content.gui.component.tooltip.TooltipData;
import com.arcanc.metamorphosis.util.Database;
import net.minecraft.world.item.Item;

public class MetaBaseItem extends Item implements ItemInterfaces.IMustAddToCreativeTab, ICustomTooltip
{
    private final StyleData style = new StyleData(true, (player, stack) -> new TooltipData(
            true,
            Database.GUI.Textures.Tooltip.TOOLTIP_BACKGROUND,
            Database.GUI.Textures.Tooltip.TOOLTIP_DECORATIONS,
            true));

    public MetaBaseItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public StyleData getStyle()
    {
        return style;
    }
}

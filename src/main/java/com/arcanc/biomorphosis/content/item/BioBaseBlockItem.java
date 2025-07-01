/**
 * @author ArcAnc
 * Created at: 29.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;

import com.arcanc.biomorphosis.content.gui.component.tooltip.ICustomTooltip;
import com.arcanc.biomorphosis.content.gui.component.tooltip.StyleData;
import com.arcanc.biomorphosis.content.gui.component.tooltip.TooltipData;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class BioBaseBlockItem extends BlockItem implements ItemInterfaces.IMustAddToCreativeTab, ICustomTooltip
{
    private final StyleData style = new StyleData(true, (player, stack) -> new TooltipData(
            true,
            Database.GUI.Textures.Tooltip.TOOLTIP_BACKGROUND,
            Database.GUI.Textures.Tooltip.TOOLTIP_DECORATIONS, true));

    private final boolean addToCreative;

    public BioBaseBlockItem(Block block, Properties properties, boolean addToCreative)
    {
        super(block, properties);
        this.addToCreative = addToCreative;
    }

    public BioBaseBlockItem(Block block, Properties properties)
    {
        this(block, properties, true);
    }

    @Override
    public boolean addSelfToCreativeTab()
    {
        return this.addToCreative;
    }

    @Override
    public StyleData getStyle()
    {
        return style;
    }
}

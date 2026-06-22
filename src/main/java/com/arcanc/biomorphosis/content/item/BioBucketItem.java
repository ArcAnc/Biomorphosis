/**
 * @author ArcAnc
 * Created at: 18.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;

import com.arcanc.biomorphosis.content.gui.component.tooltip.ICustomTooltip;
import com.arcanc.biomorphosis.content.gui.component.tooltip.StyleData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

public class BioBucketItem extends BucketItem implements ItemInterfaces.IMustAddToCreativeTab, ICustomTooltip
{
    public BioBucketItem(Fluid content, Properties properties)
    {
        super(content, properties);
    }

    @Override
    public StyleData getStyle()
    {
        return BioBaseItem.STYLE;
    }
}

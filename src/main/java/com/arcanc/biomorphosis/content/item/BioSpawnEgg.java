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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.function.Supplier;

public class BioSpawnEgg extends DeferredSpawnEggItem implements ItemInterfaces.IMustAddToCreativeTab, ICustomTooltip
{
    public BioSpawnEgg(Supplier<EntityType<? extends Mob>> defaultType, int backgroundColor, int highlightColor, Properties properties)
    {
        super(defaultType, backgroundColor, highlightColor, properties);
    }

    @Override
    public StyleData getStyle()
    {
        return BioBaseItem.STYLE;
    }
}

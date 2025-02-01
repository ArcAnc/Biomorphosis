/**
 * @author ArcAnc
 * Created at: 26.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.item;

import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BioBook extends BioBaseItem
{
    public BioBook(Properties properties)
    {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand)
    {
        if (level.isClientSide())
            RenderHelper.openGuideScreen();
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context)
    {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.isClientSide())
            RenderHelper.openGuideScreen();
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }
}

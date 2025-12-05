/**
 * @author ArcAnc
 * Created at: 28.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.util.enumextensions;

import com.arcanc.metamorphosis.util.Database;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.awt.*;
import java.util.function.UnaryOperator;

public class RarityExtension
{
    public static final EnumProxy<Rarity> META_COMMON = new EnumProxy<>(Rarity.class,
            -1,
            Database.rlStr("common"),
            (UnaryOperator<Style>) style -> style.withColor(
                    TextColor.fromRgb(
                                    new Color(70, 185, 34, 255).getRGB())).
                    withItalic(false));

    public static final EnumProxy<Rarity> META_RARE = new EnumProxy<>(Rarity.class,
            -1,
            Database.rlStr("rare"),
            (UnaryOperator<Style>) style -> style.withColor(
                    TextColor.fromRgb(
                            new Color(155, 34, 180).getRGB())).
                    withItalic(true));

    public static final EnumProxy<Rarity> META_ULTRA_RARE = new EnumProxy<>(Rarity.class,
            -1,
            Database.rlStr("ultra_rare"),
            (UnaryOperator<Style>) style -> style.withColor(
                    TextColor.fromRgb(
                            new Color(227, 163, 37).getRGB())).
                    withItalic(true));
}

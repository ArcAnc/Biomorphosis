/**
 * @author ArcAnc
 * Created at: 28.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.enumextensions;

import com.arcanc.biomorphosis.util.Database;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.awt.*;
import java.util.function.UnaryOperator;

public class RarityExtension
{
    public static final EnumProxy<Rarity> BIO_COMMON = new EnumProxy<>(Rarity.class,
            -1,
            Database.rlStr("common"),
            (UnaryOperator<Style>) style -> style.withColor(
                    TextColor.fromRgb(
                                    new Color(90, 133, 50).getRGB())).
                    withItalic(false));

    public static final EnumProxy<Rarity> BIO_RARE = new EnumProxy<>(Rarity.class,
            -1,
            Database.rlStr("rare"),
            (UnaryOperator<Style>) style -> style.withColor(
                    TextColor.fromRgb(
                            new Color(110, 11, 110).getRGB())).
                    withItalic(true));

    public static final EnumProxy<Rarity> BIO_ULTRA_RARE = new EnumProxy<>(Rarity.class,
            -1,
            Database.rlStr("ultra_rare"),
            (UnaryOperator<Style>) style -> style.withColor(
                    TextColor.fromRgb(
                            new Color(156, 109, 9).getRGB())).
                    withItalic(true));
}

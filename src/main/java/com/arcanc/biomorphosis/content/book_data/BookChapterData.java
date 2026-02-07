/**
 * @author ArcAnc
 * Created at: 30.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record BookChapterData(ResourceLocation id, String author, String title, int weight)
{
    public static final Codec<BookChapterData> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(BookChapterData :: id),
                    Codec.STRING.optionalFieldOf("author", "").forGetter(BookChapterData :: author),
                    Codec.STRING.fieldOf("title").forGetter(BookChapterData :: title),
                    Codec.INT.fieldOf("weight").forGetter(BookChapterData :: weight)
            ).apply(instance, BookChapterData :: new)));
}

/**
 * @author ArcAnc
 * Created at: 30.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.book_data;

import com.arcanc.metamorphosis.content.book_data.chapter.AbstractBookChapter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BookPageData(String author, String title, ResourceLocation chapter, String text, List<String> tags)
{
    public static final Codec<BookPageData> CODEC = Codec.lazyInitialized(() ->
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.optionalFieldOf("author", "").forGetter(BookPageData :: author),
                    Codec.STRING.optionalFieldOf("title", "").forGetter(BookPageData :: title),
                    ResourceLocation.CODEC.fieldOf("chapter").forGetter(BookPageData :: chapter),
                    Codec.STRING.fieldOf("text").forGetter(BookPageData :: text),
                    Codec.STRING.listOf().fieldOf("tags").forGetter(BookPageData :: tags)
            ).apply(instance, BookPageData :: new)));


    public static @NotNull BookPageData getTitleData(@NotNull AbstractBookChapter chapter)
    {
        return new BookPageData("ArcAnc", "", chapter.getData().id(), "", List.of());
    }
}

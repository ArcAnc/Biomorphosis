/**
 * @author ArcAnc
 * Created at: 30.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.book;

import com.arcanc.biomorphosis.content.book_data.BookPageData;
import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookPageBuilder
{
    private String author;
    private String title;
    private ResourceLocation chapter;
    private String text;
    private List<String> tags = new ArrayList<>();

    private BookPageBuilder()
    {}

    @Contract(value = " -> new", pure = true)
    public static @NotNull BookPageBuilder newPage()
    {
        return new BookPageBuilder();
    }

    public BookPageBuilder setAuthor(String author)
    {
        Preconditions.checkNotNull(author);
        author = author.trim();
        if (author.isEmpty() || author.isBlank())
            author = "";
        this.author = author;
        return this;
    }

    public BookPageBuilder setTitle(String title)
    {
        Preconditions.checkNotNull(title);
        this.title = title;
        return this;
    }

    public BookPageBuilder setChapter(ResourceLocation chapter)
    {
        Preconditions.checkNotNull(chapter);
        if (chapter.getNamespace().equals("minecraft"))
            throw new RuntimeException("Are you kidding me? Guide book for vanilla chapter? NO WAY! Change namespace");
        this.chapter = chapter;
        return this;
    }

    public BookPageBuilder setTags(List<String> tags)
    {
        Preconditions.checkNotNull(tags);
        this.tags = tags;
        return this;
    }

    public BookPageBuilder addTags(String... str)
    {
        Preconditions.checkNotNull(str);
        Arrays.stream(str).forEach(this :: addTag);
        return this;
    }

    public BookPageBuilder addTag(String str)
    {
        Preconditions.checkNotNull(str);
        this.tags.add(str);
        return this;
    }

    public BookPageBuilder setText(String text)
    {
        Preconditions.checkNotNull(text);
        this.text = text;
        return this;
    }

    public BookPageData end()
    {
        return new BookPageData(this.author, this.title, this.chapter, this.text, this.tags);
    }
}

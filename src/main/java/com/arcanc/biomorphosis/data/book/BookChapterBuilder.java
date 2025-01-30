/**
 * @author ArcAnc
 * Created at: 30.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data.book;

import com.arcanc.biomorphosis.content.book_data.BookChapterData;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BookChapterBuilder
{
    private String author;
    private String title;
    private int weight;

    private BookChapterBuilder()
    {}

    @Contract(value = " -> new", pure = true)
    public static @NotNull BookChapterBuilder newChapter()
    {
        return new BookChapterBuilder();
    }

    public BookChapterBuilder setAuthor(String author)
    {
        Preconditions.checkNotNull(author);
        author = author.trim();
        if (author.isEmpty() || author.isBlank())
            author = "";
        this.author = author;
        return this;
    }

    public BookChapterBuilder setTitle(String title)
    {
        Preconditions.checkNotNull(title);
        this.title = title.trim();
        return this;
    }

    public BookChapterBuilder setWeight(int weight)
    {
        if (weight < 0)
            throw new RuntimeException("Weight can't be lower than 0");
        this.weight = weight;
        return this;
    }

    public BookChapterData end()
    {
        return new BookChapterData(this.author, this.title, this.weight);
    }
}

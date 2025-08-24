/**
 * @author ArcAnc
 * Created at: 30.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;

import com.arcanc.biomorphosis.content.book_data.BookChapterData;
import com.arcanc.biomorphosis.content.book_data.BookPageData;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.book.BookChapterBuilder;
import com.arcanc.biomorphosis.data.book.BookPageBuilder;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BioBookProvider extends BioRegistryData
{
    private final Map<ResourceLocation, BookChapterData> chapterDataMap = new HashMap<>();
    private final Map<ResourceLocation, BookPageData> pageDataMap = new HashMap<>();

    public BioBookProvider()
    {
        super();
    }

    @Override
    protected void addContent()
    {
        addChapter(Database.GUI.GuideBook.Chapters.TITLE.location(), BookChapterBuilder.newChapter().
                setId(Database.GUI.GuideBook.Chapters.TITLE.location()).
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Chapters.TITLE.langKey()).
                setWeight(0).
                end());
        addChapter(Database.GUI.GuideBook.Chapters.BASIC.location(), BookChapterBuilder.newChapter().
                setId(Database.GUI.GuideBook.Chapters.BASIC.location()).
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Chapters.BASIC.langKey()).
                setWeight(1).
                end());
        addChapter(Database.GUI.GuideBook.Chapters.ADVANCED.location(), BookChapterBuilder.newChapter().
                setId(Database.GUI.GuideBook.Chapters.ADVANCED.location()).
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Chapters.ADVANCED.langKey()).
                setWeight(2).
                end());
        //-----------------------------------------------------------------------
        // TITLE CHAPTER
        //-----------------------------------------------------------------------
        addPage(Database.GUI.GuideBook.Pages.V001.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.V001.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.V001.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
                addTags("Patch Notes").
                end());
        addPage(Database.GUI.GuideBook.Pages.V002.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.V002.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.V002.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
                addTags("Patch Notes").
                end());
        addPage(Database.GUI.GuideBook.Pages.V003.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.V003.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.V003.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
                addTags("Patch Notes").
                end());
        addPage(Database.GUI.GuideBook.Pages.V004.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.V004.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.V004.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
                addTags("Patch Notes").
                end());
        addPage(Database.GUI.GuideBook.Pages.V005.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.V005.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.V005.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
                addTags("Patch Notes").
                end());
        //-----------------------------------------------------------------------
        // BASIC CHAPTER
        //-----------------------------------------------------------------------
        addPage(Database.GUI.GuideBook.Pages.TEST_BASIC_1.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.TEST_BASIC_1.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.TEST_BASIC_1.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
                addTags("Test", "ForTest", "AnotherTest").
                end());
        addPage(Database.GUI.GuideBook.Pages.TEST_BASIC_2.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.TEST_BASIC_2.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.TEST_BASIC_2.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
                addTags("Boo", "Foo", "Moo").
                end());
        //-----------------------------------------------------------------------
        // ADVANCED CHAPTER
        //-----------------------------------------------------------------------
        addPage(Database.GUI.GuideBook.Pages.TEST_PAGE_3.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.TEST_PAGE_3.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.TEST_PAGE_3.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.ADVANCED.location()).
                addTags("More", "Another", "WOW").
                end());
    }

    private void addChapter(ResourceLocation location, BookChapterData data)
    {
        this.chapterDataMap.putIfAbsent(location, data);
    }

    private void addPage(ResourceLocation location, BookPageData data)
    {
        this.pageDataMap.putIfAbsent(location, data);
    }

    @Override
    protected void registerContent(@NotNull RegistrySetBuilder registrySetBuilder)
    {
        registrySetBuilder.add(Registration.BookDataReg.CHAPTER_KEY, context ->
                this.chapterDataMap.forEach((location, bookChapterData) ->
                        context.register(getChapterKey(location), bookChapterData)));
        registrySetBuilder.add(Registration.BookDataReg.PAGE_KEY, context ->
                this.pageDataMap.forEach((location, bookPageData) ->
                        context.register(getPageKey(location), bookPageData)));
    }

    private @NotNull ResourceKey<BookPageData> getPageKey(ResourceLocation location)
    {
        Preconditions.checkNotNull(location);
        return getResourceKey(Registration.BookDataReg.PAGE_KEY, location);
    }

    private @NotNull ResourceKey<BookChapterData> getChapterKey(ResourceLocation location)
    {
        Preconditions.checkNotNull(location);
        return getResourceKey(Registration.BookDataReg.CHAPTER_KEY, location);
    }
}

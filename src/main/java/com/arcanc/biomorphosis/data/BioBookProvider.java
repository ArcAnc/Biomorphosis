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
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BioBookProvider extends RegistrySetBuilder
{
    private final Map<ResourceLocation, BookChapterData> chapterDataMap = new HashMap<>();
    private final Map<ResourceLocation, BookPageData> pageDataMap = new HashMap<>();

    public BioBookProvider()
    {}

    private void addContent()
    {
        addChapter(Database.GUI.GuideBook.Chapters.BASIC.location(), BookChapterBuilder.newChapter().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Chapters.BASIC.langKey()).
                setWeight(1).
                end());

        addChapter(Database.GUI.GuideBook.Chapters.ADVANCED.location(), BookChapterBuilder.newChapter().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Chapters.ADVANCED.langKey()).
                setWeight(2).
                end());

        addPage(Database.GUI.GuideBook.Pages.TEST_PAGE_1.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.TEST_PAGE_1.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.TEST_PAGE_1.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
                addTags("Test", "ForTest", "AnotherTest").
                end());
        addPage(Database.GUI.GuideBook.Pages.TEST_PAGE_2.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.TEST_PAGE_2.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.TEST_PAGE_2.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
                addTags("Boo", "Foo", "Moo").
                end());
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
        chapterDataMap.putIfAbsent(location, data);
    }

    private void addPage(ResourceLocation location, BookPageData data)
    {
        pageDataMap.putIfAbsent(location, data);
    }

    public static @NotNull BioBookProvider registerBookContent()
    {
        BioBookProvider bookProvider = new BioBookProvider();
        bookProvider.addContent();
        bookProvider.registerContent();
        return bookProvider;
    }

    private void registerContent()
    {
        addChapters(context ->
                chapterDataMap.forEach((location, bookChapterData) ->
                        context.register(getChapterKey(location), bookChapterData)));
        addPages(context ->
                pageDataMap.forEach((location, bookPageData) ->
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

    private <T> @NotNull ResourceKey<T> getResourceKey(ResourceKey<Registry<T>> registryKey, ResourceLocation location)
    {
        return ResourceKey.create(registryKey, location);
    }

    private void addChapters(RegistryBootstrap<BookChapterData> bootstrap)
    {
        this.add(Registration.BookDataReg.CHAPTER_KEY, bootstrap);
    }

    private void addPages(RegistryBootstrap<BookPageData> bootstrap)
    {
        this.add(Registration.BookDataReg.PAGE_KEY, bootstrap);
    }
}

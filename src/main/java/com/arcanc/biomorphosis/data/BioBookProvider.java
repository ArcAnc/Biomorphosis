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
	    addPage(Database.GUI.GuideBook.Pages.V006.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.V006.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.V006.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
			    addTags("Patch Notes").
			    end());
	    addPage(Database.GUI.GuideBook.Pages.V007.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.V007.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.V007.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
			    addTags("Patch Notes").
			    end());
	    addPage(Database.GUI.GuideBook.Pages.V0071.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.V0071.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.V0071.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
			    addTags("Patch Notes").
			    end());
	    addPage(Database.GUI.GuideBook.Pages.V008.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.V008.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.V008.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
			    addTags("Patch Notes").
			    end());
	    addPage(Database.GUI.GuideBook.Pages.V0081.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.V0081.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.V0081.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
			    addTags("Patch Notes").
			    end());
	    addPage(Database.GUI.GuideBook.Pages.V0082.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.V0082.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.V0082.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
			    addTags("Patch Notes").
			    end());
	    addPage(Database.GUI.GuideBook.Pages.V261.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.V261.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.V261.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.TITLE.location()).
			    addTags("Patch Notes").
			    end());
        //-----------------------------------------------------------------------
        // BASIC CHAPTER
        //-----------------------------------------------------------------------
        addPage(Database.GUI.GuideBook.Pages.FLESH.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.FLESH.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.FLESH.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
                addTags("flesh").
                end());
        addPage(Database.GUI.GuideBook.Pages.NORPH_SOURCE.location(), BookPageBuilder.newPage().
                setAuthor("ArcAnc").
                setTitle(Database.GUI.GuideBook.Pages.NORPH_SOURCE.titleLangKey()).
                setText(Database.GUI.GuideBook.Pages.NORPH_SOURCE.textLangKey()).
                setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
                addTags("norph", "source").
                end());
	    addPage(Database.GUI.GuideBook.Pages.CHAMBER.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.CHAMBER.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.CHAMBER.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
			    addTags("chamber", "morpher", "morph").
			    end());
	    addPage(Database.GUI.GuideBook.Pages.STORAGES.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.STORAGES.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.STORAGES.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
			    addTags("chest", "item", "fluids", "barrel").
			    end());
	    
	    addPage(Database.GUI.GuideBook.Pages.CRUSHER.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.CRUSHER.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.CRUSHER.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
			    addTags("crusher").
			    end());
	    
	    addPage(Database.GUI.GuideBook.Pages.FORGE.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.FORGE.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.FORGE.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
			    addTags("forge").
			    end());
	    
	    addPage(Database.GUI.GuideBook.Pages.SQUEEZER.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.SQUEEZER.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.SQUEEZER.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
			    addTags("squeezer").
			    end());
		
	    addPage(Database.GUI.GuideBook.Pages.STOMACH.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.STOMACH.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.STOMACH.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
			    addTags("stomach", "organic", "reprocessor").
			    end());
	    
	    addPage(Database.GUI.GuideBook.Pages.CATCHER.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.CATCHER.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.CATCHER.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
			    addTags("catcher", "mob", "entity").
			    end());
		
		addPage(Database.GUI.GuideBook.Pages.MANIPULATOR.location(), BookPageBuilder.newPage().
				setAuthor("ArcAnc").
				setTitle(Database.GUI.GuideBook.Pages.MANIPULATOR.titleLangKey()).
				setText(Database.GUI.GuideBook.Pages.MANIPULATOR.textLangKey()).
				setChapter(Database.GUI.GuideBook.Chapters.BASIC.location()).
				addTags("manipulator", "use", "wrench").
				end());
		
        //-----------------------------------------------------------------------
        // ADVANCED CHAPTER
        //-----------------------------------------------------------------------
	    
	    addPage(Database.GUI.GuideBook.Pages.FLUID_TRANSMITTER.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.FLUID_TRANSMITTER.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.FLUID_TRANSMITTER.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.ADVANCED.location()).
			    addTags("transmitter", "fluid", "transport").
			    end());
	    
		addPage(Database.GUI.GuideBook.Pages.TURRET.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.TURRET.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.TURRET.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.ADVANCED.location()).
			    addTags("turret", "protection", "mobs").
			    end());
	    
	    addPage(Database.GUI.GuideBook.Pages.CHRYSALIS.location(), BookPageBuilder.newPage().
			    setAuthor("ArcAnc").
			    setTitle(Database.GUI.GuideBook.Pages.CHRYSALIS.titleLangKey()).
			    setText(Database.GUI.GuideBook.Pages.CHRYSALIS.textLangKey()).
			    setChapter(Database.GUI.GuideBook.Chapters.ADVANCED.location()).
			    addTags("chrysalis", "mutation", "evolution").
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

/**
 * @author ArcAnc
 * Created at: 29.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data;

import com.arcanc.biomorphosis.content.book_data.chapter.AbstractBookChapter;
import com.arcanc.biomorphosis.content.book_data.chapter.NormalBookChapter;
import com.arcanc.biomorphosis.content.book_data.page.AbstractBookPage;
import com.arcanc.biomorphosis.content.book_data.page.NormalBookPage;
import com.arcanc.biomorphosis.content.book_data.page.TitleBookPage;
import com.arcanc.biomorphosis.content.gui.screen.GuideScreen;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Holder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class BookData
{
    private static BookData INSTANCE;

    /**
     * Key - book chapter location,
     * Value - pages for chose chapter*/
    private final Map<AbstractBookChapter, List<AbstractBookPage>> content = new LinkedHashMap<>();

    public final Deque<BookHistoryEntry> history = new ArrayDeque<>();
    private GuideScreen screen;

    public static BookData getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new BookData();
            INSTANCE.initBookData();
        }
        return INSTANCE;
    }

    public void reCalcPositions()
    {
        int upper = 0, bottom = 0;
        int guiLeft = screen == null ? 0 : screen.getGuiLeft();
        int guiTop = screen == null ? 0 : screen.getGuiTop();

        AbstractBookChapter.setPageZones(new Rect2i(guiLeft + 18, guiTop + 15, 107, 148),
                new Rect2i(guiLeft + 165, guiTop + 15, 107, 148));

        for (AbstractBookChapter chapter : this.content.keySet())
        {
            boolean isNative = chapter.isNative();
            chapter.setPosition(
                    isNative ? guiLeft + 15 + (20 * bottom) + (4 * bottom) : guiLeft + 15 * (20 * upper) + (4 * upper),
                    isNative ? guiTop + 175 : guiTop + 21);
            for (AbstractBookPage page : this.content.get(chapter))
                page.reCalcPositions();
            if (isNative)
                bottom++;
            else
                upper++;
        }
    }

    public Map<AbstractBookChapter, List<AbstractBookPage>> getContent()
    {
        return this.content;
    }

    private void initBookData()
    {
        Minecraft mc = RenderHelper.mc();
        ClientLevel level = mc.level;
        if (level == null)
            return;
        ClientPacketListener connection = mc.getConnection();
        if (connection == null)
            return;
        List<AbstractBookChapter> chapters = connection.registryAccess().
                lookup(Registration.BookDataReg.CHAPTER_KEY).
                map(registry -> registry.
                        listElements().
                        map(Holder.Reference :: value).
                        map(NormalBookChapter:: new).
                        sorted(Comparator.comparing(chapter -> chapter.
                                getData().
                                weight())).
                        map(chapter -> (AbstractBookChapter)chapter).
                        collect(Collectors.toList())).
                orElse(new ArrayList<>());
        List<AbstractBookPage> pages = connection.registryAccess().
                lookup(Registration.BookDataReg.PAGE_KEY).
                map(registry -> registry.
                        listElements().
                        map(Holder.Reference :: value).
                        map(NormalBookPage:: new).
                        map(page -> (AbstractBookPage)page).
                        collect(Collectors.toList())).
                orElse(new ArrayList<>());

        chapters.forEach(chapter ->
                this.content.computeIfAbsent(chapter, key ->
                        Stream.concat(
                                Stream.of(new TitleBookPage(chapter)),
                                pages.stream()
                                        .filter(page -> page.getData().chapter().equals(chapter.getData().id()))
                        ).collect(Collectors.toList())
                )
        );
    }

    public void addNewHistoryEntry(AbstractBookChapter chapter, int page, int subPage)
    {
        this.history.addLast(new BookHistoryEntry(chapter, page, subPage));
    }

    public BookHistoryEntry getLastHistoryEntry()
    {
        return this.history.pollLast();
    }

    public BookHistoryEntry getFirstHistoryEntry()
    {
        return this.history.pollFirst();
    }

    public BookHistoryEntry getFirstAndClear()
    {
        BookHistoryEntry entry = getFirstHistoryEntry();
        this.history.clear();
        return entry;
    }

    public int getHistorySize()
    {
        return this.history.size();
    }

    public void setScreen(GuideScreen screen)
    {
        this.screen = screen;
    }

    public GuideScreen getScreen()
    {
        return this.screen;
    }

    public @Nullable AbstractBookChapter getCurrentChapter()
    {
        return this.screen.getCurrentChapter();
    }

    public @Nullable AbstractBookPage getCurrentPage()
    {
        return this.content.get(getCurrentChapter()).get(this.screen.getCurrentPage());
    }

    public int getCurrentSubpage()
    {
        return this.screen.getCurrentSubPage();
    }

    public record BookHistoryEntry(AbstractBookChapter chapter, int page, int subPage){}
}

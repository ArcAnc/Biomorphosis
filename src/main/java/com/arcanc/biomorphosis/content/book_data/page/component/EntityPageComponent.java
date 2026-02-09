/**
 * @author ArcAnc
 * Created at: 02.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.book_data.page.component;

import com.arcanc.biomorphosis.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.jetbrains.annotations.NotNull;

public class EntityPageComponent extends AbstractPageComponent
{
    private final LivingEntity entity;

    public EntityPageComponent(ResourceLocation location)
    {
        super(0, 0, 0, 0, Component.empty());

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(location);
        Entity ent = type.create(RenderHelper.mc().level);
        if (ent instanceof LivingEntity)
            entity = (LivingEntity)ent;
        else
            entity = null;
        if (entity != null)
        {
            switch (entity)
            {
                case Zombie zombie -> this.setSize(25, 35);
                case EnderMan enderMan -> this.setSize(25, 50);
                case CaveSpider caveSpider -> this.setSize(30, 25);
                case Spider spider -> this.setSize(45, 30);
                case Slime slime -> this.setSize(35, 35);
                case Ghast ghast -> this.setSize(40, 40);
                case Villager villager -> this.setSize(25, 38);
                case Shulker shulker -> this.setSize(30, 30);
                case Blaze blaze -> this.setSize(30, 40);
                case IronGolem ironGolem -> this.setSize(35, 45);
                case AbstractSkeleton abstractSkeleton -> this.setSize(25, 35);
                case AbstractIllager abstractIllager -> this.setSize(25, 37);
                case Ravager ravager -> this.setSize(40, 35);
                case AbstractPiglin abstractPiglin -> this.setSize(25, 35);
                case Endermite endermite -> this.setSize(30, 15);
                case Silverfish silverfish -> this.setSize(30, 15);
                case Phantom phantom -> this.setSize(35, 20);
                case Pig pig -> this.setSize(30, 25);
                case Cow cow -> this.setSize(35, 30);
                case Sheep sheep -> this.setSize(32, 28);
                case Chicken chicken -> this.setSize(20, 20);
                case Wolf wolf -> this.setSize(30, 25);
                case AbstractHorse abstractHorse -> this.setSize(35, 38);
                case Cat cat -> this.setSize(28, 20);
                case WanderingTrader wanderingTrader -> this.setSize(25, 38);
                case Panda panda -> this.setSize(40, 30);
                case Dolphin dolphin -> this.setSize(35, 20);
                case WitherBoss witherBoss -> this.setSize(35, 40);
                case EnderDragon enderDragon -> this.setSize(45, 35);
                case Warden warden -> this.setSize(40, 30);
                default -> this.setSize(45, 45);
            }
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick)
    {
        if (entity != null)
        {
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, this.getX(), this.getY(), this.getX() + getWidth(), this.getY() + getHeight(), 15, 0.0625F, mouseX, mouseY, entity);
            if (isHovered())
                guiGraphics.renderTooltip(RenderHelper.mc().font, entity.getType().getDescription(), mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput)
    {

    }
}

/**
 * @author ArcAnc
 * Created at: 19.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.screen.container;


import com.arcanc.biomorphosis.content.block.multiblock.MultiblockTurret;
import com.arcanc.biomorphosis.content.gui.component.TexturedCycleButton;
import com.arcanc.biomorphosis.content.gui.component.info.FluidInfoArea;
import com.arcanc.biomorphosis.content.gui.container_menu.TurretMenu;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class TurretScreen extends BioContainerScreen<TurretMenu>
{
	public TurretScreen(TurretMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		this.imageHeight = 176;
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		BlockHelper.castTileEntity(RenderHelper.mc().level, this.menu.getBlockPos(), MultiblockTurret.class).ifPresent(turret ->
		{
			addRenderableWidget(TexturedCycleButton.<MultiblockTurret.TurretEffect>builder(effect -> Component.literal(effect.getName())).
					withBaseValues(MultiblockTurret.TurretEffect.values()).
					withTooltip(value -> Tooltip.create(Database.GUI.TurretButton.effectTooltip.apply(value))).
					withInitialValue(turret.getShootEffect()).
					withTextureProvider(effect -> Database.rl("textures/gui/elements/turret/effect/" + effect.getName() + ".png")).
					create( this.getGuiLeft() + 60,
						    this.getGuiTop() + 15,
							25,
							25,
							Component.literal("effect button"),
							(cycleButton, effect) ->
									sendUpdateToServer(tag -> tag.putInt("new_effect", effect.ordinal()))));
			
			addRenderableWidget(TexturedCycleButton.<MultiblockTurret.TargetMode>builder(mode -> Component.literal(mode.name().toLowerCase())).
					withBaseValues(MultiblockTurret.TargetMode.values()).
					withTooltip(value -> Tooltip.create(Database.GUI.TurretButton.targetModeTooltip.apply(value))).
					withInitialValue(turret.getTargetMode()).
					withTextureProvider(mode -> Database.rl("textures/gui/elements/turret/target_mode/" + mode.name().toLowerCase() + ".png")).
					create( this.getGuiLeft() + 60,
							this.getGuiTop() + 50,
							25,
							25,
							Component.literal("mode button"),
							(cycleButton, mode) ->
									sendUpdateToServer(tag -> tag.putInt("new_mode", mode.ordinal()))));
			
			addRenderableWidget(new TexturedCycleButton.TurretOnOffButton(
					this.getGuiLeft() + 25,
					this.getGuiTop() + 32,
					25,
					25,
					Component.empty(),
					Component.literal("enabled button"),
					0,
					turret.isEnabled(),
					CycleButton.ValueListSupplier.create(ImmutableList.of(Boolean.TRUE, Boolean.FALSE)),
					aBoolean -> Component.literal(aBoolean ? "enabled" : "disabled"),
					CycleButton::createDefaultNarrationMessage,
					(cycleButton, bool) ->
							sendUpdateToServer(tag -> tag.putBoolean("power", bool)),
					value -> Tooltip.create(Database.GUI.TurretButton.onOffTooltip.apply(value))
			));
			
			FluidSidedStorage fluidStorage = MultiblockTurret.getFluidHandler(turret, null);
			if (fluidStorage == null)
				return;
			fluidStorage.getHolderAt(null, 0).ifPresent(biomass ->
					addInfoArea(new FluidInfoArea(biomass, new Rect2i(this.getGuiLeft() + 100, this.getGuiTop() + 25, 20, 40))));
			fluidStorage.getHolderAt(null, 1).ifPresent(acid ->
					addInfoArea(new FluidInfoArea(acid, new Rect2i(this.getGuiLeft() + 125, this.getGuiTop() + 25, 20, 40))));
			fluidStorage.getHolderAt(null, 2).ifPresent(adrenaline ->
					addInfoArea(new FluidInfoArea(adrenaline, new Rect2i(this.getGuiLeft() + 150, this.getGuiTop() + 25, 20, 40))));
		});
	}
	
	@Override
	protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY)
	{
	}
}

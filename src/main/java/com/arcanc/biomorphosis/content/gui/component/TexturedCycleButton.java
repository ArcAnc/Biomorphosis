/**
 * @author ArcAnc
 * Created at: 25.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.component;


import com.arcanc.biomorphosis.content.gui.component.animation.AnimatedTexture;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public class TexturedCycleButton<T> extends CycleButton<T>
{
	private final Function<T, ResourceLocation> textureProvider;
	
	protected TexturedCycleButton(int x,
	                              int y,
	                              int width,
	                              int height,
	                              Component message,
	                              Component name,
	                              int index,
	                              T value,
	                              ValueListSupplier<T> values,
	                              Function<T, Component> valueStringifier,
	                              Function<CycleButton<T>, MutableComponent> narrationProvider,
	                              OnValueChange<T> onValueChange,
	                              OptionInstance.TooltipSupplier<T> tooltipSupplier,
	                              Function<T, ResourceLocation> textureProvider)
	{
		super(x, y, width, height, message, name, index, value, values, valueStringifier, narrationProvider, onValueChange, tooltipSupplier, false);
		this.textureProvider = textureProvider;
	}
	
	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
	{
		guiGraphics.blit(
				this.textureProvider.apply(this.getValue()),
				this.getX(), this.getY(),
				this.getWidth(), this.getHeight(),
				0, 0,
				16, 16,
				16, 16);
	}
	
	public static TexturedBuilder<Boolean> onOffBuilder(Function<Boolean, Component> componentProvider)
	{
		return new TexturedBuilder<>(componentProvider).
				withValues(ImmutableList.of(Boolean.TRUE, Boolean.FALSE));
	}
	
	public static <T> TexturedBuilder<T> builder(Function<T, Component> valueStringifier)
	{
		return new TexturedBuilder<>(valueStringifier);
	}
	
	public static class TexturedBuilder<T> extends Builder<T>
	{
		private Function<T, ResourceLocation> textureProvider;
		
		public TexturedBuilder(Function<T, Component> valueStringifier)
		{
			super(valueStringifier);
		}
		
		public TexturedBuilder<T> withValues(Collection<T> values)
		{
			this.withValues(CycleButton.ValueListSupplier.create(values));
			return this;
		}
		
		@SafeVarargs
		public final TexturedBuilder<T> withBaseValues(T... values)
		{
			this.withValues(ImmutableList.copyOf(values));
			return this;
		}
		
		public TexturedBuilder<T> withValues(List<T> defaultList, List<T> selectedList)
		{
			this.withValues(CycleButton.ValueListSupplier.create(CycleButton.DEFAULT_ALT_LIST_SELECTOR, defaultList, selectedList));
			return this;
		}
		
		public TexturedBuilder<T> withValues(BooleanSupplier altListSelector, List<T> defaultList, List<T> selectedList)
		{
			this.withValues(CycleButton.ValueListSupplier.create(altListSelector, defaultList, selectedList));
			return this;
		}
		
		public TexturedBuilder<T> withValues(CycleButton.ValueListSupplier<T> values)
		{
			super.withValues(values);
			return this;
		}
		
		public TexturedBuilder<T> withTooltip(OptionInstance.TooltipSupplier<T> tooltipSupplier)
		{
			super.withTooltip(tooltipSupplier);
			return this;
		}
		
		public TexturedBuilder<T> withInitialValue(T initialValue)
		{
			super.withInitialValue(initialValue);
			return this;
		}
		
		public TexturedBuilder<T> withCustomNarration(Function<CycleButton<T>, MutableComponent> narrationProvider)
		{
			super.withCustomNarration(narrationProvider);
			return this;
		}
		
		public TexturedBuilder<T> displayOnlyValue()
		{
			super.displayOnlyValue();
			return this;
		}
		
		public TexturedBuilder<T> withTextureProvider(Function<T, ResourceLocation> textureProvider)
		{
			this.textureProvider = textureProvider;
			return this;
		}
		
		public TexturedCycleButton<T> create(Component message, CycleButton.OnValueChange<T> onValueChange)
		{
			return this.create(0, 0, 150, 20, message, onValueChange);
		}
		
		public TexturedCycleButton<T> create(int x, int y, int width, int height, Component name)
		{
			return this.create(x, y, width, height, name, (button, value) -> { });
		}
		
		public TexturedCycleButton<T> create(int x, int y, int width, int height, Component name, CycleButton.OnValueChange<T> onValueChange)
		{
			List<T> list = this.values.getDefaultList();
			if (list.isEmpty())
				throw new IllegalStateException("No values for cycle button");
			else
			{
				T t = this.initialValue != null ? this.initialValue : list.get(this.initialIndex);
				Component component = this.valueStringifier.apply(t);
				Component component1 = (this.displayOnlyValue ? component : CommonComponents.optionNameValue(name, component));
				return new TexturedCycleButton<>(
						x,
						y,
						width,
						height,
						component1,
						name,
						this.initialIndex,
						t,
						this.values,
						this.valueStringifier,
						this.narrationProvider,
						onValueChange,
						this.tooltipSupplier,
						this.textureProvider
				);
			}
		}
	}
	
	public static final class TurretOnOffButton extends TexturedCycleButton<Boolean>
	{
		private static final ResourceLocation TEXTURE = Database.rl("textures/gui/elements/chamber/button.png");
		private static final ResourceLocation OVERLAY = Database.rl("textures/gui/elements/chamber/button_overlay.png");
		
		private final AnimatedTexture texture;
		
		public TurretOnOffButton(int x, int y, int width, int height, Component message, Component name, int index, Boolean value, ValueListSupplier<Boolean> values, Function<Boolean, Component> valueStringifier, Function<CycleButton<Boolean>, MutableComponent> narrationProvider, OnValueChange<Boolean> onValueChange, OptionInstance.TooltipSupplier<Boolean> tooltipSupplier)
		{
			super(x, y, width, height, message, name, index, value, values, valueStringifier, narrationProvider, onValueChange, tooltipSupplier, aBoolean -> null);
			this.texture = new AnimatedTexture(new Vector2i(16, 128), new Vector2i(16, 16), 8, TEXTURE);
		}
		
		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
		{
			this.texture.render(guiGraphics, new Rect2i(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
			
			if (this.getValue())
				return;
			guiGraphics.blit(
					OVERLAY,
					this.getX(), this.getY(),
					0,0,
					this.getWidth(), this.getHeight(),
					16, 16,
					16, 16);
		}
	}
}

/**
 * @author ArcAnc
 * Created at: 10.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import com.arcanc.biomorphosis.util.helper.MathHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record BiomeSkybox(ResourceLocation dome,
                          ResourceLocation domeNight,
                          ResourceLocation sun,
                          ResourceLocation moon,
                          int noonColor,
                          int midnightColor,
                          int noonFogColor,
                          int midnightFogColor,
                          float sphereYOffset,
                          List<BiomeSkyboxEffect> effects)
{
	public static Builder builder(ResourceLocation basePath)
	{
		return new Builder(basePath);
	}
	
	public static final class Builder
	{
		private final List<BiomeSkyboxEffect> effects = new ArrayList<>();
		private ResourceLocation dome;
		private ResourceLocation domeNight;
		private ResourceLocation sun;
		private ResourceLocation moon;
		private int noonColor = MathHelper.ColorHelper.color(255, 255, 255);
		private int midnightColor = MathHelper.ColorHelper.color(82, 92, 140);
		private int noonFogColor = MathHelper.ColorHelper.color(255, 255, 255);
		private int midnightFogColor = MathHelper.ColorHelper.color(82, 92, 140);
		private float sphereYOffset = 0.0F;
		
		private Builder(ResourceLocation basePath)
		{
			this.dome = texture(basePath, "dome");
			this.domeNight = texture(basePath, "dome_night");
			this.sun = texture(basePath, "sun");
			this.moon = texture(basePath, "moon");
		}
		
		public Builder dome(ResourceLocation texture)
		{
			this.dome = texture;
			return this;
		}
		
		public Builder domeNight(ResourceLocation texture)
		{
			this.domeNight = texture;
			return this;
		}
		
		public Builder sun(ResourceLocation texture)
		{
			this.sun = texture;
			return this;
		}
		
		public Builder moon(ResourceLocation texture)
		{
			this.moon = texture;
			return this;
		}
		
		public Builder noonColor(int rgb)
		{
			this.noonColor = rgb;
			return this;
		}
		
		public Builder noonColor(int red, int green, int blue)
		{
			return noonColor(MathHelper.ColorHelper.color(red, green, blue));
		}
		
		public Builder midnightColor(int rgb)
		{
			this.midnightColor = rgb;
			return this;
		}
		
		public Builder midnightColor(int red, int green, int blue)
		{
			return midnightColor(MathHelper.ColorHelper.color(red, green, blue));
		}
		
		public Builder noonFogColor(int rgb)
		{
			this.noonFogColor = rgb;
			return this;
		}
		
		public Builder noonFogColor(int red, int green, int blue)
		{
			return noonFogColor(MathHelper.ColorHelper.color(red, green, blue));
		}
		
		public Builder midnightFogColor(int rgb)
		{
			this.midnightFogColor = rgb;
			return this;
		}
		
		public Builder midnightFogColor(int red, int green, int blue)
		{
			return midnightFogColor(MathHelper.ColorHelper.color(red, green, blue));
		}
		
		public Builder sphereYOffset(float offset)
		{
			this.sphereYOffset = offset;
			return this;
		}
		
		public Builder effect(BiomeSkyboxEffect effect)
		{
			this.effects.add(effect);
			return this;
		}
		
		public BiomeSkybox build()
		{
			return new BiomeSkybox(
					this.dome,
					this.domeNight,
					this.sun,
					this.moon,
					this.noonColor,
					this.midnightColor,
					this.noonFogColor,
					this.midnightFogColor,
					this.sphereYOffset,
					List.copyOf(this.effects));
		}
		
		private static ResourceLocation texture(ResourceLocation basePath, String face)
		{
			return basePath.withSuffix("/" + face + ".png").withPrefix("textures/");
		}
	}
}

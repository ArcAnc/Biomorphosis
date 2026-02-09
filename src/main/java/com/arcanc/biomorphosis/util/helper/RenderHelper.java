/**
 * @author ArcAnc
 * Created at: 02.01.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.helper;

import com.arcanc.biomorphosis.content.gui.screen.GuideScreen;
import com.arcanc.biomorphosis.content.gui.screen.container.GenomeScreen;
import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.recipe.ingredient.IngredientWithSize;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.model.obj.BioGeneModel;
import com.arcanc.biomorphosis.util.model.obj.ObjRenderTypes;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.List;

public class RenderHelper
{
    public static @NotNull Minecraft mc()
    {
        return Minecraft.getInstance();
    }

    public static LocalPlayer clientPlayer()
    {
        return mc().player;
    }

    public static @NotNull TextureAtlas textureMap()
    {
        return mc().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
    }

    public static @NotNull TextureAtlasSprite getTexture(ResourceLocation location)
    {
        return textureMap().getSprite(location);
    }

    public static @NotNull ItemRenderer renderItem()
    {
        return mc().getItemRenderer();
    }

    public static void openGuideScreen()
    {
        Minecraft.getInstance().setScreen(new GuideScreen());
    }
	
	public static void openGenomeScreen(Player player, LivingEntity entity)
	{
		Minecraft.getInstance().setScreen(new GenomeScreen(player, entity));
	}

    public static ItemStack getStackAtCurrentTime(@NotNull IngredientWithSize ingredient)
    {
        return getStackAtCurrentTime(ingredient.
		        getItems().
		        peek(stack -> stack.
		                setCount(ingredient.amount())).
		        toList());
    }

    public static ItemStack getStackAtCurrentTime(@NotNull SizedIngredient ingredient)
    {
        return getStackAtCurrentTime(Arrays.stream(ingredient.getItems()).toList());
    }

    public static ItemStack getStackAtCurrentTime(@NotNull Ingredient ingredient)
    {
        return getStackAtCurrentTime(List.of(ingredient.getItems()));
    }

    public static ItemStack getStackAtCurrentTime(@NotNull List<ItemStack> items)
    {
        int perm = (int)(Util.getMillis() / 1000 % items.size());
        return items.get(perm);
    }

    public static int animateArrow (int ticks)
    {
        return (int) (Util.getMillis() / ticks % 22);
    }

    public static void blit(
            @NotNull GuiGraphics guiGraphics,
            @NotNull ResourceLocation atlasLocation,
            float x,
            float y,
            float uOffset,
            float vOffset,
            float width,
            float height,
			float blitOffset,
            float uWidth,
            float vHeight,
            int textureWidth,
            int textureHeight
    )
    {
        RenderHelper.blit(guiGraphics, atlasLocation, x, y, uOffset, vOffset, width, height, blitOffset, uWidth, vHeight, textureWidth, textureHeight,-1);
    }

    public static void blit(
            @NotNull GuiGraphics guiGraphics,
            @NotNull ResourceLocation atlasLocation,
            float x,
            float y,
            float uOffset,
            float vOffset,
            float width,
            float height,
			float blitOffset,
            float uWidth,
            float vHeight,
            int textureWidth,
            int textureHeight,
            int color
    ) {
        RenderHelper.blit(
                guiGraphics,
                atlasLocation,
                x,
                x + width,
                y,
                y + height,
				blitOffset,
                uOffset / (float)textureWidth,
                (uOffset + uWidth) / (float)textureWidth,
                vOffset / (float)textureHeight,
                (vOffset + vHeight) / (float)textureHeight,
                color
        );
    }

    public static void blit (@NotNull GuiGraphics guiGraphics,
                             @NotNull ResourceLocation atlasLocation,
                             float x1,
                             float x2,
                             float y1,
                             float y2,
							 float blitOffset,
                             float minU,
                             float maxU,
                             float minV,
                             float maxV,
                             int color)
    {
	    RenderSystem.setShaderTexture(0, atlasLocation);
	    RenderSystem.setShader(GameRenderer ::getPositionTexColorShader);
	    RenderSystem.enableBlend();
	    Matrix4f matrix4f = guiGraphics.pose().last().pose();
		float red = MathHelper.ColorHelper.redFloat(color);
		float green = MathHelper.ColorHelper.greenFloat(color);
		float blue = MathHelper.ColorHelper.blueFloat(color);
		float alpha = MathHelper.ColorHelper.alphaFloat(color);
		
	    BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
	    bufferbuilder.addVertex(matrix4f, x1, y1, blitOffset).
					    setUv(minU, minV).
			            setColor(red, green, blue, alpha);
	    bufferbuilder.addVertex(matrix4f, x1, y2, blitOffset).
					    setUv(minU, maxV).
			            setColor(red, green, blue, alpha);
	    bufferbuilder.addVertex(matrix4f, x2, y2, blitOffset).
					    setUv(maxU, maxV).
			            setColor(red, green, blue, alpha);
	    bufferbuilder.addVertex(matrix4f, x2, y1, blitOffset).
					    setUv(maxU, minV).
			            setColor(red, green, blue, alpha);
	    BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
	    RenderSystem.disableBlend();
    }
	
	public static class Gui
	{
		private static final int BASE_WIDTH = 427;
		private static final int BASE_HEIGHT = 240;
		
		public static int scaledWidthCoord(@NotNull Screen screen, int localWidth)
		{
			return (int) ((float)localWidth * screen.getMinecraft().getWindow().getGuiScaledWidth() / BASE_WIDTH);
		}
		
		public static int scaledHeightCoord(@NotNull Screen screen, int localWidth)
		{
			return (int) ((float)localWidth * screen.getMinecraft().getWindow().getGuiScaledHeight() / BASE_HEIGHT);
		}
		
		public static int getScreenWidth(@NotNull Screen screen, float percent)
		{
			return (int) (screen.width * percent);
		}
		
		public static int getScreenHeight(@NotNull Screen screen, float percent)
		{
			return (int) (screen.height * percent);
		}
	}
	
	public static class GenomeRenderer
	{
		private static final int RENDER_GENE_AMOUNT_PER_INSTANCE = 5;
		private static final ResourceLocation GENE_TEXTURE = Database.rl("textures/gui/gene.png");
		public static final BioGeneModel GENE_MODEL = new BioGeneModel(GENE_TEXTURE);
		
		public static void renderGeneInGui(@NotNull GeneInstance gene, @NotNull GuiGraphics guiGraphics, Rect2d bounds)
		{
			Minecraft mc = mc();
			ClientPacketListener listener = mc.getConnection();
			if (listener == null)
				return;
			GeneDefinition definition = listener.registryAccess().
					lookupOrThrow(Registration.GenomeReg.DEFINITION_KEY).
					get(ResourceKey.create(Registration.GenomeReg.DEFINITION_KEY, gene.id())).
					orElseThrow().
					value();
			if (definition == null)
				return;
			
			GeneDefinition.RarityData data = definition.rarityData().
					get(gene.rarity());
			if (data == null)
				return;
			
			PoseStack poseStack = guiGraphics.pose();
			float time = (Util.getMillis() % 10000) / 1000f;
			float rotation = time * 360f * 0.15f;
			float geneSize = (float) Math.min(bounds.width(), bounds.height());
			float sizeX = geneSize / RENDER_GENE_AMOUNT_PER_INSTANCE;
			for (int q = 0; q < RENDER_GENE_AMOUNT_PER_INSTANCE; q++)
			{
				float x = (float) (bounds.x() + sizeX * q + sizeX * 0.5d);
				float y = (float) (bounds.y() + geneSize * 0.5d);
				poseStack.pushPose();
				poseStack.translate(x, y, 200);
				poseStack.scale(geneSize, geneSize, geneSize);
				poseStack.mulPose(Axis.XP.rotationDegrees(rotation + q * 36));
				
				GENE_MODEL.renderModel(
						poseStack,
						ObjRenderTypes :: trianglesSolid,
						guiGraphics.bufferSource(),
						OverlayTexture.NO_OVERLAY,
						15728880,
						data.mainColor().color(),
						data.secondaryColor().color());
				
				poseStack.popPose();
			}
		}
	}
	
	public record Rect2d(double x, double y, double width, double height)
	{
	}
}

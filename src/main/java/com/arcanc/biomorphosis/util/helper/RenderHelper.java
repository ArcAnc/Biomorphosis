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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.Function;

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
        return getStackAtCurrentTime(ingredient.ingredient().getValues().stream().
                map(itemHolder -> new ItemStack(itemHolder, ingredient.amount())).toList());
    }

    public static ItemStack getStackAtCurrentTime(@NotNull SizedIngredient ingredient)
    {
        return getStackAtCurrentTime(ingredient.ingredient().getValues().stream().
                map(itemHolder -> new ItemStack(itemHolder, ingredient.count())).toList());
    }

    public static ItemStack getStackAtCurrentTime(@NotNull Ingredient ingredient)
    {
        return getStackAtCurrentTime(ingredient.getValues().stream().map(ItemStack::new).toList());
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
            @NotNull Function<ResourceLocation, RenderType> renderTypeGetter,
            @NotNull ResourceLocation atlasLocation,
            float x,
            float y,
            float uOffset,
            float vOffset,
            float width,
            float height,
            float uWidth,
            float vHeight,
            int textureWidth,
            int textureHeight
    )
    {
        RenderHelper.blit(guiGraphics, renderTypeGetter, atlasLocation, x, y, uOffset, vOffset, width, height, uWidth, vHeight, textureWidth, textureHeight,-1);
    }

    public static void blit(
            @NotNull GuiGraphics guiGraphics,
            @NotNull Function<ResourceLocation, RenderType> renderTypeGetter,
            @NotNull ResourceLocation atlasLocation,
            float x,
            float y,
            float uOffset,
            float vOffset,
            float width,
            float height,
            float uWidth,
            float vHeight,
            int textureWidth,
            int textureHeight,
            int color
    ) {
        RenderHelper.blit(
                guiGraphics,
                renderTypeGetter,
                atlasLocation,
                x,
                x + width,
                y,
                y + height,
                uOffset / (float)textureWidth,
                (uOffset + uWidth) / (float)textureWidth,
                vOffset / (float)textureHeight,
                (vOffset + vHeight) / (float)textureHeight,
                color
        );
    }

    public static void blit (@NotNull GuiGraphics guiGraphics,
                             @NotNull Function<ResourceLocation, RenderType> renderTypeGetter,
                             @NotNull ResourceLocation atlasLocation,
                             float x1,
                             float x2,
                             float y1,
                             float y2,
                             float minU,
                             float maxU,
                             float minV,
                             float maxV,
                             int color)
    {
        RenderType rendertype = renderTypeGetter.apply(atlasLocation);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        VertexConsumer vertexconsumer = guiGraphics.bufferSource.getBuffer(rendertype);
        vertexconsumer.addVertex(matrix4f, x1, y1, 0.0F).setUv(minU, minV).setColor(color);
        vertexconsumer.addVertex(matrix4f, x1, y2, 0.0F).setUv(minU, maxV).setColor(color);
        vertexconsumer.addVertex(matrix4f, x2, y2, 0.0F).setUv(maxU, maxV).setColor(color);
        vertexconsumer.addVertex(matrix4f, x2, y1, 0.0F).setUv(maxU, minV).setColor(color);
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
					getValue(gene.id());
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
				
				guiGraphics.drawSpecial(multiBufferSource ->
						GENE_MODEL.renderModel(
								poseStack,
								ObjRenderTypes :: trianglesSolid,
								multiBufferSource,
								OverlayTexture.NO_OVERLAY,
								15728880,
								data.mainColor().color(),
								data.secondaryColor().color()));
				
				poseStack.popPose();
			}
		}
	}
	
	public record Rect2d(double x, double y, double width, double height)
	{
	}
}

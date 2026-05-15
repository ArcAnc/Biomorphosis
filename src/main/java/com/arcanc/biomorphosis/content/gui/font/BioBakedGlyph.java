/**
 * @author ArcAnc
 * Created at: 05.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.font;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class BioBakedGlyph extends BakedGlyph
{
	private static final GlyphRenderTypes DUMMY_RENDER_TYPES = GlyphRenderTypes.createForColorTexture(
			ResourceLocation.withDefaultNamespace("missing"));
	
	private final BakedGlyph delegate;
	
	public BioBakedGlyph(BakedGlyph delegate)
	{
		super(DUMMY_RENDER_TYPES, 0, 0, 0, 0, 0, 0, 0, 0);
		this.delegate = delegate;
	}
	
	@Override
	public void render(boolean italic,
	                   float x,
	                   float y,
	                   Matrix4f matrix,
	                   VertexConsumer buffer,
	                   float red,
	                   float green,
	                   float blue,
	                   float alpha,
	                   int packedLight)
	{
		float delLeft = this.delegate.left;
		float delRight = this.delegate.right;
		float delUp = this.delegate.up;
		float delDown = this.delegate.down;
		
		float left = x + delLeft;
		float right = x + delRight;
		float up = y + delUp;
		float down = y + delDown;
		float f4 = italic ? 1.0F - 0.25F * delUp : 0.0F;
		float f5 = italic ? 1.0F - 0.25F * delDown : 0.0F;
		buffer.addVertex(matrix, left + f4, up, 0.0F).setColor(red, green, blue, alpha).setUv(this.delegate.u0, this.delegate.v0).setLight(packedLight);
		buffer.addVertex(matrix, left + f5, down, 0.0F).setColor(red, green, blue, alpha).setUv(this.delegate.u0, this.delegate.v1).setLight(packedLight);
		buffer.addVertex(matrix, right + f5, down, 0.0F).setColor(red, green, blue, alpha).setUv(this.delegate.u1, this.delegate.v1).setLight(packedLight);
		buffer.addVertex(matrix, right + f4, up, 0.0F).setColor(red, green, blue, alpha).setUv(this.delegate.u1, this.delegate.v0).setLight(packedLight);
	}
	
	@Override
	public void renderEffect(Effect effect, Matrix4f matrix, VertexConsumer buffer, int packedLight)
	{
		this.delegate.renderEffect(effect, matrix, buffer, packedLight);
	}
	
	@Override
	public RenderType renderType(Font.DisplayMode displayMode)
	{
		return this.delegate.renderType(displayMode);
	}
}

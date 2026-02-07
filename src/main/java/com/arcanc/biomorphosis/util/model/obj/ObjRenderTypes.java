/**
 * @author ArcAnc
 * Created at: 10.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.model.obj;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ObjRenderTypes
{
    public static RenderType trianglesSolid (ResourceLocation texture)
    {
        return RenderTypeProviders.TRIANGLES_SOLID.apply(texture);
    }

    public static RenderType trianglesTranslucent(ResourceLocation texture)
    {
        return RenderTypeProviders.TRIANGLES_TRANSLUCENT.apply(texture);
    }

    private static class RenderTypeProviders extends RenderType
    {
		public static Function<ResourceLocation, RenderType> TRIANGLES_SOLID = Util.memoize(RenderTypeProviders :: trianglesSolid);
        public static Function<ResourceLocation, RenderType> TRIANGLES_TRANSLUCENT = Util.memoize(RenderTypeProviders :: trianglesTranslucent);

        private static @NotNull RenderType trianglesSolid(ResourceLocation loc)
        {
            RenderType.CompositeState state =  RenderType.CompositeState.builder().
                    setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER).
                    setTextureState(new RenderStateShard.TextureStateShard(loc, TriState.FALSE, false)).
                    setTransparencyState(RenderStateShard.NO_TRANSPARENCY).
                    setCullState(RenderStateShard.NO_CULL).
                    setLightmapState(LIGHTMAP).
                    setOverlayState(OVERLAY).
                    createCompositeState(true);
            return create("triangles_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 1536, true, false, state);
        }

        private static @NotNull RenderType trianglesTranslucent(ResourceLocation loc)
        {
            RenderType.CompositeState state =  RenderType.CompositeState.builder().
                    setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).
                    setTextureState(new RenderStateShard.TextureStateShard(loc, TriState.FALSE, false)).
                    setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).
                    setCullState(NO_CULL).
                    setLightmapState(LIGHTMAP).
                    setOverlayState(OVERLAY).
                    createCompositeState(true);
            return create("triangles_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 1536, true, true, state);
        }

        private RenderTypeProviders(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2)
        {
            super(s, v, m, i, b, b2, r, r2);
            throw new IllegalStateException("This class is not meant to be constructed!");
        }

    }
}

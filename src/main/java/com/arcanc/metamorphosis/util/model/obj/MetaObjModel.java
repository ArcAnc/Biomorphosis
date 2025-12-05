/**
 * @author ArcAnc
 * Created at: 10.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.util.model.obj;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class MetaObjModel
{
    protected final List<Vector3f> vertices = new ArrayList<>();
    protected final List<Vector2f> uvs = new ArrayList<>();
    protected final List<Vector3f> normals = new ArrayList<>();
    protected final List<Face> faces = new ArrayList<>();
    protected final ResourceLocation texture;
    protected final boolean flipUV;

    public static @NotNull MetaObjModel newModel(ResourceLocation texture)
    {
        return new MetaObjModel(texture, false);
    }

    public static @NotNull MetaObjModel newModel(ResourceLocation texture, boolean flipUV)
    {
        return new MetaObjModel(texture, flipUV);
    }

    public MetaObjModel(ResourceLocation texture, boolean flipUV)
    {
        this.texture = texture;
        this.flipUV = flipUV;
    }

    public MetaObjModel addVertex(float x, float y, float z)
    {
        return addVertex(new Vector3f(x, y, z));
    }
    public MetaObjModel addVertex(Vector3f vertex)
    {
        Preconditions.checkNotNull(vertex);
        this.vertices.add(vertex);
        return this;
    }

    public MetaObjModel addUV(float u, float v)
    {
        return addUV(new Vector2f(u, v));
    }
    public MetaObjModel addUV(Vector2f uv)
    {
        Preconditions.checkNotNull(uv);
        this.uvs.add(uv);
        return this;
    }

    public MetaObjModel addNormal(float x, float y, float z)
    {
        return addNormal(new Vector3f(x, y, z));
    }
    public MetaObjModel addNormal(Vector3f normal)
    {
        Preconditions.checkNotNull(normal);
        this.normals.add(normal);
        return this;
    }

    public MetaObjModel addFaceInfo(Vector3f vertex, Vector2f uv, Vector3f normal)
    {
        Preconditions.checkNotNull(vertex);
        Preconditions.checkNotNull(uv);
        Preconditions.checkNotNull(normal);
        this.vertices.add(vertex);
        this.uvs.add(uv);
        this.normals.add(normal);
        return this;
    }

    /**
     * @param v00 - vertex x index
     * @param v01 - uv x index
     * @param v02 - normal x index
     * @param v10 - vertex y index
     * @param v11 - uv y index
     * @param v12 - normal y index
     * @param v20 - vertex z index
     * @param v21 - uv z index
     * @param v22 - normal z index
     * @return model
     */

    public MetaObjModel newFace (int v00, int v01, int v02, int v10, int v11, int v12, int v20, int v21, int v22)
    {
        this.faces.add(new Face(v00, v01, v02, v10, v11, v12, v20, v21, v22));
        return this;
    }

    /**
     *
     * @param vertices - x, y, z vertex index
     * @param uvs - x, y, z uv index
     * @param normals - x, y, z normal index
     * @return model
     */
    public MetaObjModel newFace(@NotNull Vector3i vertices, @NotNull Vector3i uvs, @NotNull Vector3i normals)
    {
        return newFace(vertices.x(), uvs.x(), normals.x(), vertices.y(), uvs.y(), normals.y(), vertices.z(), uvs.z(), normals.z());
    }

    public void render(@NotNull PoseStack mStack, @NotNull Function<ResourceLocation, RenderType> type, @NotNull MultiBufferSource bufferSource, int overlay, int light, int color)
    {
        Matrix4f matrix = mStack.last().pose();
        PoseStack.Pose normal = mStack.last();

        VertexConsumer builder = bufferSource.getBuffer(type.apply(this.texture));

        List<Vector3f> vertices = this.vertices;
        List<Vector2f> uvs = this.uvs;
        List<Vector3f> normals = this.normals;

        for (MetaObjModel.Face face : this.faces)
        {
            Vector2f uv0 = uvs.get(face.v01());
            Vector2f uv1 = uvs.get(face.v11());
            Vector2f uv2 = uvs.get(face.v21());

            if (this.flipUV)
            {
                uv0 = new Vector2f(uv0.x(), 1 - uv0.y());
                uv1 = new Vector2f(uv1.x(), 1 - uv1.y());
                uv2 = new Vector2f(uv2.x(), 1 - uv2.y());
            }

            builder.addVertex(matrix,
                            vertices.get(face.v00()).x(),
                            vertices.get(face.v00()).y(),
                            vertices.get(face.v00()).z()).
                    setColor(color).
                    setUv(uv0.x(),
                            uv0.y()).
                    setOverlay(overlay).
                    setLight(light).
                    setNormal(normal,
                            normals.get(face.v02()).x(),
                            normals.get(face.v02()).y(),
                            normals.get(face.v02()).z());

            builder.addVertex(matrix,
                            vertices.get(face.v10()).x(),
                            vertices.get(face.v10()).y(),
                            vertices.get(face.v10()).z()).
                    setColor(color).
                    setUv(uv1.x(),
                            uv1.y()).
                    setOverlay(overlay).
                    setLight(light).
                    setNormal(normal,
                            normals.get(face.v12()).x(),
                            normals.get(face.v12()).y(),
                            normals.get(face.v12()).z());

            builder.addVertex(matrix,
                            vertices.get(face.v20()).x(),
                            vertices.get(face.v20()).y(),
                            vertices.get(face.v20()).z()).
                    setColor(color).
                    setUv(uv2.x(),
                            uv2.y()).
                    setOverlay(overlay).
                    setLight(light).
                    setNormal(normal,
                            normals.get(face.v22()).x(),
                            normals.get(face.v22()).y(),
                            normals.get(face.v22()).z());
        }
    }

    public record Face(int v00, int v01, int v02,
                       int v10, int v11, int v12,
                       int v20, int v21, int v22)
    {
        public Face(int v00, int v01, int v02,
                    int v10, int v11, int v12,
                    int v20, int v21, int v22)
        {
            this.v00 = v00 - 1;
            this.v01 = v01 - 1;
            this.v02 = v02 - 1;
            this.v10 = v10 - 1;
            this.v11 = v11 - 1;
            this.v12 = v12 - 1;
            this.v20 = v20 - 1;
            this.v21 = v21 - 1;
            this.v22 = v22 - 1;
        }
    }
}

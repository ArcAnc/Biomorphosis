/**
 * @author ArcAnc
 * Created at: 10.07.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.sky;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.joml.Matrix4f;

public record BiomeSkyboxRenderContext(ClientLevel level,
                                       ResourceKey<Biome> biome,
                                       BiomeSkybox skybox,
                                       PoseStack poseStack,
                                       Matrix4f projectionMatrix,
                                       Camera camera,
                                       int renderTick,
                                       float partialTick,
                                       float alpha)
{
}

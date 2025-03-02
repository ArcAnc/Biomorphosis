/**
 * @author ArcAnc
 * Created at: 01.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.util.model;

import com.arcanc.biomorphosis.content.block.block_entity.BioSidedAccessBlockEntity;
import com.arcanc.biomorphosis.util.helper.DirectionHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.SidedConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.DelegateBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.LinkedList;
import java.util.List;

public class BioFluidStorageBakedModel extends DelegateBakedModel
{
    private static final BakedQuad[][] CACHED_QUADS = new BakedQuad[Direction.values().length][BasicSidedStorage.FaceMode.values().length];

    public BioFluidStorageBakedModel(BakedModel parent)
    {
        super(parent);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state,
                                             @Nullable Direction direction,
                                             @NotNull RandomSource random,
                                             @NotNull ModelData modelData,
                                             @Nullable RenderType renderType)
    {
        LinkedList<BakedQuad> quads = new LinkedList<>(parent.getQuads(state, direction, random, modelData, renderType));
        if (direction == null || quads.isEmpty())
            return quads;
        if (!modelData.has(BioSidedAccessBlockEntity.ACCESS_PROPERTIES))
            return quads;
        SidedConfig config = modelData.get(BioSidedAccessBlockEntity.ACCESS_PROPERTIES);
        if (config == null)
            config = SidedConfig.zeroAccess();
        BasicSidedStorage.RelativeFace face = DirectionHelper.getRelativeDirection(state, direction);
        BasicSidedStorage.FaceMode mode = config.getMode(face);
        Vector4f newColor = mode.getColor();
        for (int q = 0; q < quads.size(); q++)
        {
            BakedQuad quad = quads.get(q);
            if (quad.isTinted() && quad.getTintIndex() == 1)
            {
                BakedQuad cachedQuad = CACHED_QUADS[direction.ordinal()][mode.ordinal()];
                if (cachedQuad == null)
                {
                    cachedQuad = QuadTransformers.applyingColor((int) newColor.w(), (int) newColor.x(), (int) newColor.y(), (int) newColor.z()).process(quad);
                    CACHED_QUADS[direction.ordinal()][mode.ordinal()] = cachedQuad;
                }
                quads.set(q, cachedQuad);
                break;
            }
        }

        return quads;
    }
}

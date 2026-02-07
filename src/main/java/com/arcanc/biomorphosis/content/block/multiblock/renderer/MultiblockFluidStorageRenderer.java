/**
 * @author ArcAnc
 * Created at: 17.05.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.multiblock.renderer;

import com.arcanc.biomorphosis.content.block.multiblock.MultiblockFluidStorage;
import com.arcanc.biomorphosis.util.helper.BlockHelper;
import com.arcanc.biomorphosis.util.helper.MathHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Predicate;

public class MultiblockFluidStorageRenderer implements BlockEntityRenderer<MultiblockFluidStorage>
{
    private static final int[] MASK_TO_INDEX =
            {
                    4,  // 0000 - изолированный (центр) (игнорим)
                    4,  // 0001 - UP (игнорим)
                    4,  // 0010 - DOWN (игнорим)
                    4,  // 0011 - UP + DOWN (игнорим)
                    4,  // 0100 - RIGHT (игнорим)
                    6,  // 0101 - UP + RIGHT
                    0,  // 0110 - DOWN + RIGHT
                    3,  // 0111 - UP + DOWN + RIGHT
                    4,  // 1000 - LEFT (игнорим)
                    8,  // 1001 - UP + LEFT
                    2,  // 1010 - DOWN + LEFT
                    5,  // 1011 - UP + DOWN + LEFT
                    5,  // 1100 - LEFT + RIGHT
                    7,  // 1101 - UP + LEFT + RIGHT
                    1,  // 1110 - DOWN + LEFT + RIGHT
                    4   // 1111 - все стороны
            };

    private static final Vector2f[] INDEX_TO_UV =
            {
                    new Vector2f(0,0),
                    new Vector2f(1/3f, 0),
                    new Vector2f(2/3f, 0),
                    new Vector2f(0, 1/3f),
                    new Vector2f(1/3f, 1/3f),
                    new Vector2f(2/3f, 1/3f),
                    new Vector2f(0, 2/3f),
                    new Vector2f(1/3f, 2/3f),
                    new Vector2f(2/3f, 2/3f)
            };

    private static final float MIN_X =  0.01F/16F;
    private static final float MAX_X = 15.99F/16F;
    private static final float MIN_Y =  0.01F/16F;
    private static final float MAX_Y = 15.99F/16F;
    private static final float MIN_Z =  0.01F/16F;
    private static final float MAX_Z = 15.99F/16F;

    private static final float MIN_UV_T =  0.0001F;
    private static final float MAX_UV_T = 15.9999F;
    private static final float MIN_U_S  =  0.0001F;
    private static final float MAX_U_S  = 15.9999F;
    private static final float MIN_V_S  =  0.0001F;
    private static final float MAX_V_S  = 15.9999F;

    public MultiblockFluidStorageRenderer(BlockEntityRendererProvider.Context ctx)
    {
    }

    @Override
    public void render(@NotNull MultiblockFluidStorage blockEntity,
                       float partialTick,
                       @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource,
                       int packedLight,
                       int packedOverlay)
    {
        BlockPos bePos = blockEntity.getBlockPos();
        Level level = blockEntity.getLevel();
        if (level == null)
            return;

        for (Direction dir : Direction.values())
        {
            BlockPos toCheckPos = bePos.relative(dir);
            BlockState toCheckState = level.getBlockState(toCheckPos);
            if (toCheckState.isFaceSturdy(level, bePos, dir.getOpposite()))
                continue;
            renderFace(blockEntity, dir, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    private void renderFace(@NotNull MultiblockFluidStorage blockEntity,
                            @NotNull Direction dir,
                            float partialTick,
                            @NotNull PoseStack poseStack,
                            @NotNull MultiBufferSource bufferSource,
                            int packedLight,
                            int packedOverlay)
    {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        float uvSize = 1/3f;
        VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
        TextureAtlasSprite sprite = dir.getAxis().isHorizontal() ?
                getTextureLocation(blockEntity, "/side_transparent") :
                getTextureLocation(blockEntity, "/top");


        CTInfo info = computeCTInfo(dir, blockEntity.getLevel(), blockEntity.getBlockPos(), state -> state.is(blockEntity.getBlockState().getBlock()));
        poseStack.pushPose();

        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 3; x++)
            {
                float x0 = x * uvSize;
                float x1 = (x + 1) * uvSize;
                float y0 = y * uvSize;
                float y1 = (y + 1) * uvSize;

                Vector3f[] coords = getQuadForDirection(dir, x0, y0, x1, y1);
                Vector2f[] uvCoords = getUv(info.get(x, y), sprite);

                for (int q = 0; q < 4; q++)
                {
                    Vector3f quad = coords[q];
                    Vector2f uv = uvCoords[q];
                    builder.addVertex(poseStack.last(), quad.x(), quad.y(), quad.z()).
                            setUv(uv.x(), uv.y()).
                            setLight(packedLight).
                            setOverlay(packedOverlay).
                            setColor(1f, 1f, 1f, 1f).
                            setNormal(poseStack.last(), dir.getStepX(), dir.getStepY(), dir.getStepZ());
                }

            }
        poseStack.popPose();

        FluidSidedStorage storage = MultiblockFluidStorage.getHandler(blockEntity, null);
        if (storage == null)
            return;
        int multiblockHeight = blockEntity.getDefinition().map(definition ->
                    definition.getStructure(level, pos).getSize().getY()).orElse(1);
        int blockHeightInMultiblock = blockEntity.getMasterPos().map(masterPos -> Math.abs(blockEntity.getBlockPos().getY() - masterPos.getY())).orElse(0);
        FluidStack fluid = storage.getFluidInTank(0);
        if (fluid.isEmpty())
            return;
        float fluidHeightInMultiblock = multiblockHeight * (float)storage.getFluidInTank(0).getAmount()/storage.getTankCapacity(0);
        float inBlockAmount = fluidHeightInMultiblock - blockHeightInMultiblock;
        if (fluidHeightInMultiblock > blockHeightInMultiblock)
        {
            if (inBlockAmount < 0)
                return;
            if (inBlockAmount > 1)
                renderFluid(fluid, 1, poseStack);
            else
                renderFluid(fluid, inBlockAmount, poseStack);
        }
    }

    private void renderFluid(@NotNull FluidStack fluid,
                             float fluidHeight,
                             @NotNull PoseStack poseStack)
    {
        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(fluid.getFluid());

        ResourceLocation stillTex = renderProps.getStillTexture();
        TextureAtlasSprite still = RenderHelper.getTexture(stillTex);

        ResourceLocation flowTex = renderProps.getFlowingTexture();
        TextureAtlasSprite flow = RenderHelper.getTexture(flowTex);


        boolean gas = fluid.getFluid().getFluidType().isLighterThanAir();
        Vector4f color = MathHelper.ColorHelper.vector4fFromARGB(renderProps.getTintColor());

        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.enableDepthTest();

        PoseStack.Pose matrix = poseStack.last();

        drawTop(builder, matrix, fluidHeight, still, color, gas);
        drawSides(builder, matrix, fluidHeight, flow, color, gas);

        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }

    private void drawTop(@NotNull VertexConsumer builder, PoseStack.Pose pose, float height, @NotNull TextureAtlasSprite tex, @NotNull Vector4f color, boolean gas)
    {
        float minX = gas ? MAX_X : MIN_X;
        float maxX = gas ? MIN_X : MAX_X;
        float y = (MIN_Y + (gas ? (1F - height) * (MAX_Y - MIN_Y) : height * (MAX_Y - MIN_Y)));

        float minU = tex.getU(MIN_UV_T / 16f);
        float maxU = tex.getU(MAX_UV_T / 16f);
        float minV = tex.getV(MIN_UV_T / 16f);
        float maxV = tex.getV(MAX_UV_T / 16f);

        builder.addVertex(pose, maxX, y, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, minX, y, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, minX, y, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, maxX, y, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);
    }

    private void drawSides(@NotNull VertexConsumer builder, PoseStack.Pose pose, float height, @NotNull TextureAtlasSprite tex, @NotNull Vector4f color, boolean gas)
    {
        float minY = gas ? MAX_Y - (height * (MAX_Y - MIN_Y)) : MIN_Y;
        float maxY = gas ? MAX_Y : MIN_Y + height * (MAX_Y - MIN_Y);

        float minU = tex.getU(MIN_U_S / 16f);
        float maxU = tex.getU(MAX_U_S / 16f);
        float minV = tex.getV(MIN_V_S / 16f);
        float maxV = tex.getV(height);

        //North
        builder.addVertex(pose, MIN_X, maxY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MAX_X, maxY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MAX_X, minY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MIN_X, minY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);

        //South
        builder.addVertex(pose, MAX_X, maxY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MIN_X, maxY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MIN_X, minY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MAX_X, minY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);

        //East
        builder.addVertex(pose, MAX_X, maxY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MAX_X, maxY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MAX_X, minY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MAX_X, minY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);

        //West
        builder.addVertex(pose, MIN_X, maxY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, minV);
        builder.addVertex(pose, MIN_X, maxY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, minV);
        builder.addVertex(pose, MIN_X, minY, MIN_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(maxU, maxV);
        builder.addVertex(pose, MIN_X, minY, MAX_Z).setColor(color.x(), color.y(), color.z(), color.w()).setUv(minU, maxV);
    }

    private record CTInfo(byte[][] data)
    {
        public byte get(int x, int y)
        {
            return this.data[y][x];
        }

        public void set(int x, int y, byte value)
        {
            this.data[y][x] = value;
        }
    }

    // Битовая маска: 1 = DOWN, 2 = UP, 4 = RIGHT, 8 = LEFT
    private @NotNull CTInfo computeCTInfo(Direction face, Level level, @NotNull BlockPos pos, Predicate<BlockState> connectPredicate)
    {
        byte[][] mask = new byte[4][4];

        Direction upDir = getUpForFace(face);
        Direction downDir = getDownForFace(face);
        Direction leftDir = getLeftForFace(face);
        Direction rightDir = getRightForFace(face);

        boolean leftConnected = getCTInfoFrom(level, pos.relative(leftDir), connectPredicate);
        boolean rightConnected = getCTInfoFrom(level, pos.relative(rightDir), connectPredicate);
        boolean upConnected = getCTInfoFrom(level, pos.relative(upDir), connectPredicate);
        boolean downConnected = getCTInfoFrom(level, pos.relative(downDir), connectPredicate);

        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x ++)
            {
                byte conn = 0;

                // Внутренние связи
                if (y > 0 && isInternalConnected(x, y, x, y - 1))
                    conn |= 1; // UP
                if (y < 2 && isInternalConnected(x, y, x, y + 1))
                    conn |= 2; // DOWN
                if (x < 2 && isInternalConnected(x, y, x + 1, y))
                    conn |= 4; // RIGHT
                if (x > 0 && isInternalConnected(x, y, x - 1, y))
                    conn |= 8; // LEFT

                // Краевые ячейки — проверка внешних блоков
                if (x == 0 && leftConnected) conn |= 8;
                if (x == 2 && rightConnected) conn |= 4;
                if (y == 0 && upConnected) conn |= 1;
                if (y == 2 && downConnected) conn |= 2;

                mask[y][x] = conn;
            }
        }
        return new CTInfo(mask);
    }

    private boolean isInternalConnected(int x1, int y1, int x2, int y2)
    {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx + dy) == 1;
    }

    private @NotNull TextureAtlasSprite getTextureLocation(@NotNull BlockEntity blockEntity, String suffix)
    {
        return getTextureLocation(blockEntity.getBlockState().getBlock(), suffix, "");
    }

    private @NotNull TextureAtlasSprite getTextureLocation(@NotNull BlockEntity blockEntity, String suffix, String prefix)
    {
        return getTextureLocation(blockEntity.getBlockState().getBlock(), suffix, prefix);
    }

    private @NotNull TextureAtlasSprite getTextureLocation(Block block, String suffix, String prefix)
    {
        return RenderHelper.getTexture(BlockHelper.getRegistryName(block).withPrefix(prefix).withPrefix("block/").withSuffix(suffix));
    }

    private Vector2f @NotNull [] getUv(int mask, @NotNull TextureAtlasSprite sprite)
    {
        Vector2f uv = INDEX_TO_UV[MASK_TO_INDEX[mask]];

        float u0 = sprite.getU(uv.x());
        float u1 = sprite.getU(uv.x() + 1/3f);
        float v0 = sprite.getV(uv.y());
        float v1 = sprite.getV(uv.y() + 1/3f);

        return new Vector2f[]
                {
                        new Vector2f(u0, v1),
                        new Vector2f(u1, v1),
                        new Vector2f(u1, v0),
                        new Vector2f(u0, v0)
                };
    }

    private Vector3f @NotNull [] getQuadForDirection(@NotNull Direction dir, float x0, float y0, float x1, float y1)
    {
        return switch (dir)
        {
            case UP -> new Vector3f[]
            {
                    new Vector3f(x0, 1, y1),
                    new Vector3f(x1, 1, y1),
                    new Vector3f(x1, 1, y0),
                    new Vector3f(x0, 1, y0)
            };
            case DOWN -> new Vector3f[]
            {
                    new Vector3f(x0, 0, 1 - y1),
                    new Vector3f(x1, 0, 1 - y1),
                    new Vector3f(x1, 0, 1 - y0),
                    new Vector3f(x0, 0, 1 - y0)
            };
            case NORTH -> new Vector3f[]
            {
                    new Vector3f(x0, y1, 0),
                    new Vector3f(x1, y1, 0),
                    new Vector3f(x1, y0, 0),
                    new Vector3f(x0, y0, 0)
            };
            case SOUTH -> new Vector3f[]
            {
                    new Vector3f(1 - x0, y1, 1),
                    new Vector3f(1 - x1, y1, 1),
                    new Vector3f(1 - x1, y0, 1),
                    new Vector3f(1 - x0, y0, 1)
            };
            case EAST -> new Vector3f[]
            {
                    new Vector3f(1, y1, x0),
                    new Vector3f(1, y1, x1),
                    new Vector3f(1, y0, x1),
                    new Vector3f(1, y0, x0)
            };
            case WEST -> new Vector3f[]
            {
                    new Vector3f(0, y1, 1 - x0),
                    new Vector3f(0, y1, 1 - x1),
                    new Vector3f(0, y0, 1 - x1),
                    new Vector3f(0, y0, 1 - x0)
            };
        };
    }

    private Direction getLeftForFace(@NotNull Direction face)
    {
        return switch (face)
        {
            case UP, NORTH -> Direction.WEST;
            case SOUTH, DOWN -> Direction.EAST;
            case EAST -> Direction.NORTH;
            case WEST -> Direction.SOUTH;
        };
    }

    private Direction getRightForFace(@NotNull Direction face) {
        return switch (face) {
            case UP, NORTH -> Direction.EAST;
            case SOUTH, DOWN -> Direction.WEST;
            case EAST -> Direction.SOUTH;
            case WEST -> Direction.NORTH;
        };
    }

    private Direction getUpForFace(@NotNull Direction face)
    {
        return switch (face)
        {
            case UP -> Direction.NORTH; // по Z
            case DOWN -> Direction.SOUTH; // по Z
            default -> Direction.DOWN; // по Y
        };
    }

    private Direction getDownForFace(@NotNull Direction face)
    {
        return switch (face)
        {
            case UP -> Direction.SOUTH; // по Z
            case DOWN -> Direction.NORTH; // по Z
            default -> Direction.UP; // по Y
        };
    }

    private boolean getCTInfoFrom(@NotNull Level level, BlockPos pos, @NotNull Predicate<BlockState> connect)
    {
        return connect.test(level.getBlockState(pos));
    }
}
/**
 * @author ArcAnc
 * Created at: 04.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.block.block_entity;

import com.arcanc.metamorphosis.content.block.BlockInterfaces;
import com.arcanc.metamorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.metamorphosis.content.fluid.FluidTransportHandler;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.util.Database;
import com.arcanc.metamorphosis.util.helper.FluidHelper;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MetaFluidTransmitter extends MetaBaseBlockEntity implements BlockInterfaces.IWrencheable, ServerTickableBE
{

    private final static int PERIOD = 40;

    private final List<PathData> pathData = new ArrayList<>();

    private int sendAmount = 100;

    public MetaFluidTransmitter(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_FLUID_TRANSMITTER.get(), pos, blockState);
    }

    @Override
    public void tickServer()
    {
        if (this.level == null || this.pathData.isEmpty())
            return;
        List<PathData> toRemove = new ArrayList<>();
        for (PathData data : this.pathData)
        {
            if (!FluidHelper.isFluidHandler(this.level, data.start()) || !FluidHelper.isFluidHandler(this.level, data.finish()))
            {
                toRemove.add(data);
                continue;
            }
            if (this.level.getGameTime() % PERIOD != 0)
                continue;
            FluidHelper.getFluidHandler(this.level, data.start()).ifPresent(handler ->
                    {
                        if (FluidHelper.isEmpty(handler))
                            return;
                        FluidHelper.getFluidHandler(this.level, data.finish()).ifPresent(finishHandler ->
                        {
                            if (FluidHelper.isFull(finishHandler))
                                return;
                            FluidTransportHandler.addTransport(this.level, new FluidTransportHandler.FluidTransport(
                                    data.start().getBottomCenter(),
                                    getBlockPos().getBottomCenter(),
                                    data.finish().getBottomCenter(),
                                    handler.drain(sendAmount, IFluidHandler.FluidAction.EXECUTE),
                                    data.edgePath()));
                        });
                    });
        }

        if (toRemove.isEmpty())
            return;
        this.pathData.removeAll(toRemove);
        this.markDirty();
    }


    @Override
    protected void firstTick()
    {

    }

    public List<PathData> getPathData()
    {
        return pathData;
    }

    @Override
    public void readCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        pathData.clear();
        ListTag list = tag.getList("path_data", Tag.TAG_COMPOUND);
        for (Tag value : list)
        {
            PathData data = PathData.CODEC.parse(NbtOps.INSTANCE, value).result().orElseThrow();
            pathData.add(data);
        }
    }

    @Override
    public void writeCustomTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries, boolean descrPacket)
    {
        ListTag list = new ListTag();
        for (PathData data : this.pathData)
            PathData.CODEC.encodeStart(NbtOps.INSTANCE, data).ifSuccess(list :: add);
        tag.put("path_data", list);
    }

    @Override
    public InteractionResult onUsed(@NotNull ItemStack stack, @NotNull UseOnContext ctx)
    {
        Level level = ctx.getLevel();
        if (stack.has(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA))
        {
            if (level.isClientSide())
                return InteractionResult.SUCCESS;
            List<Vec3> positions = stack.get(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA);
            if (positions == null)
                return InteractionResult.TRY_WITH_EMPTY_HAND;

            Vec3 start = positions.getFirst();
            Vec3 end = positions.get(1);
            if (start.equals(Vec3.ZERO) || end.equals(Vec3.ZERO))
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            List<BlockPos> path = PathFinder.findPath(BlockPos.containing(start), getBlockPos(), BlockPos.containing(end), level);
	        //FIXME: переписать алгоритм поиска пути
			//Database.LOGGER.warn("PATH:");
			//path.forEach(blockPos -> Database.LOGGER.warn("Pos: {}", blockPos.toShortString()));
            List<Vec3> edgePath = EdgePathFinder.findEdgePath(path, level);
			//Database.LOGGER.warn("Edges:");
			//edgePath.forEach(vec3 -> Database.LOGGER.warn("Edge: {}", vec3.toString()));
            List<Vec3> interpolatedPath = PathInterpolator.interpolatePath(edgePath);
			//Database.LOGGER.warn("Interpolations:");
			//interpolatedPath.forEach(vec3 -> Database.LOGGER.warn("Interpolated: {}", vec3));
            stack.remove(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA);
            this.pathData.add(new PathData(BlockPos.containing(start), BlockPos.containing(end), path, interpolatedPath));
            this.markDirty();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static class PathFinder
    {
        public static @NotNull List<BlockPos> findPath(BlockPos start, BlockPos middle, BlockPos end, Level level)
        {
            List<BlockPos> path1 = findPathSegment(start, middle, level);
            List<BlockPos> path2 = findPathSegment(middle, end, level);

            List<BlockPos> fullPath = new ArrayList<>();
            if (path1 == null || path2 == null)
                return fullPath;

            fullPath.addAll(path1);
            path2.removeFirst(); // Убираем дублирование узла middle
            fullPath.addAll(path2);

            return fullPath;
        }

        private static @Nullable List<BlockPos> findPathSegment(BlockPos start, BlockPos goal, Level level)
        {
            PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
            Map<BlockPos, Node> allNodes = new HashMap<>();

            Node startNode = new Node(start, null, 0, heuristic(start, goal));
            openSet.add(startNode);
            allNodes.put(start, startNode);

            while (!openSet.isEmpty())
            {
                Node current = openSet.poll();

                if (current.pos.equals(goal))
                    return reconstructPath(current);

                for (BlockPos neighbor : getNeighbors(current.pos, level))
                {
                    double newGCost = current.gCost + 1;
                    Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor, null, Double.MAX_VALUE, 0));

                    if (newGCost < neighborNode.gCost)
                    {
                        neighborNode.parent = current;
                        neighborNode.gCost = newGCost;
                        neighborNode.hCost = heuristic(neighbor, goal);
                        neighborNode.fCost = neighborNode.gCost + neighborNode.hCost;

                        if (!openSet.contains(neighborNode))
                            openSet.add(neighborNode);
                        allNodes.put(neighbor, neighborNode);
                    }
                }
            }
            return null; // Путь не найден
        }

        private static @NotNull List<BlockPos> reconstructPath(Node node)
        {
            List<BlockPos> path = new ArrayList<>();
            while (node != null)
            {
                path.add(node.pos);
                node = node.parent;
            }
            Collections.reverse(path);
            return path;
        }

        private static @NotNull List<BlockPos> getNeighbors(@NotNull BlockPos pos, Level level)
        {
            List<BlockPos> neighbors = new ArrayList<>();
            BlockPos[] offsets =
                    {
                    pos.above(), pos.below(),
                    pos.north(), pos.south(),
                    pos.east(), pos.west()
            };

            for (BlockPos offset : offsets)
                if (isPassable(offset, level))
                    neighbors.add(offset);
            return neighbors;
        }

        private static boolean isPassable(BlockPos pos, @NotNull Level level)
        {
            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(level, pos);
            return !state.isEmpty() && !shape.isEmpty();
        }

        private static double heuristic(@NotNull BlockPos a, @NotNull BlockPos b)
        {
            return a.distSqr(b);
        }

        private static class Node
        {
            BlockPos pos;
            Node parent;
            double gCost, hCost, fCost;

            Node(BlockPos pos, Node parent, double gCost, double hCost)
            {
                this.pos = pos;
                this.parent = parent;
                this.gCost = gCost;
                this.hCost = hCost;
                this.fCost = gCost + hCost;
            }
        }
    }

    private static class EdgePathFinder
    {

        public static @NotNull List<Vec3> findEdgePath(@NotNull List<BlockPos> blockPath, Level level)
        {
            List<Vec3> edgePath = new ArrayList<>();

            Vec3 lastPoint = null;

            for (int q = 0; q < blockPath.size() - 1; q++)
            {
                BlockPos current = blockPath.get(q);
                BlockPos next = blockPath.get(q + 1);

                List<Vec3> segment = findPathOnEdges(lastPoint, current, next, level);
                if (segment == null)
                    return List.of();
                edgePath.addAll(segment);
                if (q < blockPath.size() - 2)
                    lastPoint = edgePath.removeLast();
            }
            return edgePath;
        }

        private static @Nullable List<Vec3> findPathOnEdges(@Nullable Vec3 startPoint, BlockPos start, BlockPos next, Level level)
        {
            List<Vec3> startEdges = getEdgePoints(start, level);
            List<Vec3> nextEdges = getEdgePoints(next, level);

            if (startEdges.isEmpty() || nextEdges.isEmpty())
                return null;

            boolean canReach = false;
            for (Vec3 startEdge : startEdges)
            {
                if (nextEdges.contains(startEdge))
                {
                    canReach = true;
                    break;
                }
            }

            List<Vec3> startEdgesSorted = new ArrayList<>(startEdges);
            List<Vec3> nextEdgesSorted = new ArrayList<>(nextEdges);
            startEdgesSorted.sort(Comparator.comparingDouble(vec3 -> heuristic(vec3, nextEdges)));
            nextEdgesSorted.sort(Comparator.comparingDouble(vec3 -> startPoint != null ? vec3.distanceToSqr(startPoint) : heuristic(vec3, startEdges)));
            Vec3 bestStart = startPoint != null ? startPoint : startEdgesSorted.getFirst();
            Vec3 bestNext = nextEdgesSorted.getFirst();

            List<Vec3> path = new ArrayList<>();
            path.add(bestStart);
            Vec3 current = bestStart;
			
			Set<Vec3> visited = new HashSet<>();
			visited.add(current);
			
            if (canReach)
            {
                while (!current.equals(bestNext))
                {
                    List<Vec3> connected = getConnectedEdges(current, start, level);
                    if (connected.isEmpty())
                        return null;
					
					connected.removeIf(visited :: contains);
					if (connected.isEmpty())
						return null;
					
                    Vec3 finalBestNext = bestNext;
                    connected.sort(Comparator.comparingDouble(vec3 -> vec3.distanceToSqr(finalBestNext)));
                    Vec3 closest = connected.getFirst();
                    path.add(closest);
					visited.add(closest);
                    current = closest;
                }
            }
            else
            {
                Vec3 finish = startEdgesSorted.getFirst();
				
                while (!current.equals(finish))
                {
                    List<Vec3> connected = getConnectedEdges(current, start, level);
                    if (connected.isEmpty())
                        return null;
					
					connected.removeIf(visited :: contains);
					if (connected.isEmpty())
						return null;
					
                    connected.sort(Comparator.comparingDouble(vec3 -> heuristic(vec3, nextEdges)));
                    Vec3 closest = connected.getFirst();
                    path.add(closest);
					visited.add(closest);
                    current = closest;
                }

                Vec3 finalCurrent = current;
                nextEdgesSorted.sort(Comparator.comparingDouble(vec3 -> vec3.distanceToSqr(finalCurrent)));
                bestNext = nextEdgesSorted.getFirst();
                path.add(bestNext);
            }
            return path;
        }

        private static @NotNull List<Vec3> getEdgePoints(BlockPos pos, @NotNull Level level)
        {
            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(level, pos);
            List<Vec3> edges = new ArrayList<>();

            shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
            {
                //Минимальный угол
                edges.add(new Vec3(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ));
                //Угол, сдвиг по Z
                edges.add(new Vec3(pos.getX() + minX, pos.getY() + minY, pos.getZ() + maxZ));
                //Угол, сдвиг по Y
                edges.add(new Vec3(pos.getX() + minX, pos.getY() + maxY, pos.getZ() + minZ));
                edges.add(new Vec3(pos.getX() + minX, pos.getY() + maxY, pos.getZ() + maxZ));
                //Угол, сдвиг по X
                edges.add(new Vec3(pos.getX() + maxX, pos.getY() + minY, pos.getZ() + minZ));
                edges.add(new Vec3(pos.getX() + maxX, pos.getY() + minY, pos.getZ() + maxZ));
                edges.add(new Vec3(pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + minZ));
                edges.add(new Vec3(pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ));

                //Ребра
                edges.add(new Vec3(pos.getX() + minX, pos.getY() + minY, pos.getZ() + (minZ + maxZ) / 2));
                edges.add(new Vec3(pos.getX() + minX, pos.getY() + maxY, pos.getZ() + (minZ + maxZ) / 2));
                edges.add(new Vec3(pos.getX() + minX, pos.getY() + (minY + maxY) / 2, pos.getZ() + minZ));
                edges.add(new Vec3(pos.getX() + minX, pos.getY() + (minY + maxY) / 2, pos.getZ() + maxZ));

                edges.add(new Vec3(pos.getX() + (minX + maxX) / 2, pos.getY() + minY, pos.getZ() + minZ));
                edges.add(new Vec3(pos.getX() + (minX + maxX) / 2, pos.getY() + minY, pos.getZ() + maxZ));
                edges.add(new Vec3(pos.getX() + (minX + maxX) / 2, pos.getY() + maxY, pos.getZ() + minZ));
                edges.add(new Vec3(pos.getX() + (minX + maxX) / 2, pos.getY() + maxY, pos.getZ() + maxZ));

                edges.add(new Vec3(pos.getX() + maxX, pos.getY() + minY, pos.getZ() + (minZ + maxZ) / 2));
                edges.add(new Vec3(pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + (minZ + maxZ) / 2));
                edges.add(new Vec3(pos.getX() + maxX, pos.getY() + (minY + maxY) / 2, pos.getZ() + minZ));
                edges.add(new Vec3(pos.getX() + maxX, pos.getY() + (minY + maxY) / 2, pos.getZ() + maxZ));
            });

            return edges;
        }

        private static @NotNull List<Vec3> getConnectedEdges(Vec3 current, BlockPos startPos, @NotNull Level level)
        {
            BlockState state = level.getBlockState(startPos);
            VoxelShape shape = state.getCollisionShape(level, startPos);

            List<Pair<Vec3, Vec3>> edgesList = getShapeEdges(shape, startPos);
            List<Vec3> connectedEdges = new ArrayList<>();

            for (Pair<Vec3, Vec3> edge : edgesList)
                if (onSameLine(current, edge))
                {
                    if (!current.equals(edge.getFirst()) && !connectedEdges.contains(edge.getFirst()))
                        connectedEdges.add(edge.getFirst());
                    if (!current.equals(edge.getSecond()) && !connectedEdges.contains(edge.getSecond()))
                        connectedEdges.add(edge.getSecond());
                }
            return connectedEdges;
        }

        private static @NotNull List<Pair<Vec3, Vec3>> getShapeEdges(@NotNull VoxelShape shape, BlockPos pos)
        {
            List<Pair<Vec3, Vec3>> edges = new ArrayList<>();

            shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
            {
                Vec3 v1 = new Vec3(pos.getX() + minX, pos.getY() + minY, pos.getZ() + minZ);
                Vec3 v2 = new Vec3(pos.getX() + minX, pos.getY() + minY, pos.getZ() + (minZ + maxZ) / 2);
                Vec3 v3 = new Vec3(pos.getX() + minX, pos.getY() + minY, pos.getZ() + maxZ);
                Vec3 v4 = new Vec3(pos.getX() + minX, pos.getY() + (minY + maxY) / 2, pos.getZ() + maxZ);
                Vec3 v5 = new Vec3(pos.getX() + minX, pos.getY() + maxY, pos.getZ() + maxZ);
                Vec3 v6 = new Vec3(pos.getX() + minX, pos.getY() + maxY, pos.getZ() + (minZ + maxZ) / 2);
                Vec3 v7 = new Vec3(pos.getX() + minX, pos.getY() + maxY, pos.getZ() + minZ);
                Vec3 v8 = new Vec3(pos.getX() + minX, pos.getY() + (minY + maxY) /2, pos.getZ() + minZ);
                Vec3 v9 = new Vec3(pos.getX() + (minX + maxX) / 2, pos.getY() + minY, pos.getZ() + minZ);
                Vec3 v10 = new Vec3(pos.getX() + (minX + maxX) / 2, pos.getY() + minY, pos.getZ() + maxZ);
                Vec3 v11 = new Vec3(pos.getX() + (minX + maxX) / 2, pos.getY() + maxY, pos.getZ() + maxZ);
                Vec3 v12 = new Vec3(pos.getX() + (minX + maxX) / 2, pos.getY() + maxY, pos.getZ() + minZ);
                Vec3 v13 = new Vec3(pos.getX() + maxX, pos.getY() + minY, pos.getZ() + minZ);
                Vec3 v14 = new Vec3(pos.getX() + maxX, pos.getY() + minY, pos.getZ() + (minZ + maxZ) / 2);
                Vec3 v15 = new Vec3(pos.getX() + maxX, pos.getY() + minY, pos.getZ() + maxZ);
                Vec3 v16 = new Vec3(pos.getX() + maxX, pos.getY() + (minY + maxY) / 2, pos.getZ() + maxZ);
                Vec3 v17 = new Vec3(pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + maxZ);
                Vec3 v18 = new Vec3(pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + (minZ + maxZ) / 2);
                Vec3 v19 = new Vec3(pos.getX() + maxX, pos.getY() + maxY, pos.getZ() + minZ);
                Vec3 v20 = new Vec3(pos.getX() + maxX, pos.getY() + (minY + maxY) /2, pos.getZ() + minZ);

                edges.add(new Pair<>(v1, v2));
                edges.add(new Pair<>(v2, v3));
                edges.add(new Pair<>(v3, v4));
                edges.add(new Pair<>(v4, v5));
                edges.add(new Pair<>(v5, v6));
                edges.add(new Pair<>(v6, v7));
                edges.add(new Pair<>(v7, v8));
                edges.add(new Pair<>(v8, v1));
                edges.add(new Pair<>(v1, v9));
                edges.add(new Pair<>(v3, v10));
                edges.add(new Pair<>(v5, v11));
                edges.add(new Pair<>(v7, v12));
                edges.add(new Pair<>(v9, v13));
                edges.add(new Pair<>(v10, v15));
                edges.add(new Pair<>(v11, v17));
                edges.add(new Pair<>(v12, v19));
                edges.add(new Pair<>(v13, v14));
                edges.add(new Pair<>(v14, v15));
                edges.add(new Pair<>(v15, v16));
                edges.add(new Pair<>(v16, v17));
                edges.add(new Pair<>(v17, v18));
                edges.add(new Pair<>(v18, v19));
                edges.add(new Pair<>(v19, v20));
                edges.add(new Pair<>(v20, v13));
            });

            return edges;
        }

        private static boolean onSameLine(@NotNull Vec3 a, @NotNull Pair<Vec3, Vec3> edge)
        {
            Vec3 edgeVec = edge.getFirst().vectorTo(edge.getSecond());
            Vec3 pointVec = edge.getFirst().vectorTo(a);
            Vec3 cross = edgeVec.cross(pointVec);

            return Math.abs(cross.x()) < 1e-9 &&
                    Math.abs(cross.y()) < 1e-9 &&
                    Math.abs(cross.z()) < 1e-9;
        }

        private static double heuristic(Vec3 a, @NotNull List<Vec3> goalEdges)
        {
            double minDist = Double.MAX_VALUE;
            for (Vec3 goal : goalEdges)
                minDist = Math.min(minDist, a.distanceToSqr(goal));
            return minDist;
        }
    }

    private static class PathInterpolator
    {
        private static final int MAX_STEPS_AMOUNT = 5;

        public static @NotNull List<Vec3> interpolatePath(@NotNull List<Vec3> edgePath)
        {
            List<Vec3> resultedPath = new ArrayList<>();
            resultedPath.add(edgePath.getFirst());
            for (int q = 0; q < edgePath.size() - 1; q++)
            {
                Vec3 start = edgePath.get(q);
                Vec3 finish = edgePath.get(q + 1);

                Vec3 sf = start.subtract(finish);
                double length = sf.length();
                int partsAmount = Math.max(1, (int)Math.round(length * MAX_STEPS_AMOUNT));
                for (int step = 0; step < partsAmount; step++)
                    resultedPath.add(start.lerp(finish, (float)step/partsAmount));
            }
            resultedPath.add(edgePath.getLast());
            return resultedPath;
        }
    }
    public record PathData(BlockPos start, BlockPos finish, List<BlockPos> blockPath, List<Vec3> edgePath)
    {
        public static final Codec<PathData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("start").forGetter(PathData :: start),
                BlockPos.CODEC.fieldOf("finish").forGetter(PathData :: finish),
                BlockPos.CODEC.listOf().fieldOf("blockPath").forGetter(PathData :: blockPath),
                Vec3.CODEC.listOf().fieldOf("edgePath").forGetter(PathData :: edgePath)).
                apply(instance, PathData :: new));

        public static final StreamCodec<FriendlyByteBuf, PathData> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                PathData :: start,
                BlockPos.STREAM_CODEC,
                PathData :: finish,
                ByteBufCodecs.<ByteBuf, BlockPos>list().
                        apply(BlockPos.STREAM_CODEC),
                PathData :: blockPath,
                ByteBufCodecs.<ByteBuf, Vec3>list().
                        apply(Vec3.STREAM_CODEC),
                PathData :: edgePath,
                PathData :: new);

        @Override
        public boolean equals(Object object)
        {
            if (this == object)
                return true;
            if (!(object instanceof PathData pathData))
                return false;
            return start.equals(pathData.start) && finish.equals(pathData.finish);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(start, finish);
        }
    }
}

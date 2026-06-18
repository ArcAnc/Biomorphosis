/**
 * @author ArcAnc
 * Created at: 04.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.block.block_entity;

import com.arcanc.biomorphosis.content.block.BlockInterfaces;
import com.arcanc.biomorphosis.content.block.block_entity.tick.ServerTickableBE;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.arcanc.biomorphosis.util.inventory.BasicSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidSidedStorage;
import com.arcanc.biomorphosis.util.inventory.fluid.FluidStackHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.*;

public class BioFluidTransmitter extends BioBaseBlockEntity implements BlockInterfaces.IWrencheable, ServerTickableBE
{
    private static final int PERIOD = 40;
    private static final float TRANSPORT_SPEED = 0.025f;
    private static final int SEND_AMOUNT = 100;
    private static final int MAX_LINK_DISTANCE = 16;

    private final Map<UUID, PathData> pathDataById = new LinkedHashMap<>();
    private final Map<UUID, TransportData> transportDataById = new LinkedHashMap<>();
    private final Map<UUID, List<Vec3>> clientPathCache = new HashMap<>();

    public BioFluidTransmitter(BlockPos pos, BlockState blockState)
    {
        super(Registration.BETypeReg.BE_FLUID_TRANSMITTER.get(), pos, blockState);
    }

    @Override
    public void tickServer()
    {
        if (this.level == null || this.pathDataById.isEmpty())
            return;
        Set<UUID> pathsToRemove = new HashSet<>();
        for (PathData data : this.pathDataById.values())
        {
            if (!FluidHelper.isFluidHandler(this.level, data.startPos()) || !FluidHelper.isFluidHandler(this.level, data.endPos()))
            {
                pathsToRemove.add(data.pathData());
                continue;
            }
            if (this.level.getGameTime() % PERIOD != 0)
                continue;
            FluidHelper.getFluidHandler(this.level, data.startPos()).ifPresent(handler ->
                    {
                        if (FluidHelper.isEmpty(handler))
                            return;
                        if (handler instanceof FluidSidedStorage storage)
                        {
                            List<FluidStackHolder> holders = storage.getHoldersForAccess(BasicSidedStorage.FaceMode.OUTPUT);
                            if (holders.isEmpty())
                                return;
                            FluidHelper.getFluidHandler(this.level, data.endPos()).ifPresent(finishHandler ->
                            {
                                if (FluidHelper.isFull(finishHandler))
                                    return;
                                
                                for (FluidStackHolder holder : holders)
                                {
                                    FluidStack fluidStack = holder.drain(SEND_AMOUNT, IFluidHandler.FluidAction.SIMULATE);
                                    
                                    if (finishHandler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) > 0)
                                        addTransport(data.pathData(), PathDirection.POSITIVE, holder.drain(SEND_AMOUNT, IFluidHandler.FluidAction.EXECUTE));
                                }
                            });
                        }
                        else
                        {
                            FluidHelper.getFluidHandler(this.level, data.endPos()).ifPresent(finishHandler ->
                            {
                                if (FluidHelper.isFull(finishHandler))
                                    return;
                                FluidStack fluidStack = handler.drain(SEND_AMOUNT, IFluidHandler.FluidAction.SIMULATE);
                                if (finishHandler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) > 0)
                                    addTransport(data.pathData(), PathDirection.POSITIVE, handler.drain(SEND_AMOUNT, IFluidHandler.FluidAction.EXECUTE));
                            });
                        }
                    });
        }

        boolean dirty = tickTransports();
        if (!pathsToRemove.isEmpty())
        {
            pathsToRemove.forEach(this.pathDataById :: remove);
            this.transportDataById.entrySet().removeIf(entry -> pathsToRemove.contains(entry.getValue().getPathDataId()));
            dirty = true;
        }
        if (dirty)
            this.markDirty();
    }


    @Override
    protected void firstTick()
    {

    }

    public Collection<PathData> getPathData()
    {
        return this.pathDataById.values();
    }

    public Collection<TransportData> getTransportData()
    {
        return this.transportDataById.values();
    }

    public Collection<Map.Entry<UUID, TransportData>> getTransportDataEntries()
    {
        return this.transportDataById.entrySet();
    }

    public List<Vec3> getClientPath(PathData data)
    {
        if (this.level == null)
            return List.of();
        return this.clientPathCache.computeIfAbsent(data.pathData(), id ->
        {
            SurfacePath path = SurfacePathFinder.findPath(data.startPos(), getBlockPos(), data.endPos(), this.level);
            if (path.points().isEmpty())
                return List.of();
            return PathInterpolator.interpolatePath(path.points());
        });
    }

    @Override
    public void readCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        Map<UUID, PathData> paths = new LinkedHashMap<>();
        ListTag pathList = tag.getList("path_data", Tag.TAG_COMPOUND);
        for (Tag value : pathList)
        {
            PathData data = PathData.load((CompoundTag)value);
            paths.put(data.pathData(), data);
        }
        this.clientPathCache.keySet().removeIf(id -> !paths.containsKey(id) || !hasSameEndpoints(this.pathDataById.get(id), paths.get(id)));
        this.pathDataById.clear();
        this.pathDataById.putAll(paths);

        this.transportDataById.clear();
        ListTag transportList = tag.getList("transport_data", Tag.TAG_COMPOUND);
        for (Tag value : transportList)
        {
            CompoundTag transportTag = (CompoundTag)value;
            this.transportDataById.put(transportTag.getUUID("id"), TransportData.load(transportTag));
        }
    }

    @Override
    public void writeCustomTag(CompoundTag tag, HolderLookup.Provider registries, boolean descrPacket)
    {
        ListTag pathList = new ListTag();
        for (PathData data : this.pathDataById.values())
            pathList.add(data.save());
        tag.put("path_data", pathList);

        ListTag transportList = new ListTag();
        for (Map.Entry<UUID, TransportData> entry : this.transportDataById.entrySet())
            transportList.add(entry.getValue().save(entry.getKey()));
        tag.put("transport_data", transportList);
    }

    @Override
    public InteractionResult onUsed(ItemStack stack, UseOnContext ctx)
    {
        Level level = ctx.getLevel();
        if (stack.has(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA))
        {
            if (level.isClientSide())
                return InteractionResult.SUCCESS;
            List<Vec3> positions = stack.get(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA);
            if (positions == null)
            {
                stack.remove(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA);
                return InteractionResult.PASS;
            }

            Vec3 start = positions.getFirst();
            Vec3 end = positions.get(1);
            stack.remove(Registration.DataComponentsReg.FLUID_TRANSMIT_DATA);
            if (start.equals(Vec3.ZERO) || end.equals(Vec3.ZERO))
                return InteractionResult.PASS;
            BlockPos startPos = BlockPos.containing(start);
            BlockPos endPos = BlockPos.containing(end);
            if (!isWithinLinkDistance(startPos) || !isWithinLinkDistance(endPos))
                return InteractionResult.FAIL;
            SurfacePath path = SurfacePathFinder.findPath(
                    startPos,
                    getBlockPos(),
                    endPos,
                    level);
            if (path.points().isEmpty())
                return InteractionResult.FAIL;

            List<Vec3> interpolatedPath = PathInterpolator.interpolatePath(path.points());
            PathData data = new PathData(
                    UUID.randomUUID(),
                    startPos,
                    endPos);
            this.pathDataById.put(data.pathData(), data);
            this.clientPathCache.put(data.pathData(), interpolatedPath);
            this.markDirty();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private boolean isWithinLinkDistance(BlockPos pos)
    {
        return pos.distSqr(getBlockPos()) <= MAX_LINK_DISTANCE * MAX_LINK_DISTANCE;
    }

    private void addTransport(UUID pathDataId, PathDirection direction, FluidStack fluid)
    {
        if (fluid.isEmpty())
            return;
        this.transportDataById.put(UUID.randomUUID(), new TransportData(pathDataId, 0, direction, fluid));
    }

    private static boolean hasSameEndpoints(PathData first, PathData second)
    {
        return first != null &&
                second != null &&
                first.startPos().equals(second.startPos()) &&
                first.endPos().equals(second.endPos());
    }

    private boolean tickTransports()
    {
        if (this.level.getGameTime() % 5 != 0 || this.transportDataById.isEmpty())
            return false;

        boolean dirty = false;
        List<UUID> toRemove = new ArrayList<>();
        List<TransportData> toAdd = new ArrayList<>();

        for (Map.Entry<UUID, TransportData> entry : this.transportDataById.entrySet())
        {
            TransportData transport = entry.getValue();
            PathData pathData = this.pathDataById.get(transport.getPathDataId());
            if (pathData == null)
            {
                toRemove.add(entry.getKey());
                dirty = true;
                continue;
            }

            transport.move(TRANSPORT_SPEED);
            dirty = true;
            if (transport.getPercent() < 1)
                continue;

            BlockPos destination = transport.getDirection() == PathDirection.POSITIVE ? pathData.endPos() : pathData.startPos();
            BlockPos returnDestination = transport.getDirection() == PathDirection.POSITIVE ? pathData.startPos() : pathData.endPos();
            FluidStack left = FluidHelper.getFluidHandler(this.level, destination).map(handler ->
            {
                FluidStack stack = transport.getFluid();
                return new FluidStack(stack.getFluid(), stack.getAmount() - handler.fill(stack, IFluidHandler.FluidAction.EXECUTE));
            }).orElse(transport.getFluid());

            toRemove.add(entry.getKey());
            if (!left.isEmpty() && FluidHelper.isFluidHandler(this.level, returnDestination))
                toAdd.add(new TransportData(pathData.pathData(), 0, transport.getDirection().opposite(), left));
        }

        toRemove.forEach(this.transportDataById :: remove);
        for (TransportData transportData : toAdd)
            this.transportDataById.put(UUID.randomUUID(), transportData);
        return dirty || !toRemove.isEmpty() || !toAdd.isEmpty();
    }

    private record SurfacePath(List<BlockPos> blocks, List<Vec3> points)
    {
    }

    private static class SurfacePathFinder
    {
        private static final int CELLS_PER_BLOCK = 8;
        private static final double CELL_SIZE = 1.0 / CELLS_PER_BLOCK;
        private static final int MAX_VISITED_NODES = 250_000;
        private static final Direction[] SUPPORT_DIRECTIONS = Direction.values();

        public static SurfacePath findPath(BlockPos start, BlockPos transmitter, BlockPos end, Level level)
        {
            SurfaceGraph graph = new SurfaceGraph(level);
            Set<GridPos> startNodes = graph.getAttachmentNodes(start);
            Set<GridPos> transmitterNodes = graph.getAttachmentNodes(transmitter);
            Set<GridPos> endNodes = graph.getAttachmentNodes(end);
            if (startNodes.isEmpty() || transmitterNodes.isEmpty() || endNodes.isEmpty())
                return new SurfacePath(List.of(), List.of());

            List<GridPos> firstSegment = findPathSegment(startNodes, transmitterNodes, graph);
            if (firstSegment.isEmpty())
                return new SurfacePath(List.of(), List.of());

            GridPos transmitterNode = firstSegment.getLast();
            List<GridPos> secondSegment = findPathSegment(Set.of(transmitterNode), endNodes, graph);
            if (secondSegment.isEmpty())
                return new SurfacePath(List.of(), List.of());

            List<GridPos> fullPath = new ArrayList<>(firstSegment);
            fullPath.addAll(secondSegment.subList(1, secondSegment.size()));

            Optional<Vec3> startPoint = graph.getSurfacePoint(start, fullPath.getFirst());
            Optional<Vec3> endPoint = graph.getSurfacePoint(end, fullPath.getLast());
            if (startPoint.isEmpty() || endPoint.isEmpty())
                return new SurfacePath(List.of(), List.of());

            List<Vec3> points = new ArrayList<>();
            points.add(startPoint.get());
            points.addAll(fullPath.stream().map(GridPos::center).toList());
            points.add(endPoint.get());

            List<BlockPos> blocks = new ArrayList<>();
            for (Vec3 point : points)
            {
                BlockPos pos = BlockPos.containing(point);
                if (blocks.isEmpty() || !blocks.getLast().equals(pos))
                    blocks.add(pos);
            }
            return new SurfacePath(blocks, points);
        }

        private static List<GridPos> findPathSegment(Set<GridPos> starts, Set<GridPos> goals, SurfaceGraph graph)
        {
            PriorityQueue<QueueEntry> openSet = new PriorityQueue<>(Comparator.comparingDouble(QueueEntry::estimatedCost));
            Map<GridPos, Double> costs = new HashMap<>();
            Map<GridPos, GridPos> parents = new HashMap<>();
            GoalBounds goalBounds = GoalBounds.of(goals);

            for (GridPos start : starts)
            {
                costs.put(start, 0.0);
                openSet.add(new QueueEntry(start, heuristic(start, goalBounds)));
            }

            int visited = 0;
            while (!openSet.isEmpty() && visited++ < MAX_VISITED_NODES)
            {
                QueueEntry entry = openSet.poll();
                GridPos current = entry.pos();
                double currentCost = costs.getOrDefault(current, Double.POSITIVE_INFINITY);
                if (entry.estimatedCost() > currentCost + heuristic(current, goalBounds) + 1.0E-9)
                    continue;
                if (goals.contains(current))
                    return reconstructPath(current, parents);

                for (GridPos neighbor : graph.getNeighbors(current))
                {
                    double newCost = currentCost + current.distanceTo(neighbor);
                    if (newCost >= costs.getOrDefault(neighbor, Double.POSITIVE_INFINITY))
                        continue;
                    costs.put(neighbor, newCost);
                    parents.put(neighbor, current);
                    openSet.add(new QueueEntry(neighbor, newCost + heuristic(neighbor, goalBounds)));
                }
            }
            return List.of();
        }

        private static List<GridPos> reconstructPath(GridPos end, Map<GridPos, GridPos> parents)
        {
            List<GridPos> path = new ArrayList<>();
            GridPos current = end;
            while (current != null)
            {
                path.add(current);
                current = parents.get(current);
            }
            Collections.reverse(path);
            return path;
        }

        private static double heuristic(GridPos pos, GoalBounds goal)
        {
            int dx = Math.max(goal.minX - pos.x, Math.max(0, pos.x - goal.maxX));
            int dy = Math.max(goal.minY - pos.y, Math.max(0, pos.y - goal.maxY));
            int dz = Math.max(goal.minZ - pos.z, Math.max(0, pos.z - goal.maxZ));
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }

        private record QueueEntry(GridPos pos, double estimatedCost)
        {
        }

        private record GoalBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
        {
            private static GoalBounds of(Set<GridPos> goals)
            {
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int minZ = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;
                int maxZ = Integer.MIN_VALUE;
                for (GridPos goal : goals)
                {
                    minX = Math.min(minX, goal.x);
                    minY = Math.min(minY, goal.y);
                    minZ = Math.min(minZ, goal.z);
                    maxX = Math.max(maxX, goal.x);
                    maxY = Math.max(maxY, goal.y);
                    maxZ = Math.max(maxZ, goal.z);
                }
                return new GoalBounds(minX, minY, minZ, maxX, maxY, maxZ);
            }
        }

        private record GridPos(int x, int y, int z)
        {
            private GridPos relative(Direction direction)
            {
                return new GridPos(
                        this.x + direction.getStepX(),
                        this.y + direction.getStepY(),
                        this.z + direction.getStepZ());
            }

            private Vec3 center()
            {
                return new Vec3(
                        (this.x + 0.5) * CELL_SIZE,
                        (this.y + 0.5) * CELL_SIZE,
                        (this.z + 0.5) * CELL_SIZE);
            }

            private double distanceTo(GridPos other)
            {
                int dx = this.x - other.x;
                int dy = this.y - other.y;
                int dz = this.z - other.z;
                return Math.sqrt(dx * dx + dy * dy + dz * dz);
            }
        }

        private static class SurfaceGraph
        {
            private final Level level;
            private final Map<GridPos, Boolean> occupied = new HashMap<>();
            private final Map<GridPos, Boolean> surface = new HashMap<>();

            private SurfaceGraph(Level level)
            {
                this.level = level;
            }

            private Set<GridPos> getAttachmentNodes(BlockPos blockPos)
            {
                Set<GridPos> result = new HashSet<>();
                int minX = blockPos.getX() * CELLS_PER_BLOCK - 1;
                int minY = blockPos.getY() * CELLS_PER_BLOCK - 1;
                int minZ = blockPos.getZ() * CELLS_PER_BLOCK - 1;
                int maxX = (blockPos.getX() + 1) * CELLS_PER_BLOCK;
                int maxY = (blockPos.getY() + 1) * CELLS_PER_BLOCK;
                int maxZ = (blockPos.getZ() + 1) * CELLS_PER_BLOCK;

                for (int x = minX; x <= maxX; x++)
                    for (int y = minY; y <= maxY; y++)
                        for (int z = minZ; z <= maxZ; z++)
                        {
                            GridPos candidate = new GridPos(x, y, z);
                            if (!isSurface(candidate))
                                continue;
                            for (Direction direction : SUPPORT_DIRECTIONS)
                            {
                                GridPos support = candidate.relative(direction);
                                if (isOccupied(support) && cellBlockPos(support).equals(blockPos))
                                {
                                    result.add(candidate);
                                    break;
                                }
                            }
                        }
                return result;
            }

            private Optional<Vec3> getSurfacePoint(BlockPos blockPos, GridPos attachmentNode)
            {
                VoxelShape shape = this.level.getBlockState(blockPos).getCollisionShape(this.level, blockPos);
                Vec3 blockOrigin = Vec3.atLowerCornerOf(blockPos);
                Vec3 localAttachment = attachmentNode.center().subtract(blockOrigin);
                return shape.closestPointTo(localAttachment).map(point -> point.add(blockOrigin));
            }

            private List<GridPos> getNeighbors(GridPos pos)
            {
                List<GridPos> result = new ArrayList<>(26);
                for (int dx = -1; dx <= 1; dx++)
                    for (int dy = -1; dy <= 1; dy++)
                        for (int dz = -1; dz <= 1; dz++)
                        {
                            if (dx == 0 && dy == 0 && dz == 0)
                                continue;
                            GridPos neighbor = new GridPos(pos.x + dx, pos.y + dy, pos.z + dz);
                            if (isSurface(neighbor))
                                result.add(neighbor);
                        }
                return result;
            }

            private boolean isSurface(GridPos pos)
            {
                return this.surface.computeIfAbsent(pos, key ->
                {
                    if (isOccupied(key))
                        return false;
                    for (Direction direction : SUPPORT_DIRECTIONS)
                        if (isOccupied(key.relative(direction)))
                            return true;
                    return false;
                });
            }

            private boolean isOccupied(GridPos pos)
            {
                return this.occupied.computeIfAbsent(pos, key ->
                {
                    BlockPos blockPos = cellBlockPos(key);
                    BlockState state = this.level.getBlockState(blockPos);
                    VoxelShape shape = state.getCollisionShape(this.level, blockPos);
                    if (shape.isEmpty())
                        return false;

                    double minX = key.x * CELL_SIZE - blockPos.getX();
                    double minY = key.y * CELL_SIZE - blockPos.getY();
                    double minZ = key.z * CELL_SIZE - blockPos.getZ();
                    VoxelShape cell = Shapes.box(
                            minX,
                            minY,
                            minZ,
                            minX + CELL_SIZE,
                            minY + CELL_SIZE,
                            minZ + CELL_SIZE);
                    return Shapes.joinIsNotEmpty(shape, cell, BooleanOp.AND);
                });
            }

            private static BlockPos cellBlockPos(GridPos pos)
            {
                return new BlockPos(
                        Math.floorDiv(pos.x, CELLS_PER_BLOCK),
                        Math.floorDiv(pos.y, CELLS_PER_BLOCK),
                        Math.floorDiv(pos.z, CELLS_PER_BLOCK));
            }
        }
    }

    private static class PathInterpolator
    {
        private static final int MAX_STEPS_AMOUNT = 5;

        public static List<Vec3> interpolatePath(List<Vec3> edgePath)
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
    public record PathData(UUID pathData, BlockPos startPos, BlockPos endPos)
    {
        private CompoundTag save()
        {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("id", this.pathData);
            tag.put("start", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.startPos).getOrThrow());
            tag.put("end", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.endPos).getOrThrow());
            return tag;
        }

        private static PathData load(CompoundTag tag)
        {
            Tag endTag = tag.contains("end") ? tag.get("end") : tag.get("finish");
            return new PathData(
                    tag.hasUUID("id") ? tag.getUUID("id") : UUID.randomUUID(),
                    BlockPos.CODEC.parse(NbtOps.INSTANCE, tag.get("start")).result().orElse(BlockPos.ZERO),
                    BlockPos.CODEC.parse(NbtOps.INSTANCE, endTag).result().orElse(BlockPos.ZERO));
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object)
                return true;
            if (!(object instanceof PathData pathData))
                return false;
            return this.pathData.equals(pathData.pathData);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(this.pathData);
        }
    }

    public static final class TransportData
    {
        private final UUID pathDataId;
        private float percent;
        private PathDirection dir;
        private FluidStack fluid;

        public TransportData(UUID pathDataId, float percent, PathDirection dir, FluidStack fluid)
        {
            this.pathDataId = pathDataId;
            this.percent = percent;
            this.dir = dir;
            this.fluid = fluid;
        }

        public UUID getPathDataId()
        {
            return this.pathDataId;
        }

        public float getPercent()
        {
            return this.percent;
        }

        public PathDirection getDirection()
        {
            return this.dir;
        }

        public FluidStack getFluid()
        {
            return this.fluid;
        }

        private void move(float amount)
        {
            this.percent = Math.min(1, this.percent + amount);
        }

        private CompoundTag save(UUID id)
        {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("id", id);
            tag.putUUID("path", this.pathDataId);
            tag.putFloat("percent", this.percent);
            tag.putString("dir", this.dir.name());
            FluidStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, this.fluid).ifSuccess(fluidTag -> tag.put("fluid", fluidTag));
            return tag;
        }

        private static TransportData load(CompoundTag tag)
        {
            PathDirection direction = Arrays.stream(PathDirection.values()).
                    filter(value -> value.name().equals(tag.getString("dir"))).
                    findFirst().
                    orElse(PathDirection.POSITIVE);
            return new TransportData(
                    tag.getUUID("path"),
                    tag.getFloat("percent"),
                    direction,
                    FluidStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, tag.get("fluid")).result().orElse(FluidStack.EMPTY));
        }
    }

    public enum PathDirection
    {
        POSITIVE,
        NEGATIVE;

        public PathDirection opposite()
        {
            return this == POSITIVE ? NEGATIVE : POSITIVE;
        }
    }
}

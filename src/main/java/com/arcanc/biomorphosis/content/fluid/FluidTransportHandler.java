/**
 * @author ArcAnc
 * Created at: 04.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.fluid;

import com.arcanc.biomorphosis.content.network.NetworkEngine;
import com.arcanc.biomorphosis.content.network.packets.S2CFluidTransportPacket;
import com.arcanc.biomorphosis.util.helper.FluidHelper;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import com.arcanc.biomorphosis.util.helper.TagHelper;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class FluidTransportHandler
{
    private static final FluidTransportHandler SERVER_INSTANCE = new FluidServerTransportHandler();
    private static final FluidTransportHandler CLIENT_INSTANCE = new FluidClientTransportHandler();

    public abstract Set<FluidTransport> getTransport(Level level);
    public abstract Set<FluidTransport> getRemoveList(Level level);
    public abstract Set<FluidTransport> getAddList(Level level);
    public static FluidTransportHandler get(@NotNull Level level)
    {
        return get(level.isClientSide());
    }

    public static FluidTransportHandler get(boolean isClient)
    {
        return isClient ? CLIENT_INSTANCE : SERVER_INSTANCE;
    }

    public static void addTransport(Level level, FluidTransport trn)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            Set<FluidTransport> addList = get(false).getAddList(level);
            addList.add(trn);
        }
    }

    public static void removeTransport(Level level, FluidTransport trn)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            FluidTransportHandler handler = get(false);
            handler.getRemoveList(level).add(trn);
        }
    }

    private static class FluidServerTransportHandler extends FluidTransportHandler
    {
        private final Map<ResourceLocation, Set<FluidTransport>> TRANSPORT = new HashMap<>();
        private final Map<ResourceLocation, Set<FluidTransport>> TO_REMOVE = new HashMap<>();
        private final Map<ResourceLocation, Set<FluidTransport>> TO_ADD = new HashMap<>();

        @Override
        public Set<FluidTransport> getTransport(@NotNull Level level)
        {
            return TRANSPORT.computeIfAbsent(level.dimension().location(), k -> new HashSet<>());
        }

        @Override
        public Set<FluidTransport> getRemoveList(@NotNull Level level)
        {
            return TO_REMOVE.computeIfAbsent(level.dimension().location(), k -> new HashSet<>());
        }

        @Override
        public Set<FluidTransport> getAddList(@NotNull Level level)
        {
            return TO_ADD.computeIfAbsent(level.dimension().location(), k -> new HashSet<>());
        }
    }

    private static class FluidClientTransportHandler extends FluidTransportHandler
    {
        private final Set<FluidTransport> TRANSPORT = new HashSet<>();
        private final Set<FluidTransport> TO_REMOVE = new HashSet<>();
        private final Set<FluidTransport> TO_ADD = new HashSet<>();

        @Override
        public Set<FluidTransport> getTransport(@Nullable Level level)
        {
            return TRANSPORT;
        }

        @Override
        public Set<FluidTransport> getRemoveList(@Nullable Level level)
        {
            return TO_REMOVE;
        }

        @Override
        public Set<FluidTransport> getAddList(Level level)
        {
            return TO_ADD;
        }
    }

    public static Set<FluidTransport> getTransportTable(@NotNull Level level)
    {
        return get(level).getTransport(level);
    }

    private static void loadLevel(final LevelEvent.@NotNull Load event)
    {
        LevelAccessor levelAccessor = event.getLevel();
        if (!(levelAccessor instanceof ServerLevel serverLevel))
            return;

        Set<FluidTransport> data = getTransportTable(serverLevel);
        data.clear();
        FluidTransportSavedData savedData = FluidTransportSavedData.getInstance(serverLevel);
        data.addAll(savedData.getSavedInfo());
    }

    private static void unloadLevel(final LevelEvent.@NotNull Unload event)
    {
        LevelAccessor level = event.getLevel();
        if (level.isClientSide())
            getTransportTable((Level) level).clear();
    }

    private static void saveLevel(final LevelEvent.@NotNull Save event)
    {
        FluidTransportSavedData.getInstance((ServerLevel) event.getLevel()).setDirty();
    }

    private static void playerLoad(final @NotNull EntityJoinLevelEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            Level level = event.getLevel();
            Set<FluidTransport> transports = getTransportTable(level);
            NetworkEngine.sendToPlayer(serverPlayer, new S2CFluidTransportPacket(transports.stream().toList(), S2CFluidTransportPacket.TransportAction.SYNC));
        }
    }

    private static void levelTickPre(final LevelTickEvent.@NotNull Pre event)
    {
        Level level = event.getLevel();
        FluidTransportHandler handler = FluidTransportHandler.get(level);
        if (!event.hasTime())
            return;

        if (!level.isClientSide())
            addTransport(level);

        handler.getTransport(level).forEach(transport -> transport.tick(level));

        if (!level.isClientSide())
            removeTransport(level);
    }

    private static void addTransport(Level level)
    {
        if (!(level instanceof ServerLevel serverLevel))
            return;

        FluidTransportHandler handler = FluidTransportHandler.get(level);
        Set<FluidTransport> addList = handler.getAddList(level);
        if (addList.isEmpty())
            return;
        addList.forEach(transport -> handler.getTransport(serverLevel).add(transport));
        serverLevel.players().forEach(serverPlayer ->
        {
            if (serverPlayer.level().dimension().compareTo(serverLevel.dimension()) == 0)
                NetworkEngine.sendToPlayer(serverPlayer, new S2CFluidTransportPacket(addList.stream().toList(), S2CFluidTransportPacket.TransportAction.ADD));
        });
        addList.clear();
    }

    private static void removeTransport(Level level)
    {
        if (!(level instanceof ServerLevel serverLevel))
            return;
        FluidTransportHandler handler = FluidTransportHandler.get(level);
        Set<FluidTransport> removeList = handler.getRemoveList(level);
        if (removeList.isEmpty())
            return;
        removeList.forEach(transport -> handler.getTransport(serverLevel).remove(transport));
        serverLevel.players().forEach(serverPlayer ->
        {
            if (serverPlayer.level().dimension().compareTo(serverLevel.dimension()) == 0)
                NetworkEngine.sendToPlayer(serverPlayer, new S2CFluidTransportPacket(removeList.stream().toList(), S2CFluidTransportPacket.TransportAction.REMOVE));
        });
        removeList.clear();
    }

    private static void transportRenderer(final @NotNull RenderLevelStageEvent event)
    {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            return;

        CLIENT_INSTANCE.getTransport(RenderHelper.mc().level).
                forEach(trn ->
                {
                    trn.lerpPos(event.getPartialTick().getRealtimeDeltaTicks() / 5);
                    renderTransport(event, trn);
                });
    }

    private static final int latSegments = 8;
    private static final int lonSegments = 8;
    private static final float RADIUS = 0.08f;

    private static void renderTransport(@NotNull RenderLevelStageEvent event, @NotNull FluidTransport trn)
    {
        IClientFluidTypeExtensions renderProps = IClientFluidTypeExtensions.of(trn.getStack().getFluid());

        ResourceLocation stillTex = renderProps.getStillTexture();
        TextureAtlasSprite still = RenderHelper.getTexture(stillTex);


        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (int lat = 0; lat < latSegments; lat++)
        {
            float theta1 = (float) (Math.PI * lat / latSegments);
            float theta2 = (float) (Math.PI * (lat + 1) / latSegments);

            for (int lon = 0; lon < lonSegments; lon++)
            {
                float phi1 = (float) (2 * Math.PI * lon / lonSegments);
                float phi2 = (float) (2 * Math.PI * (lon + 1) / lonSegments);

                Vec3 trnPos = trn.prevPos;

                float[] v1 = sphereVertex(trnPos.x(), trnPos.y(), trnPos.z(), RADIUS, theta1, phi1);
                float[] v2 = sphereVertex(trnPos.x(), trnPos.y(), trnPos.z(), RADIUS, theta1, phi2);
                float[] v3 = sphereVertex(trnPos.x(), trnPos.y(), trnPos.z(), RADIUS, theta2, phi2);
                float[] v4 = sphereVertex(trnPos.x(), trnPos.y(), trnPos.z(), RADIUS, theta2, phi1);

                builder.addVertex(poseStack.last().pose(), v1[0], v1[1], v1[2]).setUv(still.getU0(), still.getV0()).setColor(renderProps.getTintColor());
                builder.addVertex(poseStack.last().pose(), v2[0], v2[1], v2[2]).setUv(still.getU1(), still.getV0()).setColor(renderProps.getTintColor());
                builder.addVertex(poseStack.last().pose(), v3[0], v3[1], v3[2]).setUv(still.getU1(), still.getV1()).setColor(renderProps.getTintColor());
                builder.addVertex(poseStack.last().pose(), v4[0], v4[1], v4[2]).setUv(still.getU0(), still.getV1()).setColor(renderProps.getTintColor());
            }
        }
        RenderSystem.enableDepthTest();
        MeshData data = builder.build();
        if (data != null)
            BufferUploader.drawWithShader(data);
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }

    private static float @NotNull [] sphereVertex(double cx, double cy, double cz, float r, float theta, float phi)
    {
        float x = (float) (r * Math.sin(theta) * Math.cos(phi)) + (float) cx;
        float y = (float) (r * Math.cos(theta)) + (float) cy;
        float z = (float) (r * Math.sin(theta) * Math.sin(phi)) + (float) cz;
        return new float[]{x, y, z};
    }

    public static void registerHandler()
    {
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: loadLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: unloadLevel);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: playerLoad);
        NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: levelTickPre);
        if (FMLLoader.getDist().isClient())
        {
           NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: transportRenderer);
        }
        else
        {
            NeoForge.EVENT_BUS.addListener(FluidTransportHandler :: saveLevel);
        }
    }

    public static class FluidTransport
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, FluidTransport> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                FluidTransport :: getId,
                Vec3.STREAM_CODEC,
                FluidTransport :: getStartPos,
                Vec3.STREAM_CODEC,
                FluidTransport :: getTransmitterPos,
                Vec3.STREAM_CODEC,
                FluidTransport :: getFinishPos,
                FluidStack.OPTIONAL_STREAM_CODEC,
                FluidTransport :: getStack,
                ByteBufCodecs.<ByteBuf, Vec3>list().
                        apply(Vec3.STREAM_CODEC),
                FluidTransport :: getRoute,
                ByteBufCodecs.INT,
                FluidTransport :: getStep,
                FluidTransport :: new);
        private final UUID id;
        private final Vec3 startPos;
        private final Vec3 transmitterPos;
        private final Vec3 finishPos;
        private final List<Vec3> route;
        private final FluidStack stack;
        private int step;
        private Vec3 position;
        private Vec3 prevPos;

        /**
         * For server use only!!!
         */
        public FluidTransport(Vec3 startPos, Vec3 transmitterPos, Vec3 finishPos, FluidStack stack, List<Vec3> route)
        {
            this.id = UUID.randomUUID();
            this.startPos = Preconditions.checkNotNull(startPos);
            this.transmitterPos = Preconditions.checkNotNull(transmitterPos);
            this.finishPos = Preconditions.checkNotNull(finishPos);
            this.stack = Preconditions.checkNotNull(stack);
            this.route = Preconditions.checkNotNull(route);
            this.step = 0;
            this.position = route.getFirst();
            this.prevPos = route.getFirst();
        }


        /**
         * For client use only!!!
         */
        public FluidTransport(UUID id, Vec3 startPos, Vec3 transmitterPos, Vec3 finishPos, FluidStack stack, List<Vec3> route, int step)
        {
            this.id = id;
            this.startPos = Preconditions.checkNotNull(startPos);
            this.transmitterPos = Preconditions.checkNotNull(transmitterPos);
            this.finishPos = Preconditions.checkNotNull(finishPos);
            this.stack = Preconditions.checkNotNull(stack);
            this.route = Preconditions.checkNotNull(route);
            this.step = step;
            this.position = route.get(step);
            this.prevPos = route.get(step);
        }

        private FluidTransport(@NotNull CompoundTag tag)
        {
            this.id = tag.getUUID("id");
            this.startPos = TagHelper.readVec3(tag, "start");
            this.transmitterPos = TagHelper.readVec3(tag, "transmitter");
            this.finishPos = TagHelper.readVec3(tag, "finish");

            List<Vec3> route = new ArrayList<>();
            ListTag list = tag.getList("route", Tag.TAG_COMPOUND);
            for (int q = 0; q < list.size(); q++)
                route.add(TagHelper.readVec3(list.getCompound(q)));

            this.route = route;
            this.stack = FluidStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, tag.get("fluid")).
                    result().
                    orElse(FluidStack.EMPTY);
            this.step = tag.getInt("step");
            this.position = this.route.get(step);
            this.prevPos = this.route.get(step);
        }


        public CompoundTag saveTransport()
        {
            CompoundTag tag = new CompoundTag();

            tag.putUUID("id", this.id);
            TagHelper.writeVec3(this.startPos, tag, "start");
            TagHelper.writeVec3(this.transmitterPos, tag, "transmitter");
            TagHelper.writeVec3(this.finishPos, tag, "finish");

            ListTag list = new ListTag();
            for(Vec3 vec : this.route)
                list.add(TagHelper.writeVec3(vec));

            tag.put("route", list);
            FluidStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, stack).
                    ifSuccess(savedTag -> tag.put("fluid", savedTag));

            tag.putInt("step", this.step);

            return tag;
        }

        public void tick(@NotNull Level ticker)
        {
            if (ticker.getGameTime() % 5 != 0)
                return;
            if (this.step < this.route.size() - 1)
            {
                this.step++;
                this.position = this.route.get(this.step);
            }

            if (!ticker.isClientSide())
            {
                if (this.step == this.route.size() - 1)
                {
                    FluidStack ret = FluidHelper.getFluidHandler(ticker, BlockPos.containing(this.finishPos)).map(handler ->
                    {
                        FluidStack left = new FluidStack(this.stack.getFluid(), this.stack.getAmount() - handler.fill(this.stack, IFluidHandler.FluidAction.EXECUTE));
                        if (left.isEmpty())
                            return FluidStack.EMPTY;
                        return left;
                    }).orElse(this.stack);
	                FluidTransportHandler.removeTransport(ticker, this);
	                if (!ret.isEmpty())
                        FluidTransportHandler.addTransport(ticker, new FluidTransport(this.finishPos, this.transmitterPos, this.startPos, ret, new ArrayList<>(this.route).reversed()));
                }
            }
        }

        public UUID getId()
        {
            return this.id;
        }

        public Vec3 getStartPos()
        {
            return this.startPos;
        }

        public Vec3 getTransmitterPos()
        {
            return this.transmitterPos;
        }

        public Vec3 getFinishPos()
        {
            return this.finishPos;
        }

        public List<Vec3> getRoute()
        {
            return this.route;
        }

        public FluidStack getStack()
        {
            return this.stack;
        }

        public int getStep()
        {
            return this.step;
        }

        private void lerpPos(float partialTick)
        {
            this.prevPos = Mth.lerp(partialTick, this.prevPos, this.position);
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object)
                return true;
            if (!(object instanceof FluidTransport that))
                return false;
            return getId().equals(that.getId());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getId());
        }
    }

    public static class FluidTransportSavedData extends SavedData
    {
        private static final String FILE_NAME = "fluid_transport";

        private final ServerLevel level;
        private final Set<FluidTransport> savedInfo = new HashSet<>();

        public FluidTransportSavedData(ServerLevel level)
        {
            this.level = level;
            setDirty();
        }

        public Set<FluidTransport> getSavedInfo()
        {
            return this.savedInfo;
        }

        public static @NotNull FluidTransportSavedData getInstance(@NotNull ServerLevel level)
        {
            return level.getDataStorage().computeIfAbsent(new Factory<>(
                    () -> new FluidTransportSavedData(level),
                    (tag, provider) -> FluidTransportSavedData.load(level, tag)),
                    FILE_NAME);
        }

        public static @NotNull FluidTransportSavedData load(@NotNull ServerLevel level, @NotNull CompoundTag tag)
        {
            FluidTransportSavedData data = new FluidTransportSavedData(level);
            ListTag tags = tag.getList("fluid_transport", Tag.TAG_COMPOUND);
            tags.forEach(dynTag ->
            {
                if (dynTag instanceof CompoundTag compoundTag)
                {
                    FluidTransport tr = new FluidTransport(compoundTag);
                    data.savedInfo.add(tr);
                }
            });
            data.setDirty();
            return data;
        }

        @Override
        public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries)
        {
            ListTag listTag = new ListTag();
            FluidTransportHandler.get(this.level).getTransport(this.level).forEach(fluidTransport ->
                    listTag.add(fluidTransport.saveTransport()));
            tag.put("fluid_transport", listTag);
            return tag;
        }
    }
}

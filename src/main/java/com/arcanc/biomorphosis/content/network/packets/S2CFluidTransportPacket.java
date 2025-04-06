/**
 * @author ArcAnc
 * Created at: 07.03.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.network.packets;

import com.arcanc.biomorphosis.content.fluid.FluidTransportHandler;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.helper.RenderHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record S2CFluidTransportPacket(List<FluidTransportHandler.FluidTransport> transport, TransportAction action) implements IPacket
{
    public static final CustomPacketPayload.Type<S2CFluidTransportPacket> TYPE = new CustomPacketPayload.Type<>(Database.rl("message_create_fluid_transport"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CFluidTransportPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.<RegistryFriendlyByteBuf, FluidTransportHandler.FluidTransport>list().
                    apply(FluidTransportHandler.FluidTransport.STREAM_CODEC),
            S2CFluidTransportPacket :: transport,
            TransportAction.STREAM_CODEC,
            S2CFluidTransportPacket :: action,
            S2CFluidTransportPacket :: new);

    @Override
    public void process(@NotNull IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            Level level = RenderHelper.mc().level;
            if (level == null)
                return;
            Set<FluidTransportHandler.FluidTransport> data = FluidTransportHandler.getTransportTable(RenderHelper.mc().level);
            if (this.action() == TransportAction.ADD)
                data.addAll(transport());
            else if (this.action == TransportAction.REMOVE)
                transport().forEach(data :: remove);
            else
            {
                data.clear();
                data.addAll(transport());
            }
        });
    }

    @Override
    public @NotNull Type<S2CFluidTransportPacket> type()
    {
        return TYPE;
    }

    public enum TransportAction
    {
        ADD,
        REMOVE,
        SYNC;

        private static final StreamCodec<ByteBuf, TransportAction> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                Enum :: ordinal,
                integer -> TransportAction.values()[integer]);
    }
}

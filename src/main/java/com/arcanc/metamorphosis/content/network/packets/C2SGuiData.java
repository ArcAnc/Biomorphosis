/**
 * @author ArcAnc
 * Created at: 06.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.network.packets;

import com.arcanc.metamorphosis.content.gui.sync.IScreenMessageReceiver;
import com.arcanc.metamorphosis.util.Database;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record C2SGuiData(int containerId, CompoundTag tag) implements IPacket
{
    public static final Type<C2SGuiData> TYPE = new Type<>(Database.rl("c2s_gui_data"));
    public static final StreamCodec<FriendlyByteBuf, C2SGuiData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            C2SGuiData :: containerId,
            ByteBufCodecs.COMPOUND_TAG,
            C2SGuiData :: tag,
            C2SGuiData :: new);

    @Override
    public void process(@NotNull IPayloadContext context)
    {
        Player player = context.player();
        if (!(player instanceof ServerPlayer serverPlayer))
            return;
        context.enqueueWork(() ->
        {
            serverPlayer.resetLastActionTime();
            if (serverPlayer.hasContainerOpen() &&
                    serverPlayer.containerMenu.containerId == containerId() &&
                    serverPlayer.containerMenu instanceof IScreenMessageReceiver receiver)
                receiver.receiveMessage(serverPlayer, tag());
        });
    }

    @Override
    public @NotNull Type<C2SGuiData> type()
    {
        return TYPE;
    }
}

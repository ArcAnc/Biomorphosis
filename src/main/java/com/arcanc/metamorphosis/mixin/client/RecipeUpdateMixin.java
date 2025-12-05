/**
 * @author ArcAnc
 * Created at: 06.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.mixin.client;

import com.arcanc.metamorphosis.content.event.CustomEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class RecipeUpdateMixin
{

    @Inject(method = "handleUpdateRecipes",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
                    shift = At.Shift.AFTER))
    private void handleRecipeUpdate(ClientboundUpdateRecipesPacket packet, CallbackInfo ci)
    {
        CustomEvents.AddRecipeRenderer renderers = new CustomEvents.AddRecipeRenderer();
        NeoForge.EVENT_BUS.post(renderers);
        renderers.addCustomRenderers();
    }
}

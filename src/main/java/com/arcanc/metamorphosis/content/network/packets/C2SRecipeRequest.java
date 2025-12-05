/**
 * @author ArcAnc
 * Created at: 06.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.network.packets;

import com.arcanc.metamorphosis.util.Database;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record C2SRecipeRequest(ResourceLocation recipeLocation) implements IPacket
{
    public static final Type<C2SRecipeRequest> TYPE = new Type<>(Database.rl("c2s_recipe_request"));
    public static final StreamCodec<FriendlyByteBuf, C2SRecipeRequest> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            C2SRecipeRequest :: recipeLocation,
            C2SRecipeRequest :: new
    );

    @Override
    public void process(@NotNull IPayloadContext context)
    {
        context.enqueueWork(() ->
        {
            ServerPlayer serverPlayer = (ServerPlayer) context.player();
            if (serverPlayer.level() instanceof ServerLevel)
            {
                MinecraftServer server = serverPlayer.getServer();
                if (server != null)
                    server.getRecipeManager().
                            byKey(ResourceKey.create(Registries.RECIPE, recipeLocation())).
                            map(RecipeHolder :: value).
                            map(recipe -> (Recipe<?>)recipe).
                            ifPresent(recipe ->
                                    context.reply(new S2CRecipeResponse(recipeLocation(), recipe)));
            }
        });
    }

    @Override
    public @NotNull Type<C2SRecipeRequest> type()
    {
        return TYPE;
    }
}

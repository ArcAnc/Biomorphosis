/**
 * @author ArcAnc
 * Created at: 06.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.content.network.packets;

import com.arcanc.metamorphosis.content.book_data.page.component.recipes.AbstractRecipeComponent;
import com.arcanc.metamorphosis.util.Database;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record S2CRecipeResponse(ResourceLocation recipeLocation, Recipe<?> recipe) implements IPacket
{
    public static final Type<S2CRecipeResponse> TYPE = new Type<>(Database.rl("s2c_recipe_response"));
    public static final StreamCodec<RegistryFriendlyByteBuf, S2CRecipeResponse> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            S2CRecipeResponse :: recipeLocation,
            Recipe.STREAM_CODEC,
            S2CRecipeResponse :: recipe,
            S2CRecipeResponse :: new
    );

    @Override
    public void process(@NotNull IPayloadContext context)
    {
        context.enqueueWork(() ->
                AbstractRecipeComponent.RecipeCache.storeRecipe(recipeLocation(), recipe()));
    }

    @Override
    public @NotNull Type<S2CRecipeResponse> type()
    {
        return TYPE;
    }
}

/**
 * @author ArcAnc
 * Created at: 23.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.integration.jade;

import com.arcanc.biomorphosis.util.Database;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum BlockInfoProvider implements IBlockComponentProvider,
                                         IServerDataProvider<BlockAccessor>
{
    INSTANCE;

    @Override
    public void appendTooltip(@NotNull ITooltip tooltip,
                              @NotNull BlockAccessor blockAccessor,
                              IPluginConfig iPluginConfig)
    {
        CompoundTag data = blockAccessor.getServerData();
        if (data.contains("fluid_amount"))
        {
            /*IElementHelper elementHelper = IElementHelper.get();
            IElement icon = elementHelper.item(new ItemStack(Registration.BlockReg.FLUID_STORAGE.get()), 0.5f).size(new Vec2(10, 10)).translate(new Vec2(0, -1));
            icon.message(null)
            tooltip.add(icon);
            tooltip.append(Component.translatable(Database.JadeInfo.Translations.FLUID_AMOUNT, data.getString("fluid"), data.getInt("fluid_amount"), data.getInt("max_fluid")));
        */
            tooltip.add(Component.translatable(Database.Integration.JadeInfo.Translations.FLUID_AMOUNT, data.getString("fluid"), data.getInt("fluid_amount"), data.getInt("max_fluid")));
        }
    }

    @Override
    public ResourceLocation getUid()
    {
        return Database.Integration.JadeInfo.IDs.FLUID_RENDERER;
    }

    @Override
    public void appendServerData(@NotNull CompoundTag compoundTag, @NotNull BlockAccessor blockAccessor)
    {
        /*BioFluidStorage block = (BioFluidStorage) blockAccessor.getBlockEntity();
        compoundTag.putInt("fluid_amount", 10);
        compoundTag.putInt("max_fluid", 999);
        compoundTag.putString("fluid", "Water");
        */
    }

    @Override
    public int getDefaultPriority()
    {
        return 20;
    }
}

/**
 * @author ArcAnc
 * Created at: 22.02.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.integration.jade;

import com.arcanc.biomorphosis.content.block.BioFluidStorageBlock;
import com.arcanc.biomorphosis.content.block.block_entity.BioFluidStorage;
import com.arcanc.biomorphosis.util.Database;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(value = Database.MOD_ID)
public class BioPlugin implements IWailaPlugin
{
    @Override
    public void register(@NotNull IWailaCommonRegistration registration)
    {
        //registration.registerBlockDataProvider(BlockInfoProvider.INSTANCE, BioFluidStorage.class);
    }

    @Override
    public void registerClient(@NotNull IWailaClientRegistration registration)
    {
        //registration.registerBlockComponent(BlockInfoProvider.INSTANCE, BioFluidStorageBlock.class);
    }
}

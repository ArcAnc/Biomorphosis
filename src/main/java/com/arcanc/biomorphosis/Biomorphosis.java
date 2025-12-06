/**
 * @author ArcAnc
 * Created at: 27.12.2024
 * Copyright (c) 2024
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis;

import com.arcanc.biomorphosis.content.event.ClientEvents;
import com.arcanc.biomorphosis.content.event.CommonEvents;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

@Mod(Database.MOD_ID)
public class Biomorphosis
{
    public Biomorphosis(@NotNull IEventBus modEventBus, ModContainer modContainer)
    {
        Registration.init(modEventBus);

        setupEvents(modEventBus);
    }

    private void setupEvents(final @NotNull IEventBus modEventBus)
    {
        CommonEvents.registerCommonEvents(modEventBus);
        if (FMLLoader.getDist().isClient())
            ClientEvents.registerClientEvents(modEventBus);
    }
}
/**
 * @author ArcAnc
 * Created at: 06.06.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.gui.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public interface IScreenMessageReceiver
{
    void receiveMessage(ServerPlayer player, CompoundTag tag);
}

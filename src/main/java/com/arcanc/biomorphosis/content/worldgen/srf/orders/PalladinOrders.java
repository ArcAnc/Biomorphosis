/**
 * @author ArcAnc
 * Created at: 27.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.srf.orders;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PalladinOrders
{
	public static final Set<ResourceKey<PalladinOrder>> FULL_ORDERS = new HashSet<>();
	
	public static final ResourceKey<PalladinOrder> BASE_ORDER = create("base");
	
	private static @NotNull ResourceKey<PalladinOrder> create(String name)
	{
		ResourceKey<PalladinOrder> order = ResourceKey.create(Registration.PalladinOrderReg.ORDER_KEY, Database.rl(name));
		FULL_ORDERS.add(order);
		return order;
	}
}

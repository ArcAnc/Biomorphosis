/**
 * @author ArcAnc
 * Created at: 27.12.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.content.worldgen.srf.orders.PalladinOrder;
import com.arcanc.biomorphosis.content.worldgen.srf.orders.PalladinOrders;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import com.google.common.base.Preconditions;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BioPalladinOrdersProvider extends BioRegistryData
{
	private final Map<ResourceLocation, PalladinOrder> ordersMap = new HashMap<>();
	
	public BioPalladinOrdersProvider()
	{
		super();
	}
	
	@Override
	protected void addContent()
	{
		addOrder(PalladinOrders.BASE_ORDER.location(), new PalladinOrder(new PalladinOrder.OrderColor(255, 194, 194)));
	}
	
	private void addOrder(ResourceLocation id, PalladinOrder order)
	{
		this.ordersMap.put(id, order);
	}
	
	@Override
	protected void registerContent(@NotNull RegistrySetBuilder registrySetBuilder)
	{
		registrySetBuilder.add(Registration.PalladinOrderReg.ORDER_KEY, context ->
				this.ordersMap.forEach((location, orderData) ->
						context.register(getOrderKey(location), orderData)));
	}
	
	private @NotNull ResourceKey<PalladinOrder> getOrderKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(Registration.PalladinOrderReg.ORDER_KEY, location);
	}
}

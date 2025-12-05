/**
 * @author ArcAnc
 * Created at: 06.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.metamorphosis.data;


import com.arcanc.metamorphosis.content.mutations.AttributeParams;
import com.arcanc.metamorphosis.content.mutations.GeneDefinition;
import com.arcanc.metamorphosis.content.mutations.GeneRarity;
import com.arcanc.metamorphosis.content.registration.Registration;
import com.arcanc.metamorphosis.data.genome.GeneDefinitionBuilder;
import com.arcanc.metamorphosis.data.regSetBuilder.MetaRegistryData;
import com.arcanc.metamorphosis.util.Database;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MetaGenomeProvider extends MetaRegistryData
{
	private final Map<ResourceLocation, GeneDefinition> geneDataMap = new HashMap<>();
	
	public MetaGenomeProvider()
	{
		super();
	}
	
	@Override
	protected void addContent()
	{
		addDefinition(GeneDefinitionBuilder.
				builder(Database.GUI.GenomeData.BALANCE.id()).
				setImage(Database.GUI.GenomeData.BALANCE.image()).
				addRarityInfo(GeneRarity.COMMON).
						setDestabilizationAmount(-10).
						end().
				end());
		
		addDefinition(GeneDefinitionBuilder.
				builder(Database.GUI.GenomeData.HEALTH.id()).
				setImage(Database.GUI.GenomeData.HEALTH.image()).
				addRarityInfo(GeneRarity.COMMON).
						setDestabilizationAmount(1).
						addIncompatibilities(Database.GUI.GenomeData.PROTECTION.id()).
						addEffect(Registration.GenomeReg.HEALTH.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(2))))).
						end().
				addRarityInfo(GeneRarity.UNCOMMON).
						setDestabilizationAmount(2).
						addIncompatibilities(Database.GUI.GenomeData.PROTECTION.id()).
						addEffect(Registration.GenomeReg.HEALTH.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(4))))).
						end().
				end());
	}
	
	private void addDefinition(GeneDefinition data)
	{
		this.geneDataMap.putIfAbsent(data.id(), data);
	}
	
	@Override
	protected void registerContent(@NotNull RegistrySetBuilder registrySetBuilder)
	{
		registrySetBuilder.add(Registration.GenomeReg.DEFINITION_KEY, context ->
				this.geneDataMap.forEach((location, bookChapterData) ->
						context.register(getDefinitionKey(location), bookChapterData)));
	}
	
	private @NotNull ResourceKey<GeneDefinition> getDefinitionKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(Registration.GenomeReg.DEFINITION_KEY, location);
	}
}

/**
 * @author ArcAnc
 * Created at: 06.10.2025
 * Copyright (c) 2025
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.mutations.AttributeParams;
import com.arcanc.biomorphosis.content.mutations.GeneDefinition;
import com.arcanc.biomorphosis.content.mutations.GeneRarity;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.data.genome.GeneDefinitionBuilder;
import com.arcanc.biomorphosis.data.regSetBuilder.BioRegistryData;
import com.arcanc.biomorphosis.util.Database;
import com.arcanc.biomorphosis.util.SerializableColor;
import com.google.common.base.Preconditions;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BioGenomeProvider extends BioRegistryData
{
	private final Map<ResourceLocation, GeneDefinition> geneDataMap = new HashMap<>();
	
	public BioGenomeProvider()
	{
		super();
	}
	
	@Override
	protected void addContent()
	{
		addDefinition(GeneDefinitionBuilder.
				builder(Database.GUI.Genome.BALANCE.id()).
				setImage(Database.GUI.Genome.BALANCE.image()).
				addRarityInfo(GeneRarity.COMMON).
						setDestabilizationAmount(-10).
						setMainColor(new SerializableColor(190, 190, 185)).
						setSecondaryColor(new SerializableColor(70, 85, 65)).
						addEffect(Registration.GenomeReg.BALANCE.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(-10))))).
						end().
				addRarityInfo(GeneRarity.UNCOMMON).
						setDestabilizationAmount(-15).
						setMainColor(new SerializableColor(215, 205, 185)).
						setSecondaryColor(new SerializableColor(120, 85, 50)).
						addEffect(Registration.GenomeReg.BALANCE.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(-15))))).
						end().
				addRarityInfo(GeneRarity.ULTRA_RARE).
						setDestabilizationAmount(-20).
						setMainColor(new SerializableColor(230, 230, 225)).
						setSecondaryColor(new SerializableColor(70, 90, 140)).
						addEffect(Registration.GenomeReg.BALANCE.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(-20))))).
						end().
				addRarityInfo(GeneRarity.EPIC).
						setDestabilizationAmount(-25).
						setMainColor(new SerializableColor(240, 245, 255)).
						setSecondaryColor(new SerializableColor(73, 90, 140)).
						addEffect(Registration.GenomeReg.BALANCE.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(-25))))).
						end().
				addRarityInfo(GeneRarity.LEGENDARY).
						setDestabilizationAmount(-30).
						setMainColor(new SerializableColor(255, 255, 255)).
						setSecondaryColor(new SerializableColor(30, 30, 30)).
						addEffect(Registration.GenomeReg.BALANCE.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(-30))))).
						end().
				end());
		
		addDefinition(GeneDefinitionBuilder.
				builder(Database.GUI.Genome.HEALTH.id()).
				setImage(Database.GUI.Genome.HEALTH.image()).
				addRarityInfo(GeneRarity.COMMON).
						setDestabilizationAmount(1).
						addIncompatibilities(Database.GUI.Genome.PROTECTION.id()).
						setMainColor(new SerializableColor(85, 60, 60)).
						setSecondaryColor(new SerializableColor(195, 195, 190)).
						addEffect(Registration.GenomeReg.HEALTH.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(2))))).
						end().
				addRarityInfo(GeneRarity.UNCOMMON).
						setDestabilizationAmount(2).
						addIncompatibilities(Database.GUI.Genome.PROTECTION.id()).
						setMainColor(new SerializableColor(110, 55, 45)).
						setSecondaryColor(new SerializableColor(95, 110, 125)).
						addEffect(Registration.GenomeReg.HEALTH.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(4))))).
						end().
				addRarityInfo(GeneRarity.ULTRA_RARE).
						setDestabilizationAmount(3).
						addIncompatibilities(Database.GUI.Genome.PROTECTION.id()).
						setMainColor(new SerializableColor(140, 45, 45)).
						setSecondaryColor(new SerializableColor(120, 130, 90)).
						addEffect(Registration.GenomeReg.HEALTH.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(6))))).
						end().
				addRarityInfo(GeneRarity.EPIC).
						setDestabilizationAmount(5).
						addIncompatibilities(Database.GUI.Genome.PROTECTION.id()).
						setMainColor(new SerializableColor(180, 30, 30)).
						setSecondaryColor(new SerializableColor(220, 210, 200)).
						addEffect(Registration.GenomeReg.HEALTH.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(8))))).
				end().
				addRarityInfo(GeneRarity.LEGENDARY).
						setDestabilizationAmount(8).
						addIncompatibilities(Database.GUI.Genome.PROTECTION.id()).
						setMainColor(new SerializableColor(255, 40, 40)).
						setSecondaryColor(new SerializableColor(40, 200, 200)).
						addEffect(Registration.GenomeReg.HEALTH.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(10))))).
				end().
				end());
		
		addDefinition(GeneDefinitionBuilder.
				builder(Database.GUI.Genome.PROTECTION.id()).
				setImage(Database.GUI.Genome.PROTECTION.image()).
				addRarityInfo(GeneRarity.COMMON).
						setDestabilizationAmount(2).
						setMainColor(new SerializableColor(58, 64, 54)).
						setSecondaryColor(new SerializableColor(201, 207, 194)).
						addIncompatibilities(Database.GUI.Genome.HEALTH.id()).
						addEffect(Registration.GenomeReg.PROTECTION.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(2))))).
						end().
				addRarityInfo(GeneRarity.UNCOMMON).
						setDestabilizationAmount(4).
						addIncompatibilities(Database.GUI.Genome.HEALTH.id()).
						setMainColor(new SerializableColor(85, 107, 47)).
						setSecondaryColor(new SerializableColor(166, 90, 42)).
						addEffect(Registration.GenomeReg.PROTECTION.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(4))))).
						end().
				addRarityInfo(GeneRarity.ULTRA_RARE).
						setDestabilizationAmount(7).
						addIncompatibilities(Database.GUI.Genome.HEALTH.id()).
						setMainColor(new SerializableColor(59, 127, 58)).
						setSecondaryColor(new SerializableColor(79, 143, 139)).
						addEffect(Registration.GenomeReg.PROTECTION.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(6))))).
						end().
				addRarityInfo(GeneRarity.EPIC).
						setDestabilizationAmount(11).
						addIncompatibilities(Database.GUI.Genome.HEALTH.id()).
						setMainColor(new SerializableColor(46, 204,0)).
						setSecondaryColor(new SerializableColor(230, 216, 163)).
						addEffect(Registration.GenomeReg.PROTECTION.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(8))))).
						end().
				addRarityInfo(GeneRarity.LEGENDARY).
						setDestabilizationAmount(15).
						addIncompatibilities(Database.GUI.Genome.HEALTH.id()).
						setMainColor(new SerializableColor(76, 255, 0)).
						setSecondaryColor(new SerializableColor(43, 0, 51)).
						addEffect(Registration.GenomeReg.PROTECTION.get(),
								new AttributeParams(new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createMap(new HashMap<>())).
										set("amount", new Dynamic<>(JsonOps.INSTANCE, JsonOps.INSTANCE.createInt(10))))).
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
				this.geneDataMap.forEach((location, geneDefinition) ->
						context.register(getDefinitionKey(location), geneDefinition)));
	}
	
	private @NotNull ResourceKey<GeneDefinition> getDefinitionKey(ResourceLocation location)
	{
		Preconditions.checkNotNull(location);
		return getResourceKey(Registration.GenomeReg.DEFINITION_KEY, location);
	}
}

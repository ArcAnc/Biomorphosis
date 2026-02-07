/**
 * @author ArcAnc
 * Created at: 07.01.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.data;


import com.arcanc.biomorphosis.content.mutations.GeneInstance;
import com.arcanc.biomorphosis.content.mutations.GeneRarity;
import com.arcanc.biomorphosis.content.mutations.GenomeInstance;
import com.arcanc.biomorphosis.content.mutations.templates.GenomeDataDefinition;
import com.arcanc.biomorphosis.content.mutations.templates.GenomeTemplate;
import com.arcanc.biomorphosis.content.registration.Registration;
import com.arcanc.biomorphosis.util.Database;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BioGenomeTemplatesProvider
{
	private final Map<ResourceLocation, GenomeTemplate> templates = new LinkedHashMap<>();
	
	public BioGenomeTemplatesProvider()
	{
	
	}
	
	public void addTemplates()
	{
		addTemplate(new GenomeTemplate(
				EntityType.ARMADILLO,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE))
								)),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON))
								)),
						new GenomeDataDefinition(70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON))
								))
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.AXOLOTL,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)
		));
		
		addTemplate(new GenomeTemplate(
				EntityType.BAT,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.VAMPIRISM.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						),
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.VAMPIRISM.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.VAMPIRISM.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.VAMPIRISM.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								66,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.VAMPIRISM.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.CAT,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						),
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								66,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)
		));
		
		addTemplate(new GenomeTemplate(EntityType.COD,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						),
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								66,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.COW,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								6,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								23,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.HORSE,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						),
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								66,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.MULE,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								67,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.OCELOT,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								67,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.RABBIT,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								67,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.WOLF,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								67,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.SILVERFISH,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								66,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.FOX,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								66,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.POLAR_BEAR,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						)
						,
						new GenomeDataDefinition(
								6,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								23,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								60,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.DAMAGE.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.FROG,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						),
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								66,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.JUMP_STRENGTH.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.SQUID,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)
		));
		
		addTemplate(new GenomeTemplate(
				EntityType.GLOW_SQUID,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)
		));
		
		addTemplate(new GenomeTemplate(EntityType.PIG,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								6,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								6,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								23,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.TROPICAL_FISH,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)
		));
		
		addTemplate(new GenomeTemplate(
				EntityType.SALMON,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						),
						new GenomeDataDefinition(
								4,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								66,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.DOLPHIN,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						),
						new GenomeDataDefinition(
								6,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								23,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.SWIM_SPEED.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.SHEEP,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.TURTLE,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE))
								)),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON))
								)),
						new GenomeDataDefinition(70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON))
								))
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.PANDA,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.LEGENDARY),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE)
										)
								)
						),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(EntityType.PIGLIN,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON)
										)
								)
						),
						new GenomeDataDefinition(
								6,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)
						),
						new GenomeDataDefinition(
								23,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						),
						new GenomeDataDefinition(
								70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.HEALTH.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON)
										)
								)
						)
				)));
		
		addTemplate(new GenomeTemplate(
				EntityType.HOGLIN,
				WeightedRandomList.create(
						new GenomeDataDefinition(
								1,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.EPIC),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.ULTRA_RARE))
								)),
						new GenomeDataDefinition(
								9,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.ULTRA_RARE),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.UNCOMMON))
								)),
						new GenomeDataDefinition(
								20,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.UNCOMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON))
								)),
						new GenomeDataDefinition(70,
								new GenomeInstance(
										List.of(
												new GeneInstance(
														Database.GUI.Genome.PROTECTION.id(),
														GeneRarity.COMMON),
												new GeneInstance(
														Database.GUI.Genome.BALANCE.id(),
														GeneRarity.COMMON))
								))
				)));

	}
	
	private void addTemplate(@NotNull GenomeTemplate builder)
	{
		ResourceLocation id = EntityType.getKey(builder.entityType());
		if (this.templates.putIfAbsent(id, builder) != null)
			throw new IllegalStateException("Duplicate Template " + id);
	}
	
	public record Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) implements DataProvider
	{
		
		@Override
		public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output)
		{
			return this.registries().thenCompose(provider ->
			{
				final PackOutput.PathProvider pathProvider = this.packOutput.createRegistryElementsPathProvider(Registration.GenomeReg.GENOME_TEMPLATES_KEY);
				List<CompletableFuture<?>> list = new ArrayList<>();
				BioGenomeTemplatesProvider templatesProvider = new BioGenomeTemplatesProvider();
				templatesProvider.addTemplates();
				
				for (GenomeTemplate template : templatesProvider.templates.values())
					list.add(DataProvider.saveStable(output, provider, GenomeTemplate.CODEC, template, pathProvider.json(EntityType.getKey(template.entityType()))));
				
				return CompletableFuture.allOf(list.toArray(CompletableFuture[] :: new));
			});
		}
		
		@Override
		public @NotNull String getName()
		{
			return Database.MOD_ID + ": Genome Templates Provider";
		}
	}
}

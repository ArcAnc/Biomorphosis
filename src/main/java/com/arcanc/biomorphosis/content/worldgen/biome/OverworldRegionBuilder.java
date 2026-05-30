/**
 * @author ArcAnc
 * Created at: 29.05.2026
 * Copyright (c) 2026
 * <p>
 * This code is licensed under "Arc's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.arcanc.biomorphosis.content.worldgen.biome;


import com.arcanc.biomorphosis.util.helper.WorldGenHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

import java.util.function.Consumer;


/**
 * Part of this code copied from Biomes O' Plenty: <a href="https://github.com/Glitchfiend/BiomesOPlenty/blob/1.21.1/common/src/main/java/biomesoplenty/biome/BOPOverworldBiomeBuilder.java">BOPOverworldBiomeBuilder</a>
 * <p>No idea how it's working</p>
 * <p>Modified by ArcAnc</p>
 */

public class OverworldRegionBuilder
{
	private static final float VALLEY_SIZE = 0.05F;
	private static final float LOW_START = 0.26666668F;
	private static final float HIGH_START = 0.4F;
	private static final float HIGH_END = 0.93333334F;
	private static final float PEAK_SIZE = 0.1F;
	private static final float PEAK_START = 0.56666666F;
	private static final float PEAK_END = 0.7666667F;
	private static final float NEAR_INLAND_START = -0.11F;
	private static final float MID_INLAND_START = 0.03F;
	private static final float FAR_INLAND_START = 0.3F;
	private static final float EROSION_INDEX_1_START = -0.78F;
	private static final float EROSION_INDEX_2_START = -0.375F;
	
	private final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);

    /* Terminology:
        Continentalness: Low to generate near coasts, far to generate away from coasts
        Erosion: Low is hilly terrain, high is flat terrain
     */
	
	private final Climate.Parameter[] temperatures = new Climate.Parameter[]{
			Climate.Parameter.span(-1.0F, -0.45F),
			Climate.Parameter.span(-0.45F, -0.15F),
			Climate.Parameter.span(-0.15F, 0.2F),
			Climate.Parameter.span(0.2F, 0.55F),
			Climate.Parameter.span(0.55F, 1.0F)
	};
	
	private final Climate.Parameter[] humidities = new Climate.Parameter[]{
			Climate.Parameter.span(-1.0F, -0.35F),
			Climate.Parameter.span(-0.35F, -0.1F),
			Climate.Parameter.span(-0.1F, 0.1F),
			Climate.Parameter.span(0.1F, 0.3F),
			Climate.Parameter.span(0.3F, 1.0F)
	};
	
	private final Climate.Parameter[] erosions = new Climate.Parameter[]{
			Climate.Parameter.span(-1.0F, -0.78F),
			Climate.Parameter.span(-0.78F, -0.375F),
			Climate.Parameter.span(-0.375F, -0.2225F),
			Climate.Parameter.span(-0.2225F, 0.05F),
			Climate.Parameter.span(0.05F, 0.45F),
			Climate.Parameter.span(0.45F, 0.55F),
			Climate.Parameter.span(0.55F, 1.0F)
	};
	
	private static final Climate.Parameter COMMON_RARENESS_RANGE = Climate.Parameter.span(-1.0F, 0.35F);
	private static final Climate.Parameter RARE_RARENESS_RANGE = Climate.Parameter.span(0.35F, 1.0F);
	
	private final Climate.Parameter FROZEN_RANGE = this.temperatures[0];
	private final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(this.temperatures[1], this.temperatures[4]);
	private final Climate.Parameter mushroomFieldsContinentalness = Climate.Parameter.span(-1.2F, -1.05F);
	private final Climate.Parameter deepOceanContinentalness = Climate.Parameter.span(-1.05F, -0.455F);
	private final Climate.Parameter oceanContinentalness = Climate.Parameter.span(-0.455F, -0.19F);
	private final Climate.Parameter coastContinentalness = Climate.Parameter.span(-0.19F, -0.11F);
	private final Climate.Parameter inlandContinentalness = Climate.Parameter.span(-0.11F, 0.55F);
	private final Climate.Parameter nearInlandContinentalness = Climate.Parameter.span(-0.11F, 0.03F);
	private final Climate.Parameter midInlandContinentalness = Climate.Parameter.span(0.03F, 0.3F);
	private final Climate.Parameter farInlandContinentalness = Climate.Parameter.span(0.3F, 1.0F);
	
	/******************************************************************************************************************************/
	
	// Vanilla Biomes
	private final ResourceKey<Biome>[][] OCEANS = new ResourceKey[][]
	{
			{ Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN},
			{Biomes.FROZEN_OCEAN,      Biomes.COLD_OCEAN,      Biomes.OCEAN,      Biomes.LUKEWARM_OCEAN,      Biomes.WARM_OCEAN}
	};
	
	private final ResourceKey<Biome>[][] MIDDLE_BIOMES = new ResourceKey[][]
	{
			{Biomes.SNOWY_PLAINS,  Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA,  Biomes.TAIGA},
			{Biomes.PLAINS,        Biomes.PLAINS,       Biomes.FOREST,       Biomes.TAIGA,        Biomes.OLD_GROWTH_SPRUCE_TAIGA},
			{Biomes.FLOWER_FOREST, Biomes.PLAINS,       Biomes.FOREST,       Biomes.BIRCH_FOREST, Biomes.DARK_FOREST},
			{Biomes.SAVANNA,       Biomes.SAVANNA,      Biomes.FOREST,       Biomes.JUNGLE,       Biomes.JUNGLE},
			{Biomes.DESERT,        Biomes.DESERT,       Biomes.DESERT,       Biomes.DESERT,       Biomes.DESERT}
	};
	
	private final ResourceKey<Biome>[][] MIDDLE_BIOMES_VARIANT = new ResourceKey[][]
	{
			{Biomes.ICE_SPIKES,       null, Biomes.SNOWY_TAIGA, null,                           null},
			{null,                    null, null,               null,                           Biomes.OLD_GROWTH_PINE_TAIGA},
			{Biomes.SUNFLOWER_PLAINS, null, null,               Biomes.OLD_GROWTH_BIRCH_FOREST, null},
			{null,                    null, Biomes.PLAINS,      Biomes.SPARSE_JUNGLE,           Biomes.BAMBOO_JUNGLE},
			{null,                    null, null,               null,                           null}
	};
	
	private final ResourceKey<Biome>[][] PLATEAU_BIOMES = new ResourceKey[][]
	{
			{Biomes.SNOWY_PLAINS,    Biomes.SNOWY_PLAINS,    Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA,     Biomes.SNOWY_TAIGA},
			{Biomes.MEADOW,          Biomes.MEADOW,          Biomes.FOREST,       Biomes.TAIGA,           Biomes.OLD_GROWTH_SPRUCE_TAIGA},
			{Biomes.MEADOW,          Biomes.MEADOW,          Biomes.MEADOW,       Biomes.MEADOW,          Biomes.DARK_FOREST},
			{Biomes.SAVANNA_PLATEAU, Biomes.SAVANNA_PLATEAU, Biomes.FOREST,       Biomes.FOREST,          Biomes.JUNGLE},
			{Biomes.BADLANDS,        Biomes.BADLANDS,        Biomes.BADLANDS,     Biomes.WOODED_BADLANDS, Biomes.WOODED_BADLANDS}
	};
	
	private final ResourceKey<Biome>[][] PLATEAU_BIOMES_VARIANT = new ResourceKey[][]
	{
			{Biomes.ICE_SPIKES,      null,                   null,          null,                null},
			{Biomes.CHERRY_GROVE,    null,                   Biomes.MEADOW, Biomes.MEADOW,       Biomes.OLD_GROWTH_PINE_TAIGA},
			{Biomes.CHERRY_GROVE,    Biomes.CHERRY_GROVE,    Biomes.FOREST, Biomes.BIRCH_FOREST, null},
			{null,                   null,                   null,          null,                null},
			{Biomes.ERODED_BADLANDS, Biomes.ERODED_BADLANDS, null,          null,                null}
	};
	
	private final ResourceKey<Biome>[][] EXTREME_HILLS_BIOMES = new ResourceKey[][]
	{
			{Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
			{Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
			{Biomes.WINDSWEPT_HILLS,          Biomes.WINDSWEPT_HILLS,          Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
			{null,                            null,                            null,                   null,                    null},
			{null,                            null,                            null,                   null,                    null}
	};
	
	private final ResourceKey<Biome>[][] SWAMP_BIOMES = new ResourceKey[][]
	{
			{Biomes.FROZEN_RIVER,   Biomes.FROZEN_RIVER,   Biomes.FROZEN_RIVER,   Biomes.FROZEN_RIVER,   Biomes.FROZEN_RIVER},
			{Biomes.SWAMP,          Biomes.SWAMP,          Biomes.SWAMP,          Biomes.SWAMP,          Biomes.SWAMP},
			{Biomes.SWAMP,          Biomes.SWAMP,          Biomes.SWAMP,          Biomes.SWAMP,          Biomes.SWAMP},
			{Biomes.MANGROVE_SWAMP, Biomes.MANGROVE_SWAMP, Biomes.MANGROVE_SWAMP, Biomes.MANGROVE_SWAMP, Biomes.MANGROVE_SWAMP},
			{Biomes.MANGROVE_SWAMP, Biomes.MANGROVE_SWAMP, Biomes.MANGROVE_SWAMP, Biomes.MANGROVE_SWAMP, Biomes.MANGROVE_SWAMP}
	};
	
	private final ResourceKey<Biome>[][] BEACH_BIOMES = new ResourceKey[][]
	{
			{Biomes.SNOWY_BEACH, Biomes.SNOWY_BEACH, Biomes.SNOWY_BEACH, Biomes.SNOWY_BEACH, Biomes.SNOWY_BEACH},
			{Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH},
			{Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH},
			{Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH,       Biomes.BEACH},
			{Biomes.DESERT,      Biomes.DESERT,      Biomes.DESERT,      Biomes.DESERT,      Biomes.DESERT}
	};
	
	// Biomorphosis biomes
	
	private final ResourceKey<Biome>[][] MIDDLE_BIOMES_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,           null,               null},
					{BioBiomes.WASTES,          BioBiomes.WASTES,       null,           null,               null},
					{null,                      BioBiomes.WASTES,       null,           null,               null},
					{null,                      null,                   null,           null,               null},
					{null,                      null,                   null,           null,               null}
			};
	
	private final ResourceKey<Biome>[][] MIDDLE_BIOMES_VARIANT_BIO = new ResourceKey[][]
			{
					{null,                    null,                     null,               null,               null},
					{null,                    null,                     null,               null,               null},
					{null,                    null,                     null,               null,               null},
					{null,                    null,                     BioBiomes.WASTES,   null,               null},
					{null,                    null,                     null,               null,               null}
			};
	
	private final ResourceKey<Biome>[][] PLATEAU_BIOMES_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null}
			};
	
	private final ResourceKey<Biome>[][] PLATEAU_BIOMES_VARIANT_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null}
			};
	
	private final ResourceKey<Biome>[][] SLOPE_BIOMES_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null}
			};
	
	private final ResourceKey<Biome>[][] PEAK_BIOMES_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null}
			};
	
	private final ResourceKey<Biome>[][] EXTREME_HILLS_BIOMES_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null},
					{null,                      null,                   null,               null,              null}
			};
	
	private final ResourceKey<Biome>[][] SWAMP_BIOMES_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null}
			};
	
	private final ResourceKey<Biome>[][] RIVER_BIOMES_BIO = new ResourceKey[][]
			{
					{Biomes.FROZEN_RIVER,       Biomes.FROZEN_RIVER,    Biomes.FROZEN_RIVER,Biomes.FROZEN_RIVER,Biomes.FROZEN_RIVER},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null}
			};
	
	private final ResourceKey<Biome>[][] BEACH_BIOMES_BIO = new ResourceKey[][]
			{
					{Biomes.SNOWY_BEACH,        Biomes.SNOWY_BEACH,     Biomes.SNOWY_BEACH, Biomes.SNOWY_BEACH, Biomes.SNOWY_BEACH},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               Biomes.BEACH,       Biomes.BEACH},
					{Biomes.BEACH,              Biomes.BEACH,           Biomes.BEACH,       null,               null},
					{null,                      null,                   null,               null,               null}
			};
	
	private final ResourceKey<Biome>[][] STONY_SHORES_BIOMES_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null}
			};
	
	private final ResourceKey<Biome>[][] ISLAND_BIOMES_BIO = new ResourceKey[][]
			{
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null},
					{null,                      null,                   null,               null,               null}
			};
	
	public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
	{
		this.addOffCoastBiomes(registry, mapper);
		this.addInlandBiomes(registry, mapper);
		this.addUndergroundBiomes(registry, mapper);
	}
	
	private void addOffCoastBiomes(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
	{
		for (int q = 0; q < this.temperatures.length; ++q)
		{
			Climate.Parameter temperature = this.temperatures[q];
			
			for (int w = 0; w < this.humidities.length; ++w)
			{
				Climate.Parameter humidity = this.humidities[w];
				ResourceKey<Biome> islandBiomeBIO = this.pickIslandBiomeBIO(biomeRegistry, q, w);
				
				this.addSurfaceBiome(mapper, temperature, humidity, this.mushroomFieldsContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, islandBiomeBIO);
			}
			
			this.addSurfaceBiome(mapper, temperature, this.FULL_RANGE, this.deepOceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, this.OCEANS[0][q]);
			this.addSurfaceBiome(mapper, temperature, this.FULL_RANGE, this.oceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, this.OCEANS[1][q]);
		}
	}
	
	private void addInlandBiomes(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
	{
        /*
            Weirdness ranges map to specific slices in a repeating triangle wave fashion.
                   PEAKS                           PEAKS
               HIGH     HIGH                   HIGH     HIGH
            MID             MID             MID             MID
                               LOW       LOW
                                  VALLEYS
         */
		
		// First cycle
		this.addMidSlice(biomeRegistry, mapper, Climate.Parameter.span(-1.0F, -0.93333334F));
		this.addHighSlice(biomeRegistry, mapper, Climate.Parameter.span(-0.93333334F, -0.7666667F));
		this.addPeaks(biomeRegistry, mapper, Climate.Parameter.span(-0.7666667F, -0.56666666F));
		this.addHighSlice(biomeRegistry, mapper, Climate.Parameter.span(-0.56666666F, -0.4F));
		this.addMidSlice(biomeRegistry, mapper, Climate.Parameter.span(-0.4F, -0.26666668F));
		this.addLowSlice(biomeRegistry, mapper, Climate.Parameter.span(-0.26666668F, -0.05F));
		this.addValleys(biomeRegistry, mapper, Climate.Parameter.span(-0.05F, 0.05F));
		this.addLowSlice(biomeRegistry, mapper, Climate.Parameter.span(0.05F, 0.26666668F));
		this.addMidSlice(biomeRegistry, mapper, Climate.Parameter.span(0.26666668F, 0.4F));
		
		// Second cycle is truncated
		this.addHighSlice(biomeRegistry, mapper, Climate.Parameter.span(0.4F, 0.56666666F));
		this.addPeaks(biomeRegistry, mapper, Climate.Parameter.span(0.56666666F, 0.7666667F));
		this.addHighSlice(biomeRegistry, mapper, Climate.Parameter.span(0.7666667F, 0.93333334F));
		this.addMidSlice(biomeRegistry, mapper, Climate.Parameter.span(0.93333334F, 1.0F));
	}
	
	private void addPeaks(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
	{
		for (int q = 0; q < this.temperatures.length; ++q)
		{
			Climate.Parameter temperature = this.temperatures[q];
			
			for (int w = 0; w < this.humidities.length; ++w)
			{
				Climate.Parameter humidity = this.humidities[w];
				
				ResourceKey<Biome> middleBiomeBIO                         = this.pickMiddleBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> middleBadlandsOrSlopeBiomeBIO          = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBIO(biomeRegistry, q, w, weirdness);
				
				ResourceKey<Biome> plateauBiomeBIO                        = this.pickPlateauBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> extremeHillsBiome                      = this.pickExtremeHillsBiomeVanilla(q, w, weirdness);
				ResourceKey<Biome> extremeHillsBiomeBIO                   = this.pickExtremeHillsBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> shatteredBiome                         = this.maybePickShatteredBiome(biomeRegistry, q, w, weirdness, extremeHillsBiome);
				ResourceKey<Biome> peakBiomeBIO                           = this.pickPeakBiomeBIO(biomeRegistry,  q, w, weirdness);
				
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[0], weirdness, 0.0F, peakBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[1], weirdness, 0.0F, middleBadlandsOrSlopeBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], weirdness, 0.0F, peakBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], weirdness, 0.0F, plateauBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.midInlandContinentalness, this.erosions[3], weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.farInlandContinentalness, this.erosions[3], weirdness, 0.0F, plateauBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], weirdness, 0.0F, shatteredBiome);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], weirdness, 0.0F, extremeHillsBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, middleBiomeBIO);
			}
		}
		
	}
	
	private void addHighSlice(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
	{
		for (int q = 0; q < this.temperatures.length; ++q)
		{
			Climate.Parameter temperature = this.temperatures[q];
			
			for (int w = 0; w < this.humidities.length; ++w)
			{
				Climate.Parameter humidity = this.humidities[w];
				
				ResourceKey<Biome> middleBiomeVanilla                = this.pickMiddleBiomeVanilla(q, w, weirdness);
				ResourceKey<Biome> middleBiomeBIO                    = this.pickMiddleBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> middleBadlandsOrSlopeBiomeBIO     = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBIO(biomeRegistry, q, w, weirdness);
				
				ResourceKey<Biome> plateauBiomeBIO            = this.pickPlateauBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> extremeHillsBiomeBIO       = this.pickExtremeHillsBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> shatteredBiome             = this.maybePickShatteredBiome(biomeRegistry, q, w, weirdness, middleBiomeVanilla);
				ResourceKey<Biome> slopeBiomeBIO              = this.pickSlopeBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> peakBiomeBIO               = this.pickPeakBiomeBIO(biomeRegistry, q, w, weirdness);
				
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[0], weirdness, 0.0F, slopeBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[0], weirdness, 0.0F, peakBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[1], weirdness, 0.0F, middleBadlandsOrSlopeBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], weirdness, 0.0F, slopeBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], weirdness, 0.0F, plateauBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.midInlandContinentalness, this.erosions[3], weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.farInlandContinentalness, this.erosions[3], weirdness, 0.0F, plateauBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], weirdness, 0.0F, shatteredBiome);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], weirdness, 0.0F, extremeHillsBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, middleBiomeBIO);
			}
		}
	}
	
	private void addMidSlice(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
	{
		for (int q = 0; q < this.temperatures.length; ++q)
		{
			Climate.Parameter temperature = this.temperatures[q];
			
			for (int w = 0; w < this.humidities.length; ++w)
			{
				Climate.Parameter humidity = this.humidities[w];
				
				ResourceKey<Biome> middleBiomeVanilla                  = this.pickMiddleBiomeVanilla(q, w, weirdness);
				ResourceKey<Biome> middleBiomeBIO                      = this.pickMiddleBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> middleBadlandsOrSlopeBiomeBIO       = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBIO(biomeRegistry, q, w, weirdness);
				
				ResourceKey<Biome> extremeHillsBiomeBIO       = this.pickExtremeHillsBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> plateauBiomeBIO            = this.pickPlateauBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> beachBiomeBIO              = this.pickBeachBiomeBIO(biomeRegistry, q, w);
				ResourceKey<Biome> stonyShoresBiomeBIO        = this.pickStonyShoresBiomeBIO(biomeRegistry, q, w);
				ResourceKey<Biome> shatteredBiome             = this.maybePickShatteredBiome(biomeRegistry, q, w, weirdness, middleBiomeVanilla);
				ResourceKey<Biome> shatteredCoastBiome        = this.pickShatteredCoastBiome(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> slopeBiomeBIO              = this.pickSlopeBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> swampBiomeBIO              = this.pickSwampBiomeBIO(biomeRegistry, q, w, weirdness);
				
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[2]), weirdness, 0.0F, stonyShoresBiomeBIO);
				
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[0], weirdness, 0.0F, slopeBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.midInlandContinentalness), this.erosions[1], weirdness, 0.0F, middleBadlandsOrSlopeBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.farInlandContinentalness, this.erosions[1], weirdness, 0.0F, q == 0 ? slopeBiomeBIO : plateauBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[2], weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.midInlandContinentalness, this.erosions[2], weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.farInlandContinentalness, this.erosions[2], weirdness, 0.0F, plateauBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[3], weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[3], weirdness, 0.0F, middleBiomeBIO);
				
				if (weirdness.max() < 0L)
				{
					this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[4], weirdness, 0.0F, beachBiomeBIO);
					this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeBIO);
				}
				else
				{
					this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeBIO);
				}
				
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[5], weirdness, 0.0F, shatteredCoastBiome);
				this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[5], weirdness, 0.0F, shatteredBiome);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], weirdness, 0.0F, extremeHillsBiomeBIO);
				if (weirdness.max() < 0L)
				{
					this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, beachBiomeBIO);
				}
				else
				{
					this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, middleBiomeBIO);
				}
				
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, swampBiomeBIO);
			}
		}
	}
	
	private void addLowSlice(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
	{
		for (int q = 0; q < this.temperatures.length; ++q)
		{
			Climate.Parameter temperature = this.temperatures[q];
			
			for (int w = 0; w < this.humidities.length; ++w)
			{
				Climate.Parameter humidity = this.humidities[w];
				
				ResourceKey<Biome> middleBiomeVanilla                  = this.pickMiddleBiomeVanilla(q, w, weirdness);
				ResourceKey<Biome> middleBiomeBIO                      = this.pickMiddleBiomeBIO(biomeRegistry, q, w, weirdness);
				ResourceKey<Biome> middleBadlandsOrSlopeBiomeBIO       = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBIO(biomeRegistry, q, w, weirdness);
				
				ResourceKey<Biome> beachBiome                   = this.pickBeachBiomeBIO(biomeRegistry, q, w);
				ResourceKey<Biome> stonyShoresBiomeBIO          = this.pickStonyShoresBiomeBIO(biomeRegistry, q, w);
				ResourceKey<Biome> shatteredBiome               = this.maybePickShatteredBiome(biomeRegistry, q, w, weirdness, middleBiomeVanilla);
				ResourceKey<Biome> shatteredCoastBiome          = this.pickShatteredCoastBiome(biomeRegistry, q, w, weirdness);
				
				ResourceKey<Biome> swampBiomeBIO                = this.pickSwampBiomeBIO(biomeRegistry, q, w, weirdness);
				
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[2]), weirdness, 0.0F, stonyShoresBiomeBIO);
				
				// Lowest to low erosion
				this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, middleBadlandsOrSlopeBiomeBIO);
				
				// Reduced to moderate erosion
				this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[2], this.erosions[3]), weirdness, 0.0F, middleBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[3]), weirdness, 0.0F, middleBiomeBIO);
				
				// Moderate to increased erosion
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, Climate.Parameter.span(this.erosions[3], this.erosions[4]), weirdness, 0.0F, beachBiome);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], weirdness, 0.0F, middleBiomeBIO);
				
				// High erosion
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[5], weirdness, 0.0F, shatteredCoastBiome);
				this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, this.erosions[5], weirdness, 0.0F, shatteredBiome);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], weirdness, 0.0F, middleBiomeBIO);
				
				// Highest erosion
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, beachBiome);
				
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, swampBiomeBIO);
			}
		}
	}
	
	private void addValleys(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter weirdness)
	{
		// BIO River biomes
		for (int q = 0; q < this.temperatures.length; ++q)
		{
			Climate.Parameter temperature = this.temperatures[q];
			
			for (int w = 0; w < this.humidities.length; ++w)
			{
				Climate.Parameter humidity = this.humidities[w];
				ResourceKey<Biome> riverBiomeBIO       = this.pickRiverBiomeBIO(biomeRegistry, q, w);
				ResourceKey<Biome> stonyShoresBiomeBIO = this.pickStonyShoresBiomeBIO(biomeRegistry, q, w);
				
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, weirdness.max() < 0L ? stonyShoresBiomeBIO : riverBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, riverBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[2], this.erosions[5]), weirdness, 0.0F, riverBiomeBIO);
				this.addSurfaceBiome(mapper, temperature, humidity, this.coastContinentalness, this.erosions[6], weirdness, 0.0F, riverBiomeBIO);
			}
		}
		
		// BIO Swamp biomes
		for (int i = 0; i < this.temperatures.length; ++i)
		{
			Climate.Parameter temperature = this.temperatures[i];
			
			for (int j = 0; j < this.humidities.length; ++j)
			{
				Climate.Parameter humidity = this.humidities[j];
				ResourceKey<Biome> middleBiomeBIO               = this.pickMiddleBiomeBIO(biomeRegistry, i, j, weirdness);
				ResourceKey<Biome> swampBiomeBIO                = this.pickSwampBiomeBIO(biomeRegistry, i, j, weirdness);
				
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), Climate.Parameter.span(this.erosions[0], this.erosions[1]), weirdness, 0.0F, middleBiomeBIO);
				
				this.addSurfaceBiome(mapper, temperature, humidity, Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness), this.erosions[6], weirdness, 0.0F, swampBiomeBIO);
			}
		}
	}
	
	private void addUndergroundBiomes(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper)
	{
		this.addUndergroundBiome(biomeRegistry, mapper, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(0.8F, 1.0F), this.FULL_RANGE, this.FULL_RANGE, 0.0F, null, Biomes.DRIPSTONE_CAVES);
		this.addUndergroundBiome(biomeRegistry, mapper, this.FULL_RANGE, Climate.Parameter.span(0.7F, 1.0F), this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, 0.0F, null, Biomes.LUSH_CAVES);
		this.addBottomBiome(mapper, this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(this.erosions[0], this.erosions[1]), this.FULL_RANGE, 0.0F, Biomes.DEEP_DARK);
	}
	
	private ResourceKey<Biome> pickMiddleBiomeVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		if (weirdness.max() < 0L)
			return this.MIDDLE_BIOMES[temperatureIndex][humidityIndex];
		else
		{
			ResourceKey<Biome> variantBiome = this.MIDDLE_BIOMES_VARIANT[temperatureIndex][humidityIndex];
			return variantBiome == null ? this.MIDDLE_BIOMES[temperatureIndex][humidityIndex] : variantBiome;
		}
	}
	
	
	private ResourceKey<Biome> pickMiddleBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		ResourceKey<Biome> middleBiome = WorldGenHelper.biomeOrFallback(biomeRegistry, this.MIDDLE_BIOMES_BIO[temperatureIndex][humidityIndex], this.MIDDLE_BIOMES[temperatureIndex][humidityIndex]);
		
		if (weirdness.max() < 0)
			return middleBiome;
		else
		{
			return WorldGenHelper.biomeOrFallback(biomeRegistry, this.MIDDLE_BIOMES_VARIANT_BIO[temperatureIndex][humidityIndex], middleBiome);
		}
	}
	
	private ResourceKey<Biome> pickMiddleBiomeOrBadlandsIfHotOrSlopeIfColdBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		return temperatureIndex == 0 ? this.pickSlopeBiomeBIO(biomeRegistry, temperatureIndex, humidityIndex, weirdness) : this.pickMiddleBiomeBIO(biomeRegistry, temperatureIndex, humidityIndex, weirdness);
	}
	
	private ResourceKey<Biome> maybePickShatteredBiome(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness, ResourceKey<Biome> extremeHillsBiome)
	{
		return temperatureIndex > 1 && humidityIndex < 4 && weirdness.max() >= 0L ? WorldGenHelper.biomeOrFallback(biomeRegistry, null, Biomes.WINDSWEPT_SAVANNA) : extremeHillsBiome;
	}
	
	private ResourceKey<Biome> pickShatteredCoastBiome(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		ResourceKey<Biome> resourcekey = weirdness.max() >= 0L ? this.pickMiddleBiomeVanilla(temperatureIndex, humidityIndex, weirdness) : this.pickBeachBiomeBIO(biomeRegistry, temperatureIndex, humidityIndex);
		return this.maybePickShatteredBiome(biomeRegistry, temperatureIndex, humidityIndex, weirdness, resourcekey);
	}
	
	private ResourceKey<Biome> pickSwampBiomeVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		ResourceKey<Biome> resourcekey = this.SWAMP_BIOMES[temperatureIndex][humidityIndex];
		return resourcekey == null ? this.pickMiddleBiomeVanilla(temperatureIndex, humidityIndex, weirdness) : resourcekey;
	}
	
	private ResourceKey<Biome> pickSwampBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		return WorldGenHelper.biomeOrFallback(biomeRegistry, this.SWAMP_BIOMES_BIO[temperatureIndex][humidityIndex], this.pickSwampBiomeVanilla(temperatureIndex, humidityIndex, weirdness));
	}
	
	private ResourceKey<Biome> pickRiverBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex)
	{
		return WorldGenHelper.biomeOrFallback(biomeRegistry, this.RIVER_BIOMES_BIO[temperatureIndex][humidityIndex], temperatureIndex == 0 ? Biomes.FROZEN_RIVER : Biomes.RIVER);
	}
	
	private ResourceKey<Biome> pickBeachBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex)
	{
		return WorldGenHelper.biomeOrFallback(biomeRegistry, this.BEACH_BIOMES_BIO[temperatureIndex][humidityIndex], this.BEACH_BIOMES[temperatureIndex][humidityIndex]);
	}
	
	private ResourceKey<Biome> pickStonyShoresBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex)
	{
		return WorldGenHelper.biomeOrFallback(biomeRegistry, this.STONY_SHORES_BIOMES_BIO[temperatureIndex][humidityIndex], Biomes.STONY_SHORE);
	}
	
	private ResourceKey<Biome> pickIslandBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex)
	{
		return WorldGenHelper.biomeOrFallback(biomeRegistry, this.ISLAND_BIOMES_BIO[temperatureIndex][humidityIndex], Biomes.MUSHROOM_FIELDS);
	}
	
	private ResourceKey<Biome> pickBadlandsBiome(int humidityIndex, Climate.Parameter weirdness)
	{
		if (humidityIndex < 2)
			return weirdness.max() < 0L ? Biomes.ERODED_BADLANDS : Biomes.BADLANDS;
		else
			return humidityIndex < 3 ? Biomes.BADLANDS : Biomes.WOODED_BADLANDS;
	}
	
	private ResourceKey<Biome> pickPlateauBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		if (weirdness.max() < 0L)
			return WorldGenHelper.biomeOrFallback(biomeRegistry, this.PLATEAU_BIOMES_BIO[temperatureIndex][humidityIndex], this.PLATEAU_BIOMES[temperatureIndex][humidityIndex]);
		else
			return WorldGenHelper.biomeOrFallback(biomeRegistry, this.PLATEAU_BIOMES_VARIANT_BIO[temperatureIndex][humidityIndex], this.PLATEAU_BIOMES_BIO[temperatureIndex][humidityIndex], this.PLATEAU_BIOMES_VARIANT[temperatureIndex][humidityIndex], this.PLATEAU_BIOMES[temperatureIndex][humidityIndex]);
	}
	
	private ResourceKey<Biome> pickPeakBiome(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		if (temperatureIndex <= 2)
			return weirdness.max() < 0L ? Biomes.JAGGED_PEAKS : Biomes.FROZEN_PEAKS;
		else
			return temperatureIndex == 3 ? Biomes.STONY_PEAKS : this.pickBadlandsBiome(humidityIndex, weirdness);
	}
	
	private ResourceKey<Biome> pickPeakBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		return WorldGenHelper.biomeOrFallback(biomeRegistry, this.PEAK_BIOMES_BIO[temperatureIndex][humidityIndex], this.pickPeakBiome(temperatureIndex, humidityIndex, weirdness));
	}
	
	private ResourceKey<Biome> pickSlopeBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		return WorldGenHelper.biomeOrFallback(biomeRegistry, this.SLOPE_BIOMES_BIO[temperatureIndex][humidityIndex], this.pickPlateauBiomeBIO(biomeRegistry, temperatureIndex, humidityIndex, weirdness));
	}
	
	private ResourceKey<Biome> pickExtremeHillsBiomeVanilla(int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		ResourceKey<Biome> resourcekey = this.EXTREME_HILLS_BIOMES[temperatureIndex][humidityIndex];
		return resourcekey == null ? this.pickMiddleBiomeVanilla(temperatureIndex, humidityIndex, weirdness) : resourcekey;
	}
	
	private ResourceKey<Biome> pickExtremeHillsBiomeBIO(Registry<Biome> biomeRegistry, int temperatureIndex, int humidityIndex, Climate.Parameter weirdness)
	{
		return WorldGenHelper.biomeOrFallback(biomeRegistry, this.EXTREME_HILLS_BIOMES_BIO[temperatureIndex][humidityIndex], this.pickExtremeHillsBiomeVanilla(temperatureIndex, humidityIndex, weirdness));
	}
	
	private void addSurfaceBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset, ResourceKey<Biome> biome)
	{
		mapper.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(0.0F), weirdness, offset), biome));
		mapper.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(1.0F), weirdness, offset), biome));
	}
	
	private void addUndergroundBiome(Registry<Biome> biomeRegistry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset, ResourceKey<Biome> biome, ResourceKey<Biome> fallbackBiome)
	{
		mapper.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.span(0.2F, 0.9F), weirdness, offset), WorldGenHelper.biomeOrFallback(biomeRegistry, biome, fallbackBiome)));
	}
	
	private void addBottomBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset, ResourceKey<Biome> biome)
	{
		mapper.accept(Pair.of(Climate.parameters(temperature, humidity, continentalness, erosion, Climate.Parameter.point(1.1F), weirdness, offset), biome));
	}
}

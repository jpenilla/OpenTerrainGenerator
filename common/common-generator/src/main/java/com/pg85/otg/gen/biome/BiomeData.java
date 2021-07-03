package com.pg85.otg.gen.biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BiomeData
{
	public final int id;
	public final String name;
	public final int rarity;
	public final int biomeSize;
	public float biomeTemperature;
	private final List<String> configIsleInBiomes;
	private final List<String> configBorderInBiomes;
	private final List<String> configOnlyBorderNearBiomes;	
	private final List<String> configNotBorderNearBiomes;
	public final List<Integer> isleInBiomes = new ArrayList<Integer>();
	public final List<Integer> borderInBiomes = new ArrayList<Integer>();
	public final List<Integer> onlyBorderNearBiomes = new ArrayList<Integer>();
	public final List<Integer> notBorderNearBiomes = new ArrayList<Integer>();

	public BiomeData(int id, String name, int rarity, int biomeSize, float biomeTemperature, List<String> configIsleInBiomes, List<String> configBorderInBiomes, List<String> configOnlyBorderNearBiomes, List<String> configNotBorderNearBiomes)
	{
		this.id = id;
		this.name = name;
		this.rarity = rarity;
		this.biomeSize = biomeSize;
		this.biomeTemperature = biomeTemperature;
		this.configIsleInBiomes = configIsleInBiomes;
		this.configBorderInBiomes = configBorderInBiomes;
		this.configOnlyBorderNearBiomes = configOnlyBorderNearBiomes;
		this.configNotBorderNearBiomes = configNotBorderNearBiomes;
	}
	
	public void init(Map<String, Integer> worldBiomes)
	{
		for(String isleInBiomeName : this.configIsleInBiomes)
		{
			Integer isleBiome = worldBiomes.get(isleInBiomeName);
			if(isleBiome != null)
			{
				this.isleInBiomes.add(isleBiome.intValue());
			}
		}
		for(String borderInBiomeName : this.configBorderInBiomes)
		{
			Integer borderBiome = worldBiomes.get(borderInBiomeName);
			if(borderBiome != null)
			{
				this.borderInBiomes.add(borderBiome.intValue());
			}
		}
		for(String onlyBorderNearBiomeName : this.configOnlyBorderNearBiomes)
		{
			Integer onlyBorderNearBiome = worldBiomes.get(onlyBorderNearBiomeName);
			if(onlyBorderNearBiome != null)
			{
				this.onlyBorderNearBiomes.add(onlyBorderNearBiome.intValue());
			}
		}		
		for(String notBorderNearBiomeName : this.configNotBorderNearBiomes)
		{
			Integer notBorderNearBiome = worldBiomes.get(notBorderNearBiomeName);
			if(notBorderNearBiome != null)
			{
				this.notBorderNearBiomes.add(notBorderNearBiome.intValue());
			}
		}
	}

	public BiomeData clone()
	{
		BiomeData clone = new BiomeData(this.id, this.name, this.rarity, this.biomeSize, this.biomeTemperature, new ArrayList<>(this.configIsleInBiomes), new ArrayList<>(this.configBorderInBiomes), new ArrayList<>(this.configOnlyBorderNearBiomes), new ArrayList<>(this.configNotBorderNearBiomes));		
		for(Integer integer : this.isleInBiomes)
		{
			clone.isleInBiomes.add(integer.intValue());
		}
		for(Integer integer : this.borderInBiomes)
		{
			clone.borderInBiomes.add(integer.intValue());
		}
		for(Integer integer : this.onlyBorderNearBiomes)
		{
			clone.onlyBorderNearBiomes.add(integer.intValue());
		}		
		for(Integer integer : this.notBorderNearBiomes)
		{
			clone.notBorderNearBiomes.add(integer.intValue());
		}
		return clone;
	}
}
package com.pg85.otg;

import com.pg85.otg.configuration.BiomeConfig;
import com.pg85.otg.configuration.BiomeLoadInstruction;
import com.pg85.otg.configuration.ConfigProvider;
import com.pg85.otg.configuration.BiomeConfigFinder.BiomeConfigStub;
import com.pg85.otg.customobjects.CustomObjectStructureCache;
import com.pg85.otg.customobjects.bo3.EntityFunction;
import com.pg85.otg.exception.BiomeNotFoundException;
import com.pg85.otg.customobjects.bo3.BlockFunction;
import com.pg85.otg.generator.ObjectSpawner;
import com.pg85.otg.generator.SpawnableObject;
import com.pg85.otg.generator.biome.BiomeGenerator;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.NamedBinaryTag;
import com.pg85.otg.util.minecraftTypes.TreeType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public interface LocalWorld
{
	// OTG+

	public void setAllowSpawningOutsideBounds(boolean isSpawningBO3AtSpawn);

    public int getHighestBlockYAt(int x, int z, boolean findSolid, boolean findLiquid, boolean ignoreLiquid, boolean ignoreSnow);

	public ObjectSpawner getObjectSpawner();

	boolean IsInsidePregeneratedRegion(ChunkCoordinate chunk, boolean includeBorder);

	boolean IsInsideWorldBorder(ChunkCoordinate chunk, boolean spawningResources);

	public void setBlock(int x, int y, int z, LocalMaterialData material, NamedBinaryTag metaDataTag, boolean isOTPLus);

	public WorldSession GetWorldSession();

	public void DeleteWorldSessionData();

	public String getWorldSettingsName();

	public File getWorldSaveDir();

	public int getDimensionId();

	//


    // Biome init
    /**
     * Creates a LocalBiome instance for the given biome.
     * @param biomeConfig The settings for the biome, which are saved in
     * the LocalBiome instance.
     * @param requestedBiomeIds The ids of the biome, used to register the
     * LocalBiome instance. The implementation is allowed to use another id if
     * necessary.
     * @return The LocalBiome instance.
     */
    public LocalBiome createBiomeFor(BiomeConfig biomeConfig, BiomeIds biomeIds, ConfigProvider configProvider);

    /**
     * Gets how many different biome ids are in the world. Biome ids will start
     * at zero, so a returned value of 1024 means that the biome ids range from
     * 0 to 1023, inclusive.
     *
     * @return How many different biome ids are in the world.
     */
    public int getMaxBiomesCount();

    /**
     * Gets how many different biome ids Minecraft can actually save to the map
     * files. Biome ids will start at zero, so a returned value of 256 means
     * that the biome ids range from 0 to 255, inclusive. Biomes outside of this
     * range, but inside the range of {@link #getMaxBiomesCount()} must have a
     * ReplaceToBiomeName setting bringing their saved id back into the normal
     * range.
     *
     * @return How many different biome ids are in the save files.
     */
    public int getMaxSavedBiomesCount();

    public int getFreeBiomeId();

    public ArrayList<LocalBiome> getAllBiomes();

    public LocalBiome getBiomeById(int id) throws BiomeNotFoundException;

    public LocalBiome getBiomeByIdOrNull(int id);

    public LocalBiome getBiomeByNameOrNull(String name);

    public Collection<? extends BiomeLoadInstruction> getDefaultBiomes();

    // Biome manager
    /**
     * Gets the biome generator.
     * @return The biome generator.
     */
    public BiomeGenerator getBiomeGenerator();

    /**
     * Calculates the biome at the given coordinates. This is usually taken
     * from the biome generator, but this can be changed using the
     * configuration files. In that case it is read from the chunk data.
     *
     * @param x The block x.
     * @param z The block z.
     * @return The biome at the given coordinates.
     * @throws BiomeNotFoundException If the biome id is invalid.
     * @see #getCalculatedBiome(int, int) to always use the biome generator.
     * @see #getSavedBiome(int, int) to always use the chunk data.
     */
    public LocalBiome getBiome(int x, int z) throws BiomeNotFoundException;

    /**
     * Gets the (stored) biome at the given coordinates.
     *
     * @param x The block x.
     * @param z The block z.
     * @return The biome at the given coordinates.
     * @throws BiomeNotFoundException If the saved biome id is invalid.
     */
    public LocalBiome getSavedBiome(int x, int z) throws BiomeNotFoundException;

    /**
     * Gets the biome as generated by the biome generator.
     * @param x The block x.
     * @param z The block z.
     * @return The biome.
     */
    public LocalBiome getCalculatedBiome(int x, int z);

    // Default generators
    public void prepareDefaultStructures(int chunkX, int chunkZ, boolean dry);

    public boolean placeDungeon(Random rand, int x, int y, int z);

    public boolean placeFossil(Random rand, ChunkCoordinate chunkCoord);

    public boolean placeTree(TreeType type, Random rand, int x, int y, int z);

    public boolean placeDefaultStructures(Random rand, ChunkCoordinate chunkCoord);

    /**
     * Gets a structure part in Mojang's structure format.
     * @param name Full name of the structure.
     * @return The structure, or null if it does not exist.
     */
    SpawnableObject getMojangStructurePart(String name);

    /**
     * Executes ReplacedBlocks.
     *
     * <p>
     * During terrain population, four chunks are guaranteed to be loaded:
     * (chunkX, chunkZ), (chunkX + 1, chunkZ), (chunkX, chunkZ + 1) and
     * (chunkX + 1, chunkZ + 1). All populators use an 8-block offset from the
     * top left chunk, and populate an area of 16x16 blocks from there. This
     * allows them to extend 8 blocks from their population area without
     * hitting potentially unloaded chunks.
     *
     * <p>
     * Populators may place blocks in already populated chunks, which would
     * cause those blocks to be never replaced. ReplacedBlocks uses the same
     * 8-block offset to minimize this risk.
     * @see ChunkCoordinate#getPopulatingChunk(int, int) Explanation about the
     * population offset.
     * @param chunkCoord The top left chunk for ReplacedBlocks.
     */
    public void replaceBlocks(ChunkCoordinate chunkCoord);

    /**
     * Since Minecraft Beta 1.8, friendly mobs are mainly spawned during the
     * terrain generation. Calling this method will place the mobs.
     * @param biome      Biome to place the mobs of.
     * @param random     Random number generator.
     * @param chunkCoord The chunk to spawn the mobs in.
     */
    public void placePopulationMobs(LocalBiome biome, Random random, ChunkCoordinate chunkCoord);

    // Population start and end
    /**
     * Marks the given chunks as being populated. No new chunks may be created. Implementations may cache the chunk.
     * @param chunkCoord The chunk being populated.
     * @throws IllegalStateException If another chunks is being populated. Call {@link #endPopulation()} first.
     * @see #endPopulation()
     */
    public void startPopulation(ChunkCoordinate chunkCoord);

    /**
     * Stops the population step. New chunks may be created again. Implementations may cache the chunk.
     * @throws IllegalStateException If no chunk was being populated.
     * @see #startPopulation(ChunkCoordinate)
     */
    public void endPopulation();

    // Blocks
    public LocalMaterialData getMaterial(int x, int y, int z, boolean allowOutsidePopulatingArea);

    public boolean isNullOrAir(int x, int y, int z, boolean allowOutsidePopulatingArea);

    public NamedBinaryTag getMetadata(int x, int y, int z);

    public int getLiquidHeight(int x, int z);

    /**
     * @return The y location of the block above the highest solid block.
     */
    public int getSolidHeight(int x, int z);

    /**
     * @return The y location of the block above the highest block.
     */
    public int getHighestBlockYAt(int x, int z);

    public int getLightLevel(int x, int y, int z);

    public boolean isLoaded(int x, int y, int z);

    // Other information

    /**
     * Gets the configuration objects of this world.
     * @return The configuration objects.
     */
    public ConfigProvider getConfigs();

    public CustomObjectStructureCache getStructureCache();

    public String getName();

    public long getSeed();

    /**
     * Gets the height the base terrain of the world is capped at. Resources
     * ignore this limit.
     *
     * @return The height the base terrain of the world is capped at.
     */
    public int getHeightCap();

    /**
     * Returns the vertical scale of the world. 128 blocks is the normal
     * scale, 256 doubles the scale, 64 halves the scale, etc. Only powers of
     * two will be returned.
     *
     * @return The vertical scale of the world.
     */
    public int getHeightScale();

    public void mergeVanillaBiomeMobSpawnSettings(BiomeConfigStub biomeConfigStub);

	void SpawnEntity(EntityFunction entityData);

	public ChunkCoordinate getSpawnChunk();

	public BlockFunction[] getBlockColumn(int x, int z);
}

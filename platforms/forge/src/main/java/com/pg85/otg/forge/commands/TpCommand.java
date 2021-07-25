package com.pg85.otg.forge.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.forge.commands.arguments.BiomeNameArgument;
import com.pg85.otg.forge.gen.OTGNoiseChunkGenerator;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;

public class TpCommand extends BaseCommand
{
	private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType(
			object -> new TranslationTextComponent("commands.locatebiome.notFound", object));
	
	public TpCommand() {
		this.name = "tp";
		this.helpMessage = "Teleports you to a specific OTG biome.";
		this.usage = "/otg tp <biome> [range]";
		this.detailedHelp = new String[] { 
				"<biome>: The name of the biome to teleport to.",
				"[range]: The radius in blocks to search for the target biome, defaults to 10000.",
				"Note: large numbers will make the command take a long time."
			};
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder)
	{
		builder.then(Commands.literal("tp")
			.executes(context -> showHelp(context.getSource()))
				.then(Commands.argument("biome", new BiomeNameArgument())
					.executes(context -> locateBiome(context.getSource(), StringArgumentType.getString(context, "biome"), 10000))
						.then(Commands.argument("range", IntegerArgumentType.integer(0))
							.executes(context -> locateBiome(context.getSource(), StringArgumentType.getString(context, "biome"), IntegerArgumentType.getInteger(context, "range")))))
		);
	}

	@SuppressWarnings("resource")
	private int locateBiome(CommandSource source, String biome, int range) throws CommandSyntaxException
	{
        biome = biome.toLowerCase();
        
		ServerWorld world = source.getLevel();
		if (!(world.getChunkSource().generator instanceof OTGNoiseChunkGenerator))
		{
			source.sendSuccess(new StringTextComponent("OTG is not enabled in this world"), false);
			return 0;
		}
		
        String preset = ((OTGNoiseChunkGenerator) world.getChunkSource().generator).getPreset().getFolderName().toLowerCase();

        if (!biome.startsWith(preset)) {
            biome = preset + "." + biome;
        }

		ResourceLocation key = new ResourceLocation(Constants.MOD_ID_SHORT, biome);

		BlockPos pos = world.findNearestBiome(world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(key),
				new BlockPos(source.getPosition()), range, 8);

		if (pos == null)
		{
			throw ERROR_BIOME_NOT_FOUND.create(biome);
		} else
		{
			int y = world.getChunkSource().generator.getBaseHeight(pos.getX(), pos.getZ(), Type.MOTION_BLOCKING_NO_LEAVES);
			source.getPlayerOrException().teleportTo(pos.getX(), y, pos.getZ()); 
			source.sendSuccess(new StringTextComponent("Teleporting you to the nearest " + biome + "."), false);
			return 0;
		}
	}

	private int showHelp(CommandSource source)
	{
		source.sendSuccess(new StringTextComponent(getUsage()), false);
		return 0;
	}
}

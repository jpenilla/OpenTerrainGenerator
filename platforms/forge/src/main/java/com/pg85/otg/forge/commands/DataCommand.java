package com.pg85.otg.forge.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.pg85.otg.OTG;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class DataCommand extends BaseCommand
{
	
	public DataCommand() {
		this.name = "data";
		this.helpMessage = "Dumps various types of game data to files for preset development.";
		this.usage = "/otg data <type>";
		this.detailedHelp = new String[] { 
				"<type>: The type of data to dump.",
				" - biome: All registered biomes.",
				" - block: All registered blocks.",
				" - entity: All registered entities.",
				" - sound: All registered sounds.",
				" - particle: All registered particles.",
				" - configured_feature: All registered configured features (Used to decorate biomes during worldgen)."
			};
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder)
	{
		builder.then(Commands.literal("data")
			.executes(context -> execute(context.getSource(), ""))
				.then(Commands.argument("type", new DataTypeArgument())
					.executes((context -> execute(context.getSource(), context.getArgument("type", String.class)))
				)
			)
		);
	}

	public int execute(CommandSource source, String type)
	{
		// /otg data music
		// /otg data sound

		// worldgen registries aren't wrapped by forge so we need to use another object here
		IForgeRegistry<?> registry = null;
		Registry<?> worldGenRegistry = null;

		switch (type.toLowerCase())
		{
			case "biome":
				registry = ForgeRegistries.BIOMES;
				break;
			case "block":
				registry = ForgeRegistries.BLOCKS;
				break;
			case "entity":
				registry = ForgeRegistries.ENTITIES;
				break;
			case "sound":
				registry = ForgeRegistries.SOUND_EVENTS;
				break;
			case "particle":
				registry = ForgeRegistries.PARTICLE_TYPES;
				break;
			case "configured_feature":
				worldGenRegistry = source.getServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
				break;
			default:
				source.sendSuccess(new StringTextComponent(getUsage()), false);
				return 0;
		}

		Set<ResourceLocation> set;
		if (registry != null) {
			set = registry.getKeys();
		} else {
			set = worldGenRegistry.keySet();
		}

		new Thread(() -> {
			try
			{
				Path root = OTG.getEngine().getOTGRootFolder();
				String fileName = root.toString() + File.separator + "data-output-" + type + ".txt".toLowerCase();
				File output = new File(fileName);
				FileWriter writer = new FileWriter(output);
				for (ResourceLocation key : set)
				{
					writer.write(key.toString()+"\n");
				}
				writer.close();
				source.sendSuccess(new StringTextComponent("File exported as "+fileName), true);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}).start();
		return 0;
	}
	
	public static class DataTypeArgument implements ArgumentType<String>
	{
		private final String[] options = new String[]
		{ "biome", "block", "entity", "sound", "particle", "configured_feature" };

		public static DataTypeArgument create()
		{
			return new DataTypeArgument();
		}

		@Override
		public String parse(StringReader reader) throws CommandSyntaxException
		{
			return reader.readString();
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
		{
			return ISuggestionProvider.suggest(options, builder);
		}
	}
}

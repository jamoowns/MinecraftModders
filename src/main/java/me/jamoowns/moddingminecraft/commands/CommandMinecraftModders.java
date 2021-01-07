package me.jamoowns.moddingminecraft.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jamoowns.moddingminecraft.Feature;
import me.jamoowns.moddingminecraft.IFeatureListener;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;

public final class CommandMinecraftModders implements CommandExecutor {

	private IFeatureListener featureListener;

	private final List<ModdersCommand> commands;

	public CommandMinecraftModders() {
		commands = new ArrayList<>();
	}

	public final void addListener(IFeatureListener aFeatureListener) {
		featureListener = aFeatureListener;
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Optional<Consumer<Player>> commandToRun = moddersCommand(Arrays.asList(args)).map(ModdersCommand::action);
		if (commandToRun.isPresent()) {
			if (sender instanceof Player) {
				commandToRun.get().accept((Player) sender);
				return true;
			}
		}
		List<Feature> features = Arrays.asList(Feature.values());
		if (args.length == 2) {
			String enableString = args[0];
			String featureName = args[1];
			if (features.stream().map(Feature::name).anyMatch(featureName::equalsIgnoreCase)) {
				Feature feature = Feature.valueOf(featureName);

				if (enableString.equalsIgnoreCase("activate") || enableString.equalsIgnoreCase("a")) {
					featureListener.featureActivated(feature);
					return true;
				} else if (enableString.equalsIgnoreCase("deactivate") || enableString.equalsIgnoreCase("d")) {
					featureListener.featureDeactivated(feature);
					return true;
				}
			}
		}
		// Show help
		if (sender instanceof Player) {
			Broadcaster.sendInfo(sender,
					"possible features: " + features.stream().map(Feature::name).collect(Collectors.joining(", ")));
		}
		return false;
	}

	public final void registerCommand(List<String> parentChain, String command, Consumer<Player> function) {
		ModdersCommand moddersCommand = new ModdersCommand(command, function);

		Optional<ModdersCommand> parentCommand = moddersCommand(parentChain);

		if (parentCommand.isPresent()) {
			parentCommand.get().addSubCommand(moddersCommand);
		} else {
			commands.add(moddersCommand);
		}
	}

	private Optional<ModdersCommand> moddersCommand(Iterable<String> commandPath) {
		List<ModdersCommand> commandChildren = commands;
		Optional<ModdersCommand> command = Optional.empty();
		for (String parent : commandPath) {
			for (ModdersCommand c : commandChildren) {
				if (c.command().equalsIgnoreCase(parent)) {
					command = Optional.of(c);
					commandChildren = c.subCommands();
				}
			}
		}
		return command;
	}
}

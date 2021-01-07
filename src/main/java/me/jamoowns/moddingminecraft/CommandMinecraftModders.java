package me.jamoowns.moddingminecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jamoowns.moddingminecraft.common.chat.Broadcaster;

public final class CommandMinecraftModders implements CommandExecutor {

	private class ModdersCommand {

		private String command;

		private List<ModdersCommand> subCommands;

		private Consumer<Player> action;

		public ModdersCommand(final String theCommand, Consumer<Player> aAction) {
			subCommands = new ArrayList<>();
			command = theCommand;
			action = aAction;
		}

		public final void addSubCommand(ModdersCommand subCommand) {
			subCommands.add(subCommand);
		}
	}

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
		if (args.length > 0) {
			List<ModdersCommand> commandChildren = commands;
			Consumer<Player> commandToRun = null;
			for (String arg : args) {
				for (ModdersCommand c : commandChildren) {
					if (c.command.equalsIgnoreCase(arg)) {
						commandToRun = c.action;
						commandChildren = c.subCommands;
					}
				}
			}
			if (commandToRun != null) {
				if (sender instanceof Player) {
					commandToRun.accept((Player) sender);
					return true;
				}
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

		ModdersCommand parentCommand = moddersCommand(parentChain);

		if (parentCommand == null) {
			commands.add(moddersCommand);
		} else {
			parentCommand.addSubCommand(moddersCommand);
		}
	}

	/**
	 * @Nullable
	 * 
	 * @param commandPath
	 * @return
	 */
	private ModdersCommand moddersCommand(List<String> commandPath) {
		List<ModdersCommand> commandChildren = commands;
		ModdersCommand command = null;
		for (String parent : commandPath) {
			for (ModdersCommand c : commandChildren) {
				if (c.command.equalsIgnoreCase(parent)) {
					command = c;
					commandChildren = c.subCommands;
				}
			}
		}
		return command;
	}
}

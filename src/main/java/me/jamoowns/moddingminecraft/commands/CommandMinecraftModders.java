package me.jamoowns.moddingminecraft.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandMinecraftModders implements CommandExecutor {

	private final List<ModdersCommand> commands;

	public CommandMinecraftModders() {
		commands = new ArrayList<>();
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
		// Show help
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

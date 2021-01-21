package me.jamoowns.moddingminecraft.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.jamoowns.moddingminecraft.common.chat.Broadcaster;

public final class CommandMinecraftModders implements CommandExecutor, TabCompleter {

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
		Broadcaster.sendInfo(sender, "Available Commands:");
		for (ModdersCommand c : commands) {
			if (c.subCommands().isEmpty()) {
				Broadcaster.sendInfo(sender, "/mm " + c.command());
			} else {
				for (ModdersCommand c1 : c.subCommands()) {
					if (c1.subCommands().isEmpty()) {
						Broadcaster.sendInfo(sender, "/mm " + c.command() + " " + c1.command());
					} else {
						for (ModdersCommand c2 : c.subCommands()) {
							if (c2.subCommands().isEmpty()) {
								Broadcaster.sendInfo(sender,
										"/mm " + c.command() + " " + c1.command() + " " + c2.command());
							}
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<ModdersCommand> commandChildren = commands;
		Optional<ModdersCommand> c = Optional.empty();
		for (String parent : args) {
			for (ModdersCommand com : commandChildren) {
				if (com.command().startsWith(parent)) {
					c = Optional.of(com);
					commandChildren = com.subCommands();
				}
			}
		}
		List<String> results = new ArrayList<>();

		if (c.isPresent()) {
			for (ModdersCommand com : c.get().subCommands()) {
				results.add(c.get().command() + "" + com.command());
			}
		}
		return results;
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

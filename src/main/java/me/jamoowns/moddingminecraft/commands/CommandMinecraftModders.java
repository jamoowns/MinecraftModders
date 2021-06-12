package me.jamoowns.moddingminecraft.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
		Optional<Consumer<Player>> commandToRun = moddersCommand(Arrays.asList(args), false)
				.map(ModdersCommand::action);
		if (commandToRun.isPresent()) {
			if (sender instanceof Player) {
				commandToRun.get().accept((Player) sender);
				return true;
			}
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("Available Commands:\n");

		for (ModdersCommand moddersCommand : commands) {
			buffer.append("\t - " + moddersCommand.command() + "\n");
		}
		Broadcaster.sendInfo(sender, buffer.toString());
		return true;
	}

	@Override
	public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		ArrayList<String> results = new ArrayList<>();
		List<String> allArgs = new ArrayList<>(Arrays.asList(args));
		List<String> subSetArgs = new ArrayList<>(Arrays.asList(args));

		List<ModdersCommand> commandChildren;
		if (allArgs.size() == 0) {
			commandChildren = commands;
		} else {
			subSetArgs.remove(subSetArgs.size() - 1);
			Optional<ModdersCommand> moddersCommand = moddersCommand(subSetArgs, false);
			if (moddersCommand.isPresent()) {
				commandChildren = moddersCommand.get().subCommands();
			} else {
				commandChildren = commands;
			}
		}

		for (ModdersCommand com : commandChildren) {
			if (allArgs.size() > 0) {
				if (com.command().startsWith(allArgs.get(allArgs.size() - 1))) {
					results.add(com.command());
				}
			} else {
				results.add(com.command());
			}
		}
		Collections.sort(results);
		return results;
	}

	public final ModdersCommand registerCommand(ModdersCommand parentCommand, String command,
			Consumer<Player> function) {
		ModdersCommand moddersCommand = new ModdersCommand(command, function);

		parentCommand.addSubCommand(moddersCommand);
		return moddersCommand;
	}

	public final ModdersCommand registerCommand(String command, Consumer<Player> function) {
		ModdersCommand moddersCommand = new ModdersCommand(command, function);

		commands.add(moddersCommand);
		return moddersCommand;
	}

	private Optional<ModdersCommand> moddersCommand(List<String> commandPath, boolean partial) {
		List<ModdersCommand> commandChildren = commands;
		Optional<ModdersCommand> command = Optional.empty();
		for (String parent : commandPath) {
			for (ModdersCommand c : commandChildren) {
				Predicate<String> comparator = partial ? c.command()::startsWith : c.command()::equalsIgnoreCase;
				if (!parent.isEmpty() && comparator.test(parent)) {
					command = Optional.of(c);
					commandChildren = c.subCommands();
				}
			}
		}
		return command;
	}
}

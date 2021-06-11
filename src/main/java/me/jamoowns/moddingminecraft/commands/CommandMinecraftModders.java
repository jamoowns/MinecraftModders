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
		StringBuilder buffer = new StringBuilder();
//		print("", commands, buffer, "", "");

		ModdersCommand root = new ModdersCommand("/mm", p -> System.err.println(p));
		printTreeRec(buffer, "", root, commands, true);
		Broadcaster.sendInfo(sender, buffer.toString());

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
				results.add(c.get().command() + " " + com.command());
			}
		}
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

	private void printTreeRec(StringBuilder buffer, String prefix, ModdersCommand node, List<ModdersCommand> children,
			boolean isTail) {
		String nodeName = node.command();
		String nodeConnection = isTail ? "\\ " : "|- ";
		buffer.append(prefix + nodeConnection + nodeName);
		buffer.append("\n");
		for (int i = 0; i < children.size(); i++) {
			String newPrefix = prefix + (isTail ? "    " : "|   ");
			printTreeRec(buffer, newPrefix, children.get(i), children.get(i).subCommands(), i == children.size() - 1);
		}
	}
}

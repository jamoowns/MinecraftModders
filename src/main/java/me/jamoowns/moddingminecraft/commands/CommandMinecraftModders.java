package me.jamoowns.moddingminecraft.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
		Broadcaster.sendInfo(sender, "Available Commands:");
		List<String> buffer = new ArrayList<>();

		treeRec(buffer, "", commands, true);
		Broadcaster.sendInfo(sender, buffer.stream().collect(Collectors.joining("\n")));

		return true;
	}

	@Override
	public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		Optional<ModdersCommand> moddersCommand = moddersCommand(Arrays.asList(args), true);

		List<ModdersCommand> commandChildren = moddersCommand.map(ModdersCommand::subCommands)
				.orElseGet(() -> commands);
		List<String> results = new ArrayList<>();

		String prefix;
		/* Full match. */
		if (moddersCommand(Arrays.asList(args), false).isPresent()) {
			prefix = "";
		} else {
			prefix = moddersCommand.get().command() + " ";
		}
		commandChildren.forEach(Broadcaster::broadcastInfo);
		for (ModdersCommand com : commandChildren) {
			results.add(prefix + com.command());
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

	private void treeRec(List<String> buffer, String prefix, List<ModdersCommand> children, boolean isTail) {
		for (int i = 0; i < children.size(); i++) {
			String newPrefix = prefix + (isTail ? "    " : "|   ");
			treeRec(buffer, newPrefix, children.get(i), children.get(i).subCommands(), i == children.size() - 1);
		}
	}

	private void treeRec(List<String> buffer, String prefix, ModdersCommand node, List<ModdersCommand> children,
			boolean isTail) {
		String nodeName = node.command();
		String nodeConnection = isTail ? "\\ " : "|- ";
		buffer.add(prefix + nodeConnection + nodeName);
		for (int i = 0; i < children.size(); i++) {
			String newPrefix = prefix + (isTail ? "    " : "|   ");
			treeRec(buffer, newPrefix, children.get(i), children.get(i).subCommands(), i == children.size() - 1);
		}
	}
}

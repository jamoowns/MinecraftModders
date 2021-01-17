package me.jamoowns.moddingminecraft.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

final class ModdersCommand {

	private String command;

	private List<ModdersCommand> subCommands;

	private Consumer<Player> action;

	public ModdersCommand(final String theCommand, Consumer<Player> aAction) {
		subCommands = new ArrayList<>();
		command = theCommand;
		action = aAction;
	}

	public final Consumer<Player> action() {
		return action;
	}

	public final void addSubCommand(ModdersCommand subCommand) {
		subCommands.add(subCommand);
	}

	public final String command() {
		return command;
	}

	public final List<ModdersCommand> subCommands() {
		return subCommands;
	}
}
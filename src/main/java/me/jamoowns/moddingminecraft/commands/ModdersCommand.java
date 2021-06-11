package me.jamoowns.moddingminecraft.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

public final class ModdersCommand {

	private String command;

	private List<ModdersCommand> subCommands;

	private Consumer<Player> action;

	ModdersCommand(final String theCommand, Consumer<Player> aAction) {
		subCommands = new ArrayList<>();
		command = theCommand;
		action = aAction;
	}

	final Consumer<Player> action() {
		return action;
	}

	final void addSubCommand(ModdersCommand subCommand) {
		subCommands.add(subCommand);
	}

	final String command() {
		return command;
	}

	final List<ModdersCommand> subCommands() {
		return subCommands;
	}
}
package me.jamoowns.moddingminecraft.common.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class Broadcaster {

	public static final void broadcastError(final Object message) {
		broadcastError(message.toString());
	}

	public static final void broadcastError(final String message) {
		Bukkit.broadcastMessage(
				ChatColourStyles.DEFAULT_CHAT_COLOUR + "[ALL] " + ChatColourStyles.ERROR_COLOUR + message);
	}

	public static final void broadcastGameInfo(final String message) {
		Bukkit.broadcastMessage(ChatColourStyles.DEFAULT_CHAT_COLOUR + "[ALL] " + ChatColourStyles.GAME_INFO_COLOUR + ""
				+ ChatColor.BOLD + message);
	}

	public static final void broadcastInfo(final Object message) {
		broadcastInfo(message.toString());
	}

	public static final void broadcastInfo(final String message) {
		Bukkit.broadcastMessage(
				ChatColourStyles.DEFAULT_CHAT_COLOUR + "[ALL] " + ChatColourStyles.INFO_COLOUR + message);
	}

	public static final void sendError(final CommandSender player, final String message) {
		player.sendMessage(ChatColourStyles.ERROR_COLOUR + message);
	}

	public static final void sendGameInfo(final CommandSender player, final String message) {
		player.sendMessage(ChatColourStyles.GAME_INFO_COLOUR + "" + ChatColor.BOLD + message);
	}

	public static final void sendInfo(final CommandSender player, final String message) {
		player.sendMessage(ChatColourStyles.INFO_COLOUR + message);
	}
}

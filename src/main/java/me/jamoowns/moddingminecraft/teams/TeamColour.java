package me.jamoowns.moddingminecraft.teams;

import org.bukkit.ChatColor;
import org.bukkit.Material;

final class TeamColour {

	private final Material head;
	private final ChatColor colour;

	public TeamColour(Material aHead, ChatColor aColour) {
		head = aHead;
		colour = aColour;
	}

	public final Material getHead() {
		return head;
	}

	public final ChatColor getColour() {
		return colour;
	}
}

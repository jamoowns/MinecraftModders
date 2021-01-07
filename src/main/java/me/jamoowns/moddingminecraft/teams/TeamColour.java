package me.jamoowns.moddingminecraft.teams;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public final class TeamColour {

	private final Material head;
	private final Material trail;
	private final ChatColor colour;

	public TeamColour(Material aHead, ChatColor aColour, Material aTrail) {
		head = aHead;
		colour = aColour;
		trail = aTrail;
	}

	public final ChatColor getColour() {
		return colour;
	}

	public final Material getHead() {
		return head;
	}

	public final Material getTrail() {
		return trail;
	}
}

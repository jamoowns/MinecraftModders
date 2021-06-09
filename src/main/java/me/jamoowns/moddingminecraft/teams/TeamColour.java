package me.jamoowns.moddingminecraft.teams;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public final class TeamColour {

	private final Material base;
	private final Material head;
	private final Material trail;
	private final ChatColor colour;

	public TeamColour(Material aBase, Material aHead, ChatColor aColour, Material aTrail) {
		base = aBase;
		head = aHead;
		colour = aColour;
		trail = aTrail;
	}

	public final Material getBase() {
		return base;
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

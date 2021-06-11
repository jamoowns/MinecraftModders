package me.jamoowns.moddingminecraft.teams;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public final class TeamColour {

	private final Material base;
	private final Material head;
	private final Material trail;
	private final ChatColor colour;
	private final Color firework;

	public TeamColour(Material aBase, Material aHead, ChatColor aColour, Material aTrail, Color aFirework) {
		base = aBase;
		head = aHead;
		colour = aColour;
		trail = aTrail;
		firework = aFirework;
	}

	public final Material getBase() {
		return base;
	}

	public final ChatColor getColour() {
		return colour;
	}

	public final Color getFirework() {
		return firework;
	}

	public final Material getHead() {
		return head;
	}

	public final Material getTrail() {
		return trail;
	}
}

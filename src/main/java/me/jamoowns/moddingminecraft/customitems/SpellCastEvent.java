package me.jamoowns.moddingminecraft.customitems;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class SpellCastEvent {

	private Location location;
	private Player player;

	public SpellCastEvent(Location aLocation, Player aPlayer) {
		location = aLocation;
		player = aPlayer;
	}

	public Location getLocation() {
		return location;
	}

	public Player getPlayer() {
		return player;
	}
}

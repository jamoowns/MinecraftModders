package me.jamoowns.moddingminecraft.minigames.mgsettings;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * Stores information about a player at a given time.
 */
public final class PlayerInfo {

	private ItemStack[] inventory;

	private Location bedSpawn;

	public PlayerInfo(ItemStack[] aInventory, Location aBedSpawn) {
		inventory = aInventory;
		bedSpawn = aBedSpawn;
	}

	public final Location getBedSpawn() {
		return bedSpawn;
	}

	public ItemStack[] getInventory() {
		return inventory;
	}
}

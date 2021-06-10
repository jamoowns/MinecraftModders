package me.jamoowns.moddingminecraft.minigames.mgSettings;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class PlayerInfo {
	// TODO:turn this into a full on lobby settings not just inventory
	private ItemStack[] Inventory = null;
	private Location BedSpawn = null;

	public PlayerInfo(ItemStack[] inventory, Location bedSpawn) {
		setInventory(inventory);
		setBedSpawn(bedSpawn);
	}

	public Location getBedSpawn() {
		return BedSpawn;
	}

	public ItemStack[] getInventory() {
		return Inventory;
	}

	public void setBedSpawn(Location bedSpawn) {
		BedSpawn = bedSpawn;
	}

	public void setInventory(ItemStack[] inventory) {
		Inventory = inventory;
	}

}

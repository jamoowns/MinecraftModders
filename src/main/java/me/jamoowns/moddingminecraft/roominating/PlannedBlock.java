package me.jamoowns.moddingminecraft.roominating;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public final class PlannedBlock {
	static PlannedBlock plannedBlock(Location aLocation, BlockData aBlockData) {
		return new PlannedBlock(aLocation.clone(), aBlockData);
	}
	static PlannedBlock plannedBlock(Location aLocation, Material aMaterial) {
		return new PlannedBlock(aLocation.clone(), Bukkit.createBlockData(aMaterial));
	}

	private Location location;

	private BlockData blockData;

	private PlannedBlock(Location aLocation, BlockData aBlockData) {
		location = aLocation;
		blockData = aBlockData;
	}

	public final BlockData getBlockData() {
		return blockData;
	}

	public final Location getLocation() {
		return location;
	}
}

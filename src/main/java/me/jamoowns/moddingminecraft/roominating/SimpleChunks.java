package me.jamoowns.moddingminecraft.roominating;

import org.bukkit.Location;
import org.bukkit.Material;

public final class SimpleChunks {
	public void createChunk(int type, Location loc) {
		if (type == 1) {
			grassChunk(loc.clone().getChunk().getBlock(0, 60, 0).getLocation());
		}
	}

	private void grassChunk(Location loc) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				loc.clone().add(x, 0, z).getBlock().setType(Material.GRASS_BLOCK);
			}
		}
	}
}

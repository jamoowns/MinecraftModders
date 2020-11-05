package me.jamoowns.moddingminecraft.roominating;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;

public class PlannedBlocks {

	public static List<PlannedBlock> plannedBlock(List<Location> locations, Material material) {
		return locations.stream().map(location -> PlannedBlock.plannedBlock(location, material))
				.collect(Collectors.toList());
	}
}

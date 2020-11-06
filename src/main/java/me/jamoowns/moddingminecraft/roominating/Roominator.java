package me.jamoowns.moddingminecraft.roominating;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Door.Hinge;

public class Roominator {

	public static List<PlannedBlock> buildDoor(Location doorLocation, Material doorType, Hinge hinge,
			BlockFace blockFace) {
		Door doorBottom = (Door) Bukkit.createBlockData(doorType);

		doorBottom.setHinge(hinge);
		doorBottom.setFacing(blockFace);
		doorBottom.setHalf(Half.BOTTOM);

		Door doorTop = (Door) Bukkit.createBlockData(doorType);
		doorTop.setHinge(hinge);
		doorTop.setFacing(blockFace);
		doorTop.setHalf(Half.TOP);

		List<PlannedBlock> door = new ArrayList<>();
		door.add(PlannedBlock.plannedBlock(doorLocation, doorBottom));
		door.add(PlannedBlock.plannedBlock(doorLocation.clone().add(0, 1, 0), doorTop));
		return door;
	}

	public static void build(World world, List<PlannedBlock> plannedBlocks) {
		plannedBlocks.forEach(plannedBlock -> {
			world.getBlockAt(plannedBlock.getLocation()).setBlockData(plannedBlock.getBlockData());
		});
	}

	public static List<Location> wall(Location startLocation, Location endLocation) {
		List<Location> wall = new ArrayList<>();
		for (int x = 0; x <= endLocation.getX() - startLocation.getX(); x++) {
			for (int y = 0; y <= endLocation.getY() - startLocation.getY(); y++) {
				for (int z = 0; z <= endLocation.getZ() - startLocation.getZ(); z++) {
					wall.add(startLocation.clone().add(x, y, z));
				}
			}
		}
		return wall;
	}

	public static List<PlannedBlock> standardRoom(Location startLocation, int width, int length, int height) {
		List<Location> walls = Roominator.walls(startLocation, startLocation.clone().add(width, height, length));
		List<PlannedBlock> plannedBlocks = PlannedBlocks.plannedBlock(walls, Material.COBBLESTONE);

		List<Location> roof = Roominator.wall(startLocation.clone().add(0, height, 0),
				startLocation.clone().add(width, height, length));

		plannedBlocks.addAll(PlannedBlocks.plannedBlock(roof, Material.OAK_PLANKS));

		plannedBlocks.addAll(Roominator.buildDoor(startLocation.clone().add(((int) width / 2), 0, 0), Material.OAK_DOOR,
				Hinge.RIGHT, BlockFace.SOUTH));

		List<Location> windows = Roominator.walls(startLocation.clone().add(width, 1, ((int) length / 2)),
				startLocation.clone().add(width, 2, ((int) length / 2)));

		plannedBlocks.addAll(PlannedBlocks.plannedBlock(windows, Material.GLASS));

		return plannedBlocks;
	}

	public static List<Location> walls(Location startLocation, Location endLocation) {
		List<Location> room = new ArrayList<>();

		double height = endLocation.getY() - startLocation.getY();
		double xWidth = endLocation.getX() - startLocation.getX();
		double zLength = endLocation.getZ() - startLocation.getZ();

		room.addAll(wall(startLocation, startLocation.clone().add(0, height, zLength)));
		room.addAll(wall(startLocation.clone().add(xWidth, 0, 0), startLocation.clone().add(xWidth, height, zLength)));
		room.addAll(wall(startLocation, startLocation.clone().add(5, height, 0)));
		room.addAll(wall(startLocation.clone().add(0, 0, zLength), startLocation.clone().add(xWidth, height, zLength)));

		return room;
	}
}

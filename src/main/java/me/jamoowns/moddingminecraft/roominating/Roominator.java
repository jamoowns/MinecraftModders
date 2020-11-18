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

public final class Roominator {

	private static List<PlannedBlock> door(Location doorLocation, Material doorType, Hinge hinge, BlockFace blockFace) {
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

	private static List<Location> wall(Location startLocation, Location endLocation) {
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

	public static List<PlannedBlock> standardRoom(Location startLocation, int aWidth, int aLength, int aHeight,
			BlockFace direction) {
		int width;
		int length;
		switch (direction) {
		case NORTH:
			width = aWidth;
			length = aLength * -1;
		case EAST:
			width = aLength * -1;
			length = aWidth * -1;
			//
		case WEST:
			width = aLength;
			length = aWidth;
		case SOUTH:
		default:
			width = aWidth;
			length = aLength;
		}
		return standardRoom(startLocation, width, length, aHeight);
	}

	private static List<PlannedBlock> standardRoom(Location startLocation, int aWidth, int aLength, int aHeight) {
		int width = aWidth - 1;
		int length = aLength - 1;
		int height = aHeight - 1;
		List<Location> walls = walls(startLocation, startLocation.clone().add(width, height, length));
		List<PlannedBlock> plannedBlocks = PlannedBlocks.plannedBlock(walls, Material.COBBLESTONE);

		List<Location> roof = wall(startLocation.clone().add(0, height, 0),
				startLocation.clone().add(width, height, length));

		plannedBlocks.addAll(PlannedBlocks.plannedBlock(roof, Material.OAK_PLANKS));

		plannedBlocks.addAll(door(startLocation.clone().add(((int) width / 2), 0, 0), Material.OAK_DOOR, Hinge.RIGHT,
				BlockFace.SOUTH));

		List<Location> windows = walls(startLocation.clone().add(width, 1, ((int) length / 2)),
				startLocation.clone().add(width, 2, ((int) length / 2)));

		windows.addAll(walls(startLocation.clone().add(0, 1, ((int) length / 2)),
				startLocation.clone().add(0, 2, ((int) length / 2))));

		windows.addAll(walls(startLocation.clone().add(((int) width / 2), 1, length),
				startLocation.clone().add(((int) width / 2), 2, length)));

		plannedBlocks.addAll(PlannedBlocks.plannedBlock(windows, Material.GLASS));

		return plannedBlocks;
	}

	private static List<Location> walls(Location startLocation, Location endLocation) {
		List<Location> room = new ArrayList<>();

		double height = endLocation.getY() - startLocation.getY();
		double xWidth = endLocation.getX() - startLocation.getX();
		double zLength = endLocation.getZ() - startLocation.getZ();

		room.addAll(wall(startLocation, startLocation.clone().add(0, height, zLength)));
		room.addAll(wall(startLocation.clone().add(xWidth, 0, 0), startLocation.clone().add(xWidth, height, zLength)));
		room.addAll(wall(startLocation, startLocation.clone().add(xWidth, height, 0)));
		room.addAll(wall(startLocation.clone().add(0, 0, zLength), startLocation.clone().add(xWidth, height, zLength)));

		return room;
	}
}

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

	public static void build(World world, List<PlannedBlock> plannedBlocks) {
		plannedBlocks.forEach(plannedBlock -> {
			world.getBlockAt(plannedBlock.getLocation()).setBlockData(plannedBlock.getBlockData());
		});
	}

	public static List<PlannedBlock> standardRoom(Location startLocation, int aWidth, int aLength, int aHeight,
			BlockFace direction) {
		return room(startLocation, aWidth, aLength, aHeight, direction);
	}

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

	private static List<PlannedBlock> room(Location doorLocation, int aWidth, int aLength, int height,
			BlockFace direction) {
		Location bottomLeft;
		Location topRight;
		int length;
		int width;
		List<Location> windows;
		switch (direction) {
			case NORTH:
				width = aWidth;
				length = aLength * -1;
				bottomLeft = doorLocation.clone().subtract(width / 2, 0, 0);
				topRight = doorLocation.clone().add(width / 2, height, length);
				windows = walls(doorLocation.clone().add(width / 2, 1, length / 2),
						doorLocation.clone().add(width / 2, 2, length / 2));

				windows.addAll(
						walls(bottomLeft.clone().add(0, 1, length / 2), bottomLeft.clone().add(0, 2, length / 2)));

				/* Back windows. */
				windows.addAll(walls(doorLocation.clone().add(0, 1, length), doorLocation.clone().add(0, 2, length)));
				break;
			case EAST:
				width = aWidth;
				length = aLength;
				bottomLeft = doorLocation.clone().subtract(0, 0, width / 2);
				topRight = doorLocation.clone().add(length, height, width / 2);
				windows = walls(doorLocation.clone().add(length / 2, 1, width / 2),
						doorLocation.clone().add(length / 2, 2, width / 2));

				windows.addAll(
						walls(bottomLeft.clone().add(length / 2, 1, 0), bottomLeft.clone().add(length / 2, 2, 0)));

				/* Back windows. */
				windows.addAll(walls(doorLocation.clone().add(length, 1, 0), doorLocation.clone().add(length, 2, 0)));
				break;
			case WEST:
				width = aWidth * -1;
				length = aLength * -1;
				bottomLeft = doorLocation.clone().subtract(0, 0, width / 2);
				topRight = doorLocation.clone().add(length, height, width / 2);
				windows = walls(doorLocation.clone().add(length / 2, 1, width / 2),
						doorLocation.clone().add(length / 2, 2, width / 2));

				windows.addAll(
						walls(bottomLeft.clone().add(length / 2, 1, 0), bottomLeft.clone().add(length / 2, 2, 0)));

				/* Back windows. */
				windows.addAll(walls(doorLocation.clone().add(length, 1, 0), doorLocation.clone().add(length, 2, 0)));
				break;
			case SOUTH:
			default:
				width = aWidth;
				length = aLength;
				bottomLeft = doorLocation.clone().subtract(width / 2, 0, 0);
				topRight = doorLocation.clone().add(width / 2, height, length);
				windows = walls(doorLocation.clone().add(width / 2, 1, length / 2),
						doorLocation.clone().add(width / 2, 2, length / 2));

				windows.addAll(
						walls(bottomLeft.clone().add(0, 1, length / 2), bottomLeft.clone().add(0, 2, length / 2)));

				/* Back windows. */
				windows.addAll(walls(doorLocation.clone().add(0, 1, length), doorLocation.clone().add(0, 2, length)));
		}

		List<PlannedBlock> plannedBlocks = new ArrayList<>();

		List<Location> walls = walls(bottomLeft, topRight);

		plannedBlocks.addAll(PlannedBlocks.plannedBlock(walls, Material.COBBLESTONE));

		List<Location> roof = wall(bottomLeft.clone().add(0, height, 0), topRight);

		plannedBlocks.addAll(PlannedBlocks.plannedBlock(roof, Material.OAK_PLANKS));

		plannedBlocks.addAll(door(doorLocation, Material.OAK_DOOR, Hinge.RIGHT, direction));

		plannedBlocks.addAll(PlannedBlocks.plannedBlock(windows, Material.GLASS));

		return plannedBlocks;
	}

	private static List<Location> wall(Location startLocation, Location endLocation) {
		List<Location> wall = new ArrayList<>();

		for (int x = 0; x <= Math.abs(endLocation.getX() - startLocation.getX()); x++) {
			for (int y = 0; y <= Math.abs(endLocation.getY() - startLocation.getY()); y++) {
				for (int z = 0; z <= Math.abs(endLocation.getZ() - startLocation.getZ()); z++) {
					int theX = endLocation.getX() >= startLocation.getX() ? x : x * -1;
					int theY = endLocation.getY() >= startLocation.getY() ? y : y * -1;
					int theZ = endLocation.getZ() >= startLocation.getZ() ? z : z * -1;
					wall.add(startLocation.clone().add(theX, theY, theZ));
				}
			}
		}
		return wall;
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

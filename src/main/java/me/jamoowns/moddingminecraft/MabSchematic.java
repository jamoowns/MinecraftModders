package me.jamoowns.moddingminecraft;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class MabSchematic implements Listener {

	private final ModdingMinecraft javaPlugin;

	public MabSchematic(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

	public void BuildGrid(int grid, BlockFace direction, Location loc) {
		if (grid == 1) {
			Material[] buildList = new Material[] { Material.AIR, Material.STONE_BRICKS, Material.OAK_SLAB,
					Material.STONE_BRICK_STAIRS };

			Material[][][] buildGrid = new Material[10][15][20];

			/*
			 * for (int x = 0; x < buildGrid[0].length; x++) { for (int y = 0; y <
			 * buildGrid[1].length; y++) { for (int z = 0; z < buildGrid[2].length; z++) {
			 * if (x < 1 && x > buildGrid[0].length) { if (y < 1 && y > buildGrid[1].length)
			 * { if (z < 1 && z > buildGrid[2].length) { buildGrid[x][y][z] = buildList[1];
			 * } } } } } }
			 */

			for (int r = 0; r < buildGrid[0].length; r++) {
				for (int c = 0; c < buildGrid[1].length; c++) {
					for (int l = 0; l < buildGrid[2].length; l++) {
						Location temploc = loc;
						loc.getWorld().getBlockAt(temploc.add(r, l, c)).setType(Material.STONE_BRICKS);
					}
				}
			}

		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType().equals(Material.WARPED_HYPHAE)) {
			BuildGrid(1, linearFace(event.getPlayer().getLocation().getYaw()), event.getBlockPlaced().getLocation());
			event.getPlayer().sendMessage("Building");
			/*
			 * World world = event.getBlockPlaced().getLocation().getWorld(); switch
			 * (linearFace(event.getPlayer().getLocation().getYaw())) { case NORTH:
			 * 
			 * break; case EAST: for (int k = 0; k < 10; k++) { for (int i = 0; i < 21; i++)
			 * {
			 * 
			 * Location loc = event.getBlockPlaced().getLocation(); Location locThree =
			 * event.getBlockPlaced().getLocation(); if (i == 20 && k % 2 == 0) {
			 * world.getBlockAt(loc.add(0, i, 0 + k)).setType(Material.STONE_BRICK_STAIRS);
			 * world.getBlockAt(locThree.add(14, i, 0 +
			 * k)).setType(Material.STONE_BRICK_STAIRS);
			 * 
			 * BlockData blockData = world.getBlockAt(loc).getBlockData(); if (blockData
			 * instanceof Directional) { ((Directional)
			 * blockData).setFacing(BlockFace.EAST);
			 * world.getBlockAt(loc).setBlockData(blockData); } blockData =
			 * world.getBlockAt(locThree).getBlockData(); if (blockData instanceof
			 * Directional) { ((Directional) blockData).setFacing(BlockFace.WEST);
			 * world.getBlockAt(locThree).setBlockData(blockData); }
			 * 
			 * } else { loc.getWorld().getBlockAt(loc.add(0, i, 0 +
			 * k)).setType(Material.STONE_BRICKS);
			 * loc.getWorld().getBlockAt(locThree.add(14, i, 0 +
			 * k)).setType(Material.STONE_BRICKS);
			 * 
			 * Location locTwo = event.getBlockPlaced().getLocation();
			 * world.getBlockAt(locTwo.add(11, i, 0 + k)).setType(Material.STONE_BRICKS);
			 * Location locFour = event.getBlockPlaced().getLocation();
			 * world.getBlockAt(locFour.add(3, i, 0 + k)).setType(Material.STONE_BRICKS); }
			 * 
			 * } for (int i = 20; i < 21; i++) { Location locFive =
			 * event.getBlockPlaced().getLocation(); world.getBlockAt(locFive.add(1, i, 0 +
			 * k)).setType(Material.OAK_SLAB); world.getBlockAt(locFive.add(1, 0,
			 * 0)).setType(Material.OAK_SLAB); world.getBlockAt(locFive.add(1, 0,
			 * 0)).setType(Material.OAK_SLAB);
			 * 
			 * if (k == 4 || k == 5) { world.getBlockAt(locFive.add(1, 0,
			 * 0)).setType(Material.OAK_SLAB); world.getBlockAt(locFive.add(1, 0,
			 * 0)).setType(Material.OAK_SLAB); } else { world.getBlockAt(locFive.add(1, 0,
			 * 0)).setType(Material.STONE_BRICKS); world.getBlockAt(locFive.add(1, 0,
			 * 0)).setType(Material.STONE_BRICK_STAIRS);
			 * 
			 * BlockData blockData = world.getBlockAt(locFive).getBlockData(); if (blockData
			 * instanceof Directional) { ((Directional)
			 * blockData).setFacing(BlockFace.WEST);
			 * world.getBlockAt(locFive).setBlockData(blockData); }
			 * 
			 * }
			 * 
			 * world.getBlockAt(locFive.add(1, 0, 0)).setType(Material.OAK_SLAB);
			 * world.getBlockAt(locFive.add(1, 0, 0)).setType(Material.OAK_SLAB);
			 * 
			 * Location locSix = event.getBlockPlaced().getLocation();
			 * world.getBlockAt(locSix.add(13, i, 0 + k)).setType(Material.OAK_SLAB);
			 * world.getBlockAt(locSix.add(-1, 0, 0)).setType(Material.OAK_SLAB);
			 * world.getBlockAt(locSix.add(-1, 0, 0)).setType(Material.OAK_SLAB);
			 * 
			 * if (k == 4 || k == 5) { world.getBlockAt(locSix.add(-1, 0,
			 * 0)).setType(Material.OAK_SLAB); world.getBlockAt(locSix.add(-1, 0,
			 * 0)).setType(Material.OAK_SLAB); } else { world.getBlockAt(locSix.add(-1, 0,
			 * 0)).setType(Material.STONE_BRICKS); world.getBlockAt(locSix.add(-1, 0,
			 * 0)).setType(Material.STONE_BRICK_STAIRS);
			 * 
			 * BlockData blockData = world.getBlockAt(locSix).getBlockData(); if (blockData
			 * instanceof Directional) { ((Directional)
			 * blockData).setFacing(BlockFace.EAST);
			 * world.getBlockAt(locSix).setBlockData(blockData); }
			 * 
			 * }
			 * 
			 * world.getBlockAt(locSix.add(-1, 0, 0)).setType(Material.OAK_SLAB); } }
			 * 
			 * break; case WEST: break; case SOUTH: default: }
			 */
		}
	}

	public Material[][] RotateShapeSquareGrid(Material[][] shape, int rotate) {
		if (rotate == 90) {
			Material[][] newShape = new Material[shape[1].length][shape[0].length];

			for (int r = 0; r < shape[0].length; r++) {
				for (int c = 0; c < shape[1].length; c++) {
					int newR = newShape[1].length - r - 1;
					int newC = newShape[0].length - c - 1;
					newShape[newC][newR] = shape[r][c];
				}
			}

			return newShape;
		} else if (rotate == 180) {
			Material[][] newShape = new Material[shape[0].length][shape[1].length];

			for (int r = 0; r < shape[0].length; r++) {
				for (int c = 0; c < shape[1].length; c++) {
					int newR = newShape[0].length - r - 1;
					int newC = newShape[1].length - c - 1;
					newShape[newR][newC] = shape[r][c];
				}
			}
			return newShape;
		} else if (rotate == 270) {
			return RotateShapeSquareGrid(RotateShapeSquareGrid(shape, 90), 180);
		}

		return shape;
	}

	private BlockFace linearFace(float yaw) {
		double rotation = (yaw - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}

		if (0 <= rotation && rotation < 67.5 || 337.5 <= rotation && rotation < 360.0) {
			return BlockFace.WEST;
		} else if (67.5 <= rotation && rotation < 157.5) {
			return BlockFace.NORTH;
		} else if (157.5 <= rotation && rotation < 247.5) {
			return BlockFace.EAST;
		} else if (247.5 <= rotation && rotation < 337.5) {
			return BlockFace.SOUTH;
		} else {
			return BlockFace.WEST;
		}
	}
}
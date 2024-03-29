package me.jamoowns.moddingminecraft.roominating;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class StructureBuilderListener implements IGameEventListener {

	public enum GridType {
		TOWER, CORNER, T_SECTION, CROSS, DEAD_END, STRAIGHT
	}

	private Material[][][] buildGrid;

	private int[][][] directionGrid;

	private int[][][] upDownGrid;

	private int[][][] cornerGrid;

	boolean WallOne = true;
	boolean WallTwo = true;
	boolean WallThree = true;
	boolean WallFour = true;

	public final void buildGrid(GridType grid, BlockFace direction, Location loc) {
		switch (grid) {
			case TOWER:
			case CORNER:
			case T_SECTION:
			case CROSS:
			case DEAD_END:
				buildNotStraightChunk(direction, grid, loc);
				break;
			case STRAIGHT:
				buildStraightChunk(direction, loc);
				break;
		}
	}

	@Override
	public final ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return IGameEventListener.ALWAYS_ENABLED;
	}

	@Override
	public final void onDisabled() {
		/* Empty. */
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
	}

	@EventHandler
	public final void onEntitySpawnEvent(EntitySpawnEvent event) {
		// TODO: Make lantern a custom item and only remove that
		if (event.getEntity().getName().contains("Lantern")) {
			event.setCancelled(true);
		}
	}

	@Override
	public final void onServerStop() {
		/* Empty. */
	}

	private void buildNotStraightChunk(BlockFace direction, GridType grid, Location loc) {
		Material[] buildList = new Material[] { Material.AIR, Material.STONE_BRICKS, Material.OAK_SLAB,
				Material.STONE_BRICK_STAIRS, Material.IRON_BARS, Material.STONE_BRICK_SLAB, Material.LANTERN };
		createGrids(16, 30, 16);
		switch (grid) {
			case TOWER:
				WallOne = true;
				WallTwo = true;
				WallThree = true;
				WallFour = true;
				break;
			case DEAD_END:
				WallThree = !(direction == BlockFace.NORTH);
				WallOne = !(direction == BlockFace.EAST);
				WallFour = !(direction == BlockFace.SOUTH);
				WallTwo = !(direction == BlockFace.WEST);
				break;
			case CORNER:
				WallThree = !(direction == BlockFace.NORTH || direction == BlockFace.WEST);
				WallOne = !(direction == BlockFace.EAST || direction == BlockFace.NORTH);
				WallFour = !(direction == BlockFace.SOUTH || direction == BlockFace.EAST);
				WallTwo = !(direction == BlockFace.WEST || direction == BlockFace.SOUTH);
				break;
			case T_SECTION:
				WallThree = !(direction == BlockFace.NORTH || direction == BlockFace.WEST
						|| direction == BlockFace.EAST);
				WallOne = !(direction == BlockFace.EAST || direction == BlockFace.NORTH
						|| direction == BlockFace.SOUTH);
				WallFour = !(direction == BlockFace.SOUTH || direction == BlockFace.EAST
						|| direction == BlockFace.WEST);
				WallTwo = !(direction == BlockFace.WEST || direction == BlockFace.SOUTH
						|| direction == BlockFace.NORTH);
				break;
			case CROSS:
				WallOne = false;
				WallTwo = false;
				WallThree = false;
				WallFour = false;
				break;
			case STRAIGHT:
				/* Nothing to do here. */
				break;
			default:
				break;
		}
		for (int l = 0; l < buildGrid[0].length; l++) {
			for (int c = 0; c < buildGrid[0][0].length; c++) {
				for (int r = 0; r < buildGrid.length; r++) {
					insert(0 + r, 0 + l, 0 + c, buildList[0], 0, 0, 0);
				}
			}
		}

		int heightTracker = 0;

		for (; heightTracker < 19; heightTracker++) {
			for (int c = 0; c < buildGrid[0][0].length; c++) {
				for (int r = 0; r < buildGrid.length; r++) {
					notStraightGrid(r, c, r, c, heightTracker, 1, buildList, direction, grid);
				}
			}
		}

		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				notStraightGrid(r, c, r, c, heightTracker, 2, buildList, direction, grid);
			}
		}
		for (int i = 0; i < 9; i++) {
			heightTracker++;
			for (int c = 0; c < buildGrid[0][0].length; c++) {
				for (int r = 0; r < buildGrid.length; r++) {
					notStraightGrid(r, c, r, c, heightTracker, 3 + i, buildList, direction, grid);
				}
			}
		}

		if (buildGrid[15][16][15] == buildList[0]) {
			int x = 15;
			int newX = x - 1;
			int z = 15;
			int newZ = z - 1;

			for (int i = 0; i < 10; i++) {

				buildGrid[x][16 + i][z] = buildList[1];
			}
			buildGrid[newX][19][newZ] = buildList[2];
			buildGrid[newX][19][z] = buildList[2];
			buildGrid[x][19][newZ] = buildList[2];

			buildGrid[newX][22][newZ] = buildList[5];
			buildGrid[newX][22][z] = buildList[5];
			buildGrid[x][22][14] = buildList[5];
			upDownGrid[newX][22][newZ] = 1;
			upDownGrid[newX][22][z] = 1;
			upDownGrid[x][22][14] = 1;

			buildGrid[newX][23][newZ] = buildList[2];
			buildGrid[newX][23][z] = buildList[2];
			buildGrid[x][23][newZ] = buildList[2];
		}
		if (buildGrid[15][16][0] == buildList[0]) {
			int x = 15;
			int newX = x - 1;
			int z = 0;
			int newZ = z + 1;

			for (int i = 0; i < 10; i++) {

				buildGrid[x][16 + i][z] = buildList[1];
			}
			buildGrid[newX][19][newZ] = buildList[2];
			buildGrid[newX][19][z] = buildList[2];
			buildGrid[x][19][newZ] = buildList[2];

			buildGrid[newX][22][newZ] = buildList[5];
			buildGrid[newX][22][z] = buildList[5];
			buildGrid[x][22][14] = buildList[5];
			upDownGrid[newX][22][newZ] = 1;
			upDownGrid[newX][22][z] = 1;
			upDownGrid[x][22][14] = 1;

			buildGrid[newX][23][newZ] = buildList[2];
			buildGrid[newX][23][z] = buildList[2];
			buildGrid[x][23][newZ] = buildList[2];
		}
		if (buildGrid[0][16][15] == buildList[0]) {
			int x = 0;
			int newX = 0 + 1;
			int z = 15;
			int newZ = z - 1;

			for (int i = 0; i < 10; i++) {

				buildGrid[x][16 + i][z] = buildList[1];
			}
			buildGrid[newX][19][newZ] = buildList[2];
			buildGrid[newX][19][z] = buildList[2];
			buildGrid[x][19][newZ] = buildList[2];

			buildGrid[newX][22][newZ] = buildList[5];
			buildGrid[newX][22][z] = buildList[5];
			buildGrid[x][22][14] = buildList[5];
			upDownGrid[newX][22][newZ] = 1;
			upDownGrid[newX][22][z] = 1;
			upDownGrid[x][22][14] = 1;

			buildGrid[newX][23][newZ] = buildList[2];
			buildGrid[newX][23][z] = buildList[2];
			buildGrid[x][23][newZ] = buildList[2];
		}
		if (buildGrid[0][16][0] == buildList[0]) {
			int x = 0;
			int newX = x + 1;
			int z = 0;
			int newZ = z + 1;

			for (int i = 0; i < 10; i++) {

				buildGrid[x][16 + i][z] = buildList[1];
			}
			buildGrid[newX][19][newZ] = buildList[2];
			buildGrid[newX][19][z] = buildList[2];
			buildGrid[x][19][newZ] = buildList[2];

			buildGrid[newX][22][newZ] = buildList[5];
			buildGrid[newX][22][z] = buildList[5];
			buildGrid[x][22][14] = buildList[5];
			upDownGrid[newX][22][newZ] = 1;
			upDownGrid[newX][22][z] = 1;
			upDownGrid[x][22][14] = 1;

			buildGrid[newX][23][newZ] = buildList[2];
			buildGrid[newX][23][z] = buildList[2];
			buildGrid[x][23][newZ] = buildList[2];
		}
		placeGrid(loc.getChunk().getBlock(0, 60, 0).getLocation(), BlockFace.EAST);

	}

	private void buildStraightChunk(BlockFace direction, Location loc) {
		Material[] buildList = new Material[] { Material.AIR, Material.STONE_BRICKS, Material.OAK_SLAB,
				Material.STONE_BRICK_STAIRS, Material.IRON_BARS, Material.STONE_BRICK_SLAB, Material.LANTERN };
		createGrids(16, 30, 16);
		for (int l = 0; l < buildGrid[0].length; l++) {
			for (int c = 0; c < buildGrid[0][0].length; c++) {
				for (int r = 0; r < buildGrid.length; r++) {
					insert(0 + r, 0 + l, 0 + c, buildList[0], 0, 0, 0);
				}
			}
		}

		int heightTracker = 0;
		int cCount = 0;
		for (; heightTracker < 19; heightTracker++) {
			if (direction == BlockFace.NORTH || direction == BlockFace.WEST) {
				cCount = buildGrid[0][0].length;
			} else if (direction == BlockFace.SOUTH || direction == BlockFace.EAST) {
				cCount = -1;
			}
			int rCount = 0;
			for (int c = 0; c < buildGrid[0][0].length; c++) {

				if (direction == BlockFace.NORTH || direction == BlockFace.WEST) {
					cCount--;
				} else if (direction == BlockFace.SOUTH || direction == BlockFace.EAST) {
					cCount++;
				}

				if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
					rCount = buildGrid.length;
				} else if (direction == BlockFace.WEST || direction == BlockFace.EAST) {
					rCount = -1;
				}
				for (int r = 0; r < buildGrid.length; r++) {
					if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
						rCount--;
					} else if (direction == BlockFace.WEST || direction == BlockFace.EAST) {
						rCount++;
					}
					straightGrid(r, c, rCount, cCount, heightTracker, 1, buildList, direction);
				}
			}
		}

		cCount = 0;
		if (direction == BlockFace.NORTH || direction == BlockFace.WEST) {
			cCount = buildGrid[0][0].length;
		} else if (direction == BlockFace.SOUTH || direction == BlockFace.EAST) {
			cCount = -1;
		}
		int rCount = 0;
		for (int c = 0; c < buildGrid[0][0].length; c++) {

			if (direction == BlockFace.NORTH || direction == BlockFace.WEST) {
				cCount--;
			} else if (direction == BlockFace.SOUTH || direction == BlockFace.EAST) {
				cCount++;
			}

			if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
				rCount = buildGrid.length;
			} else if (direction == BlockFace.WEST || direction == BlockFace.EAST) {
				rCount = -1;
			}
			for (int r = 0; r < buildGrid.length; r++) {
				if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
					rCount--;
				} else if (direction == BlockFace.WEST || direction == BlockFace.EAST) {
					rCount++;
				}
				straightGrid(r, c, rCount, cCount, heightTracker, 2, buildList, direction);
			}
		}
		for (int i = 0; i < 10; i++) {
			heightTracker++;
			cCount = 0;
			if (direction == BlockFace.NORTH || direction == BlockFace.WEST) {
				cCount = buildGrid[0][0].length;
			} else if (direction == BlockFace.SOUTH || direction == BlockFace.EAST) {
				cCount = -1;
			}
			rCount = 0;
			for (int c = 0; c < buildGrid[0][0].length; c++) {

				if (direction == BlockFace.NORTH || direction == BlockFace.WEST) {
					cCount--;
				} else if (direction == BlockFace.SOUTH || direction == BlockFace.EAST) {
					cCount++;
				}

				if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
					rCount = buildGrid.length;
				} else if (direction == BlockFace.WEST || direction == BlockFace.EAST) {
					rCount = -1;
				}
				for (int r = 0; r < buildGrid.length; r++) {
					if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
						rCount--;
					} else if (direction == BlockFace.WEST || direction == BlockFace.EAST) {
						rCount++;
					}
					straightGrid(r, c, rCount, cCount, heightTracker, 3 + i, buildList, direction);
				}
			}
		}
		if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
			placeGrid(loc.getChunk().getBlock(0, 60, 0).getLocation(), BlockFace.NORTH);
		} else {
			placeGrid(loc.getChunk().getBlock(0, 60, 0).getLocation(), BlockFace.EAST);
		}

	}

	private void createGrids(int width, int height, int depth) {
		buildGrid = new Material[width][height][depth];

		directionGrid = new int[buildGrid.length][buildGrid[0].length][buildGrid[0][0].length];

		upDownGrid = new int[buildGrid.length][buildGrid[0].length][buildGrid[0][0].length];
		cornerGrid = new int[buildGrid.length][buildGrid[0].length][buildGrid[0][0].length];
	}

	private BlockFace getBlockface(int val, BlockFace direction) {
		if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
			if (val == 2) {
				return BlockFace.WEST;
			} else if (val == 1) {
				return BlockFace.NORTH;
			} else if (val == 4) {
				return BlockFace.EAST;
			} else if (val == 3) {
				return BlockFace.SOUTH;
			} else {
				return BlockFace.EAST;
			}
		} else {
			if (val == 1) {
				return BlockFace.WEST;
			} else if (val == 2) {
				return BlockFace.NORTH;
			} else if (val == 3) {
				return BlockFace.EAST;
			} else if (val == 4) {
				return BlockFace.SOUTH;
			} else {
				return BlockFace.EAST;
			}
		}
	}

	private int getStairFace(int east, BlockFace direction) {
		if (direction != BlockFace.EAST) {
			if (east == 1 && (direction == BlockFace.SOUTH || direction == BlockFace.NORTH)) {
				return 3;
			} else if (east == 2 && (direction == BlockFace.WEST || direction == BlockFace.NORTH)) {
				return 4;
			} else if (east == 3 && (direction == BlockFace.SOUTH || direction == BlockFace.NORTH)) {
				return 1;
			} else if (east == 4 && (direction == BlockFace.WEST || direction == BlockFace.NORTH)) {
				return 2;
			}
		}
		return east;
	}

	private void insert(int width, int height, int depth, Material material, int blockface, int updown, int corner) {
		buildGrid[0 + width][0 + height][0 + depth] = material;

		directionGrid[0 + width][0 + height][0 + depth] = blockface;

		upDownGrid[0 + width][0 + height][0 + depth] = updown;

		cornerGrid[0 + width][0 + height][0 + depth] = corner;
	}

	private void notStraightGrid(int r, int c, int rCount, int cCount, int heightTracker, int stage,
			Material[] buildList, BlockFace leftRight, GridType grid) {
		int chunkSize = 16;

		if (stage == 1) {
			if (heightTracker == 0) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
			}

			if ((cCount == 1 && WallThree && rCount > 0 && rCount < 15)
					|| (cCount == 14 && WallFour && rCount > 0 && rCount < 15)
					|| (rCount == 1 && WallTwo && cCount > 0 && cCount < 15)
					|| (rCount == 14 && WallOne && cCount > 0 && cCount < 15)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}

			if (heightTracker < 16) {
				if ((cCount == 0 && WallThree) || (cCount == 15 && WallFour)
						|| (rCount == 0 && WallTwo || (rCount == 15 && WallOne))) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				}
			}

			if (heightTracker == 16) {
				if (cCount == 0 && WallThree) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
				} else if (cCount == 15 && WallFour) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
				} else if (rCount == 0 && WallTwo) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
				} else if (rCount == 15 && WallOne) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
				}
			}
			if ((rCount == 1 || rCount == 14) && cCount == 15 && !WallFour) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if ((rCount == 1 || rCount == 14) && cCount == 0 && !WallThree) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if ((cCount == 1 || cCount == 14) && rCount == 15 && !WallOne) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if ((cCount == 1 || cCount == 14) && rCount == 0 && !WallTwo) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}

		} else if (stage == 2) {
			if (cCount > 1 && cCount < 14 && rCount > 1 && rCount < 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
			}
			if (cCount == 1) {
				if (WallThree) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (cCount == 14) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (rCount == 1) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (rCount == 14) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (cCount == 0) {
				if (WallThree) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						}
					}
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (cCount == 15) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						}
					}
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (rCount == 0) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						}
					}
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (rCount == 15) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						}
					}
				} else if (cCount > 1 && cCount < 14) {

					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
		} else if (stage == 3) {
			if (cCount == 1) {
				if (WallThree) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				}
			}
			if (cCount == 14) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				}
			}
			if (rCount == 1) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				}

			}
			if (rCount == 14) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				}
			}
			if (cCount == 0) {
				if (WallThree) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					} else {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					}
				}
			}
			if (cCount == 15) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					} else {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					}
				}
			}
			if (rCount == 0) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						}
					} else {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						}
					}
				}
			}
			if (rCount == 15) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						}
					} else {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						}
					}
				}
			}
		} else if (stage == 4) {
			if (cCount == 1) {
				if (WallThree) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				}
			}
			if (cCount == 14) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				}
			}
			if (rCount == 1) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				}

			}
			if (rCount == 14) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}
				}
			}
			if (cCount == 0) {
				if (WallThree) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						}
					} else {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						}
					}
				}

			}
			if (cCount == 15) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						}
					} else {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						}
					}
				}

			}
			if (rCount == 0) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						}
					} else {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						}
					}
				}
			}
			if (rCount == 15) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						}
					} else {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						}
					}
				}
			}
		} else if (stage == 5) {
			if (cCount > 1 && cCount < 14 && rCount > 1 && rCount < 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
			}
			if (cCount == 1) {
				if (WallThree) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				}
			}
			if (cCount == 14) {
				if (WallFour) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				}
			}
			if (rCount == 1) {
				if (WallTwo) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				}

			}
			if (rCount == 14) {
				if (WallOne) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				}
			}
			if (cCount == 0) {
				if (WallThree) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					} else {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					}
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				}

			}
			if (cCount == 15) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					} else {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					}
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				}

			}
			if (rCount == 0) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						}
					} else {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						}
					}
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				}

			}
			if (rCount == 15) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						}
					} else {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						}
					}
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				}
			}
		} else if (stage == 6) {
			if (cCount > 1 && cCount < 14 && rCount > 1 && rCount < 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
			}
			if (cCount == 1) {
				if (WallThree) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (cCount == 14) {
				if (WallFour) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (rCount == 1) {
				if (WallTwo) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}

			}
			if (rCount == 14) {
				if (WallOne) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (cCount == 0) {
				if (WallThree) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					}
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}

			}
			if (cCount == 15) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					}
				} else if (rCount > 1 && rCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}

			}
			if (rCount == 0) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						}
					}
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
			if (rCount == 15) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						}
					}
				} else if (cCount > 1 && cCount < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}
		} else if (stage == 7) {
			if (cCount == 0) {
				if (WallThree) {

					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					}
				}
			} else if (cCount == 15) {
				if (WallFour) {
					if (rCount < chunkSize / 2) {
						if (rCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					} else {
						if (rCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					}
				}

			}
			if (rCount == 0) {
				if (WallTwo) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						}
					}
				}

			} else if (rCount == 15) {
				if (WallOne) {
					if (cCount < chunkSize / 2) {
						if (cCount % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						}
					} else {
						if (cCount % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						}
					}
				}

			}
		} else if (stage == 8) {
			if (cCount == 0 && WallThree) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
			} else if (cCount == 15 && WallFour) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
			} else if (rCount == 0 && WallTwo) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
			} else if (rCount == 15 && WallOne) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
			}
		}
	}

	private void placeGrid(Location loc, BlockFace direction) {
		for (int l = 0; l < buildGrid[0].length; l++) {
			for (int c = 0; c < buildGrid[0][0].length; c++) {
				for (int r = 0; r < buildGrid.length; r++) {
					Location temploc = loc.clone();
					loc.getWorld().getBlockAt(temploc.add(0 + r, 0 + l, 0 + c)).setType(buildGrid[r][l][c]);
					if (directionGrid[r][l][c] > 0) {
						BlockData blockData = loc.getWorld().getBlockAt(temploc).getBlockData();
						if (blockData instanceof Directional) {
							((Directional) blockData).setFacing(getBlockface(directionGrid[r][l][c], direction));
							loc.getWorld().getBlockAt(temploc).setBlockData(blockData);
						}
					}
					if (cornerGrid[r][l][c] > 0) {
						BlockData blockData = loc.getWorld().getBlockAt(temploc).getBlockData();
						if (blockData instanceof Stairs) {
							if (cornerGrid[r][l][c] == 1) {
								((Stairs) blockData).setShape(Shape.OUTER_RIGHT);
							} else if (cornerGrid[r][l][c] == 2) {
								((Stairs) blockData).setShape(Shape.OUTER_LEFT);
							} else if (cornerGrid[r][l][c] == 3) {
								((Stairs) blockData).setShape(Shape.INNER_RIGHT);
							} else if (cornerGrid[r][l][c] == 4) {
								((Stairs) blockData).setShape(Shape.INNER_LEFT);
							}
							loc.getWorld().getBlockAt(temploc).setBlockData(blockData);
						}
					}
					if (upDownGrid[r][l][c] > 0) {
						BlockData blockData = loc.getWorld().getBlockAt(temploc).getBlockData();
						if (blockData instanceof Bisected) {
							((Bisected) blockData).setHalf(Half.TOP);
							loc.getWorld().getBlockAt(temploc).setBlockData(blockData);
						} else if (blockData instanceof Slab) {
							((Slab) blockData).setType(Type.TOP);
							loc.getWorld().getBlockAt(temploc).setBlockData(blockData);
						}
					}
				}
			}
		}
	}

	private void straightGrid(int r, int c, int rCount, int cCount, int heightTracker, int stage, Material[] buildList,
			BlockFace leftRight) {
		int chunkSize = 16;
		if (leftRight == BlockFace.NORTH || leftRight == BlockFace.SOUTH) {
			int temp = r;
			r = c;
			c = temp;
		}
		if (stage == 1) {
			if (heightTracker == 0) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
			}
			if (cCount == 1 || cCount == 5 || cCount == 10 || cCount == 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);

			}
			if (heightTracker < 16 && (cCount == 0 || cCount == 15)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
			if (heightTracker == 16 && (cCount == 0 || cCount == 15)) {
				if (cCount == 0) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
				} else if (cCount == 15) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
				}
			}

		} else if (stage == 2) {
			if (cCount > 1 && cCount < 4 || cCount > 6 && cCount < 9 || cCount > 11 && cCount < 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
			} else if ((rCount == 4 || rCount == 5 || rCount == chunkSize - 5 || rCount == chunkSize - 6) && cCount > 1
					&& cCount < 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
			} else if (cCount == 5 || cCount == chunkSize - 6) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if (cCount == 6 || cCount == 11) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
			} else if (cCount == 9 || cCount == 4) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
			} else if (cCount == 1) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				}
			} else if (cCount == 14) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				}
			} else if (cCount == 0) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					}
				} else {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					}
				}

			} else if (cCount == 15) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					}
				} else {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					}
				}
			}
		} else if (stage == 3) {
			if (cCount == 14 || cCount == 1) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				}
			} else if (cCount == 0) {

				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				} else {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				}
			} else if (cCount == 15) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				} else {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				}

			} else if ((cCount == 5 || cCount == 10)) {
				if ((rCount < 3 || rCount > 6 && rCount < chunkSize - 7 || rCount > chunkSize - 4)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (rCount == 3 || rCount == chunkSize - 7) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
				} else if (rCount == 6 || rCount == chunkSize - 4) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
				}
			}
		} else if (stage == 4) {
			if (cCount == 14 || cCount == 1) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				}
			} else if (cCount == 0) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					}
				} else {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					}
				}
			} else if (cCount == 15) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					}
				} else {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					}
				}
			} else if ((cCount == 5 || cCount == 10)) {
				if (rCount == 1 || rCount == 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[4], 0, 0, 0);
				} else if (rCount == 0 || rCount == 2 || rCount == 7 || rCount == 8 || rCount == 13 || rCount == 15) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (rCount == 3 || rCount == 6 || rCount == 9 || rCount == 12) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[6], 0, 0, 0);
				}
			}
		} else if (stage == 5) {
			if (cCount == 1 || cCount == 5 || cCount == 10 || cCount == 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if (cCount == 3 || cCount == 7 || cCount == 8 || cCount == 12) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
			} else if (cCount == 4 || cCount == 9 || cCount == 13) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
			} else if (cCount == 2 || cCount == 6 || cCount == 11) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
			} else if (cCount == 3 || cCount == 6 || cCount == 11) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
			} else if (cCount == 0) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				} else {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				}

			} else if (cCount == 15) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				} else {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				}
			}
		} else if (stage == 6) {
			if (cCount > 1 && cCount < 4 || cCount > 6 && cCount < 9 || cCount > 11 && cCount < 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
			} else if ((rCount == 3 || rCount == 4 || rCount == 5 || rCount == chunkSize - 4 || rCount == chunkSize - 5
					|| rCount == chunkSize - 6) && cCount > 1 && cCount < 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
			} else if (cCount == 5 || cCount == chunkSize - 6) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if (cCount == 6 || cCount == 11) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
			} else if (cCount == 9 || cCount == 4) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
			} else if (cCount == 1) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
			} else if (cCount == 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
			} else if (cCount == 0) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				} else {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				}

			} else if (cCount == 15) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				} else {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				}
			}
		} else if (stage == 7) {
			if (cCount == 0) {

				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				} else {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				}
			} else if (cCount == 15) {
				if (rCount < chunkSize / 2) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				} else {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				}

			} else if ((cCount == 5 || cCount == 10)) {
				if ((rCount < 2 || rCount > 6 && rCount < chunkSize - 7 || rCount > chunkSize - 3)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (rCount == 2 || rCount == chunkSize - 7) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
				} else if (rCount == 6 || rCount == chunkSize - 3) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
				}
			}
		} else if (stage == 8) {
			if (cCount == 0) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
			} else if (cCount == 15) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
			} else if ((cCount == 5 || cCount == 10)) {
				if (rCount == 0 || rCount == 1 || rCount == 7 || rCount == 8 || rCount == 14 || rCount == 15) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				}
			}
		} else if (stage == 9) {

			if (cCount == 0 || cCount == 15) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if ((cCount == 5 || cCount == 10)) {
				if (rCount < 2 || rCount > chunkSize - 3 || (rCount > 6 && rCount < chunkSize - 7)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (rCount == 2 || rCount == chunkSize - 7) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
				} else if (rCount == 6 || rCount == chunkSize - 3) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
				}
			}
		} else if (stage == 10) {

			if (cCount == 0 || cCount == 10) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
			} else if (cCount == 5 || cCount == 15) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
			} else if (cCount == 1 || cCount == 11) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
			} else if (cCount == 4 || cCount == 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
			}
		} else if (stage == 11) {

			if (cCount == 1 || cCount == 12) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
			} else if (cCount == 2 || cCount == 13) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if (cCount == 3 || cCount == 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
			} else if (cCount == 4 || cCount == 11) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
			}
		} else if (stage == 12) {

			if (cCount == 2 || cCount == 13) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
			}
		}
	}
}

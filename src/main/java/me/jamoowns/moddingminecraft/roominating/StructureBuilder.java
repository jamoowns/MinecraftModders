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

public final class StructureBuilder {

	public enum GridType {
		CORNER, T_SECTION, CROSS, DEAD_END, STRAIGHT, STRAIGHTCHUNK
	}

	private Material[][][] buildGrid;

	private int[][][] directionGrid;

	private int[][][] upDownGrid;

	private int[][][] cornerGrid;

	public final void buildGrid(GridType grid, BlockFace direction, Location loc) {
		switch (grid) {
		case CORNER:
		case T_SECTION:
		case CROSS:
		case DEAD_END:
			buildNotStraight(direction, grid, loc);
			break;
		case STRAIGHT:
			buildStraight(direction, loc);
			break;
		case STRAIGHTCHUNK:
			buildStraightChunk(direction, loc);
		}

	}

	private void buildNotStraight(BlockFace direction, GridType grid, Location loc) {
		Material[] buildList = new Material[] { Material.AIR, Material.STONE_BRICKS, Material.OAK_SLAB,
				Material.STONE_BRICK_STAIRS, Material.IRON_BARS, Material.STONE_BRICK_SLAB, Material.LANTERN };
		createGrids(17, 30, 17);

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
					cornerGrid(r, c, rCount, cCount, heightTracker, 1, buildList, grid, direction);
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
				cornerGrid(r, c, rCount, cCount, heightTracker, 2, buildList, grid, direction);
			}
		}
		for (int i = 0; i < 9; i++) {
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
					cornerGrid(r, c, rCount, cCount, heightTracker, 3 + i, buildList, grid, direction);
				}
			}
		}

		placeGrid(loc, BlockFace.EAST);
	}

	private void buildStraight(BlockFace direction, Location loc) {
		Material[] buildList = new Material[] { Material.AIR, Material.STONE_BRICKS, Material.OAK_SLAB,
				Material.STONE_BRICK_STAIRS, Material.IRON_BARS, Material.STONE_BRICK_SLAB, Material.LANTERN };

		if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
			createGrids(16, 30, 10);
		} else {
			createGrids(10, 30, 16);

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

					int newC = 0;
					if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
						newC = r;
					} else {
						newC = c;
					}
					if (newC == 1 || newC == 4 || newC == 11 || newC == 14) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				}
			}
		}
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				int newR = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
					newR = c;
				} else {
					newC = c;
					newR = r;
				}
				if (newC > 1 && newC < 5 || newC > 6 && newC < 9 || newC > 10 && newC < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				} else if ((newR == 4 || newR == 5) && newC > 1 && newC < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				} else if (newC == 5 || newC == 10) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (newC == 6) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
				} else if (newC == 9) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
				} else if (newC == 1) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else if (newC == 14) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else if (newC == 0) {
					if (newR % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					}
				} else if (newC == 15) {
					if (newR % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
					}
				}

			}
		}

		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				int newR = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
					newR = c;
				} else {
					newC = c;
					newR = r;
				}
				if (newC == 14 || newC == 1) {
					if (newR % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else if (newC == 0) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
					}
				} else if (newC == 15) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
					}
				} else if ((newC == 5 || newC == 10)) {
					if (newR < 3 || newR > 6) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if (newR == 3) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
					} else if (newR == 6) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
					}
				}
			}
		}
		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				int newR = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
					newR = c;
				} else {
					newC = c;
					newR = r;
				}
				if (newC == 14 || newC == 1) {
					if (newR % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else if (newC == 0) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					}
				} else if (newC == 15) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
					}
				} else if ((newC == 5 || newC == 10)) {
					if (newR == 1 || newR == 8) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[4], 0, 0, 0);
					} else if (newR == 0 || newR == 2 || newR == 7 || newR == 9) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if (newR == 3 || newR == 6) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[6], 0, 0, 0);
					}
				}
			}
		}

		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				int newR = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
					newR = c;
				} else {
					newC = c;
					newR = r;
				}
				if (newC == 1 || newC == 5 || newC == 10 || newC == 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (newC == 3 || newC == 7 || newC == 8 || newC == 12) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				} else if (newC == 4 || newC == 9 || newC == 13) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
				} else if (newC == 2 || newC == 6 || newC == 11) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
				} else if (newC == 3 || newC == 6 || newC == 11) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
				} else if (newC == 0) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
					}
				} else if (newC == 15) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
					}
				}
			}
		}
		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				int newR = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
					newR = c;
				} else {
					newC = c;
					newR = r;
				}
				if ((newC == 4 || newC == 9) && newR == 3) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 4);
				} else if ((newC == 4 || newC == 9) && newR == 6) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 3);
				} else if ((newC == 6 || newC == 11) && newR == 3) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 1);
				} else if ((newC == 6 || newC == 11) && newR == 6) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 2);
				} else if (newC > 1 && newC < 4 || newC > 6 && newC < 9 || newC > 11 && newC < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				} else if ((newR == 4 || newR == 5) && newC > 1 && newC < 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				} else if (newC == 5 || newC == 10) {
					if (newR == 3) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
					} else if (newR == 6) {

						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
					} else {

						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					}
				} else if (newC == 4 || newC == 9 || newC == 14) {
					{
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
					}
				} else if (newC == 1 || newC == 6 || newC == 11) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
				} else if (newC == 0) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					}
				} else if (newC == 15) {
					if (newR % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
					}
				}

			}
		}
		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				int newR = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
					newR = c;
				} else {
					newC = c;
					newR = r;
				}
				if (newC == 0) {
					if (newR % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
					}
				} else if (newC == 15) {
					if (newR % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
					}
				} else if ((newC == 5 || newC == 10)) {
					if (newR < 2 || newR > 7) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if (newR == 2) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
					} else if (newR == 7) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
					}
				}
			}
		}

		// dede
		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				int newR = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
					newR = c;
				} else {
					newC = c;
					newR = r;
				}
				if (newC == 0) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
				} else if (newC == 15) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
				} else if ((newC == 5 || newC == 10)) {
					if (newR < 1 || newR > 7) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if (newR == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
					} else if (newR == 7) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
					}
				}
			}
		}
		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				int newR = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
					newR = c;
				} else {
					newC = c;
					newR = r;
				}
				if (newC == 0) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (newC == 1) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
				} else if (newC == 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
				} else if (newC == 15) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if ((newC == 5 || newC == 10)) {
					if (newR < 1 || newR > 7) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if (newR == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
					} else if (newR == 7) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 1, 0);
					}
				}
			}
		}

		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
				} else {
					newC = c;
				}
				if (newC == 1 || newC == 10) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
				} else if (newC == 2 || newC == 11) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
				} else if (newC == 3 || newC == 12) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
				} else if (newC == 4 || newC == 13) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
				} else if (newC == 5 || newC == 14) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
				}
			}
		}
		heightTracker++;
		for (int c = 0; c < buildGrid[0][0].length; c++) {
			for (int r = 0; r < buildGrid.length; r++) {
				int newC = 0;
				if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
					newC = r;
				} else {
					newC = c;
				}
				if (newC == 2 || newC == 11) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
				} else if (newC == 3 || newC == 12) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (newC == 4 || newC == 13) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
				}
			}
		}

		placeGrid(loc, direction);
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
		for (int i = 0; i < 9; i++) {
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
		placeGrid(loc.getChunk().getBlock(0, 60, 0).getLocation(), BlockFace.EAST);
	}

	private void cornerGrid(int r, int c, int rCount, int cCount, int heightTracker, int stage, Material[] buildList,
			GridType grid, BlockFace leftRight) {
		if (stage == 1) {
			if (((cCount == 15 || cCount == 1) && rCount < 16 && rCount > 0)
					|| ((rCount == 15 || rCount == 1) && cCount < 16 && cCount > 0)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
			if (((cCount == 1 || cCount == 4 || cCount == 12 || cCount == 15) && rCount < 2)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}

			if (grid != GridType.DEAD_END
					&& ((rCount == 1 || rCount == 4 || rCount == 12 || rCount == 15) && cCount < 2)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
			if ((grid != GridType.DEAD_END && grid == GridType.CROSS || grid == GridType.T_SECTION)
					&& ((rCount == 1 || rCount == 4 || rCount == 12 || rCount == 15) && cCount > 14)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
			if (grid != GridType.DEAD_END && grid == GridType.CROSS
					&& ((cCount == 1 || cCount == 4 || cCount == 12 || cCount == 15) && rCount > 14)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
		} else if (stage == 2) {
			if (rCount == 0 && cCount < 16 && cCount > 0) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					if (cCount % 2 == 1) {
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 1 && cCount < 15 && cCount > 1) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 15 && cCount < 15 && cCount > 1) {
				if (grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 16 && cCount < 16 && cCount > 0) {
				if (grid != GridType.CROSS) {
					if (cCount % 2 == 1) {
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 0 && rCount < 16 && rCount > 0) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 1) {
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 1 && rCount < 15 && rCount > 1) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 15 && rCount < 15 && rCount > 1) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 16 && rCount < 16 && rCount > 0) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 1) {
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if ((rCount > 1 && rCount < 15 && cCount > 1 && cCount < 15)) {
				if ((rCount == 8 && cCount == 8) || (rCount == 5 && cCount == 5) || (rCount == 11 && cCount == 11)
						|| (rCount == 11 && cCount == 5) || (rCount == 5 && cCount == 11)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					if (rCount > 4 && rCount < 12 && cCount > 4 && cCount < 12) {
						if (rCount == 8 && (cCount == 6 || cCount == 7)) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
						} else if (rCount > 8 && rCount < 11 && cCount > 5 && cCount < 11) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						} else if (cCount > 8 && cCount < 11 && rCount > 5 && rCount < 11) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
						}

					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
					}

				}
			}

		} else if (stage == 3 || stage == 7) {
			if (rCount == 0 && cCount < 16 && cCount > 0) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					if (cCount % 2 == 1) {
						if (stage == 3) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
						}
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
					}
				}
			} else if (rCount == 1 && cCount < 15 && cCount > 1) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
					}
				}
			} else if (rCount == 15 && cCount < 15 && cCount > 1) {
				if (grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
					}
				}
			} else if (rCount == 16 && cCount < 16 && cCount > 0) {
				if (grid != GridType.CROSS) {
					if (cCount % 2 == 1) {
						if (stage == 3) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
						}
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
					}
				}
			} else if (cCount == 0 && rCount < 16 && rCount > 0) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 1) {
						if (stage == 3) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
						}

					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				}
			} else if (cCount == 1 && rCount < 15 && rCount > 1) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
					}
				}
			} else if (cCount == 15 && rCount < 15 && rCount > 1) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
					}
				}
			} else if (cCount == 16 && rCount < 16 && rCount > 0) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 1) {
						if (stage == 3) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
						}
					} else {

						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				}
			} else if ((rCount > 1 && rCount < 15 && cCount > 1 && cCount < 15)) {
				if ((rCount == 8 && cCount == 8) || (rCount == 5 && cCount == 5) || (rCount == 11 && cCount == 11)
						|| (rCount == 11 && cCount == 5) || (rCount == 5 && cCount == 11)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					if (rCount > 4 && rCount < 12 && cCount > 4 && cCount < 12) {
						if (cCount == 8 && (rCount == 9 || rCount == 10)) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
						}
						if (cCount > 8 && cCount < 11 && rCount > 5 && rCount < 11) {
							if (stage == 3 && cCount == 9 && rCount == 6) {
								insert(0 + r, 0 + heightTracker, 0 + c, buildList[6], 0, 0, 0);
							} else {
								if (rCount < 9 && stage == 7) {

								} else {
									insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
								}
							}
						}

					}

				}
			}
		} else if (stage == 4 || stage == 8) {
			if (rCount == 0 && cCount < 16 && cCount > 0) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
					} else {
						if (stage == 4) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						}
					}
				}
			} else if (rCount == 1 && cCount < 15 && cCount > 1) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
					}
				}
			} else if (rCount == 15 && cCount < 15 && cCount > 1) {
				if (grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
					}
				}
			} else if (rCount == 16 && cCount < 16 && cCount > 0) {
				if (grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
					} else {
						if (stage == 4) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						}
					}
				}
			} else if (cCount == 0 && rCount < 16 && rCount > 0) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						if (stage == 4) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						}
					}
				}
			} else if (cCount == 1 && rCount < 15 && rCount > 1) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
					}
				}
			} else if (cCount == 15 && rCount < 15 && rCount > 1) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
					}
				}
			} else if (cCount == 16 && rCount < 16 && rCount > 0) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						if (stage == 4) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						}
					}
				}
			} else if ((rCount > 1 && rCount < 15 && cCount > 1 && cCount < 15)) {
				if ((rCount == 8 && cCount == 8) || (rCount == 5 && cCount == 5) || (rCount == 11 && cCount == 11)
						|| (rCount == 11 && cCount == 5) || (rCount == 5 && cCount == 11)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					if (rCount > 4 && rCount < 12 && cCount > 4 && cCount < 12) {
						if (rCount == 8 && (cCount == 9 || cCount == 10)) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
						}
						if (cCount > 8 && cCount < 11 && rCount > 5 && rCount < 8) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					}

				}
			}

		} else if (stage == 5 || stage == 9) {
			if (rCount == 0 && cCount < 16 && cCount > 0) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					if (cCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
					}
				} else if (stage == 5) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
				}
			} else if (rCount == 1 && cCount < 15 && cCount > 1) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (stage == 5) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
				}
			} else if (rCount == 15 && cCount < 15 && cCount > 1) {
				if (grid != GridType.CROSS) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (stage == 5) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
				}
			} else if (rCount == 16 && cCount < 16 && cCount > 0) {
				if (grid != GridType.CROSS) {
					if (cCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
					}
				} else if (stage == 5) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
				}
			} else if (cCount == 0 && rCount < 16 && rCount > 0) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				} else if (stage == 5) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
				}
			} else if (cCount == 1 && rCount < 15 && rCount > 1) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (stage == 5) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
				}
			} else if (cCount == 15 && rCount < 15 && rCount > 1) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else if (stage == 5) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
				}
			} else if (cCount == 16 && rCount < 16 && rCount > 0) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				} else if (stage == 5) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
				}
			} else if ((rCount > 1 && rCount < 15 && cCount > 1 && cCount < 15)) {
				if ((rCount == 8 && cCount == 8) || (rCount == 5 && cCount == 5) || (rCount == 11 && cCount == 11)
						|| (rCount == 11 && cCount == 5) || (rCount == 5 && cCount == 11)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {

					if (rCount == 5 && cCount > 5 && cCount < 11) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
					} else if (rCount == 11 && cCount > 5 && cCount < 11) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
					} else if (cCount == 5 && rCount > 5 && rCount < 11) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else if (cCount == 11 && rCount > 5 && rCount < 11) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else if (cCount == 8 && (rCount == 7 || rCount == 6)) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					} else if (rCount > 8 && rCount < 11 && cCount > 5 && cCount < 11) {
						if (cCount == 6) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
						}
					} else if (cCount > 8 && cCount < 11 && rCount > 5 && rCount < 11) {
					} else if (stage == 5) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 2, 1, 0);
					}

				}
			}

		} else if (stage == 6) {
			if (rCount == 0 && cCount < 16 && cCount > 0) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 1 && cCount < 15 && cCount > 1) {
				if (grid != GridType.DEAD_END && grid != GridType.CORNER && grid != GridType.T_SECTION
						&& grid != GridType.CROSS) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 15 && cCount < 15 && cCount > 1) {
				if (grid != GridType.CROSS) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 16 && cCount < 16 && cCount > 0) {
				if (grid != GridType.CROSS) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 0 && rCount < 16 && rCount > 0) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 1 && rCount < 15 && rCount > 1) {
				if (grid != GridType.CORNER && grid != GridType.T_SECTION && grid != GridType.CROSS) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 15 && rCount < 15 && rCount > 1) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 16 && rCount < 16 && rCount > 0) {
				if (grid != GridType.T_SECTION && grid != GridType.CROSS) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if ((rCount > 1 && rCount < 15 && cCount > 1 && cCount < 15)) {
				if ((rCount == 8 && cCount == 8) || (rCount == 5 && cCount == 5) || (rCount == 11 && cCount == 11)
						|| (rCount == 11 && cCount == 5) || (rCount == 5 && cCount == 11)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					if (rCount > 4 && rCount < 12 && cCount > 4 && cCount < 12) {
						if (rCount == 8 && (cCount == 6 || cCount == 7)) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
						} else if (cCount == 8 && (rCount == 6 || rCount == 7)) {
						} else if (rCount > 8 && rCount < 11 && cCount > 5 && cCount < 11) {
							if (cCount < 9) {
								insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
							} else if (rCount == 10) {
								insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
							}

						} else if (cCount > 8 && cCount < 11 && rCount > 5 && rCount < 11) {
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
						}

					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
					}
				}
			}
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
		if (stage == 1) {
			if (cCount == 1 || cCount == 5 || cCount == 10 || cCount == 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
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
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
			} else if (cCount == 15) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
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

			if (cCount == 0 || cCount == 11) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
			} else if (cCount == 5 || cCount == 15) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
			} else if (cCount == 1 || cCount == 12) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
			} else if (cCount == 4 || cCount == 14) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
			}
		}
	}

}

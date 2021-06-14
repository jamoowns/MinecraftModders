package me.jamoowns.moddingminecraft.roominating;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class LabRoomBuilderListener implements IGameEventListener {

	private List<Block> blocks = new ArrayList<Block>();

	private Location origin = null;

	private int width = 0;

	private int length = 0;

	private int layer = 0;

	public LabRoomBuilderListener() {
		/* Empty. */
	}

	@EventHandler
	public final void aaaonGlassExplode(BlockExplodeEvent e) {
		List<Material> mats = new ArrayList<Material>();
		mats.add(Material.WHITE_STAINED_GLASS);
		mats.add(Material.LIGHT_BLUE_CONCRETE);
		mats.add(Material.GLOWSTONE);
		mats.add(Material.QUARTZ_BLOCK);
		mats.add(Material.QUARTZ_BRICKS);
		mats.add(Material.QUARTZ_PILLAR);
		mats.add(Material.CHISELED_QUARTZ_BLOCK);
		mats.add(Material.SMOOTH_QUARTZ);
		mats.add(Material.IRON_BLOCK);
		mats.add(Material.WHITE_CONCRETE);
		mats.add(Material.RED_STAINED_GLASS);
		mats.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
		mats.add(Material.BLACK_GLAZED_TERRACOTTA);
		e.blockList().removeIf((block) -> mats.contains(block.getType()));
	}

	@EventHandler
	public final void aaaonGlassExplode(EntityExplodeEvent e) {
		List<Material> mats = new ArrayList<Material>();
		mats.add(Material.WHITE_STAINED_GLASS);
		mats.add(Material.LIGHT_BLUE_CONCRETE);
		mats.add(Material.GLOWSTONE);
		mats.add(Material.QUARTZ_BLOCK);
		mats.add(Material.QUARTZ_BRICKS);
		mats.add(Material.QUARTZ_PILLAR);
		mats.add(Material.CHISELED_QUARTZ_BLOCK);
		mats.add(Material.SMOOTH_QUARTZ);
		mats.add(Material.IRON_BLOCK);
		mats.add(Material.WHITE_CONCRETE);
		mats.add(Material.RED_STAINED_GLASS);
		mats.add(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
		mats.add(Material.BLACK_GLAZED_TERRACOTTA);
		e.blockList().removeIf((block) -> mats.contains(block.getType()));
	}

	public final void buildHall(int width, Location origin) {
		width = 16 * width + 8;
		origin = origin.clone().getChunk().getBlock(5, 61, 8).getLocation();
		Location tempLoc = origin.clone();

		for (int x = 0; x < 9; x++) {
			for (int z = 0; z < width + 6 + 2; z++) {
				for (int y = 0; y < 7; y++) {
					tempLoc = origin.clone();
					if ((y == 2 || y == 3 || y == 4) && (x > 1 && x < 7) && z < width + 6) {
						randomBlockPlace(tempLoc.add(x - 4, y - 3, -z), -4);
					} else if (((y == 1 || y == 5) && (x > 1 && x < 7) && z != width + 7)
							|| ((y == 2 || y == 3 || y == 4) && (x == 1 || x == 7) && z != width + 7)
							|| (y == 2 || y == 3 || y == 4) && (x > 1 && x < 7) && z == width + 6) {
						randomBlockPlace(tempLoc.add(x - 4, y - 3, -z), -2);
					} else if (y == 3 && z % 3 == 2) {
						randomBlockPlace(tempLoc.add(x - 4, y - 3, -z), -1);
					} else if ((y == 0 && x == 4 && z < width + 7) || (y == 0 && x == 5 && z == (width + 6 + 2) / 2)
							|| (y == 0 && x == 6 && z == (width + 6 + 2) / 2)) {
						tempLoc.add(x - 4, y - 3, -z).getBlock().setType(Material.RED_STAINED_GLASS);
						tempLoc.add(0, -1, 0).getBlock().setType(Material.GLOWSTONE);
					} else if ((y == 6 && x == 4 && z < width + 7) || (y == 6 && x == 5 && z == (width + 6 + 2) / 2)
							|| (y == 6 && x == 6 && z == (width + 6 + 2) / 2)) {
						tempLoc.add(x - 4, y - 3, -z).getBlock().setType(Material.RED_STAINED_GLASS);
						tempLoc.add(0, 1, 0).getBlock().setType(Material.GLOWSTONE);
					} else {
						randomBlockPlace(tempLoc.add(x - 4, y - 3, -z), 0);
					}
				}
			}
		}
		for (int x = 0; x < 9; x++) {
			for (int z = 0; z < 2; z++) {
				for (int y = 0; y < 7; y++) {
					tempLoc = origin.clone().add(0, 0, 2);
					if ((y > 1 && y < 5) && (x > 1 && x < 7)) {
						randomBlockPlace(tempLoc.add(x - 4, y - 3, -z), -4);
					} else if (((y > 1 && y < 5) && (x == 1 || x == 2 || x == 7 || x == 6))
							|| (y == 1 || y == 5) && (x > 1 && x < 7)) {
						randomBlockPlace(tempLoc.add(x - 4, y - 3, -z), -2);
					} else if ((y == 0 && x == 4 && z < 2)) {
						tempLoc.add(x - 4, y - 3, -z).getBlock().setType(Material.RED_STAINED_GLASS);
						tempLoc.add(0, -1, 0).getBlock().setType(Material.GLOWSTONE);
					}
				}
			}
		}
		tempLoc = origin.clone();
		tempLoc.add(3, 0, -(width + 6 + 2) / 2).getBlock().setType(Material.BLACK_GLAZED_TERRACOTTA);
		tempLoc = origin.clone();
		tempLoc.add(0, 0, -width - 6).getBlock().setType(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
	}

	public final void buildRoom(int length, int width, int height, Location origin) {
		Location loc = origin.clone().getChunk().getBlock(7, 61, 8).getLocation();
		if (width % 2 == 0) {
			loc.add(0, 0, -8);
		}
		length = 16 * length + 8;
		width = 16 * width + 8;
		height = 24 * height + 8;

		startBuild(length, width, height, loc.clone().add(length / 2 + 5, -3, 0));
		endBuild(loc.clone());
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
	public void onRedstoneEvent(BlockRedstoneEvent e) {
		if (!blocks.contains(e.getBlock()))
			return;

		e.setNewCurrent(1);

		blocks.remove(e.getBlock());
	}

	@Override
	public final void onServerStop() {
		/* Empty. */
	}

	private void centerMass(int fill) {
		Location newStartZ = origin.clone().add(-length / 2 + 5, 0, -width / 2);
		Location newStartX = origin.clone().add(-length / 2, 0, -width / 2 + 5);
		int placeOne = 0;
		int placeTwo = -1;
		if (fill == -3) {
			placeOne = fill;
		} else if (fill != 1) {
			placeOne = fill;
			placeTwo = fill;
		}
		for (int x = 0; x < length - 10; x++) {
			for (int z = 0; z < width; z++) {
				Location tempLoc = newStartZ.clone();
				int randType = placeOne;
				if (x % 3 == 2 && z % 3 == 1) {
					randType = placeTwo;
				}
				randomBlockPlace(tempLoc.add(x, 0, z), randType);
			}
		}

		for (int z = 0; z < width - 10; z++) {
			for (int x = 0; x < length; x++) {
				Location tempLoc = newStartX.clone();
				int randType = placeOne;
				if (z % 3 == 2 && x % 3 == 1) {
					randType = placeTwo;
				}
				randomBlockPlace(tempLoc.add(x, 0, z), randType);
			}
		}

		Location tempLoc = newStartZ.clone();
		tempLoc = newStartZ.clone();
		randomBlockPlace(tempLoc.add(-1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		tempLoc = newStartZ.clone();
		randomBlockPlace(tempLoc.add(-1, 0, 2), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		tempLoc = newStartZ.clone();
		randomBlockPlace(tempLoc.add(-1, 0, 3), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		tempLoc = newStartZ.clone();
		randomBlockPlace(tempLoc.add(-1, 0, 4), placeTwo);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);

		tempLoc = newStartZ.clone();
		randomBlockPlace(tempLoc.add(length - 10, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		tempLoc = newStartZ.clone();
		randomBlockPlace(tempLoc.add(length - 10, 0, 2), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		tempLoc = newStartZ.clone();
		randomBlockPlace(tempLoc.add(length - 10, 0, 3), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		tempLoc = newStartZ.clone();
		randomBlockPlace(tempLoc.add(length - 10, 0, 4), placeTwo);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);

		tempLoc = newStartX.clone();
		randomBlockPlace(tempLoc.add(1, 0, width - 6 - 3), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		tempLoc = newStartX.clone();
		randomBlockPlace(tempLoc.add(2, 0, width - 6 - 2), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		tempLoc = newStartX.clone();
		randomBlockPlace(tempLoc.add(3, 0, width - 6 - 1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		tempLoc = newStartX.clone();
		randomBlockPlace(tempLoc.add(4, 0, width - 6 - 1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeTwo);

		tempLoc = newStartX.clone();
		randomBlockPlace(tempLoc.add(length - 1 - 1, 0, width - 6 - 3), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		tempLoc = newStartX.clone();
		randomBlockPlace(tempLoc.add(length - 2 - 1, 0, width - 6 - 2), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		tempLoc = newStartX.clone();
		randomBlockPlace(tempLoc.add(length - 3 - 1, 0, width - 6 - 1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		tempLoc = newStartX.clone();
		randomBlockPlace(tempLoc.add(length - 4 - 1, 0, width - 6 - 1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeTwo);

	}

	private final void endBuild(Location location) {
		for (int x = 0; x < 5; x++) {
			for (int z = 0; z < 7; z++) {
				for (int y = 0; y < 7; y++) {
					Location tempLoc = location.clone();
					if (y == 0 || (y == 1 || (y == 5 && x != 4)) && (z == 0 || z == 6) && x != 0 || y == 6 && x != 4) {
						randomBlockPlace(tempLoc.add(x, y - 3, z - 3), 0);
					} else if ((y == 1 || (y == 2 || y == 3 || y == 4) && (z == 0 || z == 6) && x != 0)
							|| (y == 6 && x == 4) || (y == 5)) {
						randomBlockPlace(tempLoc.add(x, y - 3, z - 3), -2);
					} else {
						randomBlockPlace(tempLoc.add(x, y - 3, z - 3), -4);
					}
				}
			}
		}
		Location tempLoc = location.clone();
		randomBlockPlace(tempLoc.add(3, 3, 3), -3);
		tempLoc = location.clone();
		randomBlockPlace(tempLoc.add(3, 3, -3), -3);
		tempLoc = location.clone();
		randomBlockPlace(tempLoc.add(3, -3, 0), -1);
		tempLoc = location.clone();
		tempLoc.add(0, -3, 0).getBlock().setType(Material.RED_STAINED_GLASS);

		tempLoc = location.clone();
		randomBlockPlace(tempLoc.add(0, 4, 0), -1);
		tempLoc = location.clone();
		tempLoc.add(0, 3, 0).getBlock().setType(Material.RED_STAINED_GLASS);

	}

	private void innerRing(int fill) {
		int placeOne = 0;
		if (fill != 1) {
			placeOne = fill;
		}
		Location tempLoc = origin.clone().add(-length / 2 + 5, 0, -width / 2 - 1);
		for (int x = 0; x < length - 10; x++) {
			randomBlockPlace(tempLoc.clone().add(x, 0, 0), placeOne);
		}
		tempLoc = origin.clone().add(-length / 2 + 5, 0, -width / 2 + width);
		for (int x = 0; x < length - 10; x++) {
			randomBlockPlace(tempLoc.clone().add(x, 0, 0), placeOne);
		}

		tempLoc = origin.clone().add(-length / 2 - 1, 0, -width / 2 + 5);
		for (int x = 0; x < width - 10; x++) {
			randomBlockPlace(tempLoc.clone().add(0, 0, x), placeOne);
		}
		tempLoc = origin.clone().add(-length / 2 + length, 0, -width / 2 + 5);
		for (int x = 0; x < width - 10; x++) {
			randomBlockPlace(tempLoc.clone().add(0, 0, x), placeOne);
		}
		tempLoc = origin.clone().add(-length / 2 + 5 - 1, 0, -width / 2);
		randomBlockPlace(tempLoc, placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, 1), placeOne);

		tempLoc = origin.clone().add(-length / 2 + length - 5, 0, -width / 2);
		randomBlockPlace(tempLoc, placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, 1), placeOne);

		tempLoc = origin.clone().add(-length / 2 + length - 5, 0, -width / 2 + width - 1);
		randomBlockPlace(tempLoc, placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);

		tempLoc = origin.clone().add(-length / 2 + 5 - 1, 0, -width / 2 + width - 1);
		randomBlockPlace(tempLoc, placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
	}

	private void outerRing(int fill) {
		int placeOne = 0;
		if (fill != 1) {
			placeOne = fill;
		}
		Location tempLoc = origin.clone().add(-length / 2 + 5, 0, -width / 2 - 2);
		for (int x = 0; x < length - 10; x++) {

			if (x % 3 == 2 && layer % 3 == 1) {

				randomBlockPlace(tempLoc.clone().add(x, 0, 0), -1);
			} else {

				randomBlockPlace(tempLoc.clone().add(x, 0, 0), placeOne);
			}
		}
		tempLoc = origin.clone().add(-length / 2 + 5, 0, -width / 2 + width + 1);
		for (int x = 0; x < length - 10; x++) {
			if (x % 3 == 2 && layer % 3 == 1) {

				randomBlockPlace(tempLoc.clone().add(x, 0, 0), -1);
			} else {

				randomBlockPlace(tempLoc.clone().add(x, 0, 0), placeOne);
			}
		}

		tempLoc = origin.clone().add(-length / 2 - 2, 0, -width / 2 + 5);
		for (int x = 0; x < width - 10; x++) {
			if (x % 3 == 2 && layer % 3 == 1) {

				randomBlockPlace(tempLoc.clone().add(0, 0, x), -1);
			} else {

				randomBlockPlace(tempLoc.clone().add(0, 0, x), placeOne);
			}
		}
		tempLoc = origin.clone().add(-length / 2 + length + 1, 0, -width / 2 + 5);
		for (int x = 0; x < width - 10; x++) {
			if (x % 3 == 2 && layer % 3 == 1) {

				randomBlockPlace(tempLoc.clone().add(0, 0, x), -1);
			} else {

				randomBlockPlace(tempLoc.clone().add(0, 0, x), placeOne);
			}
		}
		tempLoc = origin.clone().add(-length / 2 + 5 - 1, 0, -width / 2 - 1);
		randomBlockPlace(tempLoc, placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 1), placeOne);

		if (layer % 3 == 1) {
			randomBlockPlace(tempLoc.add(-1, 0, 1), -1);
		} else {
			randomBlockPlace(tempLoc.add(-1, 0, 1), placeOne);
		}
		randomBlockPlace(tempLoc.add(-1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, 1), placeOne);

		tempLoc = origin.clone().add(-length / 2 + length - 5, 0, -width / 2 - 1);
		randomBlockPlace(tempLoc, placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 1), placeOne);

		if (layer % 3 == 1) {
			randomBlockPlace(tempLoc.add(1, 0, 1), -1);
		} else {
			randomBlockPlace(tempLoc.add(1, 0, 1), placeOne);
		}
		randomBlockPlace(tempLoc.add(1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, 1), placeOne);

		tempLoc = origin.clone().add(-length / 2 + length - 5, 0, -width / 2 + width);
		randomBlockPlace(tempLoc, placeOne);
		randomBlockPlace(tempLoc.add(1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, -1), placeOne);

		if (layer % 3 == 1) {
			randomBlockPlace(tempLoc.add(1, 0, -1), -1);
		} else {
			randomBlockPlace(tempLoc.add(1, 0, -1), placeOne);
		}
		randomBlockPlace(tempLoc.add(1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);

		tempLoc = origin.clone().add(-length / 2 + 5 - 1, 0, -width / 2 + width);
		randomBlockPlace(tempLoc, placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, 0), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, -1), placeOne);

		if (layer % 3 == 1) {
			randomBlockPlace(tempLoc.add(-1, 0, -1), -1);
		} else {
			randomBlockPlace(tempLoc.add(-1, 0, -1), placeOne);
		}
		randomBlockPlace(tempLoc.add(-1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(-1, 0, -1), placeOne);
		randomBlockPlace(tempLoc.add(0, 0, -1), placeOne);
	}

	private void randomBlockPlace(Location loc, int result) {
		if (result == -4) {
			loc.getWorld().getBlockAt(loc).setType(Material.AIR);
		} else if (result == -3) {

			loc.getWorld().getBlockAt(loc).setType(Material.LIGHT_BLUE_CONCRETE);

		} else if (result == -2) {

			loc.getWorld().getBlockAt(loc).setType(Material.WHITE_STAINED_GLASS);

		} else if (result == -1) {

			loc.getWorld().getBlockAt(loc).setType(Material.GLOWSTONE);

		} else {
			Random r = new Random();
			int low = 0;
			int high = 9;
			result = r.nextInt(high - low) + low;

			if (result == 0) {
				loc.getBlock().setType(Material.QUARTZ_BLOCK);
			} else if (result == 1) {
				loc.getBlock().setType(Material.QUARTZ_BRICKS);
			} else if (result == 2) {
				loc.getBlock().setType(Material.QUARTZ_PILLAR);
				Material testX = Material.QUARTZ_PILLAR;
				BlockData dataX = testX.createBlockData();
				Orientable orientableX = (Orientable) dataX;
				orientableX.setAxis(Axis.X);
				loc.getBlock().setBlockData(dataX);
			} else if (result == 3) {
				loc.getBlock().setType(Material.QUARTZ_PILLAR);
				Material testY = Material.QUARTZ_PILLAR;
				BlockData dataY = testY.createBlockData();
				Orientable orientableY = (Orientable) dataY;
				orientableY.setAxis(Axis.Y);
				loc.getBlock().setBlockData(dataY);
			} else if (result == 4) {
				loc.getBlock().setType(Material.QUARTZ_PILLAR);
				Material testZ = Material.QUARTZ_PILLAR;
				BlockData dataZ = testZ.createBlockData();
				Orientable orientableZ = (Orientable) dataZ;
				orientableZ.setAxis(Axis.Z);
				loc.getBlock().setBlockData(dataZ);
			} else if (result == 5) {
				loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			} else if (result == 6) {
				loc.getBlock().setType(Material.SMOOTH_QUARTZ);
			} else if (result == 7) {
				loc.getBlock().setType(Material.IRON_BLOCK);
			} else if (result == 8) {
				loc.getBlock().setType(Material.WHITE_CONCRETE);
			}
		}
	}

	private void startBuild(int length, int width, int height, Location origin) {
		this.width = width;
		this.length = length;
		this.origin = origin.clone();
		centerMass(1);
		this.origin = origin.clone();
		this.origin.add(0, 1, 0);
		centerMass(-2);
		innerRing(1);

		for (int x = 0; x < 4; x++) {
			this.origin = origin.clone();
			this.origin.add(0, x + 2, 0);
			layer = x + 2;
			innerRing(-2);
			outerRing(1);
			centerMass(-4);
		}
		for (int x = 3; x < height; x++) {
			this.origin = origin.clone();
			this.origin.add(0, x + 2, 0);
			layer = x + 2;
			innerRing(-2);
			outerRing(-3);
			centerMass(-4);
		}

		this.origin = origin.clone();
		this.origin.add(0, height + 1, 0);
		centerMass(-2);
		innerRing(-3);
		this.origin = origin.clone();
		this.origin.add(0, height + 2, 0);
		centerMass(-3);
	}

}

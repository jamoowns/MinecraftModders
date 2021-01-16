package me.jamoowns.moddingminecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Husk;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.features.Feature;

public class MabListener implements Listener {

	private final ModdingMinecraft javaPlugin;

	private Map<UUID, List<Block>> trailByPlayer;

	private final Random RANDOM;

	private CustomItem multiShotBowItem;

	private CustomItem creeperArrowItem;
	private CustomItem explosiveArrowItem;
	private CustomItem treeArrowItem;
	private CustomItem rotateArrowItem;
	private CustomItem fillArrowItem;

	private CustomItem swapsiesSplashPotionItem;
	private CustomItem medusaSplashPotionItem;

	private CustomItem explosiveSnowBallItem;

	private Material[][][] buildGrid;
	private int[][][] directionGrid;
	private int[][][] upDownGrid;
	private int[][][] cornerGrid;

	Player mabmo;

	boolean mabmoSet;

	public MabListener(ModdingMinecraft aJavaPlugin) {
		RANDOM = new Random();
		javaPlugin = aJavaPlugin;
		trailByPlayer = new HashMap<>();
		setupCustomItems();
	}

	public void BuildGrid(int grid, BlockFace direction, Location loc) {
		if (grid == 2 || grid == 3 || grid == 4 || grid == 5) {
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
						CornerGrid(r, c, rCount, cCount, heightTracker, 1, buildList, grid, direction);
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
					CornerGrid(r, c, rCount, cCount, heightTracker, 2, buildList, grid, direction);
				}
			}
			for (int i = 0; i < 9; i++) {
				sendMabmoMsg("Line");
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
						CornerGrid(r, c, rCount, cCount, heightTracker, 3 + i, buildList, grid, direction);
					}
				}
			}

			placeGrid(loc, BlockFace.EAST);
		}

		if (grid == 1) {
			Material[] buildList = new Material[] { Material.AIR, Material.STONE_BRICKS, Material.OAK_SLAB,
					Material.STONE_BRICK_STAIRS, Material.IRON_BARS, Material.STONE_BRICK_SLAB, Material.LANTERN };

			if (direction == BlockFace.SOUTH || direction == BlockFace.NORTH) {
				createGrids(17, 30, 10);
			} else {
				createGrids(10, 30, 17);

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
						if (newC == 1 || newC == 4 || newC == 12 || newC == 15) {
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
					if (newC > 1 && newC < 5 || newC > 6 && newC < 10 || newC > 11 && newC < 15) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
					} else if ((newR == 4 || newR == 5) && newC > 1 && newC < 15) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
					} else if (newC == 5 || newC == 11) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if (newC == 6) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
					} else if (newC == 10) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
					} else if (newC == 1) {
						if (newR % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else if (newC == 15) {
						if (newR % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else if (newC == 0) {
						if (newR % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						}
					} else if (newC == 16) {
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
					if (newC == 15 || newC == 1) {
						if (newR % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else if (newC == 0) {
						if (newR % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					} else if (newC == 16) {
						if (newR % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
						}
					} else if ((newC == 5 || newC == 11)) {
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
					if (newC == 15 || newC == 1) {
						if (newR % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else if (newC == 0) {
						if (newR % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						}
					} else if (newC == 16) {
						if (newR % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
						}
					} else if ((newC == 5 || newC == 11)) {
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
					if (newC == 1 || newC == 5 || newC == 11 || newC == 15) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if (newC == 3 || newC == 7 || newC == 8 || newC == 9 || newC == 13) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
					} else if (newC == 4 || newC == 10 || newC == 14) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					} else if (newC == 2 || newC == 6 || newC == 12) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
					} else if (newC == 3 || newC == 6 || newC == 12) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
					} else if (newC == 0) {
						if (newR % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					} else if (newC == 16) {
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
					if ((newC == 4 || newC == 10) && newR == 3) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 4);
					} else if ((newC == 4 || newC == 10) && newR == 6) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 3);
					} else if ((newC == 6 || newC == 12) && newR == 3) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 1);
					} else if ((newC == 6 || newC == 12) && newR == 6) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 2);
					} else if (newC > 1 && newC < 4 || newC > 6 && newC < 10 || newC > 12 && newC < 15) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
					} else if ((newR == 4 || newR == 5) && newC > 1 && newC < 15) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
					} else if (newC == 5 || newC == 11) {
						if (newR == 3) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						} else if (newR == 6) {

							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 3, 0, 0);
						} else {

							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						}
					} else if (newC == 4 || newC == 10 || newC == 15) {
						{
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					} else if (newC == 1 || newC == 6 || newC == 12) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 0, 0);
					} else if (newC == 0) {
						if (newR % 2 == 0) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						} else {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
						}
					} else if (newC == 16) {
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
					} else if (newC == 16) {
						if (newR % 2 == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
						}
					} else if ((newC == 5 || newC == 11)) {
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
					} else if (newC == 16) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					} else if ((newC == 5 || newC == 11)) {
						if (newR < 1 || newR > 8) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						} else if (newR == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 0, 0);
						} else if (newR == 8) {
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
					} else if (newC == 15) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					} else if (newC == 16) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if ((newC == 5 || newC == 11)) {
						if (newR < 1 || newR > 8) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
						} else if (newR == 1) {
							insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 1, 1, 0);
						} else if (newR == 8) {
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
					if (newC == 1 || newC == 11) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 0, 0);
					} else if (newC == 2 || newC == 12) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 2, 1, 0);
					} else if (newC == 3 || newC == 13) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 1, 0);
					} else if (newC == 4 || newC == 14) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], 4, 1, 0);
					} else if (newC == 5 || newC == 15) {
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
					if (newC == 2 || newC == 12) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
					} else if (newC == 3 || newC == 13) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else if (newC == 4 || newC == 14) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[5], 0, 0, 0);
					}
				}
			}

			placeGrid(loc, direction);

		}

	}

	public final void cleanup() {
		trailByPlayer.values().forEach(blocks -> {
			blocks.forEach(block -> {
				if (block.getType().name().contains("CARPET")) {
					block.setType(Material.AIR);
				}
			});
		});
		trailByPlayer.clear();
	}

	public void CornerGrid(int r, int c, int rCount, int cCount, int heightTracker, int stage, Material[] buildList,
			int openings, BlockFace leftRight) {

		if (stage == 1) {
			if (((cCount == 15 || cCount == 1) && rCount < 16 && rCount > 0)
					|| ((rCount == 15 || rCount == 1) && cCount < 16 && cCount > 0)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
			if (((cCount == 1 || cCount == 4 || cCount == 12 || cCount == 15) && rCount < 2)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}

			if (openings != 5 && ((rCount == 1 || rCount == 4 || rCount == 12 || rCount == 15) && cCount < 2)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
			if ((openings != 5 && openings == 4 || openings == 3)
					&& ((rCount == 1 || rCount == 4 || rCount == 12 || rCount == 15) && cCount > 14)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
			if (openings != 5 && openings == 4
					&& ((cCount == 1 || cCount == 4 || cCount == 12 || cCount == 15) && rCount > 14)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			}
		}
		if (stage == 2) {
			if (rCount == 0 && cCount < 16 && cCount > 0) {
				if (openings != 5 && openings != 2 && openings != 3 && openings != 4) {
					if (cCount % 2 == 1) {
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 1, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 1 && cCount < 15 && cCount > 1) {
				if (openings != 5 && openings != 2 && openings != 3 && openings != 4) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 15 && cCount < 15 && cCount > 1) {
				if (openings != 4) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (rCount == 16 && cCount < 16 && cCount > 0) {
				if (openings != 4) {
					if (cCount % 2 == 1) {
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 0 && rCount < 16 && rCount > 0) {
				if (openings != 2 && openings != 3 && openings != 4) {
					if (rCount % 2 == 1) {
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 1 && rCount < 15 && rCount > 1) {
				if (openings != 2 && openings != 3 && openings != 4) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 15 && rCount < 15 && rCount > 1) {
				if (openings != 3 && openings != 4) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if (cCount == 16 && rCount < 16 && rCount > 0) {
				if (openings != 3 && openings != 4) {
					if (rCount % 2 == 1) {
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					}
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 2, 0, 0);
				}
			} else if ((rCount == 15 && cCount == 15) || (rCount == 1 && cCount == 15) || (rCount == 15 && cCount == 1)
					|| (rCount == 1 && cCount == 1)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if ((rCount > 1 && rCount < 15 && cCount > 1 && cCount < 15)) {
				if ((rCount == 5 && cCount == 5) || (rCount == 11 && cCount == 11) || (rCount == 11 && cCount == 5)
						|| (rCount == 5 && cCount == 11)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				} else {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[2], 0, 0, 0);
				}
			}

		}
		if (stage == 3) {
			if (rCount == 0 && cCount < 16 && cCount > 0) {
				if (openings != 5 && openings != 2 && openings != 3 && openings != 4) {
					if (cCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
					}
				}
			} else if (rCount == 1 && cCount < 15 && cCount > 1) {
				if (openings != 5 && openings != 2 && openings != 3 && openings != 4) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
					}
				}
			} else if (rCount == 15 && cCount < 15 && cCount > 1) {
				if (openings != 4) {
					if (cCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 0, 0);
					}
				}
			} else if (rCount == 16 && cCount < 16 && cCount > 0) {
				if (openings != 4) {
					if (cCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(3, leftRight), 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(1, leftRight), 1, 0);
					}
				}
			} else if (cCount == 0 && rCount < 16 && rCount > 0) {
				if (openings != 2 && openings != 3 && openings != 4) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 1, 0);
					}
				}
			} else if (cCount == 1 && rCount < 15 && rCount > 1) {
				if (openings != 2 && openings != 3 && openings != 4) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					}
				}
			} else if (cCount == 15 && rCount < 15 && rCount > 1) {
				if (openings != 3 && openings != 4) {
					if (rCount % 2 == 0) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 0, 0);
					}
				}
			} else if (cCount == 16 && rCount < 16 && rCount > 0) {
				if (openings != 3 && openings != 4) {
					if (rCount % 2 == 1) {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(4, leftRight), 0, 0);
					} else {
						insert(0 + r, 0 + heightTracker, 0 + c, buildList[3], getStairFace(2, leftRight), 1, 0);
					}
				}
			} else if ((rCount == 15 && cCount == 15) || (rCount == 1 && cCount == 15) || (rCount == 15 && cCount == 1)
					|| (rCount == 1 && cCount == 1)) {
				insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
			} else if ((rCount > 1 && rCount < 15 && cCount > 1 && cCount < 15)) {
				if ((rCount == 5 && cCount == 5) || (rCount == 11 && cCount == 11) || (rCount == 11 && cCount == 5)
						|| (rCount == 5 && cCount == 11)) {
					insert(0 + r, 0 + heightTracker, 0 + c, buildList[1], 0, 0, 0);
				}
			}
		}
	}

	public void createGrids(int width, int height, int depth) {
		buildGrid = new Material[width][height][depth];

		directionGrid = new int[buildGrid.length][buildGrid[0].length][buildGrid[0][0].length];

		upDownGrid = new int[buildGrid.length][buildGrid[0].length][buildGrid[0][0].length];
		cornerGrid = new int[buildGrid.length][buildGrid[0].length][buildGrid[0][0].length];
	}

	public BlockFace getBlockface(int val, BlockFace direction) {
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

	public void GetReady(World world) {
		List<Player> PlayerArr = new ArrayList<Player>();

		for (Player players : world.getPlayers()) {
			PlayerArr.add(players);
		}
		sendMabmoMsg("" + PlayerArr.size());
		if (PlayerArr.size() > 0) {
			for (Player player : PlayerArr) {
				player.sendTitle("Get Ready", "", 40, 40, 40);
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (PlayerArr.size() > 0) {
					for (Player player : PlayerArr) {
						player.sendTitle("Get Set", "", 40, 40, 40);
					}
				}
			}
		}, 40);
		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (PlayerArr.size() > 0) {
					for (Player player : PlayerArr) {
						player.sendTitle("Go!", "", 40, 40, 40);
					}
				}
			}
		}, 80);
		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (PlayerArr.size() > 0) {
					for (Player player : PlayerArr) {
						player.sendTitle("", "", 40, 40, 40);
					}
				}
			}
		}, 120);
	}

	public int getStairFace(int east, BlockFace direction) {
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

	public void insert(int width, int height, int depth, Material material, int blockface, int updown, int corner) {
		buildGrid[0 + width][0 + height][0 + depth] = material;

		directionGrid[0 + width][0 + height][0 + depth] = blockface;

		upDownGrid[0 + width][0 + height][0 + depth] = updown;

		cornerGrid[0 + width][0 + height][0 + depth] = corner;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if (event.getBlock().getType().equals(Material.WARPED_HYPHAE) && mabmoSet) {
			BuildGrid(1, linearFace(event.getPlayer().getLocation().getYaw()), event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.CRIMSON_HYPHAE) && mabmoSet) {
			event.getPlayer().sendMessage("Building 2");
			BuildGrid(2, linearFace(event.getPlayer().getLocation().getYaw()), event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.WET_SPONGE) && mabmoSet) {
			event.getPlayer().sendMessage("Building 3");
			BuildGrid(3, linearFace(event.getPlayer().getLocation().getYaw()), event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.CHISELED_SANDSTONE) && mabmoSet) {
			event.getPlayer().sendMessage("Building 4");
			BuildGrid(4, linearFace(event.getPlayer().getLocation().getYaw()), event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.PODZOL) && mabmoSet) {
			event.getPlayer().sendMessage("Building 5");
			BuildGrid(5, linearFace(event.getPlayer().getLocation().getYaw()), event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.HAY_BLOCK)) {
			for (Entity ent : event.getPlayer().getNearbyEntities(5.0D, 4.0D, 5.0D)) {
				if (ent instanceof Villager) {
					if (ent.getName().contains("Bob")) {

						event.getPlayer().getServer()
								.broadcastMessage("'Can you get this Basalt polished for me'-" + ent.getName());
						Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
							@Override
							public void run() {
								event.getBlockPlaced().setType(Material.BASALT);
							}
						}, 10);
					}
				}
			}
		}
		if (event.getBlock().getType().equals(Material.BASALT)) {
			for (Entity ent : event.getPlayer().getNearbyEntities(5.0D, 4.0D, 5.0D)) {
				if (ent instanceof Villager) {
					if (ent.getName().contains("Sam")) {

						event.getPlayer().getServer()
								.broadcastMessage("'I can polish that for you right now'-" + ent.getName());
						Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {

							@Override
							public void run() {
								event.getBlockPlaced().setType(Material.POLISHED_BASALT);
							}
						}, 10);
					}
				}
			}
		}

		if (event.getBlock().getType().equals(Material.POLISHED_BASALT)) {
			for (

			Entity ent : event.getPlayer().getNearbyEntities(5.0D, 4.0D, 5.0D)) {
				if (ent instanceof Villager) {
					if (ent.getName().contains("Bob")) {

						event.getPlayer().getServer().broadcastMessage(
								"'thanks for getting it polished it for me, here's a target to practice your skills'-"
										+ ent.getName());
						Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
							@Override
							public void run() {
								event.getBlockPlaced().setType(Material.TARGET);
							}
						}, 10);
					}
				}
			}
		}

		if (event.getBlock().getType().equals(Material.TARGET)) {
			for (Entity ent : event.getPlayer().getNearbyEntities(5.0D, 4.0D, 5.0D)) {
				if (ent instanceof Villager) {
					if (ent.getName().contains("Finch")) {

						event.getPlayer().getServer().broadcastMessage(
								"'Just what I needed, here's something to dry your clothes on'-" + ent.getName());
						Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
							@Override
							public void run() {
								event.getBlockPlaced().setType(Material.SCAFFOLDING);
							}
						}, 10);
					}
				}
			}
		}

		if (event.getBlock().getType().equals(Material.SCAFFOLDING)) {
			for (Entity ent : event.getPlayer().getNearbyEntities(5.0D, 4.0D, 5.0D)) {
				if (ent instanceof Villager) {
					if (ent.getName().contains("Fred")) {

						event.getPlayer().getServer().broadcastMessage(
								"'Sweet now i can dry my wet pants, have some wheat to make bread'-" + ent.getName());
						Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
							@Override
							public void run() {
								event.getBlockPlaced().setType(Material.HAY_BLOCK);
							}
						}, 10);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if (javaPlugin.featureTracker.isFeatureActive(javaPlugin, Feature.FUNKY_MOB_DEATH)) {
			if (event.getEntity() instanceof Sheep) {
				Sheep sheepEnt = (Sheep) event.getEntity();
				Player mcPlayer = sheepEnt.getKiller();
				if (mcPlayer == null)
					return;

				if (sheepEnt.isAdult()) {
					mcPlayer.sendTitle("You're about to die", "20 secs to live after this", 40, 40, 40);
					Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < 3; i++) {
								Creeper crepper = mcPlayer.getLocation().getWorld().spawn(mcPlayer.getLocation(),
										Creeper.class);
								if (mcPlayer != null) {
									crepper.setTarget(mcPlayer);
								}
							}
						}
					}, 180);
				}
			}
			if (event.getEntity() instanceof Chicken) {
				Chicken chickenEnt = (Chicken) event.getEntity();
				Player mcPlayer = chickenEnt.getKiller();
				if (mcPlayer == null)
					return;

				if (chickenEnt.isAdult()) {
					Location loc = mcPlayer.getLocation();
					mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0, -1, 0)).setType(Material.AIR);
					mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0, -1, 0)).setType(Material.AIR);
					mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0, -1, 0)).setType(Material.AIR);
					mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0, -1, 0)).setType(Material.AIR);
					mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0, -1, 0)).setType(Material.AIR);
					mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0, -1, 0)).setType(Material.STONE);
					Random r = new Random();
					int low = 1;
					int high = 11;
					int result = r.nextInt(high - low) + low;
					if (result > 6) {
						mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0, 15, 0)).setType(Material.ANVIL);
					}
					mcPlayer.teleport(mcPlayer.getLocation().add(0, -5, 0));
					event.getDrops().add(new ItemStack(Material.ENDER_PEARL, 3));
				}
			}
			if (event.getEntity() instanceof Bee) {
				Bee BeeEnt = (Bee) event.getEntity();
				Player mcPlayer = BeeEnt.getKiller();
				if (mcPlayer == null)
					return;
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Ghast.class);
				mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
				event.getEntity().getLocation().getWorld().setTime(13000);
				mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 180, 1));
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Phantom.class);
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Phantom.class);
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Phantom.class);
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Phantom.class);
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Phantom.class);
			}
			if (event.getEntity() instanceof Ghast) {
				Ghast ghastEnt = (Ghast) event.getEntity();
				Player mcPlayer = ghastEnt.getKiller();
				if (mcPlayer == null)
					return;
				ItemStack item = new ItemStack(Material.TIPPED_ARROW, 16);
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
				item.setItemMeta(meta);
				ItemStack itemb = new ItemStack(Material.TIPPED_ARROW, 16);
				PotionMeta metab = (PotionMeta) itemb.getItemMeta();
				metab.setBasePotionData(new PotionData(PotionType.INVISIBILITY));
				itemb.setItemMeta(metab);
				event.getDrops().add(item);
				event.getDrops().add(itemb);
				event.getEntity().getLocation().getWorld().setTime(1000);
			}
			if (event.getEntity() instanceof Phantom) {
				Phantom phantomEnt = (Phantom) event.getEntity();
				Player mcPlayer = phantomEnt.getKiller();
				if (mcPlayer == null)
					return;
				event.getDrops().add(new ItemStack(Material.BLAZE_POWDER, 1));
			}
			if (event.getEntity() instanceof Cow) {
				Cow cowEnt = (Cow) event.getEntity();
				Player mcPlayer = cowEnt.getKiller();
				if (mcPlayer == null)
					return;

				if (cowEnt.isAdult()) {
					mcPlayer.teleport(mcPlayer.getLocation().add(0, 5, 0));
				}
			}
			if (event.getEntity() instanceof Pig) {
				Pig pigEnt = (Pig) event.getEntity();
				Player mcPlayer = pigEnt.getKiller();
				if (mcPlayer == null)
					return;

				if (pigEnt.isAdult()) {
					event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Ravager.class);
				}
			}
			if (event.getEntity() instanceof Villager) {
				Villager villagerEnt = (Villager) event.getEntity();
				Player mcPlayer = villagerEnt.getKiller();
				if (mcPlayer == null)
					return;

				if (villagerEnt.isAdult()) {
					Random r = new Random();
					int low = 3;
					int high = 10;
					int result = r.nextInt(high - low) + low;
					for (int i = 0; i < result; i++) {
						IronGolem ironGolem = event.getEntity().getLocation().getWorld()
								.spawn(event.getEntity().getLocation(), IronGolem.class);
						ironGolem.setTarget(mcPlayer);
					}
				}
			}
			if (event.getEntity() instanceof Zombie) {
				Zombie zombieEnt = (Zombie) event.getEntity();
				Player mcPlayer = zombieEnt.getKiller();
				if (mcPlayer == null)
					return;

				if (zombieEnt.isAdult()) {
					Random r = new Random();
					int low = 3;
					int high = 10;
					int result = r.nextInt(high - low) + low;
					for (int i = 0; i < result; i++) {
						Zombie babushka = event.getEntity().getLocation().getWorld()
								.spawn(event.getEntity().getLocation(), Zombie.class);
						babushka.setBaby();
						babushka.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
								babushka.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 1 / result);
						babushka.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
								babushka.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 1 / result);
					}
				}
			}
			if (event.getEntity() instanceof Skeleton) {
				Skeleton skeletonEnt = (Skeleton) event.getEntity();
				Player mcPlayer = skeletonEnt.getKiller();
				if (mcPlayer == null)
					return;
				for (int i = 0; i < 3; i++) {
					event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation().add(0, 1, 0),
							Bat.class);
				}
				mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
				mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 180, 1));

				ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);

				if (RANDOM.nextInt(4) == 1) {
					PotionMeta meta = (PotionMeta) arrow.getItemMeta();
					meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
					arrow.setItemMeta(meta);
					event.getDrops().add(arrow);
					// event.getEntity().getLocation().getWorld().dropItem(event.getEntity().getLocation().add(0,1,0),
					// item)
				}
			}
			if (event.getEntity() instanceof Creeper) {
				Creeper creeperEnt = (Creeper) event.getEntity();
				Player mcPlayer = creeperEnt.getKiller();
				if (mcPlayer == null)
					return;
				Location loc = event.getEntity().getLocation().add(0, -2, 0);
				event.getEntity().getWorld().getBlockAt(loc).setType(Material.AIR);
				TNTPrimed tnt = event.getEntity().getLocation().getWorld()
						.spawn(event.getEntity().getLocation().add(0, 1, 0), TNTPrimed.class);
				tnt.setFuseTicks(520);
				Random r = new Random();
				int low = 0;
				int high = 10;
				int result = r.nextInt(high - low) + low;
				int yield = 0;
				if (result > 7) {
					yield = 1;
				}
				tnt.setYield(yield);
			}
			if (event.getEntity() instanceof Spider) {
				Spider spiderEnt = (Spider) event.getEntity();
				Player mcPlayer = spiderEnt.getKiller();
				if (mcPlayer == null)
					return;
				for (int i = 0; i < 7; i++) {
					for (int j = 0; j < 7; j++) {
						Location loc = event.getEntity().getLocation().add(-3 + i, 0, -3 + j);
						if (event.getEntity().getWorld().getBlockAt(loc).isEmpty()
								&& !event.getEntity().getWorld().getBlockAt(loc).isLiquid()) {
							if ((j == 0 || j == 6) && i % 3 == 0) {
								event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
							} else if ((j == 1 || j == 5) && i % 2 == 1) {
								event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
							} else if ((j == 2 || j == 4) && (i == 2 || i == 3 || i == 4)) {
								event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
							} else if (j == 3) {
								event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
							}
						}
					}
				}
				for (int k = 0; k < 2; k++) {
					int neg = 1;
					if (k > 0) {
						neg = -1;
					}
					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							Location loc = event.getEntity().getLocation().add(-1 + i, 1 * neg, -1 + j);
							if (event.getEntity().getWorld().getBlockAt(loc).isEmpty()
									&& !event.getEntity().getWorld().getBlockAt(loc).isLiquid()) {
								if ((j == 0 || j == 2) && i % 2 == 0) {
									event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
								} else if (j == 1 && i == 1) {
									event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
								}
							}
						}
					}
					Location loc = event.getEntity().getLocation().add(0, 2 * neg, 0);
					if (event.getEntity().getWorld().getBlockAt(loc).isEmpty()
							&& !event.getEntity().getWorld().getBlockAt(loc).isLiquid()) {
						event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (javaPlugin.featureTracker.isFeatureActive(javaPlugin, Feature.IRON_GOLEM)) {
			Player playerEnt = event.getEntity();
			Random r = new Random();
			int low = 0;
			int high = 3;
			int result = r.nextInt(high - low) + low;
			for (int i = 0; i < result; i++) {
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), IronGolem.class);
				if (playerEnt.getKiller() instanceof Player) {
					if (playerEnt != playerEnt.getKiller())
						event.getEntity().getLocation().getWorld().spawnEntity(playerEnt.getKiller().getLocation(),
								EntityType.FIREWORK);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerItemConsumeEvent e) {
		// get all the relative values for comparation
		final Player p = e.getPlayer();
		if (e.getItem().getType().equals(Material.SPIDER_EYE)) {
			// schedule a task to see if they have eaten the cookie(maybe the time could be
			// a little faster idk)
			Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
				@Override
				public void run() {
					p.setFallDistance(-20);
					p.teleport(p.getLocation().add(0, 20, 0));
				}
			}, 100L);

		}
		Location playerLoc = p.getLocation();
		Location playerLoc2 = p.getLocation();
		playerLoc2.setY(900);
		if (playerLoc.add(0, 2, 0).getBlock().getType().equals(Material.AIR)) {
			if (e.getItem().getType().equals(Material.COOKED_SALMON)) {
				Husk deadPlayer = p.getLocation().getWorld().spawn(p.getLocation(), Husk.class);
				deadPlayer.getEquipment().setArmorContents(p.getEquipment().getArmorContents());
				deadPlayer.getEquipment().setItemInMainHand(p.getEquipment().getItemInMainHand());
				deadPlayer.getEquipment().setItemInOffHand(p.getEquipment().getItemInOffHand());
				ItemStack is = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
				deadPlayer.getEquipment().setHelmet(is);

				p.teleport(playerLoc2);
				Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {

					@Override
					public void run() {
						p.setFallDistance(0);
						p.teleport(playerLoc.add(0, -2, 0));
					}

				}, 230);
			}
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (javaPlugin.featureTracker.isFeatureActive(javaPlugin, Feature.WINFRED)) {
			for (Entity ent : event.getPlayer().getNearbyEntities(5.0D, 4.0D, 5.0D)) {
				if (ent instanceof Witch) {
					if (ent.getName().contains("Winfred")) {
						event.getPlayer().sendMessage("'Get Back'-" + ent.getName());
						event.getPlayer().getWorld().strikeLightningEffect(event.getPlayer().getLocation());
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
						event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 180, 1));
						Random r = new Random();
						int low = 1;
						int high = 19;
						int resulty = r.nextInt(high - low) + low;

						int resultx = r.nextInt(high - low) + low;

						event.getPlayer().teleport(ent.getLocation().add(resultx - 9, 5, resulty - 9));

						if (((Witch) ent).getHealth() < ((Witch) ent).getAttribute(Attribute.GENERIC_MAX_HEALTH)
								.getDefaultValue() * .75) {
							SwitchAllPlayers(event.getPlayer().getWorld());
						}
					}
				}
			}
		}

		if (javaPlugin.featureTracker.isFeatureActive(javaPlugin, Feature.PLAYER_TRAIL)) {
			Location belowPlayer = event.getPlayer().getLocation().add(0, -1, 0);

			if (!belowPlayer.getBlock().isEmpty() && !belowPlayer.getBlock().isLiquid()) {

				BlockFace facing = event.getPlayer().getFacing();
				int x = 0;
				int z = 0;
				if (facing == BlockFace.SOUTH) {
					x = 0;
					z = -1;
				} else if (facing == BlockFace.NORTH) {
					x = 0;
					z = 1;
				} else if (facing == BlockFace.EAST) {
					x = -1;
					z = 0;
				} else if (facing == BlockFace.WEST) {
					x = 1;
					z = 0;
				}

				Location behindPlayer = event.getPlayer().getLocation().add(x, 0, z);
				Location belowBehindPlayer = event.getPlayer().getLocation().add(x, -1, z);
				Block blockBehindPlayer = event.getPlayer().getWorld().getBlockAt(behindPlayer);
				Block blockBelowBehindPlayer = event.getPlayer().getWorld().getBlockAt(belowBehindPlayer);

				if (blockBehindPlayer.isEmpty() && !blockBehindPlayer.isLiquid()
						&& !blockBelowBehindPlayer.getType().name().contains("CARPET")
						&& blockBelowBehindPlayer.getType() != Material.GRASS
						&& !blockBelowBehindPlayer.getType().name().contains("SNOW")
						&& !blockBelowBehindPlayer.isLiquid() && !blockBelowBehindPlayer.isEmpty()) {
					List<Block> trail = trailByPlayer.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());
					trail.add(event.getPlayer().getWorld().getBlockAt(behindPlayer));
					if (trail.size() > 100) {
						Block first = trail.remove(0);
						if (first.getType().name().contains("CARPET")) {
							first.setType(Material.AIR);
						}
					}
					trailByPlayer.put(event.getPlayer().getUniqueId(), trail);

					event.getPlayer().getWorld().getBlockAt(behindPlayer).setType(
							javaPlugin.teams().getTeam(event.getPlayer().getUniqueId()).getTeamColour().getTrail());
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		List<Block> trail = trailByPlayer.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());
		trail.forEach(block -> {
			if (block.getType().name().contains("CARPET")) {
				block.setType(Material.AIR);
			}
		});
		trailByPlayer.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThunderChange(ThunderChangeEvent event) {
		boolean storm = event.toThunderState();
		if (storm) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWeatherChange(WeatherChangeEvent event) {
		boolean rain = event.toWeatherState();
		if (rain) {
			event.setCancelled(true);
		}
	}

	public void placeGrid(Location loc, BlockFace direction) {

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

	@EventHandler
	public void PlayerChatEvent(PlayerChatEvent event) {
		if (event.getMessage().contains("Winfred the Weak")) {
			Witch witch = event.getPlayer().getLocation().getWorld().spawn(event.getPlayer().getLocation(),
					Witch.class);
			witch.getWorld().strikeLightningEffect(witch.getLocation());
			witch.setCustomName("Winfred the Witch");
			witch.setFallDistance(-400);
			for (int i = 0; i < 3; i++) {
				witch.getLocation().getWorld().spawn(witch.getLocation().add(0, 1, 0), Bat.class);
			}
			for (Entity players : witch.getNearbyEntities(20.0D, 20.0D, 20.0D)) {
				if (players instanceof Player) {
					players.sendMessage("'Get away from me'-" + witch.getName());
				}
			}
		}
		if (event.getMessage().contains("Switch")) {
			SwitchAllPlayers(event.getPlayer().getWorld());

		}

		if (event.getMessage().contains("SwapNearMe")) {
			SwitchAllPlayersInAnArea(event.getPlayer().getLocation(), 20, 5, 20);
		}
		if (event.getMessage().contains("Ok let's do this")) {
			GetReady(event.getPlayer().getLocation().getWorld());
		}
		if (event.getMessage().contains("Iammabmo")) {
			mabmo = event.getPlayer();
			mabmoSet = true;
		}
		if (event.getMessage().contains("Iamnotmabmo")) {
			mabmoSet = false;
		}
	}

	public void PotionAllPlayersInAnArea(Location loc, int x, int y, int z, PotionEffectType potionEffectType,
			int duration, int amplifier) {
		for (Entity players : loc.getWorld().getNearbyEntities(loc, x, y, z)) {
			if (players instanceof Player) {
				((LivingEntity) players).addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier));
			}
		}
	}

	public Material[][] RotateShapeSquareGrid(Material[][] shape, int rotate) {
		if (rotate == 90) {
			Material[][] newShape = new Material[shape[0].length][shape.length];

			for (int r = 0; r < shape.length; r++) {
				for (int c = 0; c < shape[0].length; c++) {
					int newR = newShape[0].length - r - 1;
					int newC = newShape.length - c - 1;
					newShape[newC][newR] = shape[r][c];
				}
			}

			return newShape;
		} else if (rotate == 180) {
			Material[][] newShape = new Material[shape[0].length][shape[1].length];

			for (int r = 0; r < shape.length; r++) {
				for (int c = 0; c < shape[0].length; c++) {
					int newR = newShape.length - r - 1;
					int newC = newShape[0].length - c - 1;
					newShape[newR][newC] = shape[r][c];
				}
			}
			return newShape;
		} else if (rotate == 270) {
			return RotateShapeSquareGrid(RotateShapeSquareGrid(shape, 90), 180);
		}

		return shape;
	}

	public void sendMabmoMsg(String str) {
		if (mabmoSet) {
			if (mabmo.isOnline()) {
				mabmo.sendMessage(str);
			}
		}
	}

	public void SwitchAllPlayers(World world) {
		List<Player> PlayerArr = new ArrayList<Player>();

		PlayerArr = world.getPlayers();

		if (PlayerArr.size() > 1) {
			int count = 0;
			Location firstloc = PlayerArr.get(0).getLocation();
			for (Player player : PlayerArr) {
				if (count == PlayerArr.size() - 1) {
					player.teleport(firstloc);
				} else {
					player.teleport(PlayerArr.get(count + 1).getLocation());
				}
				count++;
			}
		}
	}

	public void SwitchAllPlayersInAnArea(Location loc, int x, int y, int z) {
		List<Player> PlayerArr = new ArrayList<Player>();

		for (Entity players : loc.getWorld().getNearbyEntities(loc, x, y, z)) {
			if (players instanceof Player) {
				PlayerArr.add((Player) players);
			}
		}

		if (PlayerArr.size() > 1) {
			int count = 0;
			Location firstloc = PlayerArr.get(0).getLocation();
			for (Player player : PlayerArr) {
				if (count == PlayerArr.size() - 1) {
					player.teleport(firstloc);
				} else {
					player.teleport(PlayerArr.get(count + 1).getLocation());
				}
				count++;
			}
		}
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

	private void setupCustomItems() {
		multiShotBowItem = new CustomItem("MultiShot Bow", Material.CROSSBOW);
		ItemMeta meta = multiShotBowItem.asItem().getItemMeta();
		meta.addEnchant(Enchantment.MULTISHOT, 1, true);
		multiShotBowItem.asItem().setItemMeta(meta);
		javaPlugin.customItems().register(multiShotBowItem);

		creeperArrowItem = new CustomItem("Creeper Arrow", Material.ARROW);
		creeperArrowItem.setProjectileHitEvent(event -> {
			int result = RANDOM.nextInt(4) + 1;
			for (int i = 0; i < result; i++) {
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Creeper.class);
			}
		});
		javaPlugin.customItems().register(creeperArrowItem);

		explosiveArrowItem = new CustomItem("Explosive Arrow", Material.ARROW);
		explosiveArrowItem.setProjectileHitEvent(event -> {
			event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation(), 5.0F);
		});
		javaPlugin.customItems().register(explosiveArrowItem);

		treeArrowItem = new CustomItem("Tree Arrow", Material.ARROW);
		treeArrowItem.setProjectileHitEvent(event -> {
			Location loc = event.getEntity().getLocation();
			if (!event.getEntity().getLocation().getWorld().getBlockAt(loc.add(0, -1, 0)).getType().name()
					.contains("LEAVES")) {
				event.getEntity().getLocation().getWorld().getBlockAt(loc).setType(Material.DIRT);
				event.getEntity().getLocation().getWorld().generateTree(event.getEntity().getLocation(), TreeType.TREE);
				event.getEntity().remove();
			}
		});
		javaPlugin.customItems().register(treeArrowItem);

		rotateArrowItem = new CustomItem("Rotate Arrow", Material.ARROW);
		rotateArrowItem.setProjectileHitEvent(event -> {
			Random r = new Random();
			int low = 1;
			int high = 4;
			int result = r.nextInt(high - low) + low;
			for (int i = 0; i < 10; i++) {
				Material[][] multi = new Material[21][21];

				for (int j = 0; j < 21; j++) {
					for (int k = 0; k < 21; k++) {
						Location loc = event.getEntity().getLocation();
						loc.add(k - 10, i, j - 10);
						if (!event.getEntity().getLocation().getWorld().getBlockAt(loc).getType().name()
								.contains("WATER") || loc.getY() < 63) {
							multi[j][k] = event.getEntity().getLocation().getWorld().getBlockAt(loc).getType();
						} else {
							multi[j][k] = Material.AIR;
						}
					}
				}

				multi = RotateShapeSquareGrid(multi, 90 * result);

				for (int j = 0; j < 21; j++) {
					for (int k = 0; k < 21; k++) {
						Location loc = event.getEntity().getLocation();
						loc.add(k - 10, i, j - 10);
						if (!event.getEntity().getLocation().getWorld().getBlockAt(loc).getType().name()
								.contains("WATER")
								&& !event.getEntity().getLocation().getWorld().getBlockAt(loc).getType().name()
										.contains("LAVA")
								|| loc.getY() < 63) {
							event.getEntity().getLocation().getWorld().getBlockAt(loc).setType(multi[j][k]);
						} else {
							event.getEntity().getLocation().getWorld().getBlockAt(loc).setType(Material.AIR);
						}

					}
				}
			}

			event.getEntity().remove();
		});
		javaPlugin.customItems().register(rotateArrowItem);

		fillArrowItem = new CustomItem("Fill Arrow", Material.ARROW);
		fillArrowItem.setProjectileHitEvent(event -> {
			for (int i = 0; i < 9; i++) {
				if (event.getEntity().getLocation().getY() + i < 63) {
					for (int j = 0; j < 9; j++) {
						for (int k = 0; k < 9; k++) {
							Location loc = event.getEntity().getLocation();
							loc.add(k - 4, i, j - 4);
							if (loc.getY() < 59) {
								event.getEntity().getLocation().getWorld().getBlockAt(loc).setType(Material.STONE);
							} else {
								event.getEntity().getLocation().getWorld().getBlockAt(loc).setType(Material.DIRT);
							}
						}
					}
				}
			}
			event.getEntity().remove();
		});
		javaPlugin.customItems().register(fillArrowItem);

		swapsiesSplashPotionItem = new CustomItem("Swapsies When Dropsies", Material.SPLASH_POTION);
		swapsiesSplashPotionItem.setPotionSplashEvent(event -> {
			SwitchAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20);
		});
		swapsiesSplashPotionItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(4));
		});
		javaPlugin.customItems().register(swapsiesSplashPotionItem);

		medusaSplashPotionItem = new CustomItem("Tears of Medusa", Material.SPLASH_POTION);
		medusaSplashPotionItem.setPotionSplashEvent(event -> {
			PotionAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20, PotionEffectType.SLOW, 100, 200);
			PotionAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20, PotionEffectType.JUMP, 100, 100000);
		});
		medusaSplashPotionItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(3));
		});
		javaPlugin.customItems().register(medusaSplashPotionItem);

		explosiveSnowBallItem = new CustomItem("Ice Creep", Material.SNOWBALL);
		explosiveSnowBallItem.setProjectileHitEvent(event -> {
			event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation(), 5.0F);
		});
		explosiveSnowBallItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(8));
		});

		javaPlugin.customItems().register(explosiveSnowBallItem);
	}
}

package me.jamoowns.moddingminecraft.customitems;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.EndGateway;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.extras.Spells;
import me.jamoowns.moddingminecraft.roominating.BuildingFoundations;
import me.jamoowns.moddingminecraft.roominating.LabRoomBuilder;
import me.jamoowns.moddingminecraft.roominating.StructureBuilder;
import me.jamoowns.moddingminecraft.roominating.StructureBuilder.GridType;

public class CustomItemList {
	private static final Integer CHUNK_STICK_RANGE = 100;

	@EventHandler
	public static void onPortalTravel(PlayerPortalEvent event) {
		event.setTo(event.getPlayer().getLocation().add(0, 20, 0));
	}

	private final Random RANDOM;

	private CustomItem portalInputItem;
	private CustomItem portalOutputItem;

	private CustomItem straightChunkItem;

	private CustomItem towerChunkItem;

	private CustomItem deadEndChunkItem;

	private CustomItem cornerChunkItem;

	private CustomItem tSectionChunkItem;

	private CustomItem crossChunkItem;

	private CustomItem multiShotBowItem;

	private CustomItem creeperArrowItem;

	private CustomItem explosiveArrowItem;

	private CustomItem treeArrowItem;

	private CustomItem rotateArrowItem;

	private CustomItem fillArrowItem;

	private CustomItem swapsiesSplashPotionItem;

	private CustomItem medusaSplashPotionItem;

	private CustomItem explosiveSnowBallItem;

	private CustomItem RoomKeydim1x1x1Item;

	private CustomItem RoomKeydim2x1x1Item;

	private CustomItem RoomKeydim1x2x1Item;

	private CustomItem RoomKeydim2x2x1Item;

	private CustomItem RoomKeydim3x1x1Item;

	private CustomItem RoomKeydim1x3x1Item;

	private CustomItem RoomKeydim3x3x1Item;

	private CustomItem HallKeydim1Item;

	private CustomItem HallKeydim2Item;

	private CustomItem HallKeydim3Item;

	private final StructureBuilder structureBuilder;
	private final ModdingMinecraft javaPlugin;

	private final LabRoomBuilder labRoomBuilder;
	Location Input;
	boolean InputSet;
	Location Output;

	boolean OutputSet;

	public CustomItemList(ModdingMinecraft aJavaPlugin) {
		RANDOM = new Random();
		javaPlugin = aJavaPlugin;
		setupCustomItems();
		structureBuilder = new StructureBuilder();
		labRoomBuilder = new LabRoomBuilder();
	}

	@EventHandler
	public final void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (InputSet && OutputSet) {
			if ((int) event.getPlayer().getLocation().getX() == (int) Input.getX()
					&& (int) event.getPlayer().getLocation().getY() == (int) Input.getY()
					&& (int) event.getPlayer().getLocation().getZ() == (int) Input.getZ()) {
				Location Newloc = event.getPlayer().getLocation();
				if (event.getPlayer().getLocation().getBlock().getType().equals(Material.END_GATEWAY)
						&& Newloc.add(0, 1, 0).getBlock().getType().equals(Material.END_GATEWAY)) {
					Location loc = new Location(Output.getWorld(), (int) Output.getX(), (int) Output.getY(),
							(int) Output.getZ(), event.getPlayer().getLocation().getYaw(),
							event.getPlayer().getLocation().getPitch());
					if (loc.add(0, 0, 1).getBlock().getType().equals(Material.END_GATEWAY)
							&& loc.clone().add(0, 0, 1).getBlock().getType().equals(Material.AIR)
							&& loc.clone().add(0, 1, 1).getBlock().getType().equals(Material.AIR)) {

						if (loc.getYaw() > 90 && loc.getYaw() < 270 || loc.getYaw() < -90 && loc.getYaw() > -270) {
							loc.setYaw(loc.getYaw() + 180);
						}

						event.getPlayer().teleport(loc);
					} else if (loc.add(0, 0, -3).getBlock().getType().equals(Material.END_GATEWAY)
							&& loc.clone().add(0, 0, -1).getBlock().getType().equals(Material.AIR)
							&& loc.clone().add(0, 1, 1).getBlock().getType().equals(Material.AIR)) {
						if (loc.getYaw() < 90 && loc.getYaw() > 270 || loc.getYaw() > -90 && loc.getYaw() < -270) {
							loc.setYaw(loc.getYaw() + 180);
						}
						event.getPlayer().teleport(loc);
					}
				} else {
					Input.getBlock().setType(Material.AIR);
					Newloc.getBlock().setType(Material.AIR);
					InputSet = false;
					Location Otherloc = Output;
					Output.getBlock().setType(Material.AIR);
					Otherloc.add(0, 1, 0).getBlock().setType(Material.AIR);
					OutputSet = false;
				}

			} else if ((int) event.getPlayer().getLocation().getX() == (int) Output.getX()
					&& (int) event.getPlayer().getLocation().getY() == (int) Output.getY()
					&& (int) event.getPlayer().getLocation().getZ() == (int) Output.getZ()) {
				Location Newloc = event.getPlayer().getLocation();
				if (event.getPlayer().getLocation().getBlock().getType().equals(Material.END_GATEWAY)
						&& Newloc.add(0, 1, 0).getBlock().getType().equals(Material.END_GATEWAY)) {
					Location loc = new Location(Input.getWorld(), (int) Input.getX(), (int) Input.getY(),
							(int) Input.getZ(), event.getPlayer().getLocation().getYaw(),
							event.getPlayer().getLocation().getPitch());
					if (loc.add(0, 0, 1).getBlock().getType().equals(Material.END_GATEWAY)
							&& loc.clone().add(0, 0, 1).getBlock().getType().equals(Material.AIR)
							&& loc.clone().add(0, 1, 1).getBlock().getType().equals(Material.AIR)) {
						if (loc.getYaw() > 90 && loc.getYaw() < 270 || loc.getYaw() < -90 && loc.getYaw() > -270) {
							loc.setYaw(loc.getYaw() + 180);
						}
						event.getPlayer().teleport(loc);
					} else if (loc.add(0, 0, -2).getBlock().getType().equals(Material.END_GATEWAY)
							&& loc.clone().add(0, 0, -1).getBlock().getType().equals(Material.AIR)
							&& loc.clone().add(0, 1, 1).getBlock().getType().equals(Material.AIR)) {
						if (loc.getYaw() < 90 && loc.getYaw() > 270 || loc.getYaw() > -90 && loc.getYaw() < -270) {
							loc.setYaw(loc.getYaw() + 180);
						}
						event.getPlayer().teleport(loc);
					}
				} else {
					event.getPlayer().getLocation().getBlock().setType(Material.AIR);
					Newloc.getBlock().setType(Material.AIR);
					OutputSet = false;
					Location Otherloc = Input;
					Input.getBlock().setType(Material.AIR);
					Otherloc.add(0, 1, 0).getBlock().setType(Material.AIR);
					InputSet = false;
				}

			}
		}
	}

	public void setupCustomItems() {
		portalInputItem = new CustomItem("Portal Input", Material.WARPED_DOOR, ItemCategory.MISC);
		portalInputItem.setBlockPlaceEvent(event -> {

			Block bl = event.getBlockPlaced();
			InputSet = true;
			Input = bl.getLocation();
			Location loc = bl.getLocation();
			loc.getBlock().setType(Material.END_GATEWAY);
			EndGateway eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc = bl.getLocation();
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);

			loc = bl.getLocation();
			loc.add(0, 0, 1);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc = bl.getLocation();
			loc.add(0, -1, 1);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);

			loc = bl.getLocation();
			loc.add(0, 0, -1);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc = bl.getLocation();
			loc.add(0, -1, -1);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);

			loc = bl.getLocation();
			loc.add(0, -1, 1);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);

			loc = bl.getLocation();
			loc.add(0, -1, -1);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
		});
		javaPlugin.customItems().register(portalInputItem);

		portalOutputItem = new CustomItem("Portal Output", Material.CRIMSON_DOOR, ItemCategory.MISC);
		portalOutputItem.setBlockPlaceEvent(event -> {

			Block bl = event.getBlockPlaced();
			OutputSet = true;
			Output = bl.getLocation();
			Location loc = bl.getLocation();
			loc.getBlock().setType(Material.END_GATEWAY);
			EndGateway eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc = bl.getLocation();
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);

			loc = bl.getLocation();
			loc.add(0, 0, 1);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc = bl.getLocation();
			loc.add(0, -1, 1);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);

			loc = bl.getLocation();
			loc.add(0, 0, -1);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.END_GATEWAY);
			eG = (EndGateway) loc.getBlock().getState();
			eG.setAge(Long.MIN_VALUE);
			eG.update(true);
			loc = bl.getLocation();
			loc.add(0, -1, -1);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, 1, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);
			loc.add(0, -1, 0);
			loc.getBlock().setType(Material.QUARTZ_PILLAR);

			loc = bl.getLocation();
			loc.add(0, -1, 1);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);

			loc = bl.getLocation();
			loc.add(0, -1, -1);
			loc.getBlock().setType(Material.CHISELED_QUARTZ_BLOCK);
			loc.add(1, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
			loc.add(-2, 0, 0);
			loc.getBlock().setType(Material.QUARTZ_BLOCK);
		});
		javaPlugin.customItems().register(portalOutputItem);

		straightChunkItem = new CustomItem("Straight Chunk", Material.RED_SANDSTONE_WALL, ItemCategory.BUILDING);
		straightChunkItem.setBlockPlaceEvent(event -> {
			structureBuilder.buildGrid(GridType.STRAIGHT,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		});
		straightChunkItem.setSpellCastEvent(event -> {
			structureBuilder.buildGrid(GridType.STRAIGHT,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getLocation());
		}, CHUNK_STICK_RANGE);
		javaPlugin.customItems().register(straightChunkItem);

		towerChunkItem = new CustomItem("Tower Chunk", Material.END_STONE_BRICK_WALL, ItemCategory.BUILDING);
		towerChunkItem.setBlockPlaceEvent(event -> {
			structureBuilder.buildGrid(GridType.TOWER,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		});
		towerChunkItem.setSpellCastEvent(event -> {
			structureBuilder.buildGrid(GridType.TOWER,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getLocation());
		}, CHUNK_STICK_RANGE);
		javaPlugin.customItems().register(towerChunkItem);

		cornerChunkItem = new CustomItem("Corner Chunk", Material.BRICK_WALL, ItemCategory.BUILDING);
		cornerChunkItem.setBlockPlaceEvent(event -> {
			structureBuilder.buildGrid(GridType.CORNER,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		});
		cornerChunkItem.setSpellCastEvent(event -> {
			structureBuilder.buildGrid(GridType.CORNER,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getLocation());
		}, CHUNK_STICK_RANGE);
		javaPlugin.customItems().register(cornerChunkItem);

		tSectionChunkItem = new CustomItem("T Section Chunk", Material.NETHER_BRICK_WALL, ItemCategory.BUILDING);
		tSectionChunkItem.setBlockPlaceEvent(event -> {
			structureBuilder.buildGrid(GridType.T_SECTION,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		});
		tSectionChunkItem.setSpellCastEvent(event -> {
			structureBuilder.buildGrid(GridType.T_SECTION,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getLocation());
		}, CHUNK_STICK_RANGE);
		javaPlugin.customItems().register(tSectionChunkItem);

		crossChunkItem = new CustomItem("Cross Chunk", Material.DIORITE_WALL, ItemCategory.BUILDING);
		crossChunkItem.setBlockPlaceEvent(event -> {
			structureBuilder.buildGrid(GridType.CROSS,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		});
		crossChunkItem.setSpellCastEvent(event -> {
			structureBuilder.buildGrid(GridType.CROSS,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getLocation());
		}, CHUNK_STICK_RANGE);
		javaPlugin.customItems().register(crossChunkItem);

		deadEndChunkItem = new CustomItem("Dead End Chunk", Material.PRISMARINE_WALL, ItemCategory.BUILDING);
		deadEndChunkItem.setSpellCastEvent(event -> {
			structureBuilder.buildGrid(GridType.DEAD_END,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getLocation());
		}, CHUNK_STICK_RANGE);
		javaPlugin.customItems().register(deadEndChunkItem);

		multiShotBowItem = new CustomItem("MultiShot Bow", Material.CROSSBOW);
		ItemMeta meta = multiShotBowItem.asItem().getItemMeta();
		meta.addEnchant(Enchantment.MULTISHOT, 1, true);
		multiShotBowItem.asItem().setItemMeta(meta);
		javaPlugin.customItems().register(multiShotBowItem);

		creeperArrowItem = new CustomItem("Creeper Arrow", Material.ARROW, ItemCategory.ARROWS);
		creeperArrowItem.setProjectileHitEvent(event -> {
			int result = RANDOM.nextInt(4) + 1;
			for (int i = 0; i < result; i++) {
				event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Creeper.class);
			}
		});
		javaPlugin.customItems().register(creeperArrowItem);

		explosiveArrowItem = new CustomItem("Explosive Arrow", Material.ARROW, ItemCategory.ARROWS);
		explosiveArrowItem.setProjectileHitEvent(event -> {
			event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation(), 5.0F);
		});
		javaPlugin.customItems().register(explosiveArrowItem);

		treeArrowItem = new CustomItem("Tree Arrow", Material.ARROW, ItemCategory.ARROWS);
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

		rotateArrowItem = new CustomItem("Rotate Arrow", Material.ARROW, ItemCategory.ARROWS);
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

				multi = rotateShapeSquareGrid(multi, 90 * result);

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

		fillArrowItem = new CustomItem("Fill Arrow", Material.ARROW, ItemCategory.ARROWS);
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

		swapsiesSplashPotionItem = new CustomItem("Swapsies When Dropsies", Material.SPLASH_POTION, ItemCategory.MISC);
		swapsiesSplashPotionItem.setPotionSplashEvent(event -> {
			Spells.switchAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20);
		});
		swapsiesSplashPotionItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(4));
		});
		javaPlugin.customItems().register(swapsiesSplashPotionItem);

		medusaSplashPotionItem = new CustomItem("Tears of Medusa", Material.SPLASH_POTION, ItemCategory.MISC);
		medusaSplashPotionItem.setPotionSplashEvent(event -> {
			Spells.potionAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20, PotionEffectType.SLOW, 100,
					200);
			Spells.potionAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20, PotionEffectType.JUMP, 100,
					100000);
		});
		medusaSplashPotionItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(3));
		});
		javaPlugin.customItems().register(medusaSplashPotionItem);

		explosiveSnowBallItem = new CustomItem("Ice Creep", Material.SNOWBALL, ItemCategory.MISC);
		explosiveSnowBallItem.setProjectileHitEvent(event -> {
			event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation(), 5.0F);
		});
		explosiveSnowBallItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(8));
		});

		javaPlugin.customItems().register(explosiveSnowBallItem);

		RoomKeydim1x1x1Item = new CustomItem("Room Key 1x1x1", Material.LEVER, ItemCategory.ROOMKEYS);
		RoomKeydim1x1x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildRoom(1, 1, 1, event.getBlock().getLocation());
			}
		});
		RoomKeydim1x2x1Item = new CustomItem("Room Key 1x2x1", Material.LEVER, ItemCategory.ROOMKEYS);
		RoomKeydim1x2x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildRoom(1, 2, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(RoomKeydim1x2x1Item);

		RoomKeydim2x1x1Item = new CustomItem("Room Key 2x1x1", Material.LEVER, ItemCategory.ROOMKEYS);
		RoomKeydim2x1x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildRoom(2, 1, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(RoomKeydim2x1x1Item);

		RoomKeydim2x2x1Item = new CustomItem("Room Key 2x2x1", Material.LEVER, ItemCategory.ROOMKEYS);
		RoomKeydim2x2x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildRoom(2, 2, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(RoomKeydim2x2x1Item);

		RoomKeydim3x1x1Item = new CustomItem("Room Key 3x1x1", Material.LEVER, ItemCategory.ROOMKEYS);
		RoomKeydim3x1x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildRoom(3, 1, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(RoomKeydim3x1x1Item);

		RoomKeydim1x3x1Item = new CustomItem("Room Key 1x3x1", Material.LEVER, ItemCategory.ROOMKEYS);
		RoomKeydim1x3x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildRoom(1, 3, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(RoomKeydim1x3x1Item);

		RoomKeydim3x3x1Item = new CustomItem("Room Key 3x3x1", Material.LEVER, ItemCategory.ROOMKEYS);
		RoomKeydim3x3x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildRoom(3, 3, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(RoomKeydim3x3x1Item);

		HallKeydim1Item = new CustomItem("Hall Key 1", Material.LEVER, ItemCategory.HALLKEYS);
		HallKeydim1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(0, 0, -1).getBlock().getType()
					.equals(Material.LIGHT_BLUE_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildHall(1, event.getBlock().getLocation().add(0, 0, -1));
			}
		});

		javaPlugin.customItems().register(HallKeydim1Item);

		HallKeydim2Item = new CustomItem("Hall Key 2", Material.LEVER, ItemCategory.HALLKEYS);
		HallKeydim2Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(0, 0, -1).getBlock().getType()
					.equals(Material.LIGHT_BLUE_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildHall(2, event.getBlock().getLocation().add(0, 0, -1));
			}
		});

		javaPlugin.customItems().register(HallKeydim2Item);

		HallKeydim3Item = new CustomItem("Hall Key 3", Material.LEVER, ItemCategory.HALLKEYS);
		HallKeydim3Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(0, 0, -1).getBlock().getType()
					.equals(Material.LIGHT_BLUE_GLAZED_TERRACOTTA)) {
				labRoomBuilder.BuildHall(3, event.getBlock().getLocation().add(0, 0, -1));
			}
		});

		javaPlugin.customItems().register(HallKeydim3Item);
	}

	private Material[][] rotateShapeSquareGrid(Material[][] shape, int rotate) {
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
			return rotateShapeSquareGrid(rotateShapeSquareGrid(shape, 90), 180);
		}

		return shape;
	}
}

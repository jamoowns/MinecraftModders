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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.extras.Spells;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.roominating.BuildingFoundations;
import me.jamoowns.moddingminecraft.roominating.LabRoomBuilderListener;
import me.jamoowns.moddingminecraft.roominating.StructureBuilderListener;
import me.jamoowns.moddingminecraft.roominating.StructureBuilderListener.GridType;

public final class CustomItemListListener implements IGameEventListener {

	private static final Integer CHUNK_STICK_RANGE = 100;

	private static final Random RANDOM = new Random();

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

	private CustomItem explosiveMultiSnowBallItem;
	private CustomItem explosiveBigSnowBallItem;

	private CustomItem roomKeydim1x1x1Item;

	private CustomItem roomKeydim2x1x1Item;

	private CustomItem roomKeydim1x2x1Item;

	private CustomItem roomKeydim2x2x1Item;

	private CustomItem roomKeydim3x1x1Item;

	private CustomItem roomKeydim1x3x1Item;

	private CustomItem roomKeydim3x3x1Item;

	private CustomItem hallKeydim1Item;

	private CustomItem hallKeydim2Item;

	private CustomItem hallKeydim3Item;

	private final StructureBuilderListener structureBuilder;

	private final ModdingMinecraft javaPlugin;

	private final LabRoomBuilderListener labRoomBuilder;

	private Location input;

	private boolean inputSet;

	private Location output;

	private boolean outputSet;

	public CustomItemListListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		structureBuilder = new StructureBuilderListener();
		labRoomBuilder = new LabRoomBuilderListener();
		setupCustomItems();
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
	public final void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (inputSet && outputSet) {
			if ((int) event.getPlayer().getLocation().getX() == (int) input.getX()
					&& (int) event.getPlayer().getLocation().getY() == (int) input.getY()
					&& (int) event.getPlayer().getLocation().getZ() == (int) input.getZ()) {
				Location Newloc = event.getPlayer().getLocation();
				if (event.getPlayer().getLocation().getBlock().getType().equals(Material.END_GATEWAY)
						&& Newloc.add(0, 1, 0).getBlock().getType().equals(Material.END_GATEWAY)) {
					Location loc = new Location(output.getWorld(), (int) output.getX(), (int) output.getY(),
							(int) output.getZ(), event.getPlayer().getLocation().getYaw(),
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
					input.getBlock().setType(Material.AIR);
					Newloc.getBlock().setType(Material.AIR);
					inputSet = false;
					Location Otherloc = output;
					output.getBlock().setType(Material.AIR);
					Otherloc.add(0, 1, 0).getBlock().setType(Material.AIR);
					outputSet = false;
				}

			} else if ((int) event.getPlayer().getLocation().getX() == (int) output.getX()
					&& (int) event.getPlayer().getLocation().getY() == (int) output.getY()
					&& (int) event.getPlayer().getLocation().getZ() == (int) output.getZ()) {
				Location Newloc = event.getPlayer().getLocation();
				if (event.getPlayer().getLocation().getBlock().getType().equals(Material.END_GATEWAY)
						&& Newloc.add(0, 1, 0).getBlock().getType().equals(Material.END_GATEWAY)) {
					Location loc = new Location(input.getWorld(), (int) input.getX(), (int) input.getY(),
							(int) input.getZ(), event.getPlayer().getLocation().getYaw(),
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
					outputSet = false;
					Location Otherloc = input;
					input.getBlock().setType(Material.AIR);
					Otherloc.add(0, 1, 0).getBlock().setType(Material.AIR);
					inputSet = false;
				}

			}
		}
	}

	@EventHandler
	public final void onPortalTravel(PlayerPortalEvent event) {
		event.setTo(event.getPlayer().getLocation().add(0, 20, 0));
	}

	@Override
	public final void onServerStop() {
		/* Empty. */
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

	private void setupCustomItems() {
		portalInputItem = new CustomItem("Portal Input", Material.WARPED_DOOR, ItemCategory.MISC);
		portalInputItem.setBlockPlaceEvent(event -> {
			Block bl = event.getBlockPlaced();
			inputSet = true;
			input = bl.getLocation();
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
			outputSet = true;
			output = bl.getLocation();
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

		ItemStack crossBow = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = crossBow.getItemMeta();
		meta.addEnchant(Enchantment.MULTISHOT, 5, true);
		crossBow.setItemMeta(meta);
		multiShotBowItem = new CustomItem("MultiShot Bow", crossBow);
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

		explosiveMultiSnowBallItem = new CustomItem("Multi Ice Creep", Material.SNOWBALL, ItemCategory.MISC);
		explosiveMultiSnowBallItem.setProjectileHitEvent(event -> {

			event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation(), 15.0F);
			for (int i = 0; i < 19; i++) {
				event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation()
						.add(-5 + RANDOM.nextInt(10), -5 + RANDOM.nextInt(10), -5 + RANDOM.nextInt(10)), 15.0F);
			}

		});
		explosiveMultiSnowBallItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(8));
		});

		javaPlugin.customItems().register(explosiveMultiSnowBallItem);

		explosiveBigSnowBallItem = new CustomItem("Big Ice Creep", Material.SNOWBALL, ItemCategory.MISC);
		explosiveBigSnowBallItem.setProjectileHitEvent(event -> {

			event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation(), 18.0F);
			for (int i = 0; i < 25; i++) {
				event.getEntity().getLocation().getWorld().createExplosion(event.getEntity().getLocation()
						.add(-10 + RANDOM.nextInt(20), -16 + RANDOM.nextInt(20), -10 + RANDOM.nextInt(20)), 18.0F);
			}
		});
		explosiveBigSnowBallItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(8));
		});

		javaPlugin.customItems().register(explosiveBigSnowBallItem);

		roomKeydim1x1x1Item = new CustomItem("Room Key 1x1x1", Material.LEVER, ItemCategory.ROOMKEYS);
		roomKeydim1x1x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildRoom(1, 1, 1, event.getBlock().getLocation());
			}
		});
		roomKeydim1x2x1Item = new CustomItem("Room Key 1x2x1", Material.LEVER, ItemCategory.ROOMKEYS);
		roomKeydim1x2x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildRoom(1, 2, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(roomKeydim1x2x1Item);

		roomKeydim2x1x1Item = new CustomItem("Room Key 2x1x1", Material.LEVER, ItemCategory.ROOMKEYS);
		roomKeydim2x1x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildRoom(2, 1, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(roomKeydim2x1x1Item);

		roomKeydim2x2x1Item = new CustomItem("Room Key 2x2x1", Material.LEVER, ItemCategory.ROOMKEYS);
		roomKeydim2x2x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildRoom(2, 2, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(roomKeydim2x2x1Item);

		roomKeydim3x1x1Item = new CustomItem("Room Key 3x1x1", Material.LEVER, ItemCategory.ROOMKEYS);
		roomKeydim3x1x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildRoom(3, 1, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(roomKeydim3x1x1Item);

		roomKeydim1x3x1Item = new CustomItem("Room Key 1x3x1", Material.LEVER, ItemCategory.ROOMKEYS);
		roomKeydim1x3x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildRoom(1, 3, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(roomKeydim1x3x1Item);

		roomKeydim3x3x1Item = new CustomItem("Room Key 3x3x1", Material.LEVER, ItemCategory.ROOMKEYS);
		roomKeydim3x3x1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(1, 0, 0).getBlock().getType()
					.equals(Material.BLACK_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildRoom(3, 3, 1, event.getBlock().getLocation());
			}
		});

		javaPlugin.customItems().register(roomKeydim3x3x1Item);

		hallKeydim1Item = new CustomItem("Hall Key 1", Material.LEVER, ItemCategory.HALLKEYS);
		hallKeydim1Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(0, 0, -1).getBlock().getType()
					.equals(Material.LIGHT_BLUE_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildHall(1, event.getBlock().getLocation().add(0, 0, -1));
			}
		});

		javaPlugin.customItems().register(hallKeydim1Item);

		hallKeydim2Item = new CustomItem("Hall Key 2", Material.LEVER, ItemCategory.HALLKEYS);
		hallKeydim2Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(0, 0, -1).getBlock().getType()
					.equals(Material.LIGHT_BLUE_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildHall(2, event.getBlock().getLocation().add(0, 0, -1));
			}
		});

		javaPlugin.customItems().register(hallKeydim2Item);

		hallKeydim3Item = new CustomItem("Hall Key 3", Material.LEVER, ItemCategory.HALLKEYS);
		hallKeydim3Item.setBlockPlaceEvent(event -> {
			if (event.getBlock().getLocation().clone().add(0, 0, -1).getBlock().getType()
					.equals(Material.LIGHT_BLUE_GLAZED_TERRACOTTA)) {
				labRoomBuilder.buildHall(3, event.getBlock().getLocation().add(0, 0, -1));
			}
		});

		javaPlugin.customItems().register(hallKeydim3Item);
	}
}

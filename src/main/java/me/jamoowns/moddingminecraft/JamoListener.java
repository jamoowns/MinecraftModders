package me.jamoowns.moddingminecraft;

import static me.jamoowns.moddingminecraft.common.itemcollections.ItemCollections.BUCKET_TYPES;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;

import me.jamoowns.moddingminecraft.common.time.TimeConstants;
import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.roominating.PlannedBlock;
import me.jamoowns.moddingminecraft.roominating.Roominator;
import me.jamoowns.moddingminecraft.taskkeeper.TaskKeeper;
import me.jamoowns.moddingminecraft.teams.Army;

public final class JamoListener implements Listener {

	private final static boolean isMob(UUID uuid) {
		return Bukkit.getEntity(uuid) instanceof Mob;
	}

	private final ModdingMinecraft javaPlugin;

	private final Random RANDOM;

	private final List<Enchantment> enchantments;

	private final TaskKeeper taskKeeper;

	private List<CustomItem> mobSpawningItems;

	private CustomItem lightningAnusItem;

	private CustomItem skeletonArrowItem;

	private CustomItem normalZombieStick;

	private CustomItem normalSkeletonStick;

	private CustomItem normalCreeperStick;

	private CustomItem normalMobStick;

	private CustomItem normalRoomItem;

	public JamoListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		RANDOM = new Random();

		mobSpawningItems = new ArrayList<>();

		enchantments = Arrays.asList(Enchantment.values());

		Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (javaPlugin.isFeatureActive(Feature.RANDOM_CHESTS)) {
					randomChestSpawn();
				}
			}
		}, RANDOM.nextInt((int) (TimeConstants.ONE_MINUTE * 2)) + TimeConstants.ONE_MINUTE,
				RANDOM.nextInt((int) (TimeConstants.ONE_MINUTE * 2)) + TimeConstants.ONE_MINUTE);

		taskKeeper = new TaskKeeper(javaPlugin);

		setupCustomItems();

		taskKeeper.addBoardItem("Randomly swapping every 5 mins");
		taskKeeper.addBoardItem("Get to the nether...");

		Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				javaPlugin.teams().allTeams().stream().map(Army::teamMembers).flatMap(Collection::stream)
						.filter(JamoListener::isMob).forEach(uuid -> {
							Entity entity = Bukkit.getEntity(uuid);
							if (entity instanceof Mob) {
								Mob mob = (Mob) entity;
								Collection<Entity> nearbyEntities = mob.getWorld().getNearbyEntities(mob.getLocation(),
										15, 15, 15, e -> e instanceof LivingEntity);
								List<Entity> teamedEntitiesNearby = nearbyEntities.stream()
										.filter(e -> !javaPlugin.teams().hasTeam(e.getUniqueId())
												|| !javaPlugin.teams().getTeamName(e.getUniqueId()).equalsIgnoreCase(
														javaPlugin.teams().getTeamName(mob.getUniqueId())))
										.collect(Collectors.toList());

								Optional<Entity> target = teamedEntitiesNearby.stream()
										.sorted((o1, o2) -> Double.compare(
												entity.getLocation().distance(o1.getLocation()),
												entity.getLocation().distance(o2.getLocation())))
										.findFirst();
								if (target.isPresent()) {
									mob.setTarget((LivingEntity) target.get());
								}
							}
						});
			}
		}, 4 * TimeConstants.ONE_SECOND, 4 * TimeConstants.ONE_SECOND);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (javaPlugin.isFeatureActive(Feature.BATTLE_ROYALE)) {
					Bukkit.getOnlinePlayers().forEach(player -> {
						ItemStack item = mobSpawningItems.get(RANDOM.nextInt(mobSpawningItems.size())).asItem();
						item.setAmount(RANDOM.nextInt(15) + 10);
						player.getInventory().addItem(item);
					});
				}
			}
		}, TimeConstants.ONE_MINUTE / 2, TimeConstants.ONE_MINUTE / 2);

		javaPlugin.commandExecutor().registerCommand(java.util.Collections.emptyList(), "items", this::showAllItems);
	}

	public void cleanup() {
		javaPlugin.teams().cleanup();
	}

	@EventHandler
	public void onCraftItemEvent(CraftItemEvent event) {
		if (javaPlugin.isFeatureActive(Feature.RANDOM_ENCHANT)) {
			ItemStack result = event.getRecipe().getResult().clone();
			ItemMeta meta = result.getItemMeta();
			for (int i = 0; i < 7; i++) {
				Enchantment enchantment = enchantments.get(RANDOM.nextInt(enchantments.size()));
				if (enchantment.canEnchantItem(result)) {
					meta.addEnchant(enchantment, RANDOM.nextInt(4) + 1, false);
				}
			}
			result.setItemMeta(meta);
			event.getInventory().setResult(result);
		}
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		UUID entityUuid = event.getEntity().getUniqueId();
		if (event.getEntity().getType() == EntityType.PIG) {
			if (event.getEntity().getKiller() != null) {
				taskKeeper.incrementTask(entityUuid, "Kill pigs");
			}
		} else if (event.getEntity().getType() == EntityType.COW) {
			if (event.getEntity().getKiller() != null) {
				taskKeeper.incrementTask(entityUuid, "Kill cows");
			}
		}
		if (isMob(entityUuid) && javaPlugin.teams().hasTeam(entityUuid)) {
			javaPlugin.teams().getTeam(entityUuid).remove(entityUuid);
			event.getDrops().clear();
		}
	}

	@EventHandler
	public void onEntitySpawnEvent(BlockDropItemEvent event) {
		if (event.getBlock().getType().name().contains("CARPET")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		if (javaPlugin.isFeatureActive(Feature.RANDOM_BUCKET)) {
			event.setItemStack(new ItemStack(BUCKET_TYPES.get(RANDOM.nextInt(BUCKET_TYPES.size()))));
		}
	}

	@EventHandler
	public void onPlayerEggThrowEvent(PlayerEggThrowEvent event) {
		if (javaPlugin.isFeatureActive(Feature.EGG_WITCH)) {
			event.setHatchingType(EntityType.WITCH);
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (javaPlugin.isFeatureActive(Feature.ZOMBIE_BELL)) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BELL) {
				Player player = event.getPlayer();
				Location dundundun = player.getLocation().add(player.getLocation().getDirection().multiply(-15));

				Zombie zombie = player.getWorld().spawn(dundundun, Zombie.class);
				zombie.setTarget(player);
				zombie.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 1));
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(MessageFormat.format("Welcome, {0}! This server is running MinecraftModders V{1}",
				event.getPlayer().getName(), javaPlugin.getDescription().getVersion()));
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

	private void randomChestSpawn() {
		List<Player> players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
		Player p = players.get(RANDOM.nextInt(players.size()));

		int attempts = 0;
		boolean done = false;
		while (attempts < 30 && !done) {
			Location chestLocation = p.getLocation().add(RANDOM.nextInt(40) - 20, RANDOM.nextInt(6),
					RANDOM.nextInt(40) - 20);

			// Location chestLocation = p.getLocation().add(0, 3, 0);
			if (p.getWorld().getBlockAt(chestLocation).isEmpty()) {
				done = true;
				p.getWorld().playSound(chestLocation, Sound.BLOCK_GLASS_BREAK, 20, 1);
				p.getWorld().playSound(chestLocation, Sound.BLOCK_GLASS_BREAK, 25, 1);

				p.getWorld().getBlockAt(chestLocation).setType(Material.CHEST);

				Chest chest = (Chest) p.getWorld().getBlockAt(chestLocation).getState();

				List<Material> materials = Arrays.asList(Material.values());

				List<Material> itemsForChest = new ArrayList<>();

				for (int i = 0; i < RANDOM.nextInt(5) + 1; i++) {
					itemsForChest.add(materials.get(RANDOM.nextInt(materials.size())));
				}

				List<ItemStack> forChest = itemsForChest.stream().map(ItemStack::new).collect(Collectors.toList());

				chest.getInventory().setContents(forChest.toArray(new ItemStack[forChest.size()]));
				p.getWorld().spawnEntity(chest.getLocation(), EntityType.FIREWORK);
			}
		}
	}

	private void setupCustomItems() {
		normalZombieStick = new CustomItem(Material.REDSTONE_TORCH, "Normal Zombie");
		normalZombieStick.setClickEvent(event -> {
			Location spawnLocation = event.getClickedBlock().getLocation().add(0, 1, 0);
			Mob mob = event.getClickedBlock().getWorld().spawn(spawnLocation, Zombie.class);
			javaPlugin.teams().register(event.getPlayer().getUniqueId(), mob);
		});
		javaPlugin.customItems().register(normalZombieStick);
		mobSpawningItems.add(normalZombieStick);

		normalSkeletonStick = new CustomItem(Material.END_ROD, "Normal Skeleton");
		normalSkeletonStick.setBlockPlaceEvent(event -> {
			Location spawnLocation = event.getBlock().getLocation().add(0, 1, 0);
			Mob mob = event.getBlock().getWorld().spawn(spawnLocation, Skeleton.class);
			javaPlugin.teams().register(event.getPlayer().getUniqueId(), mob);
		});
		javaPlugin.customItems().register(normalSkeletonStick);
		mobSpawningItems.add(normalSkeletonStick);

		normalCreeperStick = new CustomItem(Material.TWISTING_VINES, "Normal Creeper");
		normalCreeperStick.setBlockPlaceEvent(event -> {
			Location spawnLocation = event.getBlock().getLocation().add(0, 1, 0);
			Mob mob = event.getBlock().getWorld().spawn(spawnLocation, Creeper.class);
			javaPlugin.teams().register(event.getPlayer().getUniqueId(), mob);
		});
		javaPlugin.customItems().register(normalCreeperStick);
		mobSpawningItems.add(normalCreeperStick);

		normalMobStick = new CustomItem(Material.SOUL_TORCH, "Unkown Mob");
		normalMobStick.setBlockPlaceEvent(event -> {
			Location spawnLocation = event.getBlock().getLocation().add(0, 1, 0);
			List<EntityType> mobList = Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON,
					EntityType.ZOMBIFIED_PIGLIN);
			List<EntityType> RaremobList = Arrays.asList(EntityType.CREEPER, EntityType.SHULKER);

			if (RANDOM.nextInt(10) > 3) {
				Entity mob = event.getBlock().getWorld().spawnEntity(spawnLocation,
						mobList.get(RANDOM.nextInt(mobList.size())));
				javaPlugin.teams().register(event.getPlayer().getUniqueId(), (Mob) mob);
			} else {
				Entity mob = event.getBlock().getWorld().spawnEntity(spawnLocation,
						RaremobList.get(RANDOM.nextInt(RaremobList.size())));
				javaPlugin.teams().register(event.getPlayer().getUniqueId(), (Mob) mob);
			}

		});
		javaPlugin.customItems().register(normalMobStick);
		mobSpawningItems.add(normalMobStick);

		normalRoomItem = new CustomItem(Material.LECTERN, "Standard Room");
		normalRoomItem.setBlockPlaceEvent(event -> {
			Location startPoint = event.getBlockPlaced().getLocation().add(0, 0, 0);

			List<PlannedBlock> standardRoom = Roominator.standardRoom(startPoint, RANDOM.nextInt(5) + 4,
					RANDOM.nextInt(5) + 4, 5, linearFace(event.getPlayer().getLocation().getYaw()));

			Roominator.build(event.getBlockAgainst().getWorld(), standardRoom);
		});
		javaPlugin.customItems().register(normalRoomItem);

		skeletonArrowItem = new CustomItem(Material.ARROW, "Skeleton Arrow");
		skeletonArrowItem.setProjectileHitEvent(event -> {
			event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Skeleton.class);
		});
		javaPlugin.customItems().register(skeletonArrowItem);

		lightningAnusItem = new CustomItem(Material.CHAIN, "Lightning Storm");
		lightningAnusItem.setBlockPlaceEvent(event -> {
			Location spawnLocation = event.getBlock().getLocation().add(0, 1, 0);

			Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
				@Override
				public void run() {
					spawnLocation.getWorld().strikeLightningEffect(spawnLocation);
				}
			}, TimeConstants.ONE_SECOND * 5, TimeConstants.ONE_SECOND * 5);
		});
		javaPlugin.customItems().register(lightningAnusItem);
	}

	private void showAllItems(Player player) {
		Inventory inv = Bukkit.createInventory(null,
				(int) (Math.ceil(javaPlugin.customItems().totalCustomItems() / 9) * 9 + 9), "Custom Items");

		List<CustomItem> allItems = Lists.newArrayList(javaPlugin.customItems().allItems());

		for (int i = 0; i < javaPlugin.customItems().totalCustomItems(); i++) {
			inv.setItem(i, allItems.get(i).asItem());
		}
		player.openInventory(inv);
	}
}

package me.jamoowns.moddingminecraft;

import static me.jamoowns.moddingminecraft.common.itemcollections.ItemCollections.BUCKET_TYPES;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.roominating.PlannedBlock;
import me.jamoowns.moddingminecraft.roominating.Roominator;
import me.jamoowns.moddingminecraft.taskkeeper.TaskKeeper;
import me.jamoowns.moddingminecraft.teams.Teams;

public final class JamoListener implements Listener {

	private final ModdingMinecraft javaPlugin;

	private final Random RANDOM;
	private final List<Enchantment> enchantments;

	private final boolean RANDOM_CHESTS = false;

	private static final long ONE_SECOND = 20L;
	private static final long ONE_MINUTE = ONE_SECOND * 60;

	private final TaskKeeper taskKeeper;

	private final Teams teams;

	private final List<UUID> teamedMobs;

	private Map<String, CustomItem> customItemsByName;

	private CustomItem normalZombieStick;

	private CustomItem normalMobStick;

	private CustomItem normalRoomItem;

	public JamoListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		RANDOM = new Random();

		teamedMobs = new ArrayList<>();

		enchantments = Arrays.asList(Enchantment.values());

		if (RANDOM_CHESTS) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
				public void run() {
					randomChestSpawn();
				}
			}, RANDOM.nextInt((int) (ONE_MINUTE * 2)) + ONE_MINUTE,
					RANDOM.nextInt((int) (ONE_MINUTE * 2)) + ONE_MINUTE);
		}

		taskKeeper = new TaskKeeper(javaPlugin);
		teams = new Teams(javaPlugin);

		setupCustomItems();

		Consumer<UUID> pigReward = playerId -> {
			Bukkit.getPlayer(playerId).getInventory().addItem(normalZombieStick.asItem());
		};
		Consumer<UUID> cowReward = playerId -> {
			Bukkit.getPlayer(playerId).getInventory().addItem(normalRoomItem.asItem());
		};
		taskKeeper.addTask("Kill pigs", pigReward, 2);
		taskKeeper.addTask("Kill cows", cowReward, 2);
		taskKeeper.addBoardItem("Could be cool to have a live band");
		taskKeeper.addBoardItem("Hello and ready for the party");

		Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				teamedMobs.forEach(uuid -> {
					Entity entity = Bukkit.getEntity(uuid);
					if (entity instanceof Mob) {
						Mob mob = (Mob) entity;
						// if (mob.getTarget() == null) {
						Collection<Entity> nearbyEntities = mob.getWorld().getNearbyEntities(mob.getLocation(), 15, 15,
								15, e -> e instanceof LivingEntity);
						List<Entity> teamedEntitiesNearby = nearbyEntities.stream()
								.filter(e -> !teams.hasTeam(e.getUniqueId()) || !teams.getTeamName(e.getUniqueId())
										.equalsIgnoreCase(teams.getTeamName(mob.getUniqueId())))
								.collect(Collectors.toList());

						Optional<Entity> target = teamedEntitiesNearby.stream()
								.sorted((o1, o2) -> Double.compare(entity.getLocation().distance(o1.getLocation()),
										entity.getLocation().distance(o2.getLocation())))
								.findFirst();
						if (target.isPresent()) {
							mob.setTarget((LivingEntity) target.get());
						}
					}
				});
			}
		}, 4 * ONE_SECOND, 4 * ONE_SECOND);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (javaPlugin.isFeatureActive(Feature.BATTLE_ROYALE)) {
					Bukkit.getOnlinePlayers().forEach(player -> {
						ItemStack item = customItemsByName.values().stream().collect(Collectors.toList())
								.get(RANDOM.nextInt(customItemsByName.values().size())).asItem();
						item.setAmount(RANDOM.nextInt(4));
						player.getInventory().addItem(item);
					});
				}
			}
		}, ONE_MINUTE, ONE_MINUTE);
	}

	private void setupCustomItems() {
		customItemsByName = new HashMap<>();

		normalZombieStick = new CustomItem(Material.REDSTONE_TORCH, "Normal Zombie");
		normalZombieStick.setBlockPlaceEvent(event -> {
			Location spawnLocation = event.getBlock().getLocation().add(0, 1, 0);
			Mob mob = event.getBlock().getWorld().spawn(spawnLocation, Zombie.class);
			teams.register(event.getPlayer().getUniqueId(), mob);
			teamedMobs.add(mob.getUniqueId());
		});
		customItemsByName.put(normalZombieStick.name(), normalZombieStick);

		normalMobStick = new CustomItem(Material.SOUL_TORCH, "Unkown Mob");
		normalMobStick.setBlockPlaceEvent(event -> {
			Location spawnLocation = event.getBlock().getLocation().add(0, 1, 0);
			List<EntityType> mobList = Arrays.asList(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.IRON_GOLEM,
					EntityType.PIGLIN_BRUTE);
			Entity mob = event.getBlock().getWorld().spawnEntity(spawnLocation,
					mobList.get(RANDOM.nextInt(mobList.size())));
			teams.register(event.getPlayer().getUniqueId(), (Mob) mob);
			teamedMobs.add(mob.getUniqueId());
		});
		customItemsByName.put(normalMobStick.name(), normalMobStick);

		normalRoomItem = new CustomItem(Material.LECTERN, "Standard Room");
		normalRoomItem.setBlockPlaceEvent(event -> {
			Location startPoint = event.getBlockPlaced().getLocation().add(0, 0, 0);

			List<PlannedBlock> standardRoom = Roominator.standardRoom(startPoint, RANDOM.nextInt(5) + 4,
					RANDOM.nextInt(5) + 4, 5, linearFace(event.getPlayer().getLocation().getYaw()));

			Roominator.build(event.getBlockAgainst().getWorld(), standardRoom);
		});
		customItemsByName.put(normalRoomItem.name(), normalRoomItem);
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

	@EventHandler
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		if (event.getItem().getType().name().contains("CARPET")) {
			event.setCancelled(true);
			event.getItem().remove();
		}
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if (event.getEntity().getType() == EntityType.PIG) {
			Player mcPlayer = event.getEntity().getKiller();
			if (mcPlayer != null) {
				taskKeeper.incrementTask(mcPlayer.getUniqueId(), "Kill pigs");
			}
		} else if (event.getEntity().getType() == EntityType.COW) {
			Player mcPlayer = event.getEntity().getKiller();
			if (mcPlayer != null) {
				taskKeeper.incrementTask(mcPlayer.getUniqueId(), "Kill cows");
			}
		}
		teamedMobs.remove(event.getEntity().getUniqueId());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(MessageFormat.format("Welcome, {0}! This server is running MinecraftModders V{1}",
				event.getPlayer().getName(), javaPlugin.getDescription().getVersion()));
	}

	@EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		event.setItemStack(new ItemStack(BUCKET_TYPES.get(RANDOM.nextInt(BUCKET_TYPES.size()))));
	}

	@EventHandler
	public void onPlayerEggThrowEvent(PlayerEggThrowEvent event) {
		event.setHatchingType(EntityType.WITCH);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		CustomItem customItem = customItemsByName.get(event.getItemInHand().getItemMeta().getDisplayName());
		if (customItem != null && customItem.hasBlockPlaceEvent()) {
			event.getBlockPlaced().setType(Material.AIR);
			customItem.blockPlaceEvent().accept(event);
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

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BELL) {
			Player player = event.getPlayer();
			Location dundundun = player.getLocation().add(player.getLocation().getDirection().multiply(-15));

			Zombie zombie = player.getWorld().spawn(dundundun, Zombie.class);
			zombie.setTarget(player);
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 1));
		}
	}

	@EventHandler
	public void onCraftItemEvent(CraftItemEvent event) {
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

	public void cleanup() {
		teams.cleanup();
	}
}

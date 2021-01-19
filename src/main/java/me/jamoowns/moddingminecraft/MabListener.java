package me.jamoowns.moddingminecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.features.Feature;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.roominating.BuildingFoundations;
import me.jamoowns.moddingminecraft.roominating.StructureBuilder;
import me.jamoowns.moddingminecraft.roominating.StructureBuilder.GridType;

public class MabListener implements IGameEventListener {

	public static void switchPlayers(Player one, Player other) {
		Location otherLocation = other.getLocation();
		other.teleport(one.getLocation());
		one.teleport(otherLocation);
	}

	private final ModdingMinecraft javaPlugin;

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

	private final StructureBuilder structureBuilder;

	Player mabmo;

	boolean mabmoSet;

	public MabListener(ModdingMinecraft aJavaPlugin) {
		RANDOM = new Random();
		javaPlugin = aJavaPlugin;
		setupCustomItems();
		structureBuilder = new StructureBuilder();
	}

	@Override
	public final void cleanup() {
		/* Empty. */
	}

	@EventHandler
	public final void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType().equals(Material.WARPED_HYPHAE) && mabmoSet) {
			structureBuilder.buildGrid(GridType.STRAIGHT,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.CRIMSON_HYPHAE) && mabmoSet) {
			event.getPlayer().sendMessage("Building 2");
			structureBuilder.buildGrid(GridType.CORNER,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.WET_SPONGE) && mabmoSet) {
			event.getPlayer().sendMessage("Building 3");
			structureBuilder.buildGrid(GridType.T_SECTION,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.CHISELED_SANDSTONE) && mabmoSet) {
			event.getPlayer().sendMessage("Building 4");
			structureBuilder.buildGrid(GridType.CROSS,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
		}
		if (event.getBlock().getType().equals(Material.PODZOL) && mabmoSet) {
			event.getPlayer().sendMessage("Building 5");
			structureBuilder.buildGrid(GridType.DEAD_END,
					BuildingFoundations.standadiseDirection(event.getPlayer().getLocation().getYaw()),
					event.getBlockPlaced().getLocation());
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
		if (javaPlugin.featureTracker().isFeatureActive(Feature.HEAVY_BLOCKS)
				|| javaPlugin.featureTracker().isFeatureActive(Feature.LIGHT_BLOCKS)) {
			Block block = event.getBlockPlaced();
			BlockData blockData = block.getBlockData();
			block.setType(Material.AIR);
			FallingBlock fb = block.getLocation().getWorld().spawnFallingBlock(block.getLocation().add(0.5, 0, 0.5),
					blockData);
			if (javaPlugin.featureTracker().isFeatureActive(Feature.LIGHT_BLOCKS)) {
				fb.setVelocity(new Vector(0, 1, 0));
				Random r = new Random();
				int low = -50;
				int high = 50;
				float x = r.nextInt(high - low) + low / 100;

				float y = r.nextInt(high - low) + low / 100;
				fb.setVelocity(new Vector(x, 2, y));
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
	public final void onEntityDeathEvent(EntityDeathEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.FUNKY_MOB_DEATH)) {
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
	public final void onEntitySpawnEvent(EntitySpawnEvent event) {
		if (event.getEntity().getName().contains("Lantern")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public final void onPlayerDeath(PlayerDeathEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.IRON_GOLEM)) {
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
	public final void onPlayerInteractEvent(PlayerItemConsumeEvent e) {
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
	public final void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.WINFRED)) {
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
							switchAllPlayers(event.getPlayer().getWorld());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public final void onThunderChange(ThunderChangeEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.STABLE_WEATHER)) {
			if (event.toThunderState()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void onWeatherChange(WeatherChangeEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.STABLE_WEATHER)) {
			if (event.toWeatherState()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void playerChatEvent(PlayerChatEvent event) {
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
			switchAllPlayers(event.getPlayer().getWorld());

		}

		if (event.getMessage().contains("SwapNearMe")) {
			switchAllPlayersInAnArea(event.getPlayer().getLocation(), 20, 5, 20);
		}
		if (event.getMessage().contains("Ok let's do this")) {
			getReady(event.getPlayer().getLocation().getWorld());
		}
		if (event.getMessage().contains("Iammabmo")) {
			mabmo = event.getPlayer();
			mabmoSet = true;
		}
		if (event.getMessage().contains("Iamnotmabmo")) {
			mabmoSet = false;
		}
	}

	private void getReady(World world) {
		List<Player> PlayerArr = new ArrayList<Player>();

		for (Player players : world.getPlayers()) {
			PlayerArr.add(players);
		}
		sendMabmoMsg("" + PlayerArr.size());
		if (PlayerArr.size() > 0) {
			for (Player player : PlayerArr) {
				player.sendTitle("Get Ready", "", 40, 40, 40);
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 255));
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
		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
			@Override
			public void run() {
				if (PlayerArr.size() > 0) {

					Location loc = PlayerArr.get(0).getLocation();
					Random r = new Random();
					int low = 3000;
					int high = 5000;
					int count = 100;
					while (count > 0) {
						Location newLoc = loc;
						newLoc.add(r.nextInt(high - low) + low, 0, r.nextInt(high - low) + low);
						if (!newLoc.getBlock().getBiome().toString().contains("OCEAN")) {
							loc = newLoc;
							count = 0;
						}
						count--;
					}
					loc.add(0, 80, 0);
					for (Player player : PlayerArr) {
						player.setVelocity(new Vector(player.getVelocity().getX(), 1, player.getVelocity().getZ()));
						player.teleport(loc);
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 255));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 180, 1));

					}
				}
			}
		}, 120);

	}

	private void potionAllPlayersInAnArea(Location loc, int x, int y, int z, PotionEffectType potionEffectType,
			int duration, int amplifier) {
		for (Entity players : loc.getWorld().getNearbyEntities(loc, x, y, z)) {
			if (players instanceof Player) {
				((LivingEntity) players).addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier));
			}
		}
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

	private void sendMabmoMsg(String str) {
		if (mabmoSet) {
			if (mabmo.isOnline()) {
				mabmo.sendMessage(str);
			}
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
			switchAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20);
		});
		swapsiesSplashPotionItem.setProjectileLaunchEvent(event -> {
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(4));
		});
		javaPlugin.customItems().register(swapsiesSplashPotionItem);

		medusaSplashPotionItem = new CustomItem("Tears of Medusa", Material.SPLASH_POTION);
		medusaSplashPotionItem.setPotionSplashEvent(event -> {
			potionAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20, PotionEffectType.SLOW, 100, 200);
			potionAllPlayersInAnArea(event.getEntity().getLocation(), 20, 5, 20, PotionEffectType.JUMP, 100, 100000);
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

	private void switchAllPlayers(World world) {
		List<Player> players = new ArrayList<>();

		players = world.getPlayers();

		if (players.size() > 1) {
			int count = 0;
			Location firstloc = players.get(0).getLocation();
			for (Player player : players) {
				if (count == players.size() - 1) {
					player.teleport(firstloc);
				} else {
					player.teleport(players.get(count + 1).getLocation());
				}
				count++;
			}
		}
	}

	private void switchAllPlayersInAnArea(Location loc, int x, int y, int z) {
		List<Player> players = new ArrayList<>();

		for (Entity ent : loc.getWorld().getNearbyEntities(loc, x, y, z)) {
			if (ent instanceof Player) {
				players.add((Player) ent);
			}
		}

		if (players.size() > 1) {
			int count = 0;
			Location firstloc = players.get(0).getLocation();
			for (Player player : players) {
				if (count == players.size() - 1) {
					player.teleport(firstloc);
				} else {
					player.teleport(players.get(count + 1).getLocation());
				}
				count++;
			}
		}
	}
}

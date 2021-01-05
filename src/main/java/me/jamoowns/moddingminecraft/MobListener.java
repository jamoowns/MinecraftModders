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
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Husk;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public class MobListener implements Listener {

	private final ModdingMinecraft javaPlugin;

	private Map<UUID, List<Block>> trailByPlayer;

	private final Random RANDOM;

	public MobListener(ModdingMinecraft aJavaPlugin) {
		RANDOM = new Random();
		javaPlugin = aJavaPlugin;
		trailByPlayer = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerItemConsumeEvent e) {
		// get all the relative values for comparation
		final Player p = e.getPlayer();
		if (e.getItem().getType().equals(Material.SPIDER_EYE)) {
			// schedule a task to see if they have eaten the cookie(maybe the time could be
			// a little faster idk)
			Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
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
					public void run() {
						p.setFallDistance(0);
						p.teleport(playerLoc.add(0, -2, 0));
					}
				}, 230);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWeatherChange(WeatherChangeEvent event) {
		boolean rain = event.toWeatherState();
		if (rain) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThunderChange(ThunderChangeEvent event) {
		boolean storm = event.toThunderState();
		if (storm) {
			event.setCancelled(true);
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile entity = event.getEntity();
		if ((entity instanceof Arrow)) {
			Arrow arrow = (Arrow) entity;
			ProjectileSource shooter = arrow.getShooter();
			if ((shooter instanceof Player) || (shooter instanceof BlockProjectileSource)) {
				if (arrow.getBasePotionData().getType() == PotionType.WEAKNESS) {
					Random r = new Random();
					int low = 1;
					int high = 5;
					int result = r.nextInt(high - low) + low;
					for (int i = 0; i < result; i++) {
						arrow.getLocation().getWorld().spawn(arrow.getLocation(), Creeper.class);
					}
					arrow.remove();
				}
				if (arrow.getBasePotionData().getType() == PotionType.SLOWNESS) {
					Random r = new Random();
					int low = 4;
					int high = 10;
					int result = r.nextInt(high - low) + low;
					for (int i = 0; i < result; i++) {
						arrow.getLocation().getWorld().spawn(arrow.getLocation(), Shulker.class);
					}
					arrow.remove();
				}
				if (arrow.getBasePotionData().getType() == PotionType.INSTANT_DAMAGE) {
					arrow.getLocation().getWorld().createExplosion(arrow.getLocation(), 5.0F);
					arrow.remove();
				}
				if (arrow.getBasePotionData().getType() == PotionType.LUCK) {
					Location loc = arrow.getLocation();
					if (!arrow.getLocation().getWorld().getBlockAt(loc.add(0, -1, 0)).getType().name()
							.contains("LEAVES")) {
						arrow.getLocation().getWorld().getBlockAt(loc).setType(Material.DIRT);
						arrow.getLocation().getWorld().generateTree(arrow.getLocation(), TreeType.TREE);
						arrow.remove();
					}
				}
				if (arrow.getBasePotionData().getType() == PotionType.STRENGTH) {
					for (int i = 0; i < 9; i++) {
						if (arrow.getLocation().getY() + i < 63) {
							for (int j = 0; j < 9; j++) {
								for (int k = 0; k < 9; k++) {
									Location loc = arrow.getLocation();
									loc.add(k - 4, i, j - 4);
									if (loc.getY() < 59) {
										arrow.getLocation().getWorld().getBlockAt(loc).setType(Material.STONE);
									} else {
										arrow.getLocation().getWorld().getBlockAt(loc).setType(Material.DIRT);
									}
								}
							}
						}
					}
					arrow.remove();
				}
				if (arrow.getBasePotionData().getType() == PotionType.INVISIBILITY) {
					Random r = new Random();
					int low = 1;
					int high = 4;
					int result = r.nextInt(high - low) + low;
					for (int i = 0; i < 10; i++) {
						Material[][] multi = new Material[21][21];

						for (int j = 0; j < 21; j++) {
							for (int k = 0; k < 21; k++) {
								Location loc = arrow.getLocation();
								loc.add(k - 10, i, j - 10);
								if (!arrow.getLocation().getWorld().getBlockAt(loc).getType().name().contains("WATER")
										|| loc.getY() < 63) {
									multi[j][k] = arrow.getLocation().getWorld().getBlockAt(loc).getType();
								} else {
									multi[j][k] = Material.AIR;
								}
							}
						}

						multi = RotateShapeSquareGrid(multi, 90 * result);

						for (int j = 0; j < 21; j++) {
							for (int k = 0; k < 21; k++) {
								Location loc = arrow.getLocation();
								loc.add(k - 10, i, j - 10);
								if (!arrow.getLocation().getWorld().getBlockAt(loc).getType().name().contains("WATER")
										&& !arrow.getLocation().getWorld().getBlockAt(loc).getType().name()
												.contains("LAVA")
										|| loc.getY() < 63) {
									arrow.getLocation().getWorld().getBlockAt(loc).setType(multi[j][k]);
								} else {
									arrow.getLocation().getWorld().getBlockAt(loc).setType(Material.AIR);
								}

							}
						}
					}

					arrow.remove();
				}

			}
		}
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (javaPlugin.isFeatureActive(Feature.PLAYER_TRAIL)) {
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

	public void cleanup() {
		trailByPlayer.values().forEach(blocks -> {
			blocks.forEach(block -> block.setType(Material.AIR));
		});
		trailByPlayer.clear();
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		List<Block> trail = trailByPlayer.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());
		trail.forEach(block -> {
			block.setType(Material.AIR);
		});
		trailByPlayer.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (javaPlugin.isFeatureActive(Feature.IRON_GOLEM)) {
			Player playerEnt = (Player) event.getEntity();
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

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		if (javaPlugin.isFeatureActive(Feature.FUNKY_MOB_DEATH)) {
			if (event.getEntity() instanceof Sheep) {
				Sheep sheepEnt = (Sheep) event.getEntity();
				Player mcPlayer = sheepEnt.getKiller();
				if (mcPlayer == null)
					return;

				if (sheepEnt.isAdult()) {
					mcPlayer.sendTitle("You're about to die", "20 secs to live after this", 40, 40, 40);
					Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
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
}

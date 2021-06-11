package me.jamoowns.moddingminecraft.extras;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.features.Feature;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class FunkyDeathsListener implements IGameEventListener {

	private ModdingMinecraft javaPlugin;

	private Random RANDOM;

	public FunkyDeathsListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

	@Override
	public final void cleanup() {
		/* Empty. */
	}

	@EventHandler
	public final void onEntityDeathEvent(EntityDeathEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.FUNKY_MOB_DEATH)) {
			if (event.getEntity() instanceof Sheep) {
				sheepDeathEvent(event);
			} else if (event.getEntity() instanceof Chicken) {
				chickenDeathEvent(event);
			} else if (event.getEntity() instanceof Bee) {
				beeDeathEvent(event);
			} else if (event.getEntity() instanceof Ghast) {
				ghastDeathEvent(event);
			} else if (event.getEntity() instanceof Phantom) {
				phantomDeathEvent(event);
			} else if (event.getEntity() instanceof Cow) {
				cowDeathEvent(event);
			} else if (event.getEntity() instanceof Pig) {
				pigDeathEvent(event);
			} else if (event.getEntity() instanceof Villager) {
				villagerDeathEvent(event);
			} else if (event.getEntity() instanceof Zombie) {
				zombieDeathEvent(event);
			} else if (event.getEntity() instanceof Skeleton) {
				skeletonDeathEvent(event);
			} else if (event.getEntity() instanceof Creeper) {
				creeperDeathEvent(event);
			} else if (event.getEntity() instanceof Spider) {
				spiderDeathEvent(event);
			}
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

	private void beeDeathEvent(EntityDeathEvent event) {
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

	private void chickenDeathEvent(EntityDeathEvent event) {
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

	private void cowDeathEvent(EntityDeathEvent event) {
		Cow cowEnt = (Cow) event.getEntity();
		Player mcPlayer = cowEnt.getKiller();
		if (mcPlayer == null)
			return;

		if (cowEnt.isAdult()) {
			mcPlayer.teleport(mcPlayer.getLocation().add(0, 5, 0));
		}
	}

	private void creeperDeathEvent(EntityDeathEvent event) {
		Creeper creeperEnt = (Creeper) event.getEntity();
		Player mcPlayer = creeperEnt.getKiller();
		if (mcPlayer == null)
			return;
		Location loc = event.getEntity().getLocation().add(0, -2, 0);
		event.getEntity().getWorld().getBlockAt(loc).setType(Material.AIR);
		TNTPrimed tnt = event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation().add(0, 1, 0),
				TNTPrimed.class);
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

	private void ghastDeathEvent(EntityDeathEvent event) {
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

	private void phantomDeathEvent(EntityDeathEvent event) {
		Phantom phantomEnt = (Phantom) event.getEntity();
		Player mcPlayer = phantomEnt.getKiller();
		if (mcPlayer == null)
			return;
		event.getDrops().add(new ItemStack(Material.BLAZE_POWDER, 1));
	}

	private void pigDeathEvent(EntityDeathEvent event) {
		Pig pigEnt = (Pig) event.getEntity();
		Player mcPlayer = pigEnt.getKiller();
		if (mcPlayer == null)
			return;

		if (pigEnt.isAdult()) {
			event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Ravager.class);
		}
	}

	private void sheepDeathEvent(EntityDeathEvent event) {
		Sheep sheepEnt = (Sheep) event.getEntity();
		Player mcPlayer = sheepEnt.getKiller();
		if (mcPlayer == null)
			return;

		if (sheepEnt.isAdult()) {
			Random r = new Random();
			int low = 1;
			int high = 11;
			int result = r.nextInt(high - low) + low;
			if (result > 6) {
				for (int i = 0; i < 1; i++) {
					Wolf wolf = mcPlayer.getLocation().getWorld().spawn(event.getEntity().getLocation(), Wolf.class);
					if (mcPlayer != null) {
						wolf.setTarget(mcPlayer);
					}
				}

			}

		}
	}

	private void skeletonDeathEvent(EntityDeathEvent event) {
		Skeleton skeletonEnt = (Skeleton) event.getEntity();
		Player mcPlayer = skeletonEnt.getKiller();
		if (mcPlayer == null)
			return;
		for (int i = 0; i < 3; i++) {
			event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation().add(0, 1, 0), Bat.class);
		}
		mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
		mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 180, 1));

		ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);

		if (RANDOM.nextInt(4) == 1) {
			PotionMeta meta = (PotionMeta) arrow.getItemMeta();
			meta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
			arrow.setItemMeta(meta);
			event.getDrops().add(arrow);
		}
	}

	private void spiderDeathEvent(EntityDeathEvent event) {
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

	private void villagerDeathEvent(EntityDeathEvent event) {
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
				IronGolem ironGolem = event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(),
						IronGolem.class);
				ironGolem.setTarget(mcPlayer);
			}
		}
	}

	private void zombieDeathEvent(EntityDeathEvent event) {
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
				Zombie babushka = event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(),
						Zombie.class);
				babushka.setBaby();
				babushka.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
						babushka.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 1 / result);
				babushka.getAttribute(Attribute.GENERIC_MAX_HEALTH)
						.setBaseValue(babushka.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 1 / result);
			}
		}
	}

}

package me.jamoowns.moddingminecraft.extras;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.jamoowns.moddingminecraft.ModdingMinecraft;

public final class Spells {

	public static void dropAllPlayersIntoRandomLocation(World world, ModdingMinecraft javaPlugin) {
		List<Player> PlayerArr = new ArrayList<Player>();

		for (Player players : world.getPlayers()) {
			PlayerArr.add(players);
		}
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

	public static void potionAllPlayersInAnArea(Location loc, int x, int y, int z, PotionEffectType potionEffectType,
			int duration, int amplifier) {
		for (Entity players : loc.getWorld().getNearbyEntities(loc, x, y, z)) {
			if (players instanceof Player) {
				((LivingEntity) players).addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier));
			}
		}
	}

	public static void switchAllPlayers(World world) {
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

	public static void switchAllPlayersInAnArea(Location loc, int x, int y, int z) {
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

	public static void switchPlayers(Player one, Player other) {
		Location otherLocation = other.getLocation();
		other.teleport(one.getLocation());
		one.teleport(otherLocation);
	}

	private Spells() {
		/* Hidden. */
	}
}

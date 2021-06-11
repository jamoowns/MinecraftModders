package me.jamoowns.moddingminecraft.extras;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Spells {
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

}

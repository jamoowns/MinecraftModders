package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import me.jamoowns.moddingminecraft.customitems.CustomItem;

public class MarkerPoints {

	private ArrayList<Location> flagBlockLocations;
	private ArrayList<Location> celebrateBlocks;
	private Map<UUID, Location> playerHomeLocationById;
	private Map<UUID, CustomItem> playerHomeItemById;
	private int maxScore;

	public MarkerPoints(int maxScore) {
		flagBlockLocations = new ArrayList<>();
		playerHomeLocationById = new HashMap<>();
		celebrateBlocks = new ArrayList<>();
		playerHomeItemById = new HashMap<>();
		this.maxScore = maxScore;
	}

	public void buildFlag(Block baseBlock, Material baseColour, Material flagColour) {
		ArrayList<Location> locations = new ArrayList<>();
		locations.add(baseBlock.getLocation().add(-1, 0, 0));
		locations.add(baseBlock.getLocation().add(1, 0, 0));
		locations.add(baseBlock.getLocation().add(-1, 0, -1));
		locations.add(baseBlock.getLocation().add(-1, 0, 1));
		locations.add(baseBlock.getLocation().add(1, 0, -1));
		locations.add(baseBlock.getLocation().add(1, 0, 1));
		locations.add(baseBlock.getLocation().add(0, 0, -1));
		locations.add(baseBlock.getLocation().add(0, 0, 1));
		locations.forEach(location -> baseBlock.getWorld().getBlockAt(location).setType(baseColour));
		flagBlockLocations.addAll(locations);

		/* Mast. */
		for (int i = 1; i <= maxScore; i++) {
			Location location = baseBlock.getLocation().add(0, i, 0);
			flagBlockLocations.add(location);
			if (i == maxScore) {
				celebrateBlocks.add(location);
			}
			baseBlock.getWorld().getBlockAt(location).setType(Material.WARPED_FENCE);
		}

		/* Flag. */
		Location topBlock = baseBlock.getLocation().add(0, maxScore, 0);
		locations.clear();
		locations.add(topBlock.clone().add(1, 0, 0));
		locations.add(topBlock.clone().add(1, -1, 0));
		locations.add(topBlock.clone().add(2, 0, 0));
		locations.add(topBlock.clone().add(2, -1, 0));
		locations.add(topBlock.clone().add(3, 0, 0));
		locations.add(topBlock.clone().add(3, -1, 0));
		locations.forEach(location -> baseBlock.getWorld().getBlockAt(location).setType(flagColour));
		flagBlockLocations.addAll(locations);
	}

	public void celebrateFireworks(ArrayList<FireworkMeta> fireworks, World world) {

		for (int i = 0; i < celebrateBlocks.size(); i++) {

			for (int j = 0; j < fireworks.size(); j++) {
				Firework fw2 = (Firework) world.spawnEntity(celebrateBlocks.get(i).clone().add(0, maxScore, 0),
						EntityType.FIREWORK);
				fw2.setFireworkMeta(fireworks.get(j));
			}
		}
	}

	public void givePlayHomeItemToAll() {
		for (Entry<UUID, CustomItem> playerHomeItem : playerHomeItemById.entrySet()) {
			Player player = Bukkit.getPlayer(playerHomeItem.getKey());
			player.getInventory().addItem(playerHomeItem.getValue().asItem());
		}
	}

	public void playerHomeAdd(UUID uniqueId, Location location) {
		playerHomeLocationById.put(uniqueId, location);
	}

	public void playerHomeItemAdd(UUID uuid, CustomItem c) {
		playerHomeItemById.put(uuid, c);
	}

	public Location playerHomeLocation(UUID uuid) {
		return playerHomeLocationById.get(uuid);
	}

	public int playerHomeSize() {
		return playerHomeLocationById.size();
	}

	public void RemoveFlags() {
		flagBlockLocations.forEach(l -> l.getBlock().setType(Material.AIR));
		flagBlockLocations.clear();
		playerHomeLocationById.values().forEach(l -> l.getBlock().setType(Material.AIR));
		playerHomeLocationById.clear();
		playerHomeItemById.clear();
	}

	public void teleportAllPlayersHome() {
		for (Entry<UUID, Location> entry : playerHomeLocationById.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			player.teleport(entry.getValue().clone().add(0, 3, 0));
		}
	}
}

package me.jamoowns.moddingminecraft.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.time.TimeConstants;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class RandomChestsFeatureListener implements IGameEventListener {

	private ModdingMinecraft javaPlugin;

	private final Random RANDOM;

	private Integer timerTaskId;

	public RandomChestsFeatureListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		RANDOM = new Random();
		timerTaskId = null;
		onEnabled();
	}

	@Override
	public final void onDisabled() {
		if (timerTaskId != null) {
			Bukkit.getScheduler().cancelTask(timerTaskId);
		}
	}

	@Override
	public final void onEnabled() {
		if (timerTaskId == null) {
			timerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, this::randomChestSpawn,
					RANDOM.nextInt((int) (TimeConstants.ONE_MINUTE * 2)) + TimeConstants.ONE_MINUTE,
					RANDOM.nextInt((int) (TimeConstants.ONE_MINUTE * 2)) + TimeConstants.ONE_MINUTE);
		}
	}

	@Override
	public final void onServerStop() {
		/* Empty. */
	}

	private void randomChestSpawn() {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.RANDOM_CHESTS)) {
			List<Player> players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
			Player p = players.get(RANDOM.nextInt(players.size()));

			int attempts = 0;
			boolean done = false;
			while (attempts < 30 && !done) {
				Location chestLocation = p.getLocation().add(RANDOM.nextInt(40) - 20, RANDOM.nextInt(6),
						RANDOM.nextInt(40) - 20);

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
				attempts++;
			}
		} else {
			onDisabled();
		}
	}
}

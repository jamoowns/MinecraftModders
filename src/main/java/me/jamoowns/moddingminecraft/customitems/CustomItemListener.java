package me.jamoowns.moddingminecraft.customitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import me.jamoowns.moddingminecraft.ModdingMinecraft;

public final class CustomItemListener implements Listener {

	private static int MAX_RANGE = 30;

	private ModdingMinecraft javaPlugin;

	public CustomItemListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

	@EventHandler
	public final void onBlockPlace(BlockPlaceEvent event) {
		CustomItem customItem = javaPlugin.customItems().getItem(event.getItemInHand().getItemMeta().getDisplayName());
		if (customItem != null && customItem.hasBlockPlaceEvent()) {
			event.getBlockPlaced().setType(Material.AIR);
			customItem.blockPlaceEvent().accept(event);
		}
	}

	@EventHandler
	public final void onEntityShootBowEvent(EntityShootBowEvent event) {
		if (event.getProjectile() != null && event.getConsumable() != null
				&& event.getConsumable().getItemMeta() != null) {
			event.getProjectile().setCustomName(event.getConsumable().getItemMeta().getDisplayName());
		}
	}

	@EventHandler
	public final void onPlayerInteractEvent(PlayerInteractEvent event) {
		Block target = event.getPlayer().getTargetBlockExact(MAX_RANGE);
		if (target != null) {
			lineBetween(event.getPlayer().getLocation(), target.getLocation(), 0.1).forEach(loc -> {
				event.getPlayer().getWorld().spawnParticle(Particle.TOTEM, loc, 1, 0, 0, 0, 0);
			});
		} else {
			for (double d = 0; d <= MAX_RANGE; d += 0.1) {
				event.getPlayer().getWorld().spawnParticle(Particle.FLAME, event.getPlayer().getEyeLocation()
						.add(event.getPlayer().getEyeLocation().getDirection().multiply(d)), 1, 0, 0, 0, 0);
			}
		}
	}

	@EventHandler
	public final void onPotionSplashEvent(PotionSplashEvent event) {
		Projectile entity = event.getEntity();
		CustomItem customItem = javaPlugin.customItems().getItem(entity.getCustomName());
		if (customItem != null && customItem.hasPotionSplashEvent()) {
			customItem.potionSplashEvent().accept(event);
			event.getEntity().remove();
		}
	}

	@EventHandler
	public final void onProjectileHit(ProjectileHitEvent event) {
		Projectile entity = event.getEntity();
		CustomItem customItem = javaPlugin.customItems().getItem(entity.getCustomName());
		if (customItem != null && customItem.hasProjectileHitEvent()) {
			customItem.projectileHitEvent().accept(event);
			event.getEntity().remove();
		}
	}

	@EventHandler
	public final void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		Projectile entity = event.getEntity();
		CustomItem customItem = javaPlugin.customItems().getItem(entity.getCustomName());
		if (customItem != null && customItem.hasProjectileLaunchEvent()) {
			customItem.projectileLaunchEvent().accept(event);
		}
		ProjectileSource shooter = event.getEntity().getShooter();
		if (shooter instanceof Player) {
			ItemStack item = ((Player) shooter).getInventory().getItemInMainHand();
			if (event.getEntity() != null && item != null && item.getItemMeta() != null) {
				event.getEntity().setCustomName(item.getItemMeta().getDisplayName());
			}
		}
	}

	private final List<Location> lineBetween(Location loc1, Location loc2, double ticksBetween) {
		ArrayList<Location> locations = new ArrayList<>();
		for (double d = 0; d <= loc2.distance(loc1); d += 0.1) {
			locations.add(loc1.clone().add(loc2.clone().subtract(loc1).toVector().multiply(d)));
		}
		return locations;
	}
}

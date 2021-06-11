package me.jamoowns.moddingminecraft.customitems;

import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class CustomItemListener implements IGameEventListener {

	private ModdingMinecraft javaPlugin;

	public CustomItemListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

	@Override
	public final void cleanup() {
		/* Empty. */
	}

	@EventHandler
	public final void onBlockPlace(BlockPlaceEvent event) {
		javaPlugin.customItems().getItem(event.getItemInHand()).filter(CustomItem::hasBlockPlaceEvent)
				.map(CustomItem::blockPlaceEvent).ifPresent(c -> c.accept(event));
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
		if (event.getItem() != null) {
			Optional<CustomItem> customItem = javaPlugin.customItems().getItem(event.getItem())
					.filter(CustomItem::hasSpellCastEvent);

			if (customItem.isPresent() && isLeftClick(event.getAction())) {
				CustomItem item = customItem.get();
				Block target = event.getPlayer().getTargetBlockExact(item.maxRange());
				if (target != null) {
					for (double d = 0; d <= target.getLocation().distance(event.getPlayer().getLocation()); d += 0.1) {
						event.getPlayer().getWorld().spawnParticle(Particle.TOTEM, event.getPlayer().getEyeLocation()
								.add(event.getPlayer().getEyeLocation().getDirection().multiply(d)), 1, 0, 0, 0, 0);
					}
					item.spellCastEvent()
							.accept(new SpellCastEvent(target.getLocation().add(0.5, 0.5, 0.5), event.getPlayer()));
					if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
						event.getItem().setAmount(event.getItem().getAmount() - 1);
					}
				} else {
					for (double d = 0; d <= item.maxRange(); d += 0.1) {
						event.getPlayer().getWorld().spawnParticle(Particle.FLAME, event.getPlayer().getEyeLocation()
								.add(event.getPlayer().getEyeLocation().getDirection().multiply(d)), 1, 0, 0, 0, 0);
					}
				}
			}
		}
	}

	@EventHandler
	public final void onPotionSplashEvent(PotionSplashEvent event) {
		javaPlugin.customItems().getItem(event.getEntity()).filter(CustomItem::hasPotionSplashEvent)
				.map(CustomItem::potionSplashEvent).ifPresent(c -> {
					c.accept(event);
					event.getEntity().remove();
				});
	}

	@EventHandler
	public final void onProjectileHit(ProjectileHitEvent event) {
		javaPlugin.customItems().getItem(event.getEntity()).filter(CustomItem::hasProjectileHitEvent)
				.map(CustomItem::projectileHitEvent).ifPresent(c -> {
					c.accept(event);
					event.getEntity().remove();
				});
	}

	@EventHandler
	public final void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		Projectile entity = event.getEntity();
		javaPlugin.customItems().getItem(entity).filter(CustomItem::hasProjectileLaunchEvent)
				.map(CustomItem::projectileLaunchEvent).ifPresent(c -> c.accept(event));
		ProjectileSource shooter = event.getEntity().getShooter();
		if (shooter instanceof Player) {
			ItemStack projectileSource = ((Player) shooter).getInventory().getItemInMainHand();
			if (projectileSource.getType() == Material.BOW || projectileSource.getType() == Material.CROSSBOW) {
				javaPlugin.customItems().getItem(projectileSource).filter(CustomItem::hasProjectileLaunchEvent)
						.map(CustomItem::projectileLaunchEvent).ifPresent(c -> c.accept(event));
			}
			if (event.getEntityType() != EntityType.ARROW && projectileSource != null
					&& projectileSource.getItemMeta() != null) {
				event.getEntity().setCustomName(projectileSource.getItemMeta().getDisplayName());
			}
		}
	}

	private final boolean isLeftClick(Action action) {
		switch (action) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				return true;
			case PHYSICAL:
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
			default:
				return false;
		}
	}
}

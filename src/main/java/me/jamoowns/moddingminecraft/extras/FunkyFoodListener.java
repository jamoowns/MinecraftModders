package me.jamoowns.moddingminecraft.extras;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public class FunkyFoodListener implements IGameEventListener {

	private ModdingMinecraft javaPlugin;

	public FunkyFoodListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

	@Override
	public final ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return IGameEventListener.ALWAYS_ENABLED;
	}

	@Override
	public final void onDisabled() {
		/* Empty. */
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
	}

	@EventHandler
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

	@Override
	public final void onServerStop() {
		/* Empty. */
	}
}

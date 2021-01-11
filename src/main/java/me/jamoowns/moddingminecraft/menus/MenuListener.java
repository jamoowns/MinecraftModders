package me.jamoowns.moddingminecraft.menus;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public final class MenuListener implements Listener {

	private Map<Inventory, ICustomMenu> menus;

	public MenuListener() {
		menus = new HashMap<>();
	}

	@EventHandler
	public final void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getInventory();

		if (menus.containsKey(inventory)) {
			ICustomMenu menu = menus.get(inventory);
			menu.getAction(event.getCurrentItem()).accept(player);
			event.setCancelled(true);
			event.getClickedInventory().setItem(event.getSlot(), menu.menuItem(event.getCurrentItem()).asItem());
		}
	}

	public final void register(ICustomMenu menu) {
		menus.put(menu.asInventory(), menu);
	}
}

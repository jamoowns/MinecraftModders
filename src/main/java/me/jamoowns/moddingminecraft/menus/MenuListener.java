package me.jamoowns.moddingminecraft.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
			event.setCancelled(true);
			if (event.getCurrentItem() != null && event.getClickedInventory().equals(event.getInventory())) {
				Optional<CustomMenuItem> customItem = menu.menuItem(event.getCurrentItem());
				if (customItem.isPresent()) {
					menu.getAction(event.getCurrentItem()).accept(player);
					event.getClickedInventory().setItem(event.getSlot(), customItem.get().asItem());
				}
			}
		}
	}

	public final void register(ICustomMenu menu) {
		menus.put(menu.asInventory(), menu);
	}
}

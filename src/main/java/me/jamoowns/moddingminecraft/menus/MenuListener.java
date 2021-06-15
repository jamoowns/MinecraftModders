package me.jamoowns.moddingminecraft.menus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class MenuListener implements IGameEventListener {

	private Map<Inventory, ICustomMenu> menus;

	public MenuListener() {
		menus = new HashMap<>();
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
	public final void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getInventory();

		if (menus.containsKey(inventory)) {
			event.setCancelled(true);
			ICustomMenu menu = menus.get(inventory);
			if (event.getCurrentItem() != null) {
				Optional<CustomMenuItem> customMenuItem = menu.menuItem(event.getCurrentItem());
				if (!customMenuItem.isPresent()) {
					return;
				}
				boolean wholeStack = event.isShiftClick();
				if (event.getClickedInventory().equals(event.getInventory())) {
					ItemStack itemToSet = customMenuItem.get().asItem();
					if (wholeStack) {
						itemToSet.setAmount(itemToSet.getType().getMaxStackSize());
					}
					menu.getAction(itemToSet).accept(player);
					event.getClickedInventory().setItem(event.getSlot(), customMenuItem.get().asItem());
				} else if (event.getClickedInventory().equals(player.getInventory())) {
					event.getCurrentItem().setAmount(wholeStack ? 0 : event.getCurrentItem().getAmount() - 1);
				}
			}
		}

	}

	@Override
	public final void onServerStop() {
		/* Empty. */
	}

	public final void register(ICustomMenu menu) {
		menu.asInventories().forEach(inventory -> menus.put(inventory, menu));
	}
}

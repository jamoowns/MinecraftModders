package me.jamoowns.moddingminecraft.customitems;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public final class CustomItems {

	private Map<String, CustomItem> publicCustomItemsByName;

	private Map<String, CustomItem> allCustomItemsByName;

	public CustomItems() {
		publicCustomItemsByName = new HashMap<>();
		allCustomItemsByName = new HashMap<>();
	}

	public final Collection<CustomItem> allItems() {
		return publicCustomItemsByName.values();
	}

	/**
	 * Register a publicly accessible item.
	 * 
	 * @param item item to register
	 */
	public final void register(CustomItem item) {
		allCustomItemsByName.put(item.name(), item);
		publicCustomItemsByName.put(item.name(), item);
	}

	/**
	 * Register an item but dont include it in the item list.
	 * 
	 * @param item item to register
	 */
	public final void silentRegister(CustomItem item) {
		allCustomItemsByName.put(item.name(), item);
	}

	protected final Optional<CustomItem> getItem(ItemStack item) {
		if (item.getItemMeta() == null) {
			return Optional.empty();
		}
		CustomItem customItem = allCustomItemsByName.get(item.getItemMeta().getDisplayName());
		if (customItem == null) {
			return Optional.empty();
		}
		return Optional.of(customItem);
	}

	protected final Optional<CustomItem> getItem(Projectile projectile) {
		if (projectile.getCustomName() == null) {
			return Optional.empty();
		}
		CustomItem customItem = allCustomItemsByName.get(projectile.getCustomName());
		if (customItem == null) {
			return Optional.empty();
		}
		return Optional.of(customItem);
	}
}

package me.jamoowns.moddingminecraft.customitems;

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

	public final Iterable<CustomItem> allItems() {
		return publicCustomItemsByName.values();
	}

	public final Optional<CustomItem> getItem(ItemStack item) {
		if (item.getItemMeta() == null) {
			return Optional.empty();
		}
		CustomItem customItem = allCustomItemsByName.get(item.getItemMeta().getDisplayName());
		if (customItem == null) {
			return Optional.empty();
		}
		return Optional.of(customItem);
	}

	public final Optional<CustomItem> getItem(Projectile projectile) {
		if (projectile.getCustomName() == null) {
			return Optional.empty();
		}
		CustomItem customItem = allCustomItemsByName.get(projectile.getCustomName());
		if (customItem == null) {
			return Optional.empty();
		}
		return Optional.of(customItem);
	}

	public final void register(CustomItem item) {
		allCustomItemsByName.put(item.name(), item);
		publicCustomItemsByName.put(item.name(), item);
	}

	public final void silentRegister(CustomItem item) {
		allCustomItemsByName.put(item.name(), item);
	}
}

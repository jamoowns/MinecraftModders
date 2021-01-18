package me.jamoowns.moddingminecraft.customitems;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public final class CustomItems {

	private Map<String, CustomItem> customItemsByName;

	public CustomItems() {
		customItemsByName = new HashMap<>();
	}

	public final Iterable<CustomItem> allItems() {
		return customItemsByName.values();
	}

	public final Optional<CustomItem> getItem(ItemStack item) {
		if (item.getItemMeta() == null) {
			return Optional.empty();
		}
		return Optional.of(customItemsByName.get(item.getItemMeta().getDisplayName()));
	}

	public final Optional<CustomItem> getItem(Projectile projectile) {
		if (projectile.getCustomName() == null) {
			return Optional.empty();
		}
		return Optional.of(customItemsByName.get(projectile.getCustomName()));
	}

	public final void register(CustomItem item) {
		customItemsByName.put(item.name(), item);
	}
}

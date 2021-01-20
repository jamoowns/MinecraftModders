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
		return Optional.of(allCustomItemsByName.get(item.getItemMeta().getDisplayName()));
	}

	public final Optional<CustomItem> getItem(Projectile projectile) {
		if (projectile.getCustomName() == null) {
			return Optional.empty();
		}
		return Optional.of(allCustomItemsByName.get(projectile.getCustomName()));
	}

	public final void register(CustomItem item) {
		allCustomItemsByName.put(item.name(), item);
		publicCustomItemsByName.put(item.name(), item);
	}

	public final void silentRegister(CustomItem item) {
		allCustomItemsByName.put(item.name(), item);
	}
}

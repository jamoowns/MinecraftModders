package me.jamoowns.moddingminecraft.customitems;

import java.util.HashMap;
import java.util.Map;

public class CustomItems {

	private Map<String, CustomItem> customItemsByName;

	public CustomItems() {
		customItemsByName = new HashMap<>();
	}

	public Iterable<CustomItem> allItems() {
		return customItemsByName.values();
	}

	public final CustomItem getItem(String itemName) {
		return customItemsByName.get(itemName);
	}

	public final void register(CustomItem item) {
		customItemsByName.put(item.name(), item);
	}
}

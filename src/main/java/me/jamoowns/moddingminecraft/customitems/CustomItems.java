package me.jamoowns.moddingminecraft.customitems;

import java.util.HashMap;
import java.util.Map;

public class CustomItems {

	private Map<String, CustomItem> customItemsByName;

	public CustomItems() {
		customItemsByName = new HashMap<>();
	}

	public final Map<String, CustomItem> customItemsByName() {
		return customItemsByName;
	}
}

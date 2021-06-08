package me.jamoowns.moddingminecraft.menus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.fated.Collections;
import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.customitems.ItemCategory;

public class ItemMenu implements ICustomMenu {

	private Inventory inventory;

	private List<CustomMenuItem> menuItems;

	public ItemMenu(ModdingMinecraft javaPlugin) {
		List<CustomItem> allItems = Lists.newArrayList(javaPlugin.customItems().allItems());

		inventory = Bukkit.createInventory(null, (int) (Math.ceil(allItems.size() / 9.0) * 9.0), "Custom Items");

		int column = 0;
		int row = 0;
		Map<ItemCategory, List<CustomItem>> itemsByCategory = allItems.stream()
				.collect(Collectors.groupingBy(CustomItem::getCategory));
		for (List<CustomItem> itemsInCategory : itemsByCategory.values()) {
			for (CustomItem item : itemsInCategory) {
				inventory.setItem((row * 9) + column, item.asItem());
				column++;
			}
			row++;
		}

		menuItems = allItems.stream().map(
				item -> new CustomMenuItem(item.name(), item.material(), p -> p.getInventory().addItem(item.asItem())))
				.collect(Collectors.toList());
	}

	@Override
	public final Inventory asInventory() {
		return inventory;
	}

	@Override
	public final Optional<CustomMenuItem> menuItem(ItemStack item) {
		if (item.getItemMeta() == null) {
			return Optional.empty();
		}
		return Collections.find(menuItems, CustomMenuItem::displayName, item.getItemMeta().getDisplayName());
	}
}

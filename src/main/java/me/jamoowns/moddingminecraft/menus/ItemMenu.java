package me.jamoowns.moddingminecraft.menus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.fated.Collections;
import me.jamoowns.moddingminecraft.customitems.CustomItem;

public class ItemMenu implements ICustomMenu {

	private Inventory inventory;

	private List<CustomMenuItem> menuItems;

	public ItemMenu(ModdingMinecraft javaPlugin) {
		List<CustomItem> allItems = Lists.newArrayList(javaPlugin.customItems().allItems());

		Inventory inv = Bukkit.createInventory(null, (int) (Math.ceil(allItems.size() / 9.0) * 9.0), "Custom Items");

		allItems.stream().map(CustomItem::asItem).forEach(inv::addItem);

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

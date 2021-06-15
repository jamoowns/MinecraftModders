package me.jamoowns.moddingminecraft.menus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.customitems.ItemCategory;

public final class ItemMenu implements ICustomMenu {

	private Menu menu;

	public ItemMenu(ModdingMinecraft javaPlugin) {
		Collection<CustomItem> allItems = javaPlugin.customItems().allItems();

		Map<ItemCategory, List<CustomItem>> itemsByCategory = allItems.stream()
				.collect(Collectors.groupingBy(CustomItem::getCategory));

		List<List<CustomMenuItem>> aItemsByGroup = new ArrayList<>();
		for (List<CustomItem> itemsInCategory : itemsByCategory.values()) {
			List<CustomMenuItem> items = itemsInCategory.stream().map(item -> new CustomMenuItem(item.name(),
					item.material(), p -> p.getInventory().addItem(item.asItem()))).collect(Collectors.toList());
			aItemsByGroup.add(items);
		}
		menu = Menu.menu("Custom Items", aItemsByGroup);
	}

	@Override
	public final Inventory asInventory() {
		return menu.asInventory();
	}

	@Override
	public final Consumer<Player> displayMenu() {
		return menu.displayMenu();
	}

	@Override
	public final Consumer<Player> getAction(ItemStack item) {
		return menu.getAction(item);
	}

	@Override
	public final Optional<CustomMenuItem> menuItem(ItemStack item) {
		return menu.menuItem(item);
	}
}

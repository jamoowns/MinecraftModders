package me.jamoowns.moddingminecraft.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.common.fated.Collections;

public final class Menu {

	/** Max rows of an inventory. */
	private static final Integer INVENTORY_MAX_COLUMNS = 9;

	/** Max rows of an inventory. */
	private static final Integer INVENTORY_MAX_ROWS = 6;

	/** Max rows. One space reserved for buttons. */
	private static final Integer MAX_ROWS = INVENTORY_MAX_ROWS - 1;

	public static Menu menu(String aMenuTitle, List<List<CustomMenuItem>> aItemsByGroup) {
		List<Row<CustomMenuItem>> theRows = new ArrayList<>();
		for (List<CustomMenuItem> items : aItemsByGroup) {
			Row<CustomMenuItem> row = new Row<>();
			for (CustomMenuItem item : items) {
				if (!row.hasSpace()) {
					theRows.add(row);
					row = new Row<>();
				}
				row.addItem(item);
			}
			if (!row.isEmpty()) {
				theRows.add(row);
			}
		}
		return new Menu(aMenuTitle, theRows);
	}

	private List<Row<CustomMenuItem>> rows;

	/** Should stay in sync with rows. */
	private List<CustomMenuItem> menuItems;

	private Inventory inventory;

	private CustomMenuItem nextButton;

	private CustomMenuItem backButton;

	private Menu nextMenu;

	private Menu(String aMenuTitle, List<Row<CustomMenuItem>> aRows) {
		this(aMenuTitle, aRows, null);
	}

	private Menu(String aMenuTitle, List<Row<CustomMenuItem>> aRows, Menu previousMenu) {
		if (previousMenu != null) {
			backButton = new CustomMenuItem("Back", Material.RED_WOOL, previousMenu.displayMenu());
		}
		List<Row<CustomMenuItem>> lastRows = aRows.stream().skip(MAX_ROWS).collect(Collectors.toList());
		List<Row<CustomMenuItem>> firstRows = aRows;
		if (!lastRows.isEmpty()) {
			firstRows = aRows.subList(0, MAX_ROWS);
			nextMenu = new Menu(aMenuTitle, lastRows, this);
			nextButton = new CustomMenuItem("Next", Material.GREEN_WOOL, nextMenu.displayMenu());
		}
		rows = firstRows;
		menuItems = new ArrayList<>();
		for (Row<CustomMenuItem> r : rows) {
			menuItems.addAll(r.getItems());
		}
		inventory = Bukkit.createInventory(null, (rows.size() + 1) * 9, aMenuTitle);

		int columnNumber = 0;
		int rowNumber = 0;
		for (Row<CustomMenuItem> itemRow : rows) {
			for (CustomMenuItem item : itemRow.getItems()) {
				if (columnNumber >= INVENTORY_MAX_COLUMNS) {
					columnNumber = 0;
					rowNumber++;
				}
				inventory.setItem((rowNumber * INVENTORY_MAX_COLUMNS) + columnNumber, item.asItem());
				columnNumber++;
			}
			columnNumber = 0;
			rowNumber++;
		}

		if (backButton != null) {
			inventory.setItem((rows.size()) * INVENTORY_MAX_COLUMNS, backButton.asItem());
		}
		if (nextButton != null) {
			inventory.setItem(((rows.size()) * INVENTORY_MAX_COLUMNS) + INVENTORY_MAX_COLUMNS - 1, nextButton.asItem());
		}
	}

	public final List<Inventory> asInventories() {
		List<Inventory> inventories = new ArrayList<>();
		inventories.add(getInventory());
		for (Menu child : childrenMenus()) {
			inventories.add(child.getInventory());
		}
		return inventories;
	}

	public final Consumer<Player> displayMenu() {
		return p -> p.openInventory(getInventory());
	}

	public final Consumer<Player> getAction(ItemStack item) {
		return p -> menuItem(item).get().itemAction().accept(p);
	}

	public final Optional<CustomMenuItem> menuItem(ItemStack item) {
		if (item.getItemMeta() == null) {
			return Optional.empty();
		}
		String displayName = item.getItemMeta().getDisplayName();
		if (nextButton != null && nextButton.displayName().equals(displayName)) {
			return Optional.of(nextButton);
		} else if (backButton != null && backButton.displayName().equals(displayName)) {
			return Optional.of(backButton);
		} else {
			List<CustomMenuItem> allItems = new ArrayList<>();
			allItems.addAll(menuItems);

			Optional<CustomMenuItem> menuItem = Collections.find(menuItems, CustomMenuItem::displayName, displayName);
			if (menuItem.isPresent()) {
				return menuItem;
			}
			for (Menu child : childrenMenus()) {
				menuItem = child.menuItem(item);
				if (menuItem.isPresent()) {
					return menuItem;
				}
			}
			return menuItem;
		}
	}

	private Iterable<Menu> childrenMenus() {
		List<Menu> children = new ArrayList<>();
		Optional<Menu> next = nextMenu();
		while (next.isPresent()) {
			children.add(next.get());
			next = next.get().nextMenu();
		}
		return children;
	}

	private Inventory getInventory() {
		return inventory;
	}

	private Optional<Menu> nextMenu() {
		return Optional.ofNullable(nextMenu);
	}
}

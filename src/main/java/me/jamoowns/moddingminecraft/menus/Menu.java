package me.jamoowns.moddingminecraft.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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

	private Menu(String aMenuTitle, List<Row<CustomMenuItem>> aRows) {
		this(aMenuTitle, aRows, null);
	}

	private Menu(String aMenuTitle, List<Row<CustomMenuItem>> aRows, Menu previousMenu) {
		if (previousMenu != null) {
			backButton = new CustomMenuItem("Back", Material.RED_WOOL, previousMenu.displayMenu());
		}
		List<Row<CustomMenuItem>> firstRows = aRows.subList(0, Math.min(aRows.size() - 1, MAX_ROWS - 1));
		if (aRows.size() > MAX_ROWS) {
			List<Row<CustomMenuItem>> lastRows = aRows.subList(MAX_ROWS - 1, aRows.size() - 1);
			Menu nextMenu = new Menu(aMenuTitle, lastRows);
			nextButton = new CustomMenuItem("Next", Material.GREEN_WOOL, nextMenu.displayMenu());
		}
		rows = firstRows;
		menuItems = new ArrayList<>();
		for (Row<CustomMenuItem> r : rows) {
			menuItems.addAll(r.getItems());
		}
		inventory = Bukkit.createInventory(null, (rows.size() + 1) * 9, aMenuTitle);

		int column = 0;
		int row = 0;
		for (Row<CustomMenuItem> itemRow : rows) {
			for (CustomMenuItem item : itemRow.getItems()) {
				inventory.setItem((row * INVENTORY_MAX_COLUMNS) + column, item.asItem());
				column++;
				if (column >= INVENTORY_MAX_COLUMNS) {
					column = 0;
					row++;
				}
			}
			column = 0;
			row++;
		}

		if (backButton != null) {
			inventory.setItem((rows.size()) * INVENTORY_MAX_COLUMNS, backButton.asItem());
		}
		if (nextButton != null) {
			inventory.setItem(((rows.size()) * INVENTORY_MAX_COLUMNS) + INVENTORY_MAX_COLUMNS - 1, nextButton.asItem());
		}
	}

	public final Inventory asInventory() {
		return inventory;
	}

	public final Consumer<Player> displayMenu() {
		return p -> p.openInventory(asInventory());
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
			return Collections.find(menuItems, CustomMenuItem::displayName, displayName);
		}
	}
}

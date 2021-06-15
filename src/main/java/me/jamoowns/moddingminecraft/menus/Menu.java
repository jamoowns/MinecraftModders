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

	/** Max rows. One space reserved for buttons. */
	private static final Integer MAX_ROWS = 8;

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

	private Menu previousMenu;

	private CustomMenuItem nextButton;

	private CustomMenuItem backButton;

	private Menu(String aMenuTitle, List<Row<CustomMenuItem>> aRows) {
		List<Row<CustomMenuItem>> firstRows = aRows.subList(0, Math.min(aRows.size() - 1, MAX_ROWS - 1));
		if (aRows.size() > MAX_ROWS) {
			List<Row<CustomMenuItem>> lastRows = aRows.subList(MAX_ROWS - 1, rows.size() - 1);
			Menu nextMenu = new Menu(aMenuTitle, lastRows);
			nextButton = new CustomMenuItem("Next", Material.GREEN_WOOL, nextMenu.displayMenu());
			nextMenu.setPreviousMenu(this);
		}
		rows = firstRows;
		menuItems = new ArrayList<>();
		for (Row<CustomMenuItem> r : rows) {
			menuItems.addAll(r.getItems());
		}
		inventory = Bukkit.createInventory(null, Math.max(rows.size(), 1) * 9, aMenuTitle);
		menuItems.stream().map(CustomMenuItem::asItem).forEach(inventory::addItem);
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

	final void setPreviousMenu(Menu aPreviousMenu) {
		previousMenu = aPreviousMenu;
		backButton = new CustomMenuItem("Back", Material.RED_WOOL, previousMenu.displayMenu());
	}
}

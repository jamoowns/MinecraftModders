package me.jamoowns.moddingminecraft.menus;

import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

interface ICustomMenu {

	Inventory asInventory();

	default Consumer<Player> displayMenu() {
		return p -> p.openInventory(asInventory());
	}

	default Consumer<HumanEntity> getAction(ItemStack item) {
		return p -> p.getInventory().addItem(item);
	}

	Optional<CustomMenuItem> menuItem(ItemStack item);
}

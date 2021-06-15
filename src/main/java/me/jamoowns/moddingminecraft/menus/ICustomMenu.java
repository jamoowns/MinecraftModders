package me.jamoowns.moddingminecraft.menus;

import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

interface ICustomMenu {

	Inventory asInventory();

	Consumer<Player> displayMenu();

	Consumer<Player> getAction(ItemStack item);

	Optional<CustomMenuItem> menuItem(ItemStack item);
}

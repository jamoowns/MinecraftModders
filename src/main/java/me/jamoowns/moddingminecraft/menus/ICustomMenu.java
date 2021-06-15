package me.jamoowns.moddingminecraft.menus;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

interface ICustomMenu {

	List<Inventory> asInventories();

	Consumer<Player> displayMenu();

	Consumer<Player> getAction(ItemStack item);

	Optional<CustomMenuItem> menuItem(ItemStack item);
}

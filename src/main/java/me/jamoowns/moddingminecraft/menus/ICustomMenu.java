package me.jamoowns.moddingminecraft.menus;

import java.util.function.Consumer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

interface ICustomMenu {

	Inventory asInventory();

	default Consumer<HumanEntity> getAction(ItemStack item) {
		return p -> p.getInventory().addItem(item);
	}

	CustomMenuItem menuItem(ItemStack item);
}

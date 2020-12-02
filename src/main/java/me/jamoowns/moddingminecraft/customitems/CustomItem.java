package me.jamoowns.moddingminecraft.customitems;

import static java.util.Optional.empty;

import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CustomItem {

	private Material material;

	private Optional<Consumer<BlockPlaceEvent>> blockPlaceEvent;

	private String name;

	private final ItemStack item;

	public CustomItem(Material aItem, String aName) {
		name = aName;
		material = aItem;
		blockPlaceEvent = empty();
		item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(aName);
		item.setItemMeta(itemMeta);
	}

	public final void setBlockPlaceEvent(Consumer<BlockPlaceEvent> event) {
		blockPlaceEvent = Optional.of(event);
	}

	public final Consumer<BlockPlaceEvent> blockPlaceEvent() {
		return blockPlaceEvent.get();
	}

	public final boolean hasBlockPlaceEvent() {
		return blockPlaceEvent.isPresent();
	}

	public final String name() {
		return name;
	}

	public final ItemStack asItem() {
		return item.clone();
	}
}

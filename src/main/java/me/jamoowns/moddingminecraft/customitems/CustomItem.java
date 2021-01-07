package me.jamoowns.moddingminecraft.customitems;

import static java.util.Optional.empty;

import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CustomItem {

	private Material material;

	private Optional<Consumer<BlockPlaceEvent>> blockPlaceEvent;

	private Optional<Consumer<ProjectileHitEvent>> projectileHitEvent;

	private String name;

	private final ItemStack item;

	private Optional<Consumer<PotionSplashEvent>> potionSplashEvent;

	public CustomItem(Material aItem, String aName) {
		name = aName;
		material = aItem;
		blockPlaceEvent = empty();
		projectileHitEvent = empty();
		potionSplashEvent = empty();
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

	public final void setProjectileHitEvent(Consumer<ProjectileHitEvent> event) {
		projectileHitEvent = Optional.of(event);
	}

	public final Consumer<ProjectileHitEvent> projectileHitEvent() {
		return projectileHitEvent.get();
	}

	public final boolean hasProjectileHitEvent() {
		return projectileHitEvent.isPresent();
	}

	public final void setPotionSplashEvent(Consumer<PotionSplashEvent> event) {
		potionSplashEvent = Optional.of(event);
	}

	public final Consumer<PotionSplashEvent> potionSplashEvent() {
		return potionSplashEvent.get();
	}

	public final boolean hasPotionSplashEvent() {
		return potionSplashEvent.isPresent();
	}

	public final String name() {
		return name;
	}

	public final ItemStack asItem() {
		return item.clone();
	}
}

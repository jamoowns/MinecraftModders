package me.jamoowns.moddingminecraft.customitems;

import static java.util.Optional.empty;

import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CustomItem {

	private Optional<Consumer<BlockPlaceEvent>> blockPlaceEvent;

	private Optional<Consumer<ProjectileHitEvent>> projectileHitEvent;

	private Optional<Consumer<ProjectileLaunchEvent>> projectileLaunchEvent;

	private Optional<Consumer<SpellCastEvent>> spellCastEvent;

	private Optional<Consumer<PotionSplashEvent>> potionSplashEvent;

	private String name;

	private final ItemStack item;

	private Integer maxRange;

	public CustomItem(String aName, ItemStack aItem) {
		name = aName;
		blockPlaceEvent = empty();
		projectileHitEvent = empty();
		potionSplashEvent = empty();
		spellCastEvent = empty();
		projectileLaunchEvent = empty();

		item = aItem.clone();
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(aName);
		item.setItemMeta(itemMeta);
	}

	public CustomItem(String aName, Material aMaterial) {
		this(aName, new ItemStack(aMaterial));
	}

	public final ItemStack asItem() {
		return item.clone();
	}

	public final Consumer<BlockPlaceEvent> blockPlaceEvent() {
		return blockPlaceEvent.get();
	}

	public final boolean hasBlockPlaceEvent() {
		return blockPlaceEvent.isPresent();
	}

	public final boolean hasPotionSplashEvent() {
		return potionSplashEvent.isPresent();
	}

	public final boolean hasProjectileHitEvent() {
		return projectileHitEvent.isPresent();
	}

	public final boolean hasProjectileLaunchEvent() {
		return projectileLaunchEvent.isPresent();
	}

	public final boolean hasSpellCastEvent() {
		return spellCastEvent.isPresent();
	}

	public final Material material() {
		return item.getType();
	}

	public final Integer maxRange() {
		return maxRange;
	}

	public final String name() {
		return name;
	}

	public final Consumer<PotionSplashEvent> potionSplashEvent() {
		return potionSplashEvent.get();
	}

	public final Consumer<ProjectileHitEvent> projectileHitEvent() {
		return projectileHitEvent.get();
	}

	public final Consumer<ProjectileLaunchEvent> projectileLaunchEvent() {
		return projectileLaunchEvent.get();
	}

	public final void setBlockPlaceEvent(Consumer<BlockPlaceEvent> event) {
		blockPlaceEvent = Optional.of(event);
	}

	public final void setPotionSplashEvent(Consumer<PotionSplashEvent> event) {
		potionSplashEvent = Optional.of(event);
	}

	public final void setProjectileHitEvent(Consumer<ProjectileHitEvent> event) {
		projectileHitEvent = Optional.of(event);
	}

	public final void setProjectileLaunchEvent(Consumer<ProjectileLaunchEvent> event) {
		projectileLaunchEvent = Optional.of(event);
	}

	public final void setSpellCastEvent(Consumer<SpellCastEvent> event, Integer aMaxRange) {
		spellCastEvent = Optional.of(event);
		maxRange = aMaxRange;
	}

	public final Consumer<SpellCastEvent> spellCastEvent() {
		return spellCastEvent.get();
	}
}

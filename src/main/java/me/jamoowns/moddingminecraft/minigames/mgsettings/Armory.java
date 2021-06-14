package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Stores the kits available.
 */
public final class Armory {

	public enum KitLevel {
		WEAKEST, LOW, AVERAGE, GOOD, STRONGEST
	}

	public enum KitType {
		Archer, Soldier, Warrior, Knight
	}

	private static Map<KitType, GameKit> kits;
	static {
		kits = new HashMap<>();
		kits.put(KitType.Archer, archer());
		kits.put(KitType.Soldier, soldier());
		kits.put(KitType.Warrior, warrior());
		kits.put(KitType.Knight, knight());
	}

	public static GameKit defence(KitLevel level) {
		switch (level) {
			case WEAKEST:
				ItemStack[] items = { (new ItemStack(Material.LEATHER_BOOTS, 1)),
						(new ItemStack(Material.LEATHER_CHESTPLATE, 1)), (new ItemStack(Material.LEATHER_LEGGINGS, 1)),
						(new ItemStack(Material.LEATHER_HELMET, 1)) };
				return new GameKit(items);
			case AVERAGE:
				ItemStack[] items1 = { (new ItemStack(Material.SHIELD, 1)), (new ItemStack(Material.IRON_BOOTS, 1)),
						(new ItemStack(Material.IRON_CHESTPLATE, 1)), (new ItemStack(Material.IRON_LEGGINGS, 1)),
						(new ItemStack(Material.IRON_HELMET, 1)) };
				return new GameKit(items1);
			case GOOD:
				ItemStack[] items2 = { (new ItemStack(Material.SHIELD, 1)), (new ItemStack(Material.DIAMOND_BOOTS, 1)),
						(new ItemStack(Material.DIAMOND_CHESTPLATE, 1)), (new ItemStack(Material.DIAMOND_LEGGINGS, 1)),
						(new ItemStack(Material.DIAMOND_HELMET, 1)) };
				return new GameKit(items2);
			case STRONGEST:
				ItemStack[] items3 = { (new ItemStack(Material.SHIELD, 1)),
						(new ItemStack(Material.NETHERITE_BOOTS, 1)), (new ItemStack(Material.NETHERITE_CHESTPLATE, 1)),
						(new ItemStack(Material.NETHERITE_LEGGINGS, 1)),
						(new ItemStack(Material.NETHERITE_HELMET, 1)) };
				return new GameKit(items3);
			default:
				return new GameKit(Arrays.asList(new ItemStack(Material.DIRT)));
		}
	}

	public static GameKit food(KitLevel level) {
		switch (level) {
			case WEAKEST:
				ItemStack[] items = { (new ItemStack(Material.POTATO, 32)) };
				return new GameKit(items);
			case LOW:
				ItemStack[] items1 = { (new ItemStack(Material.BAKED_POTATO, 32)) };
				return new GameKit(items1);
			case AVERAGE:
				ItemStack[] items2 = { (new ItemStack(Material.COOKED_BEEF, 32)) };
				return new GameKit(items2);
			case GOOD:
				ItemStack[] items3 = { (new ItemStack(Material.GOLDEN_CARROT, 32)) };
				return new GameKit(items3);
			case STRONGEST:
				ItemStack[] items4 = { (new ItemStack(Material.GOLDEN_APPLE, 32)) };
				return new GameKit(items4);
			default:
				return new GameKit(Arrays.asList(new ItemStack(Material.DIRT)));
		}
	}

	public static final GameKit kit(KitType type) {
		return kits.get(type);
	}

	public static GameKit offense(KitLevel level) {
		switch (level) {
			case WEAKEST:
				ItemStack[] items = { (new ItemStack(Material.WOODEN_SWORD, 1)),
						(new ItemStack(Material.WOODEN_AXE, 1)) };
				return new GameKit(items);
			case AVERAGE:
				ItemStack[] items1 = { (new ItemStack(Material.IRON_SWORD, 1)), (new ItemStack(Material.IRON_AXE, 1)) };
				return new GameKit(items1);
			case GOOD:
				ItemStack[] items2 = { (new ItemStack(Material.DIAMOND_SWORD, 1)),
						(new ItemStack(Material.DIAMOND_AXE, 1)) };
				return new GameKit(items2);
			case STRONGEST:
				ItemStack[] items3 = { (new ItemStack(Material.NETHERITE_SWORD, 1)),
						(new ItemStack(Material.NETHERITE_AXE, 1)) };
				return new GameKit(items3);
			default:
				return new GameKit(Arrays.asList(new ItemStack(Material.DIRT)));
		}
	}

	private static GameKit archer() {
		ItemStack[] items = { (new ItemStack(Material.LEATHER_BOOTS, 1)),
				(new ItemStack(Material.LEATHER_CHESTPLATE, 1)), (new ItemStack(Material.LEATHER_LEGGINGS, 1)),
				(new ItemStack(Material.LEATHER_HELMET, 1)), (new ItemStack(Material.BOW, 1)),
				(new ItemStack(Material.ARROW, 64)), (new ItemStack(Material.ARROW, 64)),
				(new ItemStack(Material.ARROW, 64)), (new ItemStack(Material.ARROW, 64)),
				(new ItemStack(Material.BAKED_POTATO, 32)) };
		return new GameKit(items);
	}

	private static GameKit knight() {
		ItemStack[] items = { (new ItemStack(Material.NETHERITE_SWORD, 1)), (new ItemStack(Material.SHIELD, 1)),
				(new ItemStack(Material.NETHERITE_BOOTS, 1)), (new ItemStack(Material.NETHERITE_CHESTPLATE, 1)),
				(new ItemStack(Material.NETHERITE_LEGGINGS, 1)), (new ItemStack(Material.NETHERITE_HELMET, 1)),
				(new ItemStack(Material.BAKED_POTATO, 32)) };
		return new GameKit(items);
	}

	private static GameKit soldier() {
		ItemStack[] items = { (new ItemStack(Material.IRON_SWORD, 1)), (new ItemStack(Material.IRON_BOOTS, 1)),
				(new ItemStack(Material.IRON_CHESTPLATE, 1)), (new ItemStack(Material.IRON_LEGGINGS, 1)),
				(new ItemStack(Material.IRON_HELMET, 1)), (new ItemStack(Material.GOLDEN_CARROT, 32)) };
		return new GameKit(items);
	}

	private static GameKit warrior() {
		ItemStack[] items = { (new ItemStack(Material.DIAMOND_AXE, 1)), (new ItemStack(Material.DIAMOND_BOOTS, 1)),
				(new ItemStack(Material.DIAMOND_CHESTPLATE, 1)), (new ItemStack(Material.DIAMOND_LEGGINGS, 1)),
				(new ItemStack(Material.DIAMOND_HELMET, 1)), (new ItemStack(Material.COOKED_BEEF, 32)) };
		return new GameKit(items);
	}

	private Armory() {
		/* Empty. */
	}
}

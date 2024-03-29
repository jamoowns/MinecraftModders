package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public final class GameKit {

	private ItemStack[] items;

	public GameKit(ItemStack[] aItems) {
		items = aItems;
	}

	public GameKit(List<ItemStack> aItems) {
		items = aItems.toArray(new ItemStack[aItems.size()]);
	}

	/**
	 * Add some contraband to the {@link GameKit}.
	 * 
	 * @param contraband contraband to add to the kit
	 * @return the combined {@link GameKit}
	 */
	public final GameKit addContraband(ItemStack contraband) {
		ArrayList<ItemStack> asArray = new ArrayList<>(Arrays.asList(items));
		asArray.add(contraband);
		return new GameKit(asArray);
	}

	/**
	 * Combine with another {@link GameKit}.
	 * 
	 * @param other other kit to combine
	 * @return the combined {@link GameKit}
	 */
	public final GameKit combine(GameKit other) {
		ArrayList<ItemStack> asArray = new ArrayList<>(Arrays.asList(items));
		for (ItemStack item : other.items()) {
			asArray.add(item);
		}
		return new GameKit(asArray);
	}

	public final ItemStack[] items() {
		return items;
	}
}

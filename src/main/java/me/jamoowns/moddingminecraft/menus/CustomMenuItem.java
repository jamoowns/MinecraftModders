package me.jamoowns.moddingminecraft.menus;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class CustomMenuItem {

	private String title;

	private boolean isToggle;
	private Consumer<Player> enableAction;
	private Consumer<Player> disableAction;
	private Supplier<Boolean> isEnabled;
	private ItemStack item;
	private ItemStack disabledItem;
	private Consumer<Player> onClickAction;

	public CustomMenuItem(final String aTitle, Material icon, Consumer<Player> aOnClickAction) {
		title = aTitle;
		item = new ItemStack(icon);
		ItemMeta itemMeta = item.getItemMeta().clone();
		itemMeta.setDisplayName(title);
		item.setItemMeta(itemMeta);

		disabledItem = new ItemStack(Material.STONE);
		ItemMeta disabledItemMeta = disabledItem.getItemMeta();
		disabledItemMeta.setDisplayName(title);
		disabledItem.setItemMeta(disabledItemMeta);

		onClickAction = aOnClickAction;
		isToggle = false;
	}

	public CustomMenuItem(final String aTitle, Material icon, Consumer<Player> aEnableAction,
			Consumer<Player> aDisableAction, Supplier<Boolean> aIsEnabled) {
		title = aTitle;
		item = new ItemStack(icon);
		ItemMeta itemMeta = item.getItemMeta().clone();
		itemMeta.setDisplayName(title);
		item.setItemMeta(itemMeta);

		disabledItem = new ItemStack(Material.STONE);
		ItemMeta disabledItemMeta = disabledItem.getItemMeta();
		disabledItemMeta.setDisplayName(title);
		disabledItem.setItemMeta(disabledItemMeta);

		enableAction = aEnableAction;
		disableAction = aDisableAction;
		isEnabled = aIsEnabled;
		isToggle = true;
	}

	public final ItemStack asItem() {
		if (!isToggle || isEnabled.get()) {
			return item.clone();
		} else {
			return disabledItem.clone();
		}
	}

	public final String displayName() {
		return title;
	}

	public final Consumer<Player> itemAction() {
		if (isToggle) {
			return isEnabled.get() ? disableAction : enableAction;
		}
		return onClickAction;
	}
}

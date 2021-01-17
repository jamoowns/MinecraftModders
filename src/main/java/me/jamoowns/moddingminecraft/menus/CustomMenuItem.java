package me.jamoowns.moddingminecraft.menus;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class CustomMenuItem {

	private String title;

	private boolean isToggle;
	private Consumer<HumanEntity> enableAction;
	private Consumer<HumanEntity> disableAction;
	private Supplier<Boolean> isEnabled;
	private ItemStack item;
	private ItemStack disabledItem;
	private Consumer<HumanEntity> onClickAction;

	public CustomMenuItem(final String aTitle, Material icon, Consumer<HumanEntity> aOnClickAction) {
		title = aTitle;
		item = new ItemStack(icon);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(title);
		item.setItemMeta(itemMeta);

		disabledItem = new ItemStack(Material.STONE);
		ItemMeta disabledItemMeta = disabledItem.getItemMeta();
		disabledItemMeta.setDisplayName(title);
		disabledItem.setItemMeta(disabledItemMeta);

		onClickAction = aOnClickAction;
		isToggle = false;
	}

	public CustomMenuItem(final String aTitle, Material icon, Consumer<HumanEntity> aEnableAction,
			Consumer<HumanEntity> aDisableAction, Supplier<Boolean> aIsEnabled) {
		title = aTitle;
		item = new ItemStack(icon);
		ItemMeta itemMeta = item.getItemMeta();
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
		if (isEnabled.get()) {
			return item.clone();
		} else {
			return disabledItem.clone();
		}
	}

	public final String displayName() {
		return title;
	}

	public final void onClick(HumanEntity p) {
		if (isToggle) {
			if (isEnabled.get()) {
				disableAction.accept(p);
			} else {
				enableAction.accept(p);
			}
		} else {
			onClickAction.accept(p);
		}
	}
}

package me.jamoowns.moddingminecraft.menus;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.fated.Collections;
import me.jamoowns.moddingminecraft.features.Feature;

public final class FeaturesMenu implements ICustomMenu {

	private ArrayList<CustomMenuItem> featureMenuItems;
	private Inventory inventory;

	public FeaturesMenu(ModdingMinecraft javaPlugin) {
		featureMenuItems = new ArrayList<>();
		Feature[] features = Feature.values();

		for (Feature feature : features) {
			switch (feature) {
				case BATTLE_ROYALE:
					featureMenuItems.add(new CustomMenuItem("Battle Royale", Material.GOLD_BLOCK,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case EGG_WITCH:
					featureMenuItems.add(new CustomMenuItem("Egg Witch", Material.DIAMOND_BLOCK,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case FUNKY_MOB_DEATH:
					featureMenuItems.add(new CustomMenuItem("Random mob deaths", Material.LAPIS_BLOCK,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case IRON_GOLEM:
					featureMenuItems.add(new CustomMenuItem("Iron golem player death", Material.IRON_BLOCK,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case PLAYER_TRAIL:
					featureMenuItems.add(new CustomMenuItem("Player trails", Material.LIME_WOOL,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case RANDOM_BUCKET:
					featureMenuItems.add(new CustomMenuItem("Random Buckets", Material.BUCKET,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case RANDOM_CHESTS:
					featureMenuItems.add(new CustomMenuItem("Random Chests", Material.CHEST,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case RANDOM_ENCHANT:
					featureMenuItems.add(new CustomMenuItem("Random Enchants", Material.ENCHANTING_TABLE,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case WINFRED:
					featureMenuItems.add(new CustomMenuItem("Winfred", Material.BREWING_STAND,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
				case ZOMBIE_BELL:
					featureMenuItems.add(new CustomMenuItem("Zombie bell", Material.BELL,
							() -> javaPlugin.featureTracker().enable(feature),
							() -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(javaPlugin, feature)));
			}
		}
		inventory = Bukkit.createInventory(null, (int) (Math.ceil(featureMenuItems.size() / 9) * 9 + 9),
				"Features Menu");
	}

	@Override
	public final Inventory asInventory() {
		inventory.clear();
		featureMenuItems.stream().map(CustomMenuItem::asItem).forEach(inventory::addItem);
		return inventory;
	}

	@Override
	public final Consumer<HumanEntity> getAction(ItemStack item) {
		return p -> menuItem(item).onClick(p);
	}

	@Override
	public final CustomMenuItem menuItem(ItemStack item) {
		return Collections.find(featureMenuItems, CustomMenuItem::displayName, item.getItemMeta().getDisplayName())
				.get();
	}
}

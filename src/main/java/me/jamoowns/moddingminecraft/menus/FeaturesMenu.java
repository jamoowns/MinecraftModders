package me.jamoowns.moddingminecraft.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

	private List<CustomMenuItem> featureMenuItems;
	private Inventory inventory;

	public FeaturesMenu(ModdingMinecraft javaPlugin) {
		featureMenuItems = new ArrayList<>();
		Feature[] features = Feature.values();

		for (Feature feature : features) {
			switch (feature) {
				case BATTLE_ROYALE:
					featureMenuItems.add(new CustomMenuItem("Battle Royale", Material.DIAMOND_SWORD,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case EGG_WITCH:
					featureMenuItems.add(new CustomMenuItem("Egg Witch", Material.EGG,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case FUNKY_MOB_DEATH:
					featureMenuItems.add(new CustomMenuItem("Random mob deaths", Material.REDSTONE,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case IRON_GOLEM:
					featureMenuItems.add(new CustomMenuItem("Iron golem player death", Material.IRON_BLOCK,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case PLAYER_TRAIL:
					featureMenuItems.add(new CustomMenuItem("Player trails", Material.LIME_WOOL,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case RANDOM_BUCKET:
					featureMenuItems.add(new CustomMenuItem("Random Buckets", Material.BUCKET,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case RANDOM_CHESTS:
					featureMenuItems.add(new CustomMenuItem("Random Chests", Material.CHEST,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case RANDOM_ENCHANT:
					featureMenuItems.add(new CustomMenuItem("Random Enchants", Material.ENCHANTING_TABLE,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case WINFRED:
					featureMenuItems.add(new CustomMenuItem("Winfred", Material.BREWING_STAND,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case ZOMBIE_BELL:
					featureMenuItems.add(new CustomMenuItem("Zombie bell", Material.BELL,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case STABLE_WEATHER:
					featureMenuItems.add(new CustomMenuItem("Stable Weather", Material.GOLDEN_HELMET,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case HEAVY_BLOCKS:
					featureMenuItems.add(new CustomMenuItem("Heavy Blocks", Material.ANVIL,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				case LIGHT_BLOCKS:
					featureMenuItems.add(new CustomMenuItem("Light Blocks", Material.SAND,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
				default:
					featureMenuItems.add(new CustomMenuItem(feature.toString(), Material.TARGET,
							p -> javaPlugin.featureTracker().enable(feature),
							p -> javaPlugin.featureTracker().disable(feature),
							() -> javaPlugin.featureTracker().isFeatureActive(feature)));
					break;
			}
		}
		inventory = Bukkit.createInventory(null, (int) (Math.ceil(featureMenuItems.size() / 9.0) * 9.0 + 9),
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
		return p -> menuItem(item).get().onClick(p);
	}

	@Override
	public final Optional<CustomMenuItem> menuItem(ItemStack item) {
		if (item.getItemMeta() == null) {
			return Optional.empty();
		}
		return Collections.find(featureMenuItems, CustomMenuItem::displayName, item.getItemMeta().getDisplayName());
	}
}

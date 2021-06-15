package me.jamoowns.moddingminecraft.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.features.Feature;

public final class FeaturesMenu implements ICustomMenu {

	private Menu menu;

	public FeaturesMenu(ModdingMinecraft javaPlugin) {
		List<CustomMenuItem> featureMenuItems = new ArrayList<>();
		Feature[] features = Feature.values();

		for (Feature feature : features) {
			switch (feature) {
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
		List<List<CustomMenuItem>> groupedFeatureMenuItems = new ArrayList<>();
		groupedFeatureMenuItems.add(featureMenuItems);
		menu = Menu.menu("Features Menu", groupedFeatureMenuItems);
	}

	@Override
	public final List<Inventory> asInventories() {
		return menu.asInventories();
	}

	@Override
	public final Consumer<Player> displayMenu() {
		return menu.displayMenu();
	}

	@Override
	public final Consumer<Player> getAction(ItemStack item) {
		return menu.getAction(item);
	}

	@Override
	public final Optional<CustomMenuItem> menuItem(ItemStack item) {
		return menu.menuItem(item);
	}
}

package me.jamoowns.moddingminecraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import me.jamoowns.moddingminecraft.commands.CommandMinecraftModders;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.customitems.CustomItemListener;
import me.jamoowns.moddingminecraft.customitems.CustomItems;
import me.jamoowns.moddingminecraft.features.Feature;
import me.jamoowns.moddingminecraft.features.FeatureTracker;
import me.jamoowns.moddingminecraft.features.IFeatureListener;
import me.jamoowns.moddingminecraft.features.PlayerTrailFeatureListener;
import me.jamoowns.moddingminecraft.features.RandomChestsFeatureListener;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.menus.FeaturesMenu;
import me.jamoowns.moddingminecraft.menus.ItemMenu;
import me.jamoowns.moddingminecraft.menus.MenuListener;
import me.jamoowns.moddingminecraft.minigames.blockhunter.BlockHunterListener;
import me.jamoowns.moddingminecraft.teams.Teams;

public class ModdingMinecraft extends JavaPlugin implements IFeatureListener {

	private List<IGameEventListener> gameListeners;

	private CustomItems customItems;

	private CommandMinecraftModders commandExecutor;

	private Teams teams;

	private FeatureTracker featureTracker;

	private PlayerTrailFeatureListener playerTrailFeatureListener;

	private RandomChestsFeatureListener randomChestsFeatureListener;

	public final CommandMinecraftModders commandExecutor() {
		return commandExecutor;
	}

	public final CustomItems customItems() {
		return customItems;
	}

	@Override
	public final void featureActivated(Feature feature) {
		Broadcaster.broadcastInfo("Activated: " + feature.name());
		switch (feature) {
			case BATTLE_ROYALE:
				break;
			case EGG_WITCH:
				break;
			case FUNKY_MOB_DEATH:
				break;
			case IRON_GOLEM:
				break;
			case PLAYER_TRAIL:
				break;
			case RANDOM_BUCKET:
				break;
			case RANDOM_CHESTS:
				randomChestsFeatureListener.start();
				break;
			case RANDOM_ENCHANT:
				break;
			case STABLE_WEATHER:
				break;
			case WINFRED:
				break;
			case ZOMBIE_BELL:
				break;
			default:
				break;
		}
	}

	@Override
	public final void featureDeactivated(Feature feature) {
		Broadcaster.broadcastInfo("Deactivated: " + feature.name());
		switch (feature) {
			case BATTLE_ROYALE:
				break;
			case EGG_WITCH:
				break;
			case FUNKY_MOB_DEATH:
				break;
			case IRON_GOLEM:
				break;
			case PLAYER_TRAIL:
				playerTrailFeatureListener.cleanup();
				break;
			case RANDOM_BUCKET:
				break;
			case RANDOM_CHESTS:
				randomChestsFeatureListener.stop();
				break;
			case RANDOM_ENCHANT:
				break;
			case STABLE_WEATHER:
				break;
			case WINFRED:
				break;
			case ZOMBIE_BELL:
				break;
			default:
				break;
		}
	}

	public final FeatureTracker featureTracker() {
		return featureTracker;
	}

	// Fired when plug-in is disabled
	@Override
	public final void onDisable() {
		gameListeners.forEach(IGameEventListener::cleanup);
	}

	// Fired when plug-in is first enabled
	@Override
	public final void onEnable() {
		commandExecutor = new CommandMinecraftModders();
		customItems = new CustomItems();
		teams = new Teams(this);

		featureTracker = new FeatureTracker();
		featureTracker.addListener(this);

		featureTracker().enable(Feature.RANDOM_ENCHANT);
		featureTracker().enable(Feature.ZOMBIE_BELL);
		featureTracker().enable(Feature.EGG_WITCH);
		featureTracker().enable(Feature.RANDOM_BUCKET);
		featureTracker().enable(Feature.FUNKY_MOB_DEATH);
		featureTracker().enable(Feature.IRON_GOLEM);
		featureTracker().enable(Feature.PLAYER_TRAIL);
		featureTracker().enable(Feature.WINFRED);
		featureTracker().enable(Feature.STABLE_WEATHER);

		gameListeners = new ArrayList<>();
		addGameListener(new JamoListener(this));
		addGameListener(new MabListener(this));
		addGameListener(new MoshyListener());
		addGameListener(new BlockHunterListener(this));
		addGameListener(new CustomItemListener(this));

		playerTrailFeatureListener = new PlayerTrailFeatureListener(this);
		addGameListener(playerTrailFeatureListener);

		randomChestsFeatureListener = new RandomChestsFeatureListener(this);
		addGameListener(randomChestsFeatureListener);

		MenuListener menuListener = new MenuListener();
		addGameListener(menuListener);

		FeaturesMenu featureMenu = new FeaturesMenu(this);
		ItemMenu itemMenu = new ItemMenu(this);
		menuListener.register(featureMenu);
		menuListener.register(itemMenu);

		commandExecutor().registerCommand(java.util.Collections.emptyList(), "features",
				p -> p.openInventory(featureMenu.asInventory()));

		commandExecutor().registerCommand(java.util.Collections.emptyList(), "items",
				p -> p.openInventory(itemMenu.asInventory()));

		this.getCommand("mm").setExecutor(commandExecutor);
	}

	public final Teams teams() {
		return teams;
	}

	private void addGameListener(IGameEventListener listener) {
		gameListeners.add(listener);
		getServer().getPluginManager().registerEvents(listener, this);
	}
}

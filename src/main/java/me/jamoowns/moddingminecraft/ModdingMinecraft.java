package me.jamoowns.moddingminecraft;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.jamoowns.moddingminecraft.commands.CommandMinecraftModders;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.customitems.CustomItemListListener;
import me.jamoowns.moddingminecraft.customitems.CustomItemListener;
import me.jamoowns.moddingminecraft.customitems.CustomItems;
import me.jamoowns.moddingminecraft.extras.BossesListener;
import me.jamoowns.moddingminecraft.extras.FunkyDeathsListener;
import me.jamoowns.moddingminecraft.extras.FunkyFoodListener;
import me.jamoowns.moddingminecraft.features.Feature;
import me.jamoowns.moddingminecraft.features.FeatureTracker;
import me.jamoowns.moddingminecraft.features.IFeatureListener;
import me.jamoowns.moddingminecraft.features.PlayerTrailFeatureListener;
import me.jamoowns.moddingminecraft.features.RandomChestsFeatureListener;
import me.jamoowns.moddingminecraft.features.WeatherListener;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.menus.FeaturesMenu;
import me.jamoowns.moddingminecraft.menus.ItemMenu;
import me.jamoowns.moddingminecraft.menus.MenuListener;
import me.jamoowns.moddingminecraft.minigames.battleroyale.BattleRoyaleListener;
import me.jamoowns.moddingminecraft.minigames.blockhunter.BlockHunterListener;
import me.jamoowns.moddingminecraft.minigames.mgsettings.LobbyListener;
import me.jamoowns.moddingminecraft.minigames.sheepsheer.SheepSheerListener;
import me.jamoowns.moddingminecraft.roominating.LabRoomBuilderListener;
import me.jamoowns.moddingminecraft.roominating.StructureBuilderListener;
import me.jamoowns.moddingminecraft.taskkeeper.TaskKeeper;
import me.jamoowns.moddingminecraft.teams.Teams;

public final class ModdingMinecraft extends JavaPlugin implements IFeatureListener, Listener {

	private List<IGameEventListener> gameListeners;

	private CustomItems customItems;

	private CommandMinecraftModders commandExecutor;

	private Teams teams;

	private FeatureTracker featureTracker;

	private PlayerTrailFeatureListener playerTrailFeatureListener;

	private RandomChestsFeatureListener randomChestsFeatureListener;

	private TaskKeeper taskKeeper;

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
			case FUNKY_MOB_DEATH:
				break;
			case IRON_GOLEM:
				break;
			case PLAYER_TRAIL:
				break;
			case RANDOM_BUCKET:
				break;
			case RANDOM_CHESTS:
				randomChestsFeatureListener.onEnabled();
				break;
			case RANDOM_ENCHANT:
				break;
			case STABLE_WEATHER:
				break;
			case WINFRED:
				break;
			case ZOMBIE_BELL:
				break;
			case HEAVY_BLOCKS:
				break;
			case LIGHT_BLOCKS:
				break;
			default:
				break;
		}
	}

	@Override
	public final void featureDeactivated(Feature feature) {
		Broadcaster.broadcastInfo("Deactivated: " + feature.name());
		switch (feature) {
			case FUNKY_MOB_DEATH:
				break;
			case IRON_GOLEM:
				break;
			case PLAYER_TRAIL:
				playerTrailFeatureListener.onDisabled();
				break;
			case RANDOM_BUCKET:
				break;
			case RANDOM_CHESTS:
				randomChestsFeatureListener.onDisabled();
				break;
			case RANDOM_ENCHANT:
				break;
			case STABLE_WEATHER:
				break;
			case WINFRED:
				break;
			case ZOMBIE_BELL:
				break;
			case HEAVY_BLOCKS:
				break;
			case LIGHT_BLOCKS:
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
		teams().cleanup();
		gameListeners.forEach(IGameEventListener::onServerStop);
	}

	// Fired when plug-in is first enabled
	@Override
	public final void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		commandExecutor = new CommandMinecraftModders();
		customItems = new CustomItems();
		teams = new Teams(this);

		taskKeeper = new TaskKeeper(this);

		featureTracker = new FeatureTracker();

		featureTracker().enable(Feature.RANDOM_ENCHANT);
		featureTracker().enable(Feature.ZOMBIE_BELL);
		featureTracker().enable(Feature.RANDOM_BUCKET);
		featureTracker().enable(Feature.FUNKY_MOB_DEATH);
		featureTracker().enable(Feature.IRON_GOLEM);
		featureTracker().enable(Feature.WINFRED);
		featureTracker().enable(Feature.STABLE_WEATHER);

		featureTracker.addListener(this);

		gameListeners = new ArrayList<>();
		/* Our game listeners. */
		addGameListener(new JamoListener(this));
		addGameListener(new MabListener(this));
		addGameListener(new MoshyListener());

		addGameListener(new CustomItemListener(this));

		/* Minigames. */
		addGameListener(new BlockHunterListener(this));
		addGameListener(new BattleRoyaleListener(this));
		addGameListener(new SheepSheerListener(this));
		addGameListener(new LobbyListener());

		/* Custom mabmo stuff. */
		addGameListener(new CustomItemListListener(this));
		addGameListener(new FunkyDeathsListener(this));
		addGameListener(new LabRoomBuilderListener());
		addGameListener(new BossesListener(this));
		addGameListener(new FunkyFoodListener(this));
		addGameListener(new StructureBuilderListener());
		addGameListener(new WeatherListener(this));

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

		commandExecutor().registerCommand("features", featureMenu.displayMenu());
		commandExecutor().registerCommand("items", itemMenu.displayMenu());

		getCommand("mm").setExecutor(commandExecutor);

		Broadcaster.broadcastInfo("Modding Minecraft has been enabled!");
	}

	@EventHandler
	public final void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(MessageFormat.format("Welcome, {0}! This server is running MinecraftModders V{1}",
				event.getPlayer().getName(), getDescription().getVersion()));
	}

	public final TaskKeeper taskKeeper() {
		return taskKeeper;
	}

	public final Teams teams() {
		return teams;
	}

	private void addGameListener(IGameEventListener listener) {
		listener.gameEnabled().addObserver(state -> listenerEnabledStateChanged(state, listener));

		gameListeners.add(listener);
		getServer().getPluginManager().registerEvents(listener, this);
	}

	private void listenerEnabledStateChanged(boolean enabledState, IGameEventListener listener) {
		if (enabledState && !gameListeners.contains(listener)) {
			gameListeners.add(listener);
			getServer().getPluginManager().registerEvents(listener, this);
		} else if (gameListeners.contains(listener)) {
			HandlerList.unregisterAll(listener);
			gameListeners.remove(listener);
		}
	}
}

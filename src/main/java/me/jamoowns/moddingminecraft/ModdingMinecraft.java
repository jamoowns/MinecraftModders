package me.jamoowns.moddingminecraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import me.jamoowns.moddingminecraft.commands.CommandMinecraftModders;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.customitems.CustomItemListener;
import me.jamoowns.moddingminecraft.customitems.CustomItems;
import me.jamoowns.moddingminecraft.features.Feature;
import me.jamoowns.moddingminecraft.features.FeatureTracker;
import me.jamoowns.moddingminecraft.features.IFeatureListener;
import me.jamoowns.moddingminecraft.features.PlayerTrailFeature;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.menus.FeaturesMenu;
import me.jamoowns.moddingminecraft.menus.MenuListener;
import me.jamoowns.moddingminecraft.minigames.blockhunter.BlockHunterListener;
import me.jamoowns.moddingminecraft.teams.Teams;

public class ModdingMinecraft extends JavaPlugin implements IFeatureListener {

	private List<IGameEventListener> listeners;

	private CustomItems customItems;

	private JamoListener jamoListener;

	private MabListener mabListener;

	private MoshyListener moshyListener;

	private BlockHunterListener blockHunterListener;

	public Map<Feature, Boolean> statusByFeature;

	private CommandMinecraftModders commandExecutor;

	private Teams teams;

	private CustomItemListener customItemListener;

	private FeatureTracker featureTracker;

	private MenuListener menuListener;

	public final CommandMinecraftModders commandExecutor() {
		return commandExecutor;
	}

	public final CustomItems customItems() {
		return customItems;
	}

	@Override
	public final void featureActivated(Feature feature) {
		statusByFeature.put(feature, true);
		Broadcaster.broadcastInfo("Activated: " + feature.name());
	}

	@Override
	public final void featureDeactivated(Feature feature) {
		statusByFeature.put(feature, false);
		Broadcaster.broadcastInfo("Deactivated: " + feature.name());
	}

	public final FeatureTracker featureTracker() {
		return featureTracker;
	}

	// Fired when plug-in is disabled
	@Override
	public final void onDisable() {
		listeners.forEach(IGameEventListener::cleanup);
	}

	// Fired when plug-in is first enabled
	@Override
	public final void onEnable() {
		commandExecutor = new CommandMinecraftModders();
		statusByFeature = new HashMap<>();

		customItems = new CustomItems();

		Feature[] features = Feature.values();

		for (Feature feature : features) {
			switch (feature) {
				case BATTLE_ROYALE:
					statusByFeature.put(Feature.BATTLE_ROYALE, false);
					break;
				case RANDOM_CHESTS:
					statusByFeature.put(Feature.RANDOM_CHESTS, false);
					break;
				case RANDOM_ENCHANT:
					statusByFeature.put(Feature.RANDOM_ENCHANT, true);
					break;
				case ZOMBIE_BELL:
					statusByFeature.put(Feature.ZOMBIE_BELL, true);
					break;
				case EGG_WITCH:
					statusByFeature.put(Feature.EGG_WITCH, true);
					break;
				case RANDOM_BUCKET:
					statusByFeature.put(Feature.RANDOM_BUCKET, true);
					break;
				case FUNKY_MOB_DEATH:
					statusByFeature.put(Feature.FUNKY_MOB_DEATH, true);
					break;
				case IRON_GOLEM:
					statusByFeature.put(Feature.IRON_GOLEM, true);
					break;
				case PLAYER_TRAIL:
					statusByFeature.put(Feature.PLAYER_TRAIL, true);
					break;
				case WINFRED:
					statusByFeature.put(Feature.WINFRED, true);
					break;
				case STABLE_WEATHER:
					statusByFeature.put(Feature.STABLE_WEATHER, true);
					break;
				default:
					break;
			}
		}
		teams = new Teams(this);

		addListener(new JamoListener(this));
		mabListener = new MabListener(this);
		moshyListener = new MoshyListener();
		blockHunterListener = new BlockHunterListener(this);
		customItemListener = new CustomItemListener(this);
		featureTracker = new FeatureTracker();
		featureTracker.addListener(this);
		menuListener = new MenuListener();
		FeaturesMenu featureMenu = new FeaturesMenu(this);
		menuListener.register(featureMenu);

		addListener(new PlayerTrailFeature(this));

		commandExecutor().registerCommand(java.util.Collections.emptyList(), "features",
				p -> p.openInventory(featureMenu.asInventory()));

		this.getCommand("mm").setExecutor(commandExecutor);
		getServer().getPluginManager().registerEvents(jamoListener, this);
		getServer().getPluginManager().registerEvents(mabListener, this);
		getServer().getPluginManager().registerEvents(moshyListener, this);
		getServer().getPluginManager().registerEvents(blockHunterListener, this);
		getServer().getPluginManager().registerEvents(customItemListener, this);
		getServer().getPluginManager().registerEvents(menuListener, this);
	}

	public final Teams teams() {
		return teams;
	}

	private void addListener(IGameEventListener listener) {
		listeners.add(listener);
		getServer().getPluginManager().registerEvents(listener, this);
	}
}

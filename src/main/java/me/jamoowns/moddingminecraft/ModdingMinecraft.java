package me.jamoowns.moddingminecraft;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import me.jamoowns.moddingminecraft.commands.CommandMinecraftModders;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.customitems.CustomItemListener;
import me.jamoowns.moddingminecraft.customitems.CustomItems;
import me.jamoowns.moddingminecraft.minigames.blockhunter.BlockHunterListener;
import me.jamoowns.moddingminecraft.teams.Teams;

public class ModdingMinecraft extends JavaPlugin implements IFeatureListener {

	private CustomItems customItems;

	private JamoListener jamoListener;

	private MabListener mobListener;

	private MoshyListener moshyListener;

	private BlockHunterListener blockHunterListener;

	private Map<Feature, Boolean> statusByFeature;

	private CommandMinecraftModders commandExecutor;

	private Teams teams;

	private CustomItemListener customItemListener;

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

	public final boolean isFeatureActive(Feature feature) {
		return statusByFeature.getOrDefault(feature, false);
	}

	// Fired when plug-in is disabled
	@Override
	public final void onDisable() {
		jamoListener.cleanup();
		mobListener.cleanup();
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
				case Winfred:
					statusByFeature.put(Feature.Winfred, true);
					break;
				default:
					break;
			}
		}
		teams = new Teams(this);

		jamoListener = new JamoListener(this);
		mobListener = new MabListener(this);
		moshyListener = new MoshyListener();
		blockHunterListener = new BlockHunterListener(this);
		customItemListener = new CustomItemListener(this);
		commandExecutor.addListener(this);

		this.getCommand("mm").setExecutor(commandExecutor);
		getServer().getPluginManager().registerEvents(jamoListener, this);
		getServer().getPluginManager().registerEvents(mobListener, this);
		getServer().getPluginManager().registerEvents(moshyListener, this);
		getServer().getPluginManager().registerEvents(blockHunterListener, this);
		getServer().getPluginManager().registerEvents(customItemListener, this);
	}

	public final Teams teams() {
		return teams;
	}
}

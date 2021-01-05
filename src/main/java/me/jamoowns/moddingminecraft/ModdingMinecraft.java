package me.jamoowns.moddingminecraft;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.teams.Teams;

public class ModdingMinecraft extends JavaPlugin implements IFeatureListener {

	private JamoListener jamoListener;

	private MobListener mobListener;

	private MoshyListener moshyListener;

	private Map<Feature, Boolean> statusByFeature;

	private Teams teams;

	// Fired when plug-in is first enabled
	@Override
	public final void onEnable() {
		statusByFeature = new HashMap<>();

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
			}
		}
		teams = new Teams(this);

		jamoListener = new JamoListener(this);
		mobListener = new MobListener(this);
		moshyListener = new MoshyListener();

		CommandMinecraftModders commandExecutor = new CommandMinecraftModders();
		commandExecutor.addListener(this);

		this.getCommand("mm").setExecutor(commandExecutor);
		getServer().getPluginManager().registerEvents(jamoListener, this);
		getServer().getPluginManager().registerEvents(mobListener, this);
		getServer().getPluginManager().registerEvents(moshyListener, this);
	}

	public final Teams teams() {
		return teams;
	}

	// Fired when plug-in is disabled
	@Override
	public final void onDisable() {
		jamoListener.cleanup();
		mobListener.cleanup();
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
}

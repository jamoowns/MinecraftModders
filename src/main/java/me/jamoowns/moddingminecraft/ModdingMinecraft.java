package me.jamoowns.moddingminecraft;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
		statusByFeature.put(Feature.BATTLE_ROYALE, false);
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

	public final Teams getTeams() {
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
		Bukkit.broadcastMessage("ACTIVATED " + feature.name());
	}

	@Override
	public final void featureDeactivated(Feature feature) {
		statusByFeature.put(feature, false);
		Bukkit.broadcastMessage("DEACTIVATED " + feature.name());
	}

	public final boolean isFeatureActive(Feature feature) {
		return statusByFeature.getOrDefault(feature, false);
	}
}

package me.jamoowns.moddingminecraft;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.jamoowns.moddingminecraft.teams.Teams;

public class ModdingMinecraft extends JavaPlugin implements IFeatureListener {

	private JamoListener playerListener;
	private MobListener mobListener;

	private Map<Feature, Boolean> statusByFeature;

	private Teams teams;

	// Fired when plug-in is first enabled
	@Override
	public final void onEnable() {
		statusByFeature = new HashMap<>();
		statusByFeature.put(Feature.BATTLE_ROYALE, false);
		teams = new Teams(this);

		playerListener = new JamoListener(this);
		mobListener = new MobListener(this);

		CommandMinecraftModders commandExecutor = new CommandMinecraftModders();
		commandExecutor.addListener(this);

		this.getCommand("mm").setExecutor(commandExecutor);
		getServer().getPluginManager().registerEvents(playerListener, this);
		getServer().getPluginManager().registerEvents(mobListener, this);
	}

	public final Teams getTeams() {
		return teams;
	}

	// Fired when plug-in is disabled
	@Override
	public final void onDisable() {
		playerListener.cleanup();
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

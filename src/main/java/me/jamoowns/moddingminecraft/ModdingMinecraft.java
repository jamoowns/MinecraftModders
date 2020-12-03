package me.jamoowns.moddingminecraft;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

public class ModdingMinecraft extends JavaPlugin implements IFeatureListener {

	private JamoListener playerListener;
	private MobListener mobListener;

	private Map<Feature, Boolean> statusByFeature;

	// Fired when plug-in is first enabled
	@Override
	public void onEnable() {
		statusByFeature = new HashMap<>();
		statusByFeature.put(Feature.BATTLE_ROYALE, true);

		playerListener = new JamoListener(this);
		mobListener = new MobListener(this);

		CommandMinecraftModders commandExecutor = new CommandMinecraftModders();
		commandExecutor.addListener(this);

		this.getCommand("mm").setExecutor(commandExecutor);
		getServer().getPluginManager().registerEvents(playerListener, this);
		getServer().getPluginManager().registerEvents(mobListener, this);
	}

	// Fired when plug-in is disabled
	@Override
	public void onDisable() {
		playerListener.cleanup();
		mobListener.cleanup();
	}

	@Override
	public final void featureActivated(Feature feature) {
		statusByFeature.put(feature, true);
	}

	@Override
	public final void featureDeactivated(Feature feature) {
		statusByFeature.put(feature, false);
	}

	public final boolean isFeatureActive(Feature feature) {
		return statusByFeature.get(feature);
	}
}

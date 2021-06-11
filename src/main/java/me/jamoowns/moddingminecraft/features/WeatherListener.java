package me.jamoowns.moddingminecraft.features;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public class WeatherListener implements IGameEventListener {
	private final ModdingMinecraft javaPlugin;

	public WeatherListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@EventHandler
	public final void onThunderChange(ThunderChangeEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.STABLE_WEATHER)) {
			if (event.toThunderState()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public final void onWeatherChange(WeatherChangeEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.STABLE_WEATHER)) {
			if (event.toWeatherState()) {
				event.setCancelled(true);
			}
		}
	}
}

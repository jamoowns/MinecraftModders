package me.jamoowns.moddingminecraft.features;

import me.jamoowns.moddingminecraft.ModdingMinecraft;

public class FeatureTracker {

	private IFeatureListener featureListener;

	public final void addListener(IFeatureListener aFeatureListener) {
		featureListener = aFeatureListener;
	}

	public final void disable(Feature feature) {
		featureListener.featureDeactivated(feature);
	}

	public final void enable(Feature feature) {
		featureListener.featureActivated(feature);
	}

	public final boolean isFeatureActive(ModdingMinecraft moddingMinecraft, Feature feature) {
		return moddingMinecraft.statusByFeature.getOrDefault(feature, false);
	}
}

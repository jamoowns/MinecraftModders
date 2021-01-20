package me.jamoowns.moddingminecraft.features;

import java.util.ArrayList;
import java.util.List;

public final class FeatureTracker {

	private List<IFeatureListener> featureListeners;

	private List<Feature> enabledFeatures;

	public FeatureTracker() {
		enabledFeatures = new ArrayList<>();
		featureListeners = new ArrayList<>();
	}

	public final void addListener(IFeatureListener aFeatureListener) {
		featureListeners.add(aFeatureListener);
	}

	public final void disable(Feature feature) {
		if (enabledFeatures.contains(feature)) {
			enabledFeatures.remove(feature);
			featureListeners.forEach(f -> f.featureDeactivated(feature));
		}
	}

	public final void enable(Feature feature) {
		if (!enabledFeatures.contains(feature)) {
			enabledFeatures.add(feature);
			featureListeners.forEach(f -> f.featureActivated(feature));
		}
	}

	public final boolean isFeatureActive(Feature feature) {
		return enabledFeatures.contains(feature);
	}
}

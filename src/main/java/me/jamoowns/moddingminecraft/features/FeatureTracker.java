package me.jamoowns.moddingminecraft.features;

import java.util.ArrayList;
import java.util.List;

public final class FeatureTracker {

	private IFeatureListener featureListener;

	private List<Feature> enabledFeatures;

	public FeatureTracker() {
		enabledFeatures = new ArrayList<>();
	}

	public final void addListener(IFeatureListener aFeatureListener) {
		featureListener = aFeatureListener;
	}

	public final void disable(Feature feature) {
		enabledFeatures.remove(feature);
		featureListener.featureDeactivated(feature);
	}

	public final void enable(Feature feature) {
		if (!enabledFeatures.contains(feature)) {
			enabledFeatures.add(feature);
		}
		featureListener.featureActivated(feature);
	}

	public final boolean isFeatureActive(Feature feature) {
		return enabledFeatures.contains(feature);
	}
}

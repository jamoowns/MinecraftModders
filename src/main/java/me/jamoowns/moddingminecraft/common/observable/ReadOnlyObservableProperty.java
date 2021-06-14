package me.jamoowns.moddingminecraft.common.observable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ReadOnlyObservableProperty<T> {
	private Set<Consumer<T>> obs;
	private T value;

	ReadOnlyObservableProperty(T aValue) {
		obs = new HashSet<>();
		value = aValue;
	}

	public final void addObserver(Consumer<T> consumer) {
		obs.add(consumer);
	}

	void notifyValueChanged() {
		obs.forEach(c -> c.accept(value));
	}

	void setValue(T aValue) {
		value = aValue;
	}
}

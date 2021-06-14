package me.jamoowns.moddingminecraft.common.observable;

public final class ObservableProperty<T> extends ReadOnlyObservableProperty<T> {

	public ObservableProperty(T aValue) {
		super(aValue);
	}

	public final void set(T aSomeVariable) {
		setValue(aSomeVariable);
	}
}

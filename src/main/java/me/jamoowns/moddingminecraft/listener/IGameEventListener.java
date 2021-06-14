package me.jamoowns.moddingminecraft.listener;

import org.bukkit.event.Listener;

import me.jamoowns.moddingminecraft.common.observable.ObservableProperty;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;

public interface IGameEventListener extends Listener {

	public static final ObservableProperty<Boolean> ALWAYS_ENABLED = new ObservableProperty<Boolean>(true);

	public ReadOnlyObservableProperty<Boolean> gameEnabled();

	/** Called when this listener is disabled. */
	public void onDisabled();

	/** Called when this listener is enabled. */
	public void onEnabled();

	/**
	 * Called when the server is being stopped. Should perform the final cleanup
	 * here.
	 */
	public void onServerStop();
}

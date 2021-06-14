package me.jamoowns.moddingminecraft.listener;

import org.bukkit.event.Listener;

public interface IGameEventListener extends Listener {

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

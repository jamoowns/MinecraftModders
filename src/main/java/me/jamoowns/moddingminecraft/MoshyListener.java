package me.jamoowns.moddingminecraft;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class MoshyListener implements IGameEventListener {

	public MoshyListener() {
		/* Empty. */
	}

	@Override
	public final ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return IGameEventListener.ALWAYS_ENABLED;
	}

	@EventHandler
	public final void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		/* Empty. */
	}

	@Override
	public final void onDisabled() {
		/* Empty. */
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
	}

	@Override
	public final void onServerStop() {
		/* Empty. */
	}
}

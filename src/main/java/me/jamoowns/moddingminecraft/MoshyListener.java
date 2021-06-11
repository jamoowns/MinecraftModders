package me.jamoowns.moddingminecraft;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public class MoshyListener implements IGameEventListener {

	@Override
	public final void cleanup() {
		/* Empty. */
	}

	@EventHandler
	public final void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		/* Empty. */
	}
}

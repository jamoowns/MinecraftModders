package me.jamoowns.moddingminecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.jamoowns.moddingminecraft.common.chat.Broadcaster;

public class MoshyListener implements Listener {

	@EventHandler
	public final void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		Player flyingPlayer = event.getPlayer();
		Broadcaster.sendInfo(flyingPlayer, "In the good ole days this used to make you fly...");
	}
}

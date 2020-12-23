package me.jamoowns.moddingminecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.util.Vector;

public class MoshyListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerChatEvent event) {
		Player flyingChat = event.getPlayer();
		flyingChat.setVelocity(new Vector(0, 10, 0));
		flyingChat.setGliding(true);
	}
}

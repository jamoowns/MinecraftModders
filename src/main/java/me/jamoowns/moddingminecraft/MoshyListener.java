package me.jamoowns.moddingminecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.Vector;

public class MoshyListener {
	public void onPlayerJoin (PlayerChatEvent event) {
		Player flyingChat = event.getPlayer();
		flyingChat..setVelocity(new Vector(0, 10, 0));
		flyingChat.setGliding(bool True);
	}
}

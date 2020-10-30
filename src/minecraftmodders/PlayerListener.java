package minecraftmodders;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.material.Chest;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener {

	private final JavaPlugin javaPlugin;
	
	
	public PlayerListener(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(MessageFormat.format("Welcome, {0}! This server is running MinecraftModders V{1}", event.getPlayer().getName(), javaPlugin.getDescription().getVersion()));
    }
}

package me.jamoowns.moddingminecraft;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerListener implements Listener {

	private final JavaPlugin javaPlugin;
	
	private final Random RANDOM;
	private final List<Material> bucketTypes;
	
	
	public PlayerListener(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		RANDOM = new Random();
		bucketTypes = new ArrayList<>();
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(MessageFormat.format("Welcome, {0}! This server is running MinecraftModders V{1}", event.getPlayer().getName(), javaPlugin.getDescription().getVersion()));
    }
    
    @EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
    	bucketTypes.add(Material.BUCKET);
    	bucketTypes.add(Material.COD_BUCKET);
    	bucketTypes.add(Material.LAVA_BUCKET);
    	bucketTypes.add(Material.MILK_BUCKET);
    	bucketTypes.add(Material.PUFFERFISH_BUCKET);
    	bucketTypes.add(Material.SALMON_BUCKET);
    	bucketTypes.add(Material.TROPICAL_FISH_BUCKET);
    	bucketTypes.add(Material.WATER_BUCKET);

    	event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), new ItemStack(bucketTypes.get(RANDOM.nextInt(bucketTypes.size()))));
    }
}

package me.jamoowns.moddingminecraft;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
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
    	bucketTypes.add(Material.BUCKET);
    	bucketTypes.add(Material.COD_BUCKET);
    	bucketTypes.add(Material.LAVA_BUCKET);
    	bucketTypes.add(Material.MILK_BUCKET);
    	bucketTypes.add(Material.PUFFERFISH_BUCKET);
    	bucketTypes.add(Material.SALMON_BUCKET);
    	bucketTypes.add(Material.TROPICAL_FISH_BUCKET);
    	bucketTypes.add(Material.WATER_BUCKET);
    	
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				List<? extends Player> players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
				
				Player p = players.get(RANDOM.nextInt(players.size()));
				
				Location chestLocation = p.getLocation().add(RANDOM.nextInt(60)-30, 1, RANDOM.nextInt(60)-30);
				
				if (p.getWorld().getBlockAt(chestLocation).isEmpty()) {
					p.getWorld().getBlockAt(chestLocation).setType(Material.CHEST);
					
					 Chest chest = (Chest) p.getLocation().getBlock().getState();
					 
					 
					 List<Material> materials = Arrays.asList(Material.values());
					 
					 List<Material> itemsForChest = new ArrayList<>();
					 
					 for (int i = 0; i < RANDOM.nextInt(5) + 1; i++) {
						 itemsForChest.add(materials.get(RANDOM.nextInt(materials.size())));
					 }
					 
					 List<ItemStack> forChest = itemsForChest.stream().map(ItemStack::new).collect(Collectors.toList());
					 
					 chest.getInventory().setContents(forChest.toArray(new ItemStack[forChest.size()]));
				}
			}
		}, RANDOM.nextInt(120000) + 60000, RANDOM.nextInt(120000) + 60000);
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(MessageFormat.format("Welcome, {0}! This server is running MinecraftModders V{1}", event.getPlayer().getName(), javaPlugin.getDescription().getVersion()));
    }
    
    @EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
    	event.setItemStack(new ItemStack(bucketTypes.get(RANDOM.nextInt(bucketTypes.size()))));
    }
    
    @EventHandler
	public void onPlayerEggThrowEvent(PlayerEggThrowEvent event) {
    	event.setHatchingType(EntityType.WITCH);
    }
}

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
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerListener implements Listener {

	private final JavaPlugin javaPlugin;
	
	private final Random RANDOM;
	private final List<Material> bucketTypes;
	private final List<Enchantment> enchantments;
	
	
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
    	

		enchantments = new ArrayList<>();
		List<Enchantment> enchantments = Arrays.asList(Enchantment.values());
    	
    	Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				List<Player> players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
				Player p = players.get(RANDOM.nextInt(players.size()));
				
				int attempts = 0;
				boolean done = false;
				while (attempts < 30 && !done) {
					Location chestLocation = p.getLocation().add(RANDOM.nextInt(40)-20, RANDOM.nextInt(6)-3, RANDOM.nextInt(40)-20);
					if (p.getWorld().getBlockAt(chestLocation).isEmpty()) {
						done = true;
						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 10, 1);
						p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 15, 1);
						
				        Firework fw = (Firework) p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
				        FireworkMeta fwm = fw.getFireworkMeta();
				       
				        fwm.setPower(2);
				        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
				       
				        fw.setFireworkMeta(fwm);
				        fw.detonate();
				       
				        for(int i = 0;i<10; i++){
				            Firework fw2 = (Firework) p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
				            fw2.setFireworkMeta(fwm);
				        }
						
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
			}
		}, RANDOM.nextInt(100000) + 50000, RANDOM.nextInt(100000) + 50000);
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
    
    @EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
    	if (event.getClickedBlock().getType() == Material.BELL) {
    		Player player = event.getPlayer();
    		Location dundundun = player.getLocation().add(player.getLocation().getDirection().multiply(-15));

        	Zombie zombie =  player.getWorld().spawn(dundundun, Zombie.class);
        	zombie.setTarget(player);
        	zombie.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 1));
    	}
    }
    
    @EventHandler
	public void onPrepareItemCraftEvent(PrepareItemCraftEvent event) {
    	for (int i = 0; i < RANDOM.nextInt(5); i--) {
    		Enchantment enchantment = enchantments.get(i);
    		ItemStack result = event.getRecipe().getResult().clone();
    		if (enchantment.canEnchantItem(result)) {
    			ItemMeta meta = result.getItemMeta();
    			meta.addEnchant(enchantment, RANDOM.nextInt(4)+1, false);
    			result.setItemMeta(meta);
    			event.getInventory().setResult(result);
    		}
    	}
    }
}

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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Door.Hinge;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public final class PlayerListener implements Listener {

    private final JavaPlugin javaPlugin;
    
    private final Random RANDOM;
    private final List<Material> bucketTypes;
    private final List<Enchantment> enchantments;
    
    private final boolean RANDOM_CHESTS = false;
    
    private static final long ONE_SECOND = 20L;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    
    
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
        

        enchantments = Arrays.asList(Enchantment.values());

        if (RANDOM_CHESTS) {
	        Bukkit.getScheduler().scheduleSyncRepeatingTask(javaPlugin, new Runnable() {
	            public void run() {
	                List<Player> players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());
	                Player p = players.get(RANDOM.nextInt(players.size()));
	                
	                int attempts = 0;
	                boolean done = false;
	                while (attempts < 30 && !done) {
	                    Location chestLocation = p.getLocation().add(RANDOM.nextInt(40)-20, RANDOM.nextInt(6), RANDOM.nextInt(40)-20);
	    
	                    // Location chestLocation = p.getLocation().add(0, 3, 0);
	                    if (p.getWorld().getBlockAt(chestLocation).isEmpty()) {
	                        done = true;
	                        p.getWorld().playSound(chestLocation, Sound.BLOCK_GLASS_BREAK, 20, 1);
	                        p.getWorld().playSound(chestLocation, Sound.BLOCK_GLASS_BREAK, 25, 1);
	                        
	                         p.getWorld().getBlockAt(chestLocation).setType(Material.CHEST);
	                        
	                         Chest chest = (Chest) p.getWorld().getBlockAt(chestLocation).getState();
	                         
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
	        }, RANDOM.nextInt((int) (ONE_MINUTE*2)) + ONE_MINUTE, RANDOM.nextInt((int) (ONE_MINUTE * 2)) + ONE_MINUTE);
        }
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
    public void onBlockPlace(BlockPlaceEvent event)
    {
    	if (event.getBlockPlaced().getType() == Material.STONE_PRESSURE_PLATE) {
    		event.getBlockPlaced().setType(Material.AIR);
        	List<Location> walls = new ArrayList<>();
    		
        	Location startPoint = event.getBlockAgainst().getLocation().add(0, 1, 0);
        	
        	walls.addAll(wall(startPoint, startPoint.clone().add(0, 3, 5)));
        	walls.addAll(wall(startPoint.clone().add(5, 0, 0), startPoint.clone().add(5, 3, 5)));
        	walls.addAll(wall(startPoint, startPoint.clone().add(5, 3, 0)));
        	walls.addAll(wall(startPoint.clone().add(0, 0, 5), startPoint.clone().add(5, 3, 5)));
        	
        	World world = event.getBlockAgainst().getWorld();
        	
        	build(world, walls, Material.COBBLESTONE);

        	List<Location> roof = new ArrayList<>();
        	roof.addAll(wall(startPoint.clone().add(0, 4, 0), startPoint.clone().add(5, 4, 5)));

        	build(world, roof, Material.OAK_PLANKS);

        	buildDoor(world, startPoint.clone().add(2, 0, 0), Material.OAK_DOOR, Hinge.RIGHT, BlockFace.SOUTH);

        	List<Location> windows = new ArrayList<>();
        	windows.addAll(wall(startPoint.clone().add(4, 1, 0), startPoint.clone().add(4, 2, 0)));
        	build(world, windows, Material.GLASS);
    	}
    }
    
    private void buildDoor(World world, Location doorLocation, Material doorType, Hinge hinge, BlockFace blockFace) {
		Door doorBottom = (Door) Bukkit.createBlockData(doorType);
		
		doorBottom.setHinge(hinge);
		doorBottom.setFacing(blockFace);
		doorBottom.setHalf(Half.BOTTOM);
		world.getBlockAt(doorLocation).setBlockData(doorBottom);
		
		Door doorTop = (Door) Bukkit.createBlockData(doorType);
    	doorTop.setHinge(hinge);
    	doorTop.setFacing(blockFace);
    	doorTop.setHalf(Half.TOP);
    	
    	world.getBlockAt(doorLocation.clone().add(0, 1, 0)).setBlockData(doorTop);
    }
    
	private void build(World world, List<Location> positions, Material material){
		positions.forEach(loc -> {
			world.getBlockAt(loc).setType(material);
		});
    }
    
    private List<Location> wall(Location startLocation, Location endLocation){
    	List<Location> wall = new ArrayList<>();
    	for (int x = 0; x <= endLocation.getX() - startLocation.getX(); x++) {
        	for (int y = 0; y <= endLocation.getY() - startLocation.getY(); y++) {
            	for (int z = 0; z <= endLocation.getZ() - startLocation.getZ(); z++) {
            		wall.add(startLocation.clone().add(x, y, z));
            	}
        	}
    	}
    	return wall;
    }
    
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
    	// ScoreboardManager manager = Bukkit.getScoreboardManager();
    	// Scoreboard board = manager.getNewScoreboard();
    	// Team team = board.registerNewTeam("teamname");
    	
    	
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BELL) {
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

    public void cleanup() {
        /* Empty for now. */
    }
}

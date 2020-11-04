package me.jamoowns.moddingminecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class MobListener implements Listener {

	private final JavaPlugin javaPlugin;
	
	private Map<UUID, List<Location>> trailByPlayer;
	
	private boolean playerTrail = true;
	private boolean playerDeath = true;
	private boolean spiderWebDeath = true;
	private boolean cowTeleDeath = true;
	private boolean vilIronBeeDeath = true;
	private boolean creeperWaitDeath = true;
	private boolean skeletonVisionDeath = true;
	private boolean zombieBabushkaDeath = true;
	private boolean sheepDeath = true;
	private boolean chickenDeath = true;
	
	public MobListener(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		
		trailByPlayer = new HashMap<>();
	}

    @EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
    	if(playerTrail) {
    		Location belowPlayer = event.getPlayer().getLocation().add(0, -1, 0);
        	
        	if(!belowPlayer.getBlock().isEmpty()
        			&& !belowPlayer.getBlock().isLiquid()) {
        		
    		    	BlockFace facing = event.getPlayer().getFacing();
    		    	int x = 0;
    				int z = 0;
    				if (facing == BlockFace.SOUTH) {
    		    		x = 0;
    		    		z = -1;
    				} else if (facing == BlockFace.NORTH) {
    		        		x = 0;
    		        		z = 1;
    		    	} else if (facing == BlockFace.EAST) {
    		    		x = -1;
    		    		z = 0;
    		    	} else if (facing == BlockFace.WEST) {
    		    		x = 1;
    		    		z = 0;
    		    	}
    				
    		    	Location behindPlayer = event.getPlayer().getLocation().add(x, 0, z);
    		    	Location belowBehindPlayer = event.getPlayer().getLocation().add(x, -1, z);
    		    	Block blockBehindPlayer = event.getPlayer().getWorld().getBlockAt(behindPlayer);
    		    	Block blockBelowBehindPlayer = event.getPlayer().getWorld().getBlockAt(belowBehindPlayer);
    		    	if(blockBehindPlayer.isEmpty() && 
    		    			!blockBehindPlayer.isLiquid() &&
    		    			!blockBelowBehindPlayer.getType().name().contains("CARPET") &&
    		    			!blockBelowBehindPlayer.isLiquid() &&
    		    			!blockBelowBehindPlayer.isEmpty()) {
    		    		List<Location> trail = trailByPlayer.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());
    	    			trail.add(behindPlayer);
    	    			if (trail.size() > 100) {
    	    				Location first = trail.remove(0);
    	    	    		event.getPlayer().getWorld().getBlockAt(first).setType(Material.AIR);
    	    			}
    	    			
    	    			trailByPlayer.put(event.getPlayer().getUniqueId(), trail);
    			    	if(event.getPlayer().getName().equalsIgnoreCase("mabmo")) {
    			    		event.getPlayer().getWorld().getBlockAt(behindPlayer).setType(Material.CYAN_CARPET);
    			    	}else{
    			    		event.getPlayer().getWorld().getBlockAt(behindPlayer).setType(Material.RED_CARPET);
    			    	}
    		    	}
        	
    		}
    	}
    	
	}
    @EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
    	if(playerTrail) {
	    	List<Location> trail = trailByPlayer.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());
	    	trail.forEach(loc -> {
	    		event.getPlayer().getWorld().getBlockAt(loc).setType(Material.AIR);
	    	});
	    	trailByPlayer.remove(event.getPlayer().getUniqueId());
    	}
    }
    
   
    
    @EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
    	if(playerDeath) {
    		if(event.getEntity() instanceof Player)
            { 
        		Player playerEnt = (Player) event.getEntity();
            	Player mcPlayer = playerEnt.getKiller();
                 {

                 	mcPlayer.dropItem(false);
                	Random r = new Random();
                	int low = 0;
                	int high = 3;
                	int result = r.nextInt(high-low) + low;
                    for(int i = 0; i < result; i++) {
                    	IronGolem ironGolem =  event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), IronGolem.class);
                    	if(mcPlayer != null) {
                    		ironGolem.setTarget(mcPlayer);
                        }
                    }
                } 
            }
    	}
    	if(sheepDeath) {
    		
            if(event.getEntity() instanceof Sheep)
            { 
            	Sheep sheepEnt = (Sheep) event.getEntity();
            	Player mcPlayer = sheepEnt.getKiller();
                if(mcPlayer == null)
                    return;
                
                if(sheepEnt.isAdult()) {
                	mcPlayer.sendTitle("You're about to die", "20 secs to live after this", 40, 40, 40);
                		Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {
                		public void run() {
                			for(int i = 0; i < 3; i++) {
                            	Creeper crepper =  mcPlayer.getLocation().getWorld().spawn(mcPlayer.getLocation(), Creeper.class);
                            	if(mcPlayer != null) {
                            		crepper.setTarget(mcPlayer);
                                }
                            }
                			mcPlayer.sendMessage("test"); //send the message "test"
                		}
                		}, 180);
                }
            }
    	}
    	if(chickenDeath) {
    		if(event.getEntity() instanceof Chicken)
            { 
    			Chicken chickenEnt = (Chicken) event.getEntity();
            	Player mcPlayer = chickenEnt.getKiller();
                if(mcPlayer == null)
                    return;
                
                if(chickenEnt.isAdult()) {
                	Location loc = mcPlayer.getLocation();
                	mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0,-1,0)).setType(Material.AIR);
                	mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0,-1,0)).setType(Material.AIR);
                	mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0,-1,0)).setType(Material.AIR);
                	mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0,-1,0)).setType(Material.AIR);
                	mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0,-1,0)).setType(Material.AIR);
                	mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0,-1,0)).setType(Material.STONE);
                	Random r = new Random();
                	int low = 1;
                	int high = 11;
                	int result = r.nextInt(high-low) + low;
                	if(result>6) {
                		mcPlayer.getLocation().getWorld().getBlockAt(loc.add(0,15,0)).setType(Material.ANVIL);
                	}
                	mcPlayer.teleport(mcPlayer.getLocation().add(0,-5,0));
                	event.getDrops().add(new ItemStack(Material.ENDER_PEARL, 3));
                }
            }
    	}
    	if(cowTeleDeath) {
    		if(event.getEntity() instanceof Cow)
            { 
        		Cow cowEnt = (Cow) event.getEntity();
            	Player mcPlayer = cowEnt.getKiller();
                if(mcPlayer == null)
                    return;
                
                if(cowEnt.isAdult()) {
                	mcPlayer.teleport(mcPlayer.getLocation().add(0,5,0));
                }
            }
    	}
    	if(vilIronBeeDeath) {
    		if(event.getEntity() instanceof Villager)
            { 
        		Villager villagerEnt = (Villager) event.getEntity();
            	Player mcPlayer = villagerEnt.getKiller();
                if(mcPlayer == null)
                    return;
                
                if(villagerEnt.isAdult()) {
                	Random r = new Random();
                	int low = 3;
                	int high = 10;
                	int result = r.nextInt(high-low) + low;
                    for(int i = 0; i < result; i++) {
                    	IronGolem ironGolem =  event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), IronGolem.class);
                    	ironGolem.setTarget(mcPlayer);
                    }
                }
            }
    	}
    	
    	if(zombieBabushkaDeath) {
    		if(event.getEntity() instanceof Zombie)
            { 
            	Zombie zombieEnt = (Zombie) event.getEntity();
            	Player mcPlayer = zombieEnt.getKiller();
                if(mcPlayer == null)
                    return;
                
                if(zombieEnt.isAdult()) {
                	Random r = new Random();
                	int low = 3;
                	int high = 10;
                	int result = r.nextInt(high-low) + low;
                    for(int i = 0; i < result; i++) {
                        Zombie babushka =  event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Zombie.class);
                        babushka.setBaby();
                        babushka.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(babushka.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * 1/result);
                        babushka.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(babushka.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() * 1/result);
                    }
                }
            }
    	}
        if(skeletonVisionDeath) {
        	if(event.getEntity() instanceof Skeleton)
            { 
            	Skeleton skeletonEnt = (Skeleton) event.getEntity();
            	Player mcPlayer = skeletonEnt.getKiller();
                if(mcPlayer == null)
                    return;
                for(int i = 0; i < 3; i++) {
                    event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation().add(0,1,0), Bat.class);
                }
                mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1));
                mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 180, 1));
            }
        }
        
        if(creeperWaitDeath) {
        	if(event.getEntity() instanceof Creeper)
            { 
            	Creeper creeperEnt = (Creeper) event.getEntity();
            	Player mcPlayer = creeperEnt.getKiller();
                if(mcPlayer == null)
                    return;
                Location loc = event.getEntity().getLocation().add(0,-2,0);
                event.getEntity().getWorld().getBlockAt(loc).setType(Material.AIR);
        		TNTPrimed tnt = event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation().add(0,1,0), TNTPrimed.class);
        		tnt.setFuseTicks(520);
        		Random r = new Random();
            	int low = 0;
            	int high = 10;
            	int result = r.nextInt(high-low) + low;
            	int yield = 0;
            	if(result >7) {
            		yield = 1;
            	}
        		tnt.setYield(yield);
            	
            }
        }
        
        if(spiderWebDeath) {
        	if(event.getEntity() instanceof Spider)
            { 
            	Spider spiderEnt = (Spider) event.getEntity();
            	Player mcPlayer = spiderEnt.getKiller();
                if(mcPlayer == null)
                    return;
                for(int i = 0; i < 7; i++) {
                    for(int j = 0; j < 7; j++) {
    	                Location loc = event.getEntity().getLocation().add(-3+i,0,-3+j);
    	            	if(event.getEntity().getWorld().getBlockAt(loc).isEmpty() 
    			    			&& !event.getEntity().getWorld().getBlockAt(loc).isLiquid()) {
    	            		if((j==0||j==6)&&i%3==0) {
    	            			event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
    	            		}else if((j==1||j==5)&&i%2==1) {
    	            			event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
    	            		}else if((j==2||j==4)&&(i==2||i==3||i==4)) {
    	            			event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
    	            		}else if(j==3) {
    	            			event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
    	            		}
    			    	}
    	            }
                }
                for(int k = 0; k < 2; k++) {
                	int neg = 1;
                	if(k>0) {
                		neg = -1;
                	}
    	            for(int i = 0; i < 3; i++) {
    	                for(int j = 0; j < 3; j++) {
    		                Location loc = event.getEntity().getLocation().add(-1+i,1*neg,-1+j);
    		            	if(event.getEntity().getWorld().getBlockAt(loc).isEmpty() 
    				    			&& !event.getEntity().getWorld().getBlockAt(loc).isLiquid()) {
    		            		if((j==0||j==2)&&i%2==0) {
    		            			event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
    		            		}else if(j==1&&i==1) {
    		            			event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
    		            		}
    				    	}
    		            }
    	            }
    	            Location loc = event.getEntity().getLocation().add(0,2*neg,0);
    	        	if(event.getEntity().getWorld().getBlockAt(loc).isEmpty() 
    		    			&& !event.getEntity().getWorld().getBlockAt(loc).isLiquid()) {
    	        		event.getEntity().getWorld().getBlockAt(loc).setType(Material.COBWEB);
    		    	}
                }
                
            }
        }
        
    }
    
}

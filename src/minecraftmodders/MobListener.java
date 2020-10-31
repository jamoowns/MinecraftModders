package minecraftmodders;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sun.xml.internal.stream.Entity;

public class MobListener implements Listener {

	private final JavaPlugin javaPlugin;
	
	
	public MobListener(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

    @EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		// TODO Auto-generated method stub
    	if(!event.getPlayer().getLocation().add(0, -1, 0).getBlock().isEmpty()
    			&& !event.getPlayer().getLocation().add(0, -1, 0).getBlock().isLiquid()) {
    		
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
		    	Location loc = event.getPlayer().getLocation().add(x, -1, z);
		    	if(!event.getPlayer().getWorld().getBlockAt(loc).isEmpty() 
		    			&& !event.getPlayer().getWorld().getBlockAt(loc).isLiquid()) {
			    	if(event.getPlayer().getName()=="mabmo") {
			    		event.getPlayer().getWorld().getBlockAt(loc).setType(Material.CYAN_WOOL);
			    	}else{
			    		event.getPlayer().getWorld().getBlockAt(loc).setType(Material.RED_WOOL);
			    	}
		    	}
    	
		}
	}
    @EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) {
		// TODO Auto-generated method stub
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
                }
            }
        }
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

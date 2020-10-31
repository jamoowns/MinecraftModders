package minecraftmodders;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.sun.xml.internal.stream.Entity;

public class MobListener implements Listener {

	private final JavaPlugin javaPlugin;
	
	
	public MobListener(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

    @EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		// TODO Auto-generated method stub
    	if(!event.getPlayer().getLocation().add(0, -1, 0).getBlock().isEmpty()) {
    		if(!event.getPlayer().getLocation().add(0, -1, 0).getBlock().isLiquid()) {
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
                for(int i = 0; i < 3; i++) {
                    Zombie babushka =  event.getEntity().getLocation().getWorld().spawn(event.getEntity().getLocation(), Zombie.class);
                    babushka.setBaby();
                    if(i == 0) {
                        babushka.setCustomName("ba      ");
                    }else if(i == 1) {
                        babushka.setCustomName("  bush  ");
                    }else if(i == 2) {
                        babushka.setCustomName("      ka");
                    }
                    babushka.setCustomNameVisible(true);
                    babushka.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(babushka.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() * .33);
                }
            }
        }
    }
}

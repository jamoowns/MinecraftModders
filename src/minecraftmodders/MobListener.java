package minecraftmodders;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MobListener implements Listener {

	private final JavaPlugin javaPlugin;
	
	
	public MobListener(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}

    @EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		// TODO Auto-generated method stub
    	if(!event.getPlayer().getLocation().add(0, -1, 0).getBlock().isEmpty()) {
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

package minecraftmodders;

import org.bukkit.plugin.java.JavaPlugin;

import minecraftmodders.MobListener;
import minecraftmodders.PlayerListener;

public class MinecraftModders extends JavaPlugin {

    // Fired when plug-in is first enabled
    @Override
    public void onEnable() {
   		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    	getServer().getPluginManager().registerEvents(new MobListener(this), this);
    }
    // Fired when plug-in is disabled
    @Override
    public void onDisable() {
    	
    }
}

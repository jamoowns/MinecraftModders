package minecraftmodders;

import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftModders extends JavaPlugin {

    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
   		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    	getServer().getPluginManager().registerEvents(new MobListener(this), this);
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    	
    }
}

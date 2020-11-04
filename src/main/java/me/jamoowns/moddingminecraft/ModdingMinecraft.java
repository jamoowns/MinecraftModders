package me.jamoowns.moddingminecraft;

import org.bukkit.plugin.java.JavaPlugin;

public class ModdingMinecraft extends JavaPlugin {

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
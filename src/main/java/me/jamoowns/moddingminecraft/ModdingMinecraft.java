package me.jamoowns.moddingminecraft;

import org.bukkit.plugin.java.JavaPlugin;

public class ModdingMinecraft extends JavaPlugin {

    private JamoListener playerListener;
    private MobListener mobListener;
    
    // Fired when plug-in is first enabled
    @Override
    public void onEnable() {
        playerListener = new JamoListener(this);
        mobListener = new MobListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(mobListener, this);
    }
    // Fired when plug-in is disabled
    @Override
    public void onDisable() {
        playerListener.cleanup();    
        mobListener.cleanup();
    }
}

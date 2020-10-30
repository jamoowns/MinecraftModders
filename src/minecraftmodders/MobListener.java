package minecraftmodders;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MobListener implements Listener {

	private final JavaPlugin javaPlugin;
	
	
	public MobListener(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
	}
}

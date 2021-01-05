package me.jamoowns.moddingminecraft;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.jamoowns.moddingminecraft.common.chat.Broadcaster;

public final class CommandMinecraftModders implements CommandExecutor {

	private IFeatureListener featureListener;

	private final Map<String, Consumer<Player>> commands;

	public CommandMinecraftModders() {
		commands = new HashMap<>();
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Consumer<Player> commandToRun = commands.get(args[0]);
		if (commandToRun != null) {
			if (sender instanceof Player) {
				commandToRun.accept((Player) sender);
				return true;
			}
		}
		List<Feature> features = Arrays.asList(Feature.values());
		if (args.length == 2) {
			String enableString = args[0];
			String featureName = args[1];
			if (features.stream().map(Feature::name).anyMatch(featureName::equalsIgnoreCase)) {
				Feature feature = Feature.valueOf(featureName);

				if (enableString.equalsIgnoreCase("activate") || enableString.equalsIgnoreCase("a")) {
					featureListener.featureActivated(feature);
					return true;
				} else if (enableString.equalsIgnoreCase("deactivate") || enableString.equalsIgnoreCase("d")) {
					featureListener.featureDeactivated(feature);
					return true;
				}
			}
		}
		// Show help
		if (sender instanceof Player) {
			Broadcaster.sendInfo(sender,
					"possible features: " + features.stream().map(Feature::name).collect(Collectors.joining(", ")));
		}
		return false;
	}

	public final void registerCommand(String command, Consumer<Player> function) {
		commands.put(command, function);
	}

	public final void addListener(IFeatureListener aFeatureListener) {
		featureListener = aFeatureListener;
	}
}

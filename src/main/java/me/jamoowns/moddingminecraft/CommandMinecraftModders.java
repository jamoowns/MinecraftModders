package me.jamoowns.moddingminecraft;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMinecraftModders implements CommandExecutor {

	private IFeatureListener featureListener;

	public CommandMinecraftModders() {
		// Empty.
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		List<Feature> features = Arrays.asList(Feature.values());
		if (args.length == 2) {
			String featureName = args[1];
			String enableString = args[0];
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
			Player player = (Player) sender;
			player.sendMessage(
					"possible features: " + features.stream().map(Feature::name).collect(Collectors.joining(", ")));
		}
		return false;
	}

	public final void addListener(IFeatureListener aFeatureListener) {
		featureListener = aFeatureListener;
	}
}

package me.jamoowns.moddingminecraft.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class PlayerTrailFeatureListener implements IGameEventListener {

	private Map<UUID, List<Block>> trailByPlayer;

	private ModdingMinecraft javaPlugin;

	public PlayerTrailFeatureListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		trailByPlayer = new HashMap<>();
	}

	@Override
	public ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return IGameEventListener.ALWAYS_ENABLED;
	}

	@Override
	public final void onDisabled() {
		onServerStop();
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
	}

	@EventHandler
	public final void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (javaPlugin.featureTracker().isFeatureActive(Feature.PLAYER_TRAIL)) {
			Location belowPlayer = event.getPlayer().getLocation().add(0, -1, 0);

			if (!belowPlayer.getBlock().isEmpty() && !belowPlayer.getBlock().isLiquid()) {

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

				Location behindPlayer = event.getPlayer().getLocation().add(x, 0, z);
				Location belowBehindPlayer = event.getPlayer().getLocation().add(x, -1, z);
				Block blockBehindPlayer = event.getPlayer().getWorld().getBlockAt(behindPlayer);
				Block blockBelowBehindPlayer = event.getPlayer().getWorld().getBlockAt(belowBehindPlayer);

				if (blockBehindPlayer.isEmpty() && !blockBehindPlayer.isLiquid()
						&& !blockBelowBehindPlayer.getType().name().contains("CARPET")
						&& blockBelowBehindPlayer.getType() != Material.GRASS
						&& !blockBelowBehindPlayer.getType().name().contains("SNOW")
						&& !blockBelowBehindPlayer.isLiquid() && !blockBelowBehindPlayer.isEmpty()) {
					List<Block> trail = trailByPlayer.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());
					trail.add(event.getPlayer().getWorld().getBlockAt(behindPlayer));
					if (trail.size() > 100) {
						Block first = trail.remove(0);
						if (first.getType().name().contains("CARPET")) {
							first.setType(Material.AIR);
						}
					}
					trailByPlayer.put(event.getPlayer().getUniqueId(), trail);

					event.getPlayer().getWorld().getBlockAt(behindPlayer).setType(
							javaPlugin.teams().getTeam(event.getPlayer().getUniqueId()).getTeamColour().getTrail());
				}
			}
		}
	}

	@EventHandler
	public final void onPlayerQuitEvent(PlayerQuitEvent event) {
		List<Block> trail = trailByPlayer.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());
		trail.forEach(block -> {
			if (block.getType().name().contains("CARPET")) {
				block.setType(Material.AIR);
			}
		});
		trailByPlayer.remove(event.getPlayer().getUniqueId());
	}

	@Override
	public final void onServerStop() {
		trailByPlayer.values().forEach(blocks -> {
			blocks.forEach(block -> {
				if (block.getType().name().contains("CARPET")) {
					block.setType(Material.AIR);
				}
			});
		});
		trailByPlayer.clear();
	}
}

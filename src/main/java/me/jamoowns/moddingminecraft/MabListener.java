package me.jamoowns.moddingminecraft;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.jamoowns.moddingminecraft.extras.SpellsListener;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.roominating.SimpleChunks;

public class MabListener implements IGameEventListener {

	private final ModdingMinecraft javaPlugin;

	private final Random RANDOM;
	private final SimpleChunks simpleChunks;

	boolean mabmoSet;
	Player mabmo;

	boolean whoCares = false;

	public MabListener(ModdingMinecraft aJavaPlugin) {
		RANDOM = new Random();
		javaPlugin = aJavaPlugin;
		simpleChunks = new SimpleChunks();
	}

	@Override
	public final void cleanup() {
		/* Empty. */
	}

	@EventHandler
	public final void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getType().equals(Material.GRASS_BLOCK)) {
			// simpleChunks.createChunk(1, event.getBlock().getLocation());
		}
	}

	@EventHandler
	public void onFallingBlockLand(final EntityChangeBlockEvent event) {
		if (whoCares && event.getEntity() instanceof FallingBlock) {
			FallingBlock fallingBlock = (FallingBlock) event.getEntity();

			if (event.getBlock().getType() == Material.AIR && fallingBlock.isOnGround()) {
				new BukkitRunnable() {
					@Override
					public void run() {
						event.getBlock().setType(Material.SAND);
					}
				}.runTaskLater(javaPlugin, 1L);
			}
		}
	}

	@EventHandler
	public final void onPlayerMoveEvent(PlayerMoveEvent event) {

		if (whoCares) {
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
			Location belowBehindPlayer = event.getPlayer().getLocation().add(x, -1, z);

			if (!belowBehindPlayer.getBlock().isEmpty() && !belowBehindPlayer.getBlock().isLiquid()) {

				Block block = event.getPlayer().getWorld().getBlockAt(belowBehindPlayer);
				BlockData blockData = block.getBlockData();
				block.setType(Material.AIR);
				FallingBlock fb = block.getLocation().getWorld().spawnFallingBlock(block.getLocation().add(0.5, 0, 0.5),
						blockData);
				fb.setVelocity(new Vector(0, 1, 0));
				fb.setGravity(false);
				Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, new Runnable() {

					@Override
					public void run() {

						fb.setGravity(true);
						Random r = new Random();
						int low = -50;
						int high = 50;
						double x = (r.nextInt(high - low) + low) / 10;
						double z = (r.nextInt(high - low) + low) / 10;
						fb.setVelocity(new Vector(x / 10, 0, z / 10));
					}
				}, 10);

			}
		}

	}

	@EventHandler
	public final void playerChatEvent(PlayerChatEvent event) {
		if (event.getMessage().contains("Ok let's do this")) {
			SpellsListener.getReady(event.getPlayer().getLocation().getWorld());
		}
		if (event.getMessage().contains("Iammabmo")) {
			mabmo = event.getPlayer();
			mabmoSet = true;
		}
		if (event.getMessage().contains("Iamnotmabmo")) {
			mabmoSet = false;
		}
	}

	private void sendMabmoMsg(String str) {
		if (mabmoSet) {
			if (mabmo.isOnline()) {
				mabmo.sendMessage(str);
			}
		}
	}

}

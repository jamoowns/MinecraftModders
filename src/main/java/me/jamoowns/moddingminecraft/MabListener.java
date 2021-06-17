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

import me.jamoowns.moddingminecraft.commands.ModdersCommand;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory.KitLevel;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameCore;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameKit;
import me.jamoowns.moddingminecraft.minigames.newminigames.MiniGame;
import me.jamoowns.moddingminecraft.minigames.newminigames.MiniGameList;
import me.jamoowns.moddingminecraft.roominating.SimpleChunks;

public class MabListener implements IGameEventListener {

	private static MiniGameList mgList;

	private final ModdingMinecraft javaPlugin;
	private final Random RANDOM;
	private final SimpleChunks simpleChunks;
	private GameCore gameCore;
	boolean mabmoSet;
	Player mabmo;

	boolean whoCares = false;

	public MabListener(ModdingMinecraft aJavaPlugin) {
		RANDOM = new Random();
		javaPlugin = aJavaPlugin;
		simpleChunks = new SimpleChunks();
		mgList = new MiniGameList();
		Broadcaster.broadcastGameInfo("mgList has been initiated!");
		ModdersCommand rootCommand = javaPlugin.commandExecutor().registerCommand("mg",
				p -> Broadcaster.sendGameInfo(p, "mg Stuff"));

		ModdersCommand addCommand = aJavaPlugin.commandExecutor().registerCommand(rootCommand, "add", p -> {
		});
		aJavaPlugin.commandExecutor().registerCommand(addCommand, "One", p -> {
			MiniGame mg = new MiniGame(mgList.getNewID(), "One", p.getPlayer().getUniqueId());
			mgList.addMiniGame(mg);
		});
		aJavaPlugin.commandExecutor().registerCommand(addCommand, "Two", p -> {
			MiniGame mg = new MiniGame(mgList.getNewID(), "Two", p.getPlayer().getUniqueId());
			mgList.addMiniGame(mg);
		});
		aJavaPlugin.commandExecutor().registerCommand(addCommand, "Three", p -> {
			MiniGame mg = new MiniGame(mgList.getNewID(), "Three", p.getPlayer().getUniqueId());
			mgList.addMiniGame(mg);
		});
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "size",
				p -> Broadcaster.broadcastGameInfo("mgList size is " + mgList.size()));
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "list", p -> mgList.getGameList(p.getPlayer()));

	}

	@Override
	public final ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return IGameEventListener.ALWAYS_ENABLED;
	}

	@EventHandler
	public final void onBlockPlace(BlockPlaceEvent event) {

		if (event.getBlock().getType().equals(Material.GRASS_BLOCK)) {
			// simpleChunks.createChunk(1, event.getBlock().getLocation());
			GameKit gameKit = Armory.offense(KitLevel.AVERAGE).combine(Armory.defence(KitLevel.AVERAGE))
					.combine(Armory.food(KitLevel.LOW));

			gameCore = new GameCore(javaPlugin, "test", "Test", 5, 5, gameKit, 1, true);
		}
	}

	@Override
	public final void onDisabled() {
		/* Empty. */
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
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

	@Override
	public final void onServerStop() {
		/* Empty. */
	}

	@EventHandler
	public final void playerChatEvent(PlayerChatEvent event) {

	}

}

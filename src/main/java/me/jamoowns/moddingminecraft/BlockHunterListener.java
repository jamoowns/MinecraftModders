package me.jamoowns.moddingminecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.common.fated.Collections;
import me.jamoowns.moddingminecraft.common.time.CountdownTimer;
import me.jamoowns.moddingminecraft.common.time.TimeConstants;

public final class BlockHunterListener implements Listener {

	private final ItemStack blockStand;

	private final List<GamePlayer> gameplayers;

	private class GamePlayer {

		private UUID playerId;

		private Location standLocation;

		private Material chosenBlock;

		private GamePlayer targetPlayer;

		private boolean hasFoundBlock;

		public GamePlayer(UUID aPlayer) {
			playerId = aPlayer;
			hasFoundBlock = false;
		}

		public UUID playerId() {
			return playerId;
		}

		public Location standLocation() {
			return standLocation;
		}

		public Material chosenBlock() {
			return chosenBlock;
		}

		public void chosenBlock(Material mat) {
			chosenBlock = mat;
		}

		public GamePlayer targetPlayer() {
			return targetPlayer;
		}

		public void setStand(Location loc) {
			standLocation = loc;
		}

		public void clearStand() {
			if (standLocation() != null) {
				standLocation().getBlock().setType(Material.AIR);
				blockAbove(standLocation()).getBlock().setType(Material.AIR);
			}
			standLocation = null;
		}

		public void clearChosenBlock() {
			chosenBlock = null;
		}

		public void foundBlock(boolean found) {
			hasFoundBlock = found;
		}

		public boolean hasFoundBlock() {
			return hasFoundBlock;
		}
	}

	private GameState currentGameState = GameState.STOPPED;

	private ModdingMinecraft javaPlugin;

	private static final long CHOOSING_TIME = TimeConstants.ONE_MINUTE * 2;
	private static final long SEARCHING_TIME = TimeConstants.ONE_MINUTE * 5;

	private enum GameState {
		SETUP, SEARCHING, CHOOSING, STOPPED
	}

	public BlockHunterListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		gameplayers = new ArrayList<>();

		blockStand = new ItemStack(Material.BEDROCK);
		ItemMeta meta = blockStand.getItemMeta();
		meta.setDisplayName("Block stand");
		blockStand.setItemMeta(meta);

		javaPlugin.commandExecutor().registerCommand("blockhunter_init", p -> initiateGame());
		javaPlugin.commandExecutor().registerCommand("blockhunter_start", p -> beginGame(p));
		javaPlugin.commandExecutor().registerCommand("blockhunter_join", this::join);
	}

	@EventHandler
	public final void onBlockPlaceEvent(BlockPlaceEvent event) {
		UUID playerUuid = event.getPlayer().getUniqueId();
		Optional<GamePlayer> gp = gamePlayer(playerUuid);
		switch (currentGameState) {
			case SETUP:
				if (event.getItemInHand().equals(blockStand)) {
					if (gp.isPresent()) {
						gp.get().setStand(event.getBlock().getLocation());
						Broadcaster.sendInfo(event.getPlayer(),
								"Block stand has been placed at: " + event.getBlock().getLocation().toString());
					}
				}
				break;
			case CHOOSING:
				if (gp.isPresent()) {
					if (event.getBlockPlaced().getLocation().equals(blockAbove(gp.get().standLocation()))) {
						Broadcaster.sendInfo(event.getPlayer(), "You have chosen: " + event.getBlock().getType());

						gp.get().chosenBlock(event.getBlock().getType());
					}
				}
				break;
			case SEARCHING:
				if (gp.isPresent()) {
					if (event.getBlockPlaced().getLocation().equals(blockAbove(gp.get().standLocation()))) {
						Material targetsBlock = gp.get().targetPlayer().chosenBlock();
						if (targetsBlock.equals(event.getBlock().getType())) {
							Broadcaster.sendInfo(event.getPlayer(), "Correct! wait for other players to finish");
							Broadcaster.broadcastInfo(event.getPlayer().getDisplayName() + " has found their block");
							gp.get().foundBlock(true);
						} else {
							Broadcaster.sendInfo(event.getPlayer(), "Incorrect, keep searching for: " + targetsBlock);
						}
					}
				}
				break;
			case STOPPED:
				break;
		}
	}

	@EventHandler
	public final void onBlockBreakEvent(BlockBreakEvent event) {
		UUID playerUuid = event.getPlayer().getUniqueId();
		Optional<GamePlayer> gp = gamePlayer(playerUuid);
		switch (currentGameState) {
			case SETUP:
				break;
			case CHOOSING:
				if (gp.isPresent()) {
					if (event.getBlock().getLocation().equals(blockAbove(gp.get().standLocation()))) {
						Broadcaster.sendInfo(event.getPlayer(), "You have removed your choice!");

						gp.get().clearChosenBlock();
					}
				}
				break;
			case SEARCHING:
				if (gp.isPresent()) {
					if (event.getBlock().getLocation().equals(blockAbove(gp.get().standLocation()))) {
						if (gp.get().hasFoundBlock()) {
							Broadcaster.sendInfo(event.getPlayer(), "Woops, you have unfound your block");
							Broadcaster
									.broadcastError(event.getPlayer().getDisplayName() + " has unfound their block??");
							gp.get().foundBlock(false);
						}
					}
				}
				break;
			case STOPPED:
				break;
		}
	}

	public final void initiateGame() {
		Broadcaster.broadcastInfo("Blockhunt has been initiated!");
		currentGameState = GameState.SETUP;
	}

	public final void join(Player p) {
		if (currentGameState == GameState.SETUP) {
			Broadcaster.broadcastInfo(p.getDisplayName() + " has joined the blockhunt");

			GamePlayer gamePlayer = new GamePlayer(p.getUniqueId());
			gameplayers.add(gamePlayer);
		} else {
			Broadcaster.sendError(p, "Please initiate the game first");
		}
	}

	public final void beginGame(Player host) {
		if (currentGameState == GameState.SETUP) {
			if (gameplayers.size() >= 2 && gameplayers.stream().allMatch(g -> g.standLocation() != null)) {
				Broadcaster.broadcastInfo("Blockhunt has STARTED!");
				setChoosingPhase();
			} else {
				Broadcaster.sendError(host,
						"Unable to start game. Requires 2 players and all players to have placed stands");
			}
		} else {
			Broadcaster.sendError(host, "Please initiate the game first");
		}
	}

	private void setChoosingPhase() {
		currentGameState = GameState.CHOOSING;
		for (GamePlayer gp : gameplayers) {
			Player player = Bukkit.getPlayer(gp.playerId());
			Broadcaster.sendInfo(player, "Choose a block and place it on your stand!");

			gp.clearStand();
			player.getInventory().addItem(blockStand);
		}
		Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
			new CountdownTimer(javaPlugin, 10,
					() -> Broadcaster.broadcastInfo("You have 10 seconds left to choose your block"),
					() -> Broadcaster.broadcastInfo("Time's up!"),
					timer -> Broadcaster.broadcastInfo("Time left to choose: " + timer.getSecondsLeft()));
		}, CHOOSING_TIME - TimeConstants.ONE_SECOND * 10);
		Bukkit.getScheduler().runTaskLater(javaPlugin, this::setSearchingPhase, CHOOSING_TIME);
	}

	private void setSearchingPhase() {
		currentGameState = GameState.SEARCHING;

		Bukkit.getScheduler().runTaskLater(javaPlugin,
				() -> Broadcaster.broadcastInfo("You have 1 minute left to search for your block!"),
				TimeConstants.ONE_MINUTE);
		Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
			new CountdownTimer(javaPlugin, 10,
					() -> Broadcaster.broadcastInfo("You have 10 seconds left to search for your block!"),
					() -> Broadcaster.broadcastInfo("Time's up!"),
					timer -> Broadcaster.broadcastInfo("Time left to find the block: " + timer.getSecondsLeft()));
		}, SEARCHING_TIME - TimeConstants.ONE_SECOND * 10);
		Bukkit.getScheduler().runTaskLater(javaPlugin, this::checkWinnerPhase, SEARCHING_TIME);
	}

	private void checkWinnerPhase() {
		for (GamePlayer gp : gameplayers) {
			if (!gp.hasFoundBlock()) {
				Broadcaster.broadcastInfo(Bukkit.getPlayer(gp.playerId).getDisplayName() + " has been eliminated");
			}
		}
		if (gameplayers.size() == 0) {
			Broadcaster.broadcastInfo("Stalemate! Round will start again");
			setChoosingPhase();
		} else if (gameplayers.size() == 1) {
			Broadcaster.broadcastInfo(
					"WINNER!! " + Bukkit.getPlayer(Collections.findFirst(gameplayers).playerId()).getDisplayName()
							+ " has won block hunter!");
			stopGame();
		} else {
			setChoosingPhase();
		}
	}

	private Optional<GamePlayer> gamePlayer(UUID player) {
		return Collections.find(gameplayers, GamePlayer::playerId, player);
	}

	private Location blockAbove(Location loc) {
		return loc.add(0, 1, 0).clone();
	}

	public final void stopGame() {
		for (GamePlayer gp : gameplayers) {
			gp.clearStand();
		}
		gameplayers.clear();
		currentGameState = GameState.STOPPED;
		Broadcaster.broadcastInfo("Blockhunt has been stopped!");
	}
}

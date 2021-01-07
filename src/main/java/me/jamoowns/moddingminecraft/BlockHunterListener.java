package me.jamoowns.moddingminecraft;

import static com.google.common.base.Predicates.not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

		public boolean hasStandPlaced() {
			return standLocation != null;
		}

		public Location standLocation() {
			return standLocation.clone();
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
			if (hasStandPlaced()) {
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

		public void setTargetPlayer(GamePlayer aTargetPlayer) {
			targetPlayer = aTargetPlayer;
		}
	}

	private GameState currentGameState = GameState.STOPPED;

	private ModdingMinecraft javaPlugin;

	private static final int CHOOSING_TIME_MINUTES = 2;
	private static final int SEARCHING_TIME_MINUTES = 5;

	private static final long CHOOSING_TIME = TimeConstants.ONE_MINUTE * CHOOSING_TIME_MINUTES;
	private static final long SEARCHING_TIME = TimeConstants.ONE_MINUTE * SEARCHING_TIME_MINUTES;

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

		javaPlugin.commandExecutor().registerCommand(Arrays.asList("blockhunter"), "init", p -> initiateGame());
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("blockhunter"), "join", this::join);
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("blockhunter"), "start", this::beginGame);
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("blockhunter"), "stop", p -> stopGame());
	}

	@EventHandler
	public final void onBlockPlaceEvent(BlockPlaceEvent event) {
		UUID playerUuid = event.getPlayer().getUniqueId();
		Optional<GamePlayer> gp = gamePlayer(playerUuid);
		switch (currentGameState) {
			case SETUP:
				break;
			case CHOOSING:
				if (gp.isPresent()) {
					if (event.getItemInHand().equals(blockStand)) {
						gp.get().setStand(event.getBlock().getLocation());
						Broadcaster.sendInfo(event.getPlayer(),
								"Block stand has been placed at: " + event.getBlock().getLocation().toString());
					} else {
						if (gp.get().hasStandPlaced()
								&& event.getBlockPlaced().getLocation().equals(blockAbove(gp.get().standLocation()))) {
							Broadcaster.sendInfo(event.getPlayer(), "You have chosen: " + event.getBlock().getType());

							gp.get().chosenBlock(event.getBlock().getType());
						}
					}
				}
				break;
			case SEARCHING:
				if (gp.isPresent()) {
					if (event.getItemInHand().equals(blockStand)) {
						gp.get().setStand(event.getBlock().getLocation());
						Broadcaster.sendInfo(event.getPlayer(),
								"Block stand has been placed at: " + event.getBlock().getLocation().toString());
					} else {
						if (gp.get().hasStandPlaced()
								&& event.getBlockPlaced().getLocation().equals(blockAbove(gp.get().standLocation()))) {
							Material targetsBlock = gp.get().targetPlayer().chosenBlock();
							if (targetsBlock.equals(event.getBlock().getType())) {
								Broadcaster.sendInfo(event.getPlayer(), "Correct! wait for other players to finish");
								Broadcaster
										.broadcastInfo(event.getPlayer().getDisplayName() + " has found their block");
								gp.get().foundBlock(true);
							} else {
								Broadcaster.sendInfo(event.getPlayer(),
										"Incorrect, keep searching for: " + targetsBlock);
							}
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
					if (gp.get().hasStandPlaced()
							&& event.getBlock().getLocation().equals(blockAbove(gp.get().standLocation()))) {
						Broadcaster.sendInfo(event.getPlayer(), "You have removed your choice!");

						gp.get().clearChosenBlock();
					}
				}
				break;
			case SEARCHING:
				if (gp.isPresent()) {
					if (gp.get().hasStandPlaced()
							&& event.getBlock().getLocation().equals(blockAbove(gp.get().standLocation()))) {
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
			if (gameplayers.size() >= 2) {
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
			Broadcaster.sendInfo(player, "Choose a block and place it on your stand You have 2 minutes!");

			gp.clearChosenBlock();
			gp.clearStand();
			player.getInventory().addItem(blockStand);
		}
		CountdownTimer countDown = new CountdownTimer(javaPlugin, 10, CHOOSING_TIME_MINUTES * 60 - 10,
				() -> Broadcaster.broadcastInfo("You have 10 seconds left to choose your block"),
				() -> Broadcaster.broadcastInfo("Time's up!"),
				timer -> Broadcaster.broadcastInfo("Time left to choose: " + timer.getSecondsLeft()));
		countDown.scheduleTimer();
		Bukkit.getScheduler().runTaskLater(javaPlugin, this::setSearchingPhase,
				CHOOSING_TIME + TimeConstants.ONE_SECOND);
	}

	private void setSearchingPhase() {
		currentGameState = GameState.SEARCHING;

		List<GamePlayer> unChosenPlayers = new ArrayList<>(gameplayers);
		for (GamePlayer gp : gameplayers) {
			GamePlayer targetPlayer;
			if (gameplayers.size() % 2 == 0) {
				targetPlayer = Collections
						.findFirst(unChosenPlayers.stream().filter(not(gp::equals)).collect(Collectors.toList()));
			} else {
				targetPlayer = Collections.findFirst(unChosenPlayers.stream().filter(not(gp::equals))
						.filter(other -> !other.targetPlayer().equals(gp)).collect(Collectors.toList()));
			}
			gp.setTargetPlayer(targetPlayer);
			Player player = Bukkit.getPlayer(gp.playerId());
			Player target = Bukkit.getPlayer(gp.targetPlayer().playerId());
			Material targetBlock = gp.targetPlayer().chosenBlock();
			Broadcaster.sendInfo(player, "You are searching for " + target.getDisplayName() + "'s block: " + targetBlock
					+ ". Place it on your stand. You have 4 minutes!");

			gp.clearStand();
			gp.foundBlock(false);
			player.getInventory().addItem(blockStand);
		}

		Bukkit.getScheduler().runTaskLater(javaPlugin,
				() -> Broadcaster.broadcastInfo("You have 1 minute left to search for your block!"),
				SEARCHING_TIME - TimeConstants.ONE_MINUTE);

		CountdownTimer countDown = new CountdownTimer(javaPlugin, 10, SEARCHING_TIME_MINUTES * 60 - 10,
				() -> Broadcaster.broadcastInfo("You have 10 seconds left to search for your block!"),
				() -> Broadcaster.broadcastInfo("Time's up!"),
				timer -> Broadcaster.broadcastInfo("Time left to find the block: " + timer.getSecondsLeft()));

		countDown.scheduleTimer();
		Bukkit.getScheduler().runTaskLater(javaPlugin, this::checkWinnerPhase,
				SEARCHING_TIME + TimeConstants.ONE_SECOND);
	}

	private void checkWinnerPhase() {
		for (GamePlayer gp : gameplayers) {
			if (!gp.hasFoundBlock()) {
				Broadcaster.broadcastInfo(Bukkit.getPlayer(gp.playerId).getDisplayName() + " has been eliminated");
			}
		}
		gameplayers.removeIf(gp -> !gp.hasFoundBlock());
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
		return loc.clone().add(0, 1, 0).clone();
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

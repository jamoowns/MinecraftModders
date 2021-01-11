package me.jamoowns.moddingminecraft.minigames.blockhunter;

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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Predicates;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.common.fated.Collections;
import me.jamoowns.moddingminecraft.common.time.CountdownTimer;
import me.jamoowns.moddingminecraft.common.time.TimeConstants;

public final class BlockHunterListener implements Listener {

	private enum GameState {
		SETUP, SEARCHING, CHOOSING, STOPPED
	}

	private static final int CHOOSING_TIME_MINUTES = 2;

	private static final int SEARCHING_TIME_MINUTES = 5;

	private static final long CHOOSING_TIME = TimeConstants.ONE_MINUTE * CHOOSING_TIME_MINUTES;

	private static final long SEARCHING_TIME = TimeConstants.ONE_MINUTE * SEARCHING_TIME_MINUTES;
	private final ItemStack blockStand;

	private final List<GamePlayer> gameplayers;
	private GameState currentGameState = GameState.STOPPED;

	private ModdingMinecraft javaPlugin;

	private List<CountdownTimer> countdownTimers;

	private List<Integer> timers;

	public BlockHunterListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		gameplayers = new ArrayList<>();
		countdownTimers = new ArrayList<>();
		timers = new ArrayList<>();

		blockStand = new ItemStack(Material.BEDROCK);
		ItemMeta meta = blockStand.getItemMeta();
		meta.setDisplayName("Block stand");
		blockStand.setItemMeta(meta);

		javaPlugin.commandExecutor().registerCommand(Arrays.asList("blockhunter"), "init", p -> initiateGame());
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("blockhunter"), "join", this::join);
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("blockhunter"), "start", this::beginGame);
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("blockhunter"), "stop", p -> stopGame());
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
								"Block stand has been placed at: " + pretty(event.getBlock().getLocation()));
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
								"Block stand has been placed at: " + pretty(event.getBlock().getLocation()));
					} else {
						if (gp.get().hasStandPlaced()
								&& event.getBlockPlaced().getLocation().equals(blockAbove(gp.get().standLocation()))) {
							Material targetsBlock = gp.get().targetPlayer().chosenBlock();
							if (targetsBlock.equals(event.getBlock().getType())) {
								Broadcaster.sendInfo(event.getPlayer(), "Correct! wait for other players to finish");
								Broadcaster
										.broadcastInfo(event.getPlayer().getDisplayName() + " has found their block");
								gp.get().foundBlock(true);

								if (gameplayers.stream().allMatch(GamePlayer::hasFoundBlock)) {
									stopAllTimers();
									setChoosingPhase();
								}
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

	public final void stopGame() {
		for (GamePlayer gp : gameplayers) {
			removeStand(gp);
			gp.clearStand();
		}
		gameplayers.clear();
		stopAllTimers();
		currentGameState = GameState.STOPPED;
		Broadcaster.broadcastInfo("Blockhunt has been stopped!");
	}

	private Location blockAbove(Location loc) {
		return loc.clone().add(0, 1, 0).clone();
	}

	private void checkWinnerPhase() {
		List<GamePlayer> removedGameplayers = gameplayers;
		removedGameplayers.removeIf(Predicates.not(GamePlayer::hasFoundBlock));
		if (removedGameplayers.size() != 0) {
			for (GamePlayer gp : gameplayers) {
				if (!gp.hasFoundBlock()) {
					Broadcaster
							.broadcastInfo(Bukkit.getPlayer(gp.playerId()).getDisplayName() + " has been eliminated");
				}
			}
		}
		if (gameplayers.size() == 0) {
			Broadcaster.broadcastInfo("Stalemate! Round will start again");
			setChoosingPhase();
		} else if (gameplayers.size() == 1) {
			Player player = Bukkit.getPlayer(Collections.findFirst(gameplayers).playerId());
			Broadcaster.broadcastInfo("WINNER!! " + player.getDisplayName() + " has won block hunter!");
			CountdownTimer countDown = new CountdownTimer(javaPlugin, 0, 5,
					() -> player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK),
					() -> player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK),
					timer -> player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK));
			countDown.scheduleTimer();
			countdownTimers.add(countDown);
			stopGame();
		} else {
			setChoosingPhase();
		}
	}

	private Optional<GamePlayer> gamePlayer(UUID player) {
		return Collections.find(gameplayers, GamePlayer::playerId, player);
	}

	private String pretty(Location loc) {
		return "X:" + loc.getX() + ", Y:" + loc.getY() + ", Z:" + loc.getZ();
	}

	private void removeStand(GamePlayer gp) {
		if (gp.hasStandPlaced()) {
			gp.standLocation().getBlock().setType(Material.AIR);
			blockAbove(gp.standLocation()).getBlock().setType(Material.AIR);
		}
	}

	private void setChoosingPhase() {
		currentGameState = GameState.CHOOSING;
		for (GamePlayer gp : gameplayers) {
			Player player = Bukkit.getPlayer(gp.playerId());
			Broadcaster.sendInfo(player, "Choose a block and place it on your stand You have 2 minutes!");

			gp.clearChosenBlock();
			removeStand(gp);
			gp.clearStand();
			player.getInventory().addItem(blockStand);
		}
		CountdownTimer countDown = new CountdownTimer(javaPlugin, 10, CHOOSING_TIME_MINUTES * 60 - 10,
				() -> Broadcaster.broadcastInfo("You have 10 seconds left to choose your block"),
				() -> Broadcaster.broadcastInfo("Time's up!"),
				timer -> Broadcaster.broadcastInfo("Time left to choose: " + timer.getSecondsLeft()));
		countDown.scheduleTimer();
		countdownTimers.add(countDown);
		int task = Bukkit.getScheduler()
				.runTaskLater(javaPlugin, this::setSearchingPhase, CHOOSING_TIME + TimeConstants.ONE_SECOND)
				.getTaskId();
		timers.add(task);
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

			removeStand(gp);
			gp.clearStand();
			gp.foundBlock(false);
			player.getInventory().addItem(blockStand);
		}

		int task = Bukkit.getScheduler()
				.runTaskLater(javaPlugin,
						() -> Broadcaster.broadcastInfo("You have 1 minute left to search for your block!"),
						SEARCHING_TIME - TimeConstants.ONE_MINUTE)
				.getTaskId();
		timers.add(task);

		CountdownTimer countDown = new CountdownTimer(javaPlugin, 10, SEARCHING_TIME_MINUTES * 60 - 10,
				() -> Broadcaster.broadcastInfo("You have 10 seconds left to search for your block!"),
				() -> Broadcaster.broadcastInfo("Time's up!"),
				timer -> Broadcaster.broadcastInfo("Time left to find the block: " + timer.getSecondsLeft()));

		countDown.scheduleTimer();
		countdownTimers.add(countDown);
		task = Bukkit.getScheduler()
				.runTaskLater(javaPlugin, this::checkWinnerPhase, SEARCHING_TIME + TimeConstants.ONE_SECOND)
				.getTaskId();
		timers.add(task);
	}

	private void stopAllTimers() {
		timers.forEach(Bukkit.getScheduler()::cancelTask);
		timers.clear();
		countdownTimers.forEach(CountdownTimer::kill);
		countdownTimers.clear();
	}
}

package me.jamoowns.moddingminecraft.minigames.blockhunter;

import static com.google.common.base.Predicates.not;

import java.util.ArrayList;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Predicates;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.commands.ModdersCommand;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.common.fated.Collections;
import me.jamoowns.moddingminecraft.common.observable.ObservableProperty;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.common.time.CountdownTimer;
import me.jamoowns.moddingminecraft.common.time.TimeConstants;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class BlockHunterListener implements IGameEventListener {

	private enum GameState {
		SETUP, SEARCHING, CHOOSING, STOPPED
	}

	private static final int CHOOSING_TIME_MINUTES = 2;

	private static final int SEARCHING_TIME_MINUTES = 10;

	private static final long SEARCHING_TIME = TimeConstants.ONE_MINUTE * SEARCHING_TIME_MINUTES;

	private final ItemStack blockStand;

	private List<GamePlayer> gameplayers;

	private GameState currentGameState = GameState.STOPPED;

	private ModdingMinecraft javaPlugin;

	private List<CountdownTimer> countdownTimers;

	private List<Integer> timers;

	private ObservableProperty<Boolean> gameEnabled;

	public BlockHunterListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		gameplayers = new ArrayList<>();
		countdownTimers = new ArrayList<>();
		timers = new ArrayList<>();
		gameEnabled = new ObservableProperty<>(false);

		blockStand = new ItemStack(Material.BEDROCK);
		ItemMeta meta = blockStand.getItemMeta();
		meta.setDisplayName("Block stand");
		blockStand.setItemMeta(meta);

		ModdersCommand rootCommand = javaPlugin.commandExecutor().registerCommand("blockhunter",
				p -> Broadcaster.sendGameInfo(p, "Blockhunter!"));
		javaPlugin.commandExecutor().registerCommand(rootCommand, "init", p -> initiateGame());
		javaPlugin.commandExecutor().registerCommand(rootCommand, "join", this::join);
		javaPlugin.commandExecutor().registerCommand(rootCommand, "start", this::beginGame);
		javaPlugin.commandExecutor().registerCommand(rootCommand, "stop", p -> stopGame());
	}

	@Override
	public final ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return gameEnabled;
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
						Broadcaster.sendGameInfo(event.getPlayer(), "You have removed your choice!");

						gp.get().clearChosenBlock();
					}
				}
				break;
			case SEARCHING:
				if (gp.isPresent()) {
					if (gp.get().hasStandPlaced()
							&& event.getBlock().getLocation().equals(blockAbove(gp.get().standLocation()))) {
						if (gp.get().hasFoundBlock()) {
							Broadcaster.sendGameInfo(event.getPlayer(), "Woops, you have unfound your block");
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
						Broadcaster.sendGameInfo(event.getPlayer(),
								"Block stand has been placed at: " + pretty(event.getBlock().getLocation()));
					} else {
						if (gp.get().hasStandPlaced()
								&& event.getBlockPlaced().getLocation().equals(blockAbove(gp.get().standLocation()))) {
							Broadcaster.sendGameInfo(event.getPlayer(),
									"You have chosen: " + event.getBlock().getType());

							gp.get().chosenBlock(event.getBlock().getType());
						}
					}
				}
				break;
			case SEARCHING:
				if (gp.isPresent()) {
					if (event.getItemInHand().equals(blockStand)) {
						gp.get().setStand(event.getBlock().getLocation());
						Broadcaster.sendGameInfo(event.getPlayer(),
								"Block stand has been placed at: " + pretty(event.getBlock().getLocation()));
					} else {
						if (gp.get().hasStandPlaced()
								&& event.getBlockPlaced().getLocation().equals(blockAbove(gp.get().standLocation()))) {
							Material targetsBlock = gp.get().targetPlayer().chosenBlock();
							if (targetsBlock.equals(event.getBlock().getType())) {
								Broadcaster.sendGameInfo(event.getPlayer(),
										"Correct! wait for other players to finish");
								Broadcaster.broadcastGameInfo(
										event.getPlayer().getDisplayName() + " has found their block");
								gp.get().foundBlock(true);

								if (gameplayers.stream().allMatch(GamePlayer::hasFoundBlock)) {
									stopAllTimers();
									setChoosingPhase();
								}
							} else {
								Broadcaster.sendGameInfo(event.getPlayer(),
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

	@Override
	public final void onDisabled() {
		stopGame();
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
	}

	@EventHandler
	public final void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Optional<GamePlayer> gp = gamePlayer(player.getUniqueId());
		if (gp.isPresent() && player.getInventory().contains(blockStand)) {
			event.getDrops().remove(blockStand);
			player.getInventory().clear();
			player.getInventory().addItem(blockStand);
			event.setKeepInventory(true);
		}
	}

	@Override
	public final void onServerStop() {
		stopGame();
	}

	private final void beginGame(Player host) {
		if (currentGameState == GameState.SETUP) {
			if (gameplayers.size() >= 2) {
				Broadcaster.broadcastInfo("Blockhunt has STARTED!");
				setChoosingPhase();
			} else {
				Broadcaster.sendError(host, "Unable to start game. Requires 2+ players.");
			}
		} else {
			Broadcaster.sendError(host, "Please initiate the game first");
		}
	}

	private Location blockAbove(Location loc) {
		return loc.clone().add(0, 1, 0).clone();
	}

	private void checkWinnerPhase() {
		List<GamePlayer> removedGameplayers = new ArrayList<>(gameplayers);
		List<GamePlayer> playersLeft = new ArrayList<>(gameplayers);
		playersLeft.removeIf(Predicates.not(GamePlayer::hasFoundBlock));
		removedGameplayers.removeIf(GamePlayer::hasFoundBlock);

		if (playersLeft.size() != 0) {
			for (GamePlayer gp : removedGameplayers) {
				Broadcaster
						.broadcastGameInfo(Bukkit.getPlayer(gp.playerId()).getDisplayName() + " has been eliminated");
			}
		}
		if (playersLeft.size() == 0) {
			Broadcaster.broadcastGameInfo("Stalemate! Round will start again");
			setChoosingPhase();
		} else if (playersLeft.size() == 1) {
			Player winner = Bukkit.getPlayer(Collections.findFirst(playersLeft).playerId());
			Broadcaster.broadcastGameInfo("WINNER!! " + winner.getDisplayName() + " has won block hunter!");
			CountdownTimer countDown = new CountdownTimer(javaPlugin, 5, 0,
					() -> winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK),
					() -> winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK),
					timer -> winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK));
			countDown.scheduleTimer();
			countDown = new CountdownTimer(javaPlugin, 5, 0.5,
					() -> winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK),
					() -> winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK),
					timer -> winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK));
			countDown.scheduleTimer();
			stopGame();
		} else {
			gameplayers = playersLeft;
			setChoosingPhase();
		}
	}

	private Optional<GamePlayer> gamePlayer(UUID player) {
		return Collections.find(gameplayers, GamePlayer::playerId, player);
	}

	private final void initiateGame() {
		Broadcaster.broadcastGameInfo("Blockhunt has been initiated!");
		currentGameState = GameState.SETUP;
		gameEnabled.set(true);
	}

	private final void join(Player p) {
		boolean alreadyPlaying = gameplayers.stream().map(GamePlayer::playerId).anyMatch(p.getUniqueId()::equals);
		if (currentGameState == GameState.SETUP && !alreadyPlaying) {
			Broadcaster.broadcastGameInfo(p.getDisplayName() + " has joined the Blockhunt");

			GamePlayer gamePlayer = new GamePlayer(p.getUniqueId());
			gameplayers.add(gamePlayer);
		} else {
			Broadcaster.sendError(p, "Please initiate the game first");
		}
	}

	private String pretty(Location loc) {
		return "X:" + loc.getX() + ", Y:" + loc.getY() + ", Z:" + loc.getZ();
	}

	private void removeStand(GamePlayer gp) {
		if (gp.hasStandPlaced()) {
			gp.standLocation().getBlock().setType(Material.AIR);
			blockAbove(gp.standLocation()).getBlock().setType(Material.AIR);
		} else {
			Player player = Bukkit.getPlayer(gp.playerId());
			if (player != null) {
				player.getInventory().remove(blockStand);
			}
		}
	}

	private void setChoosingPhase() {
		currentGameState = GameState.CHOOSING;
		for (GamePlayer gp : gameplayers) {
			Player player = Bukkit.getPlayer(gp.playerId());

			gp.clearChosenBlock();
			removeStand(gp);
			gp.clearStand();
			player.getInventory().addItem(blockStand);
		}

		Broadcaster.broadcastGameInfo("Choose a block and place it on your stand You have 2 minutes!");
		CountdownTimer countDown = new CountdownTimer(javaPlugin, 10, CHOOSING_TIME_MINUTES * 60 - 10,
				() -> Broadcaster.broadcastGameInfo("You have 10 seconds left to choose your block"),
				this::setSearchingPhase,
				timer -> Broadcaster.broadcastGameInfo("Time left to choose: " + timer.getSecondsLeft()));
		countDown.scheduleTimer();
		countdownTimers.add(countDown);
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
						.filter(other -> other.targetPlayer() == null || !other.targetPlayer().equals(gp))
						.collect(Collectors.toList()));
			}
			gp.setTargetPlayer(targetPlayer);
			Player player = Bukkit.getPlayer(gp.playerId());
			Player target = Bukkit.getPlayer(gp.targetPlayer().playerId());
			Material targetBlock = gp.targetPlayer().chosenBlock();
			if (targetBlock == null) {
				gp.targetPlayer().chosenBlock(Material.DIRT);
				targetBlock = Material.DIRT;
			}
			Broadcaster.sendGameInfo(player, "You are searching for " + target.getDisplayName() + "'s block: "
					+ targetBlock + ". Place it on your stand. You have 5 minutes!");

			removeStand(gp);
			gp.clearStand();
			gp.foundBlock(false);
			player.getInventory().addItem(blockStand);
		}

		int task = Bukkit.getScheduler()
				.runTaskLater(javaPlugin,
						() -> Broadcaster.broadcastGameInfo("You have 1 minute left to search for your block!"),
						SEARCHING_TIME - TimeConstants.ONE_MINUTE)
				.getTaskId();
		timers.add(task);

		CountdownTimer countDown = new CountdownTimer(javaPlugin, 10, SEARCHING_TIME_MINUTES * 60 - 10,
				() -> Broadcaster.broadcastGameInfo("You have 10 seconds left to search for your block!"),
				this::checkWinnerPhase,
				timer -> Broadcaster.broadcastGameInfo("Time left to find the block: " + timer.getSecondsLeft()));

		countDown.scheduleTimer();
		countdownTimers.add(countDown);
	}

	private void stopAllTimers() {
		timers.forEach(Bukkit.getScheduler()::cancelTask);
		timers.clear();
		countdownTimers.forEach(CountdownTimer::kill);
		countdownTimers.clear();
	}

	private final void stopGame() {
		for (GamePlayer gp : gameplayers) {
			removeStand(gp);
			gp.clearStand();
		}
		gameplayers.clear();
		stopAllTimers();
		if (currentGameState != GameState.STOPPED) {
			currentGameState = GameState.STOPPED;
			Broadcaster.broadcastGameInfo("Blockhunt has been stopped!");
		}
		gameEnabled.set(false);
	}
}

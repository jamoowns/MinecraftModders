package me.jamoowns.moddingminecraft.minigames.battleroyale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class BattleRoyaleListener implements IGameEventListener {

	private enum GameState {
		LOBBY, SETUP, PLAYING, STOPPED
	}

	private static final String GAME_NAME = "Battle Royale";

	private static final Integer GOAL_SCORE = 5;

	private static final Integer GOAL_STAND_LOCATIONS = 5;

	private List<Location> goalStands;

	private CustomItem goalBlockStand;

	private CustomItem goalBlock;

	private Map<UUID, Integer> playerScoreById;

	private Map<UUID, Location> playerHomeLocationById;

	private Map<UUID, CustomItem> playerHomeItemById;

	private GameState currentGameState;

	private ModdingMinecraft javaPlugin;

	HashMap<Player, Inventory> oldInvs = new HashMap<Player, Inventory>();

	public BattleRoyaleListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		currentGameState = GameState.STOPPED;
		goalStands = new ArrayList<>();
		playerScoreById = new HashMap<>();
		playerHomeItemById = new HashMap<>();
		playerHomeLocationById = new HashMap<>();

		goalBlock = new CustomItem("Goal Block", Material.DIAMOND_BLOCK);
		goalBlock.setBlockPlaceEvent(event -> {
			if (currentGameState == GameState.PLAYING) {
				for (Location goalStandLoc : goalStands) {
					if (event.getBlock().getLocation().distance(goalStandLoc) < 3) {
						Integer currentScore = playerScoreById.get(event.getPlayer().getUniqueId());
						playerScoreById.put(event.getPlayer().getUniqueId(), currentScore + 1);
						checkForVictory(event.getPlayer());
					}
				}
			}
		});

		goalBlockStand = new CustomItem("Goal Block Stand", Material.OBSIDIAN);
		goalBlockStand.setBlockPlaceEvent(event -> {
			if (currentGameState == GameState.SETUP) {
				Broadcaster.sendGameInfo(event.getPlayer(), "Added a goal location to the game");
				goalStands.add(event.getBlock().getLocation());
			}
		});
		aJavaPlugin.customItems().silentRegister(goalBlock);
		aJavaPlugin.customItems().silentRegister(goalBlockStand);

		aJavaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "init", p -> initiate());
		aJavaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "join", this::join);
		aJavaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "setup", this::setup);
		aJavaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "start", this::start);
		aJavaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "stop", p -> cleanup());
	}

	@Override
	public final void cleanup() {
		goalStands.clear();
		playerScoreById.clear();
		playerHomeItemById.clear();
		playerHomeLocationById.clear();
		if (currentGameState != GameState.STOPPED) {
			currentGameState = GameState.STOPPED;
			Broadcaster.broadcastGameInfo(GAME_NAME + " has been stopped!");
		}
		for (Map.Entry<Player, Inventory> entry : oldInvs.entrySet()) {
			Player key = entry.getKey();
			Inventory value = entry.getValue();
			key.getInventory().setContents(value.getContents());
			key.updateInventory();
		}

	}

	public final void initiate() {
		Broadcaster.broadcastGameInfo(GAME_NAME + " been initiated!");
		currentGameState = GameState.LOBBY;
	}

	public final void join(Player p) {
		boolean alreadyPlaying = playerScoreById.containsKey(p.getUniqueId());
		if (currentGameState == GameState.LOBBY && !alreadyPlaying) {
			Broadcaster.broadcastGameInfo(p.getDisplayName() + " has joined the " + GAME_NAME);
			playerScoreById.put(p.getUniqueId(), 0);
			oldInvs.put(p, p.getInventory());
			CustomItem homeStand = new CustomItem(p.getDisplayName() + "'s Home", Material.GREEN_BED);
			homeStand.setBlockPlaceEvent(event -> {
				if (currentGameState == GameState.SETUP) {
					playerHomeLocationById.put(event.getPlayer().getUniqueId(), event.getBlock().getLocation());
					Broadcaster.sendGameInfo(event.getPlayer(), "Home sweet home has been set");
				}
			});
			javaPlugin.customItems().silentRegister(homeStand);

			playerHomeItemById.put(p.getUniqueId(), homeStand);
		} else {
			Broadcaster.sendError(p, "Game must be in the lobby");
		}
	}

	public final void setup(Player host) {
		if (currentGameState == GameState.LOBBY && playerScoreById.size() >= 2) {
			Broadcaster.broadcastGameInfo("Setting up " + GAME_NAME);
			Broadcaster.sendGameInfo(host, "Place all of the goal stands on the battle field");
			currentGameState = GameState.SETUP;
			ItemStack goal = goalBlockStand.asItem();
			goal.setAmount(GOAL_STAND_LOCATIONS);
			host.getInventory().addItem(goal);

			for (Entry<UUID, CustomItem> playerHomeItem : playerHomeItemById.entrySet()) {
				Player player = Bukkit.getPlayer(playerHomeItem.getKey());
				player.getInventory().addItem(playerHomeItem.getValue().asItem());
			}
		} else {
			Broadcaster.sendError(host, "Game must be in the lobby and atleast two players joined");
		}
	}

	public final void start(Player host) {
		if (currentGameState == GameState.SETUP && playerScoreById.size() == playerHomeLocationById.size()) {
			Broadcaster.broadcastGameInfo(GAME_NAME + " has started!");
			for (Entry<UUID, Location> entry : playerHomeLocationById.entrySet()) {
				Player player = Bukkit.getPlayer(entry.getKey());
				// player.getInventory().clear();

				player.teleport(entry.getValue().add(0, 3, 0));
			}
			currentGameState = GameState.PLAYING;
		} else {
			Broadcaster.sendError(host, "Must setup first. Not all players have placed their homes yet.");
		}
	}

	private void checkForVictory(Player player) {
		Integer currentScore = playerScoreById.get(player.getUniqueId());
		if (currentScore >= GOAL_SCORE) {
			Broadcaster.broadcastInfo(player.getDisplayName() + " has won " + GAME_NAME + "!");
			cleanup();
		} else {
			Broadcaster.sendGameInfo(player, "Current score: " + currentScore + "/" + GOAL_SCORE);
		}
	}
}

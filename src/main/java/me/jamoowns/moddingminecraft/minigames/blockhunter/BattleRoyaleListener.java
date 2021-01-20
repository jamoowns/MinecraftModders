package me.jamoowns.moddingminecraft.minigames.blockhunter;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class BattleRoyaleListener implements IGameEventListener {

	private enum GameState {
		LOBBY, SETUP, PLAYING, STOPPED
	}

	private static final String GAME_NAME = "Battle Royale";

	private static final Integer GOAL_SCORE = 5;

	private static final Integer GOAL_STAND_LOCATIONS = 5;

	private List<Location> goalStands;

	private ItemStack goalBlockStand;

	private ItemStack goalBlock;

	private Map<UUID, Integer> playerScoreById;

	private Map<UUID, Location> playerHomeLocationById;

	private Map<UUID, ItemStack> playerHomeItemById;

	private GameState currentGameState;

	public BattleRoyaleListener(ModdingMinecraft javaPlugin) {
		currentGameState = GameState.STOPPED;
		playerScoreById = new HashMap<>();
		playerHomeItemById = new HashMap<>();
		playerHomeLocationById = new HashMap<>();

		goalBlockStand = new ItemStack(Material.OBSIDIAN);
		ItemMeta meta = goalBlockStand.getItemMeta();
		meta.setDisplayName("Goal block stand");
		goalBlockStand.setItemMeta(meta);

		goalBlock = new ItemStack(Material.DIAMOND_BLOCK);
		ItemMeta goalMeta = goalBlockStand.getItemMeta();
		goalMeta.setDisplayName("Goal Block");
		goalBlockStand.setItemMeta(goalMeta);

		javaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "init", p -> initiate());
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "join", this::join);
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "setup", this::setup);
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "start", this::start);
		javaPlugin.commandExecutor().registerCommand(Arrays.asList("royale"), "stop", p -> cleanup());
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

			ItemStack homeStand = new ItemStack(Material.ARMOR_STAND);
			ItemMeta meta = homeStand.getItemMeta();
			meta.setDisplayName(p.getDisplayName() + "'s Home Stand");
			homeStand.setItemMeta(meta);

			playerHomeItemById.put(p.getUniqueId(), homeStand);
		} else {
			Broadcaster.sendError(p, "Game must be in the lobby");
		}
	}

	@EventHandler
	public final void onBlockPlaceEvent(BlockPlaceEvent event) {
		switch (currentGameState) {
			case PLAYING:
				if (event.getItemInHand().equals(goalBlock)) {
					for (Location goalStandLoc : goalStands) {
						if (event.getBlock().getLocation().distance(goalStandLoc) < 3) {
							Integer currentScore = playerScoreById.get(event.getPlayer().getUniqueId());
							playerScoreById.put(event.getPlayer().getUniqueId(), currentScore + 1);
							checkForVictory(event.getPlayer());
						}
					}
				}
				break;
			case SETUP:
				Broadcaster.sendInfo(event.getPlayer(), "Do we get here atleast?");
				Broadcaster.sendInfo(event.getPlayer(), event.getItemInHand());
				if (event.getItemInHand().equals(goalBlockStand)) {
					Broadcaster.sendGameInfo(event.getPlayer(), "Set a goal block location");
					goalStands.add(event.getBlock().getLocation());
				} else {
					if (event.getItemInHand().equals(playerHomeItemById.get(event.getPlayer().getUniqueId()))) {
						playerHomeLocationById.put(event.getPlayer().getUniqueId(), event.getBlock().getLocation());

						Broadcaster.sendGameInfo(event.getPlayer(), "Home sweet home has been set");
					}
				}
				break;
			case LOBBY:
			case STOPPED:
				break;
		}
	}

	public final void setup(Player host) {
		if (currentGameState == GameState.LOBBY && playerScoreById.size() >= 2) {
			Broadcaster.broadcastGameInfo("Setting up " + GAME_NAME);
			Broadcaster.sendGameInfo(host, "Place all of the goal stands on the battle field");
			currentGameState = GameState.SETUP;
			ItemStack goal = goalBlockStand.clone();
			goal.setAmount(GOAL_STAND_LOCATIONS);
			host.getInventory().addItem(goal);

			for (Entry<UUID, ItemStack> playerHomeItem : playerHomeItemById.entrySet()) {
				Player player = Bukkit.getPlayer(playerHomeItem.getKey());
				player.getInventory().addItem(playerHomeItem.getValue());
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
				player.getInventory().clear();

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

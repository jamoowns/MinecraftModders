package me.jamoowns.moddingminecraft.minigames.battleroyale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.commands.ModdersCommand;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory.KitLevel;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameKit;
import me.jamoowns.moddingminecraft.minigames.mgsettings.LobbyListener;
import me.jamoowns.moddingminecraft.teams.TeamColour;

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

	private final Random RANDOM;

	private final Vector ABOVE;

	private Location goalLocation;

	private ArrayList<Location> flagBlockLocations;
	private LobbyListener lobby;
	private GameKit gameKit;

	public BattleRoyaleListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		currentGameState = GameState.STOPPED;
		flagBlockLocations = new ArrayList<>();
		goalStands = new ArrayList<>();
		playerScoreById = new HashMap<>();
		playerHomeItemById = new HashMap<>();
		playerHomeLocationById = new HashMap<>();
		lobby = new LobbyListener();
		gameKit = Armory.offense(KitLevel.AVERAGE).combine(Armory.defence(KitLevel.AVERAGE))
				.combine(Armory.food(KitLevel.LOW));

		RANDOM = new Random();
		ABOVE = new Vector(0, 1, 0);

		goalBlock = new CustomItem("Goal Block", Material.DIAMOND_BLOCK);
		goalBlock.setBlockPlaceEvent(event -> {
			if (currentGameState == GameState.PLAYING) {
				Location playerHome = playerHomeLocationById.get(event.getPlayer().getUniqueId());
				if (event.getBlockPlaced().getLocation().distance(playerHome) < 7) {
					event.getItemInHand().setAmount(0);
					event.getBlock().setType(Material.AIR);
					Integer currentScore = playerScoreById.get(event.getPlayer().getUniqueId());
					Integer updatedScore = currentScore + 1;
					Location scoreLocation = playerHome.clone().add(0, updatedScore, 0);
					event.getBlock().getWorld().getBlockAt(scoreLocation).setType(goalBlock.material());
					playerScoreById.put(event.getPlayer().getUniqueId(), updatedScore);
					boolean hasWon = checkForVictory(event.getPlayer());
					if (!hasWon) {
						lobby.sendLobbyMessage(event.getPlayer().getDisplayName() + " has scored a point!");
						resetGoalBlock();
					}
				} else {
					lobby.sendPlayerMessage(event.getPlayer(), "You must place that closer to your homebase");

					event.setCancelled(true);
				}
			}
		});

		goalBlockStand = new CustomItem("Goal Block Stand", Material.OBSIDIAN);
		goalBlockStand.setBlockPlaceEvent(event -> {
			if (currentGameState == GameState.SETUP) {
				lobby.sendPlayerMessage(event.getPlayer(), "Added a goal location to the game");
				goalStands.add(event.getBlock().getLocation());
			}
		});
		aJavaPlugin.customItems().silentRegister(goalBlock);
		aJavaPlugin.customItems().silentRegister(goalBlockStand);

		ModdersCommand rootCommand = javaPlugin.commandExecutor().registerCommand("royale",
				p -> Broadcaster.sendGameInfo(p, "Battle royale!"));
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "init", p -> initiate());
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "join", this::join);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "unjoin", this::unjoin);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "setup", this::setup);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "start", this::start);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "stop", p -> cleanup());
	}

	@Override
	public final void cleanup() {
		goalStands.forEach(l -> l.getBlock().setType(Material.AIR));
		goalStands.clear();
		flagBlockLocations.forEach(l -> l.getBlock().setType(Material.AIR));
		flagBlockLocations.clear();
		playerScoreById.clear();
		playerHomeItemById.clear();
		playerHomeLocationById.values().forEach(l -> l.getBlock().setType(Material.AIR));
		playerHomeLocationById.clear();
		if (currentGameState != GameState.STOPPED) {
			currentGameState = GameState.STOPPED;
			lobby.sendLobbyMessage(GAME_NAME + " has been stopped!");
		}
		lobby.removeAllFromLobby();
	}

	@EventHandler
	public final void onBlockDamageEvent(BlockDamageEvent event) {
		if (currentGameState == GameState.PLAYING) {
			if (event.getBlock().getLocation().equals(goalLocation)) {
				event.getBlock().setType(Material.AIR);
				event.getPlayer().getInventory().addItem(goalBlock.asItem());
			}
		}
	}

	private void buildFlag(Block baseBlock, Material baseColour, Material flagColour) {
		ArrayList<Location> locations = new ArrayList<>();
		locations.add(baseBlock.getLocation().add(-1, 0, 0));
		locations.add(baseBlock.getLocation().add(1, 0, 0));
		locations.add(baseBlock.getLocation().add(-1, 0, -1));
		locations.add(baseBlock.getLocation().add(-1, 0, 1));
		locations.add(baseBlock.getLocation().add(1, 0, -1));
		locations.add(baseBlock.getLocation().add(1, 0, 1));
		locations.add(baseBlock.getLocation().add(0, 0, -1));
		locations.add(baseBlock.getLocation().add(0, 0, 1));
		locations.forEach(location -> baseBlock.getWorld().getBlockAt(location).setType(baseColour));
		flagBlockLocations.addAll(locations);

		/* Mast. */
		for (int i = 1; i <= GOAL_SCORE; i++) {
			Location location = baseBlock.getLocation().add(0, i, 0);
			flagBlockLocations.add(location);
			baseBlock.getWorld().getBlockAt(location).setType(Material.WARPED_FENCE);
		}

		/* Flag. */
		Location topBlock = baseBlock.getLocation().add(0, GOAL_SCORE, 0);
		locations.clear();
		locations.add(topBlock.clone().add(1, 0, 0));
		locations.add(topBlock.clone().add(1, -1, 0));
		locations.add(topBlock.clone().add(2, 0, 0));
		locations.add(topBlock.clone().add(2, -1, 0));
		locations.add(topBlock.clone().add(3, 0, 0));
		locations.add(topBlock.clone().add(3, -1, 0));
		locations.forEach(location -> baseBlock.getWorld().getBlockAt(location).setType(flagColour));
		flagBlockLocations.addAll(locations);
	}

	private boolean checkForVictory(Player player) {
		Integer currentScore = playerScoreById.get(player.getUniqueId());
		if (currentScore >= GOAL_SCORE) {
			lobby.sendLobbyMessage(player.getDisplayName() + " has won " + GAME_NAME + "!");
			playWinningFireworks(javaPlugin.teams().getTeam(player.getUniqueId()).getTeamColour().getFirework(),
					player);
			cleanup();
			return true;
		} else {
			lobby.sendPlayerMessage(player, "Your current score: " + currentScore + "/" + GOAL_SCORE);
			return false;
		}
	}

	private final void initiate() {
		Broadcaster.broadcastGameInfo(GAME_NAME + " been initiated!");
		currentGameState = GameState.LOBBY;
	}

	private final void join(Player p) {
		boolean alreadyPlaying = playerScoreById.containsKey(p.getUniqueId());
		if (currentGameState == GameState.LOBBY && !alreadyPlaying) {

			playerScoreById.put(p.getUniqueId(), 0);
			lobby.addToLobby(p);
			for (ItemStack item : gameKit.items()) {
				p.getInventory().addItem(item);
			}
			lobby.sendLobbyMessage(p.getDisplayName() + " has joined the " + GAME_NAME + " ( " + lobby.size() + " / "
					+ lobby.maxSize() + " )");

			TeamColour teamColour = javaPlugin.teams().getTeam(p.getUniqueId()).getTeamColour();
			CustomItem homeStand = new CustomItem(p.getDisplayName() + "'s Home", teamColour.getBase());
			homeStand.setBlockPlaceEvent(event -> {
				if (currentGameState == GameState.SETUP) {
					playerHomeLocationById.put(event.getPlayer().getUniqueId(), event.getBlock().getLocation());
					lobby.sendPlayerMessage(event.getPlayer(), "Home sweet home has been set");
					buildFlag(event.getBlock(), teamColour.getBase(), teamColour.getHead());
				}
			});
			javaPlugin.customItems().silentRegister(homeStand);

			playerHomeItemById.put(p.getUniqueId(), homeStand);
		} else {
			Broadcaster.sendError(p, "Game must be in the lobby");
		}

	}

	private void playWinningFireworks(Color color, Player p) {
		World world = flagBlockLocations.get(0).getWorld();

		Firework fw = (Firework) world.spawnEntity(p.getLocation(), EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		fwm.setPower(2);
		fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

		FireworkMeta fwm1 = fw.getFireworkMeta();
		fwm1.setPower(1);
		fwm1.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

		FireworkMeta fwm2 = fw.getFireworkMeta();
		fwm2.setPower(2);
		fwm2.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

		FireworkMeta fwm3 = fw.getFireworkMeta();
		fwm3.setPower(3);
		fwm3.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

		fw.setFireworkMeta(fwm);
		fw.detonate();

		for (int i = 0; i < flagBlockLocations.size(); i++) {
			Firework fw2 = (Firework) world.spawnEntity(flagBlockLocations.get(i).clone().add(0, 5, 0),
					EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm1);
			fw2 = (Firework) world.spawnEntity(flagBlockLocations.get(i).clone().add(0, 5, 0), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm2);
			fw2 = (Firework) world.spawnEntity(flagBlockLocations.get(i).clone().add(0, 5, 0), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm3);
		}
		for (int i = 0; i < goalStands.size(); i++) {
			Firework fw2 = (Firework) world.spawnEntity(goalStands.get(i).clone(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm1);
			fw2 = (Firework) world.spawnEntity(goalStands.get(i).clone(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm2);
			fw2 = (Firework) world.spawnEntity(goalStands.get(i).clone(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm3);
		}
		ArrayList<Player> plist = lobby.playerList();
		for (int i = 0; i < plist.size(); i++) {
			Firework fw2 = (Firework) world.spawnEntity(plist.get(i).getLocation().clone(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm1);
			fw2 = (Firework) world.spawnEntity(plist.get(i).getLocation().clone(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm2);
			fw2 = (Firework) world.spawnEntity(plist.get(i).getLocation().clone(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm3);
		}

	}

	private void resetGoalBlock() {
		lobby.sendLobbyMessage("Block has returned to a goal stand.");
		/* Places goal block on a random goal stand. */
		Location goalStandToPlaceOn = goalStands.get(RANDOM.nextInt(goalStands.size())).clone().add(ABOVE);
		goalStandToPlaceOn.getWorld().getBlockAt(goalStandToPlaceOn).setType(goalBlock.material());
		goalLocation = goalStandToPlaceOn;
	}

	private final void setup(Player host) {
		if (currentGameState == GameState.LOBBY && playerScoreById.size() >= 2) {
			lobby.sendLobbyMessage("Setting up " + GAME_NAME);
			lobby.sendPlayerMessage(host, "Place all of the goal stands on the battle field");
			currentGameState = GameState.SETUP;
			ItemStack goal = goalBlockStand.asItem();
			goal.setAmount(GOAL_STAND_LOCATIONS);
			host.getInventory().addItem(goal);

			for (Entry<UUID, CustomItem> playerHomeItem : playerHomeItemById.entrySet()) {
				Player player = Bukkit.getPlayer(playerHomeItem.getKey());
				player.getInventory().addItem(playerHomeItem.getValue().asItem());
			}
		} else {
			lobby.sendPlayerMessage(host, "Game must be in the lobby and atleast two players joined");
		}
	}

	private final void start(Player host) {
		if (currentGameState == GameState.SETUP && playerScoreById.size() == playerHomeLocationById.size()) {
			Broadcaster.broadcastGameInfo(GAME_NAME + " has started!");
			for (Entry<UUID, Location> entry : playerHomeLocationById.entrySet()) {
				Player player = Bukkit.getPlayer(entry.getKey());
				player.teleport(entry.getValue().clone().add(0, 3, 0));
			}
			currentGameState = GameState.PLAYING;
			resetGoalBlock();
		} else {
			lobby.sendPlayerMessage(host, "Must setup first. Not all players have placed their homes yet.");
		}
	}

	private final void unjoin(Player p) {
		boolean alreadyPlaying = playerScoreById.containsKey(p.getUniqueId());
		if (currentGameState == GameState.LOBBY && alreadyPlaying) {
			lobby.sendLobbyMessage(p.getDisplayName() + " has left the " + GAME_NAME);
			playerScoreById.remove(p.getUniqueId());
			lobby.removeFromLobby(p);
		} else {
			Broadcaster.sendError(p, "You must be in a lobby");
		}
	}
}

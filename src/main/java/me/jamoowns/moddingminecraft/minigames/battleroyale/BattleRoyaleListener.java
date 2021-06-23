package me.jamoowns.moddingminecraft.minigames.battleroyale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.commands.ModdersCommand;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.common.observable.ObservableProperty;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
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

	private static final String CUSTOM_ITEM_METADATA_KEY = "CUSTOM_ITEM";

	private static final String GAME_NAME = "Battle Royale";

	private static final Integer GOAL_SCORE = 5;

	private static final Integer GOAL_STAND_AMOUNT = 4;

	private static final Integer POWERUP_STAND_AMOUNT = 4;

	private static final Integer MINIMUM_PLAYERS = 1;

	private static final Random RANDOM = new Random();

	private static final Vector ABOVE = new Vector(0, 1, 0);

	/** All the goal stand locations. */
	private List<Location> goalStands;

	/** The stand where the goal block is spawned. */
	private CustomItem goalBlockStand;

	/** The goal block item. */
	private CustomItem goalBlock;

	/** All the power-up stand locations. */
	private List<Location> powerUpBlockStands;

	/** The stand where the power-up block is spawned. */
	private CustomItem powerUpBlockStand;

	/** The power-up block item. */
	private CustomItem powerUpBlock;

	private Map<UUID, Integer> playerScoreById;

	private Map<UUID, Location> playerHomeLocationById;

	private Map<UUID, CustomItem> playerHomeItemById;

	private GameState currentGameState;

	private ModdingMinecraft javaPlugin;

	private Location goalLocation;

	private ArrayList<Location> flagBlockLocations;

	private LobbyListener lobby;

	private GameKit gameKit;

	private final ObservableProperty<Boolean> gameEnabled;

	private Player host;

	public BattleRoyaleListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		currentGameState = GameState.STOPPED;
		gameEnabled = new ObservableProperty<Boolean>(false);
		flagBlockLocations = new ArrayList<>();
		goalStands = new ArrayList<>();
		powerUpBlockStands = new ArrayList<>();
		playerScoreById = new HashMap<>();
		playerHomeItemById = new HashMap<>();
		playerHomeLocationById = new HashMap<>();
		lobby = new LobbyListener();
		gameKit = Armory.offense(KitLevel.AVERAGE).combine(Armory.defence(KitLevel.AVERAGE))
				.combine(Armory.food(KitLevel.LOW));
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
						lobby.sendLobbyMessage("Block has returned to a goal stand.");
					}
				} else {
					lobby.sendPlayerMessage(event.getPlayer(), "You must place that closer to your homebase");
					event.setCancelled(true);
				}
			} else {
				Broadcaster.sendInfo(event.getPlayer(), "Current game state: " + currentGameState);
			}
		});
		goalBlockStand = new CustomItem("Goal Block Stand", Material.OBSIDIAN);
		goalBlockStand.setBlockPlaceEvent(event -> {
			lobby.sendPlayerMessage(event.getPlayer(), "Added a goal location to the game");
			goalStands.add(event.getBlock().getLocation());
		});
		aJavaPlugin.customItems().silentRegister(goalBlock);
		aJavaPlugin.customItems().silentRegister(goalBlockStand);
		powerUpBlock = new CustomItem("Powerup Block", Material.DIORITE);
		powerUpBlockStand = new CustomItem("Powerup Block Stand", Material.GOLD_BLOCK);
		powerUpBlockStand.setBlockPlaceEvent(event -> {
			lobby.sendPlayerMessage(event.getPlayer(), "Added a powerup location to the game");
			powerUpBlockStands.add(event.getBlock().getLocation());
		});
		aJavaPlugin.customItems().silentRegister(powerUpBlock);
		aJavaPlugin.customItems().silentRegister(powerUpBlockStand);
		ModdersCommand rootCommand = javaPlugin.commandExecutor().registerCommand("royale",
				p -> Broadcaster.sendGameInfo(p, "Battle royale!"));
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "init", this::initiate);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "join", this::join);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "unjoin", this::unjoin);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "setup", this::setup);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "start", this::start);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "stop", this::stop);
	}

	@Override
	public final ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return gameEnabled;
	}

	@Override
	public final void onDisabled() {
		cleanup();
	}

	@Override
	public final void onEnabled() {
		/* Nothing to do here. */
	}

	@EventHandler
	public final void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (currentGameState == GameState.PLAYING && event.getAction() == Action.LEFT_CLICK_BLOCK) {
			List<MetadataValue> meta = event.getClickedBlock().getMetadata(CUSTOM_ITEM_METADATA_KEY);
			meta = meta.stream().filter(m -> m.getOwningPlugin().equals(javaPlugin)).collect(Collectors.toList());
			if (event.getClickedBlock().getLocation().equals(goalLocation)) {
				event.getClickedBlock().setType(Material.AIR);
				event.getPlayer().getInventory().addItem(goalBlock.asItem());
				event.setCancelled(true);
			} else if (!meta.isEmpty()) {
				String blockName = meta.get(0).asString();
				if (blockName.equals(powerUpBlock.name())) {
					event.getPlayer().getInventory().addItem(javaPlugin.jamoListener().skeletonArrowItem().asItem());
					event.getClickedBlock().setType(Material.AIR);
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	public final void onServerStop() {
		cleanup();
	}

	private void buildFlag(Block baseBlock, Material baseColour, Material flagColour) {
		ArrayList<Location> locations = new ArrayList<>();
		/* Base. */
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

	private void cleanup() {
		gameEnabled.set(false);
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

	private final void initiate(Player aHost) {
		host = aHost;
		Broadcaster.broadcastGameInfo(GAME_NAME + " been initiated!");
		currentGameState = GameState.LOBBY;
		gameEnabled.set(true);
	}

	private boolean isHost(Player p) {
		return host != null && host.equals(p);
	}

	private final boolean isPlaying(Player p) {
		return playerScoreById.containsKey(p.getUniqueId());
	}

	private final void join(Player p) {
		if (currentGameState == GameState.LOBBY && !isPlaying(p)) {
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
		World world = p.getWorld();
		ArrayList<FireworkMeta> fireworks = new ArrayList<>();

		Firework fw = (Firework) world.spawnEntity(p.getLocation(), EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		fwm.setPower(2);
		fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

		FireworkMeta fwm1 = fw.getFireworkMeta();
		fwm1.setPower(1);
		fwm1.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());
		fireworks.add(fwm1);

		FireworkMeta fwm2 = fw.getFireworkMeta();
		fwm2.setPower(2);
		fwm2.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());
		fireworks.add(fwm2);

		FireworkMeta fwm3 = fw.getFireworkMeta();
		fwm3.setPower(3);
		fwm3.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());
		fireworks.add(fwm3);

		fw.setFireworkMeta(fwm);
		fw.detonate();
		for (Location flagLocation : flagBlockLocations) {
			Firework fw2 = (Firework) world.spawnEntity(flagLocation.clone().add(0, 5, 0), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm1);
			fw2 = (Firework) world.spawnEntity(flagLocation.clone().add(0, 5, 0), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm2);
			fw2 = (Firework) world.spawnEntity(flagLocation.clone().add(0, 5, 0), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm3);
		}
		for (Location goalStand : goalStands) {
			Firework fw2 = (Firework) world.spawnEntity(goalStand, EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm1);
			fw2 = (Firework) world.spawnEntity(goalStand, EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm2);
			fw2 = (Firework) world.spawnEntity(goalStand, EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm3);
		}
		for (Player gamePlayer : lobby.playerList()) {
			Firework fw2 = (Firework) world.spawnEntity(gamePlayer.getLocation(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm1);
			fw2 = (Firework) world.spawnEntity(gamePlayer.getLocation(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm2);
			fw2 = (Firework) world.spawnEntity(gamePlayer.getLocation(), EntityType.FIREWORK);
			fw2.setFireworkMeta(fwm3);
		}
	}

	private void resetGoalBlock() {
		/* Places goal block on a random goal stand. */
		Location goalStandToPlaceOn = goalStands.get(RANDOM.nextInt(goalStands.size())).clone().add(ABOVE);
		goalStandToPlaceOn.getWorld().getBlockAt(goalStandToPlaceOn).setType(goalBlock.material());
		goalLocation = goalStandToPlaceOn;

		/* Places a powerup on a random stand. */
		Location powerUpLocationToPlace = powerUpBlockStands.get(RANDOM.nextInt(powerUpBlockStands.size())).clone()
				.add(ABOVE);
		Block powerUpLocationBlock = powerUpLocationToPlace.getWorld().getBlockAt(powerUpLocationToPlace);
		powerUpLocationBlock.setType(powerUpBlock.material());
		powerUpLocationBlock.setMetadata(CUSTOM_ITEM_METADATA_KEY,
				new FixedMetadataValue(javaPlugin, powerUpBlock.name()));
	}

	private final void setup(Player p) {
		if (!isHost(p)) {
			Broadcaster.sendGameInfo(p, "Only the host may setup the game");
			return;
		}
		if (currentGameState == GameState.LOBBY && playerScoreById.size() >= MINIMUM_PLAYERS) {
			lobby.sendLobbyMessage("Setting up " + GAME_NAME);
			lobby.sendPlayerMessage(p, "Place all of the goal stands on the battle field");
			currentGameState = GameState.SETUP;
			ItemStack goalItems = goalBlockStand.asItem();
			goalItems.setAmount(GOAL_STAND_AMOUNT);
			p.getInventory().addItem(goalItems);

			lobby.sendPlayerMessage(p, "Place all of the powerup stands on the battle field");
			ItemStack powerUpStands = powerUpBlockStand.asItem();
			powerUpStands.setAmount(POWERUP_STAND_AMOUNT);
			p.getInventory().addItem(powerUpStands);
			for (Entry<UUID, CustomItem> playerHomeItem : playerHomeItemById.entrySet()) {
				Player player = Bukkit.getPlayer(playerHomeItem.getKey());
				player.getInventory().addItem(playerHomeItem.getValue().asItem());
			}
		} else {
			lobby.sendPlayerMessage(p, "Game must be in the lobby and atleast two players joined");
		}
	}

	private final void start(Player p) {
		if (!isHost(p)) {
			lobby.sendPlayerMessage(host, "Only the host may start the game");
			return;
		}
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

	private void stop(Player p) {
		if (currentGameState != GameState.STOPPED) {
			if (isHost(p)) {
				cleanup();
			} else {
				lobby.sendPlayerMessage(host, "Only the host may stop the game");
			}
		} else {
			lobby.sendPlayerMessage(host, "Game has not been started");
		}
	}

	private final void unjoin(Player p) {
		if (currentGameState == GameState.LOBBY && isPlaying(p)) {
			lobby.sendLobbyMessage(p.getDisplayName() + " has left the " + GAME_NAME);
			playerScoreById.remove(p.getUniqueId());
			lobby.removeFromLobby(p);
		} else {
			Broadcaster.sendError(p, "You must be in a lobby");
		}
	}
}
package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.commands.ModdersCommand;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.teams.TeamColour;

public class GameCore {
	private enum GameState {
		LOBBY, SETUP, PLAYING, STOPPED
	}

	private static String GAME_NAME = "Default";

	private static GameState currentGameState;
	private Map<UUID, Integer> playerScoreById;
	private ModdingMinecraft javaPlugin;
	private MarkerPoints flags;
	private LobbyListener lobby;
	private GameKit gameKit;
	private Goal goal;

	public GameCore(ModdingMinecraft aJavaPlugin, String CommandStr, String GameName, int GameScore, int GoalLocs,
			GameKit gameKit, int minSize) {
		javaPlugin = aJavaPlugin;
		GAME_NAME = GameName;

		playerScoreById = new HashMap<>();
		currentGameState = GameState.STOPPED;
		goal = new Goal(aJavaPlugin, GameScore, GoalLocs);
		flags = new MarkerPoints(goal.getGoalScore());
		lobby = new LobbyListener();
		lobby.setMIN_LOBBY_SIZE(minSize);

		ModdersCommand rootCommand = javaPlugin.commandExecutor().registerCommand(CommandStr,
				p -> Broadcaster.sendGameInfo(p, GameName + "!"));
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "init", p -> initiate());
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "join", this::join);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "unjoin", this::unjoin);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "setup", this::setup);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "start", this::start);
		aJavaPlugin.commandExecutor().registerCommand(rootCommand, "stop", p -> cleanup());
		createGoalItem();

	}

	public void cleanup() {
		goal.cleanup();
		flags.RemoveFlags();
		playerScoreById.clear();
		if (currentGameState != GameState.STOPPED) {
			currentGameState = GameState.STOPPED;
			lobby.sendLobbyMessage(GAME_NAME + " has been stopped!");
		}
		lobby.removeAllFromLobby();

	}

	public void GoalBlockTouched(Player p, Location touch) {
		if (currentGameState == GameState.PLAYING) {
			if (touch.equals(goal.getGoalLocation())) {
				touch.getBlock().setType(Material.AIR);
				p.getInventory().addItem(goal.goalItem());
			}
		}
	}

	private boolean checkForVictory(Player player) {
		Integer currentScore = playerScoreById.get(player.getUniqueId());
		if (currentScore >= goal.getGoalScore()) {
			lobby.sendLobbyMessage(player.getDisplayName() + " has won " + GAME_NAME + "!");
			playWinningFireworks(javaPlugin.teams().getTeam(player.getUniqueId()).getTeamColour().getFirework(),
					player);
			cleanup();
			return true;
		} else {
			lobby.sendPlayerMessage(player, "Your current score: " + currentScore + "/" + goal.getGoalScore());
			return false;
		}
	}

	private void createGoalItem() {
		CustomItem goalItem = new CustomItem("Goal Block", Material.DIAMOND_BLOCK);

		goalItem.setBlockPlaceEvent(event -> {
			GoalCheck(event);
		});

		goal.setGoalBlock(goalItem);
		javaPlugin.customItems().silentRegister(goalItem);

	}

	private void createHomeItem(Player p) {
		TeamColour teamColour = javaPlugin.teams().getTeam(p.getUniqueId()).getTeamColour();
		CustomItem homeStand = new CustomItem(p.getDisplayName() + "'s Home", teamColour.getBase());

		homeStand.setBlockPlaceEvent(event -> {
			if (currentGameState == GameState.SETUP) {
				flags.playerHomeAdd(event.getPlayer().getUniqueId(), event.getBlock().getLocation());
				lobby.sendPlayerMessage(event.getPlayer(), "Home sweet home has been set");
				flags.buildFlag(event.getBlock(), teamColour.getBase(), teamColour.getHead());
			}
		});

		javaPlugin.customItems().silentRegister(homeStand);

		flags.playerHomeItemAdd(p.getUniqueId(), homeStand);

	}

	private void GoalCheck(BlockPlaceEvent event) {
		if (currentGameState == GameState.PLAYING) {
			Location playerHome = flags.playerHomeLocation(event.getPlayer().getUniqueId());
			if (event.getBlockPlaced().getLocation().distance(playerHome) < 7) {
				event.getItemInHand().setAmount(0);
				event.getBlock().setType(Material.AIR);
				Integer currentScore = playerScoreById.get(event.getPlayer().getUniqueId());
				Integer updatedScore = currentScore + 1;
				Location scoreLocation = playerHome.clone().add(0, updatedScore, 0);
				event.getBlock().getWorld().getBlockAt(scoreLocation).setType(goal.getGoalBlockStand().material());
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

			createHomeItem(p);
		} else {
			Broadcaster.sendError(p, "Game must be in the lobby");
		}

	}

	private void playWinningFireworks(Color color, Player p) {
		World world = p.getWorld();
		ArrayList<FireworkMeta> fireworks = new ArrayList<FireworkMeta>();

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

		goal.celebrateFireworks(fireworks, world);
		flags.celebrateFireworks(fireworks, world);
		lobby.celebrateFireworks(fireworks, world);
	}

	private void resetGoalBlock() {
		lobby.sendLobbyMessage("Block has returned to a goal stand.");
		/* Places goal block on a random goal stand. */
		goal.resetGoal();
	}

	private final void setup(Player host) {
		if (currentGameState == GameState.LOBBY && playerScoreById.size() >= lobby.getMIN_LOBBY_SIZE()) {
			lobby.sendLobbyMessage("Setting up " + GAME_NAME);
			lobby.sendPlayerMessage(host, "Place all of the goal stands on the field");
			currentGameState = GameState.SETUP;
			goal.setGoalBlockStand(new CustomItem("Goal Block Stand", Material.OBSIDIAN));
			goal.getGoalBlockStand().setBlockPlaceEvent(event -> {
				if (currentGameState == GameState.SETUP) {
					lobby.sendPlayerMessage(event.getPlayer(), "Added a goal location to the game");
					goal.getGoalStands().add(event.getBlock().getLocation());
				}
			});
			javaPlugin.customItems().silentRegister(goal.getGoalBlockStand());
			ItemStack goalitem = goal.getGoalBlockStand().asItem();
			goalitem.setAmount(goal.goalLocMaxCount());
			host.getInventory().addItem(goalitem);

			flags.givePlayHomeItemToAll();
		} else {
			lobby.sendPlayerMessage(host, "Game must be in the lobby and atleast two players joined");
		}
	}

	private final void start(Player host) {
		if (currentGameState == GameState.SETUP && playerScoreById.size() == flags.playerHomeSize()) {
			Broadcaster.broadcastGameInfo(GAME_NAME + " has started!");
			flags.teleportAllPlayersHome();
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
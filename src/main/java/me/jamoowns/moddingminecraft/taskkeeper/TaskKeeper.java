package me.jamoowns.moddingminecraft.taskkeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.fated.Collections;

/**
 * Scoreboard based tasks, and information.
 */
public final class TaskKeeper {

	private class PlayerEventListener implements Listener {

		private TaskKeeper taskKeeper;

		PlayerEventListener(TaskKeeper aTaskKeeper) {
			taskKeeper = aTaskKeeper;
		}

		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			taskKeeper.register(event.getPlayer());
		}
	}

	private Map<UUID, Scoreboard> boardsByPlayer;

	private final PlayerEventListener playerEventListener;

	private final List<BoardItem> boardItems;

	private final List<Task> tasks;

	private final String DEFFAULT_BOARD_TASK_NAME = "tasks";

	/**
	 * Constructor.
	 * 
	 * @param javaPlugin the singleton plugin
	 */
	public TaskKeeper(ModdingMinecraft javaPlugin) {
		boardItems = new ArrayList<>();
		tasks = new ArrayList<>();

		boardsByPlayer = new HashMap<>();

		for (Player online : Bukkit.getOnlinePlayers()) {
			register(online);
		}

		playerEventListener = new PlayerEventListener(this);
		javaPlugin.getServer().getPluginManager().registerEvents(playerEventListener, javaPlugin);
	}

	/**
	 * Adds a board item for all players to see.
	 * 
	 * @param aBoardItem
	 */
	public final void addBoardItem(String aBoardItem) {
		BoardItem boardItem = new BoardItem(aBoardItem);
		boardItems.add(boardItem);

		for (Player online : Bukkit.getOnlinePlayers()) {
			addBoardItem(online.getUniqueId(), boardItem);
		}
	}

	/**
	 * Adds a board item for a specific player to see.
	 * 
	 * @param player     player to add board item to
	 * @param aBoardItem board text to add
	 */
	public final void addBoardItem(UUID player, String aBoardItem) {
		BoardItem boardItem = new BoardItem(aBoardItem);
		boardItems.add(boardItem);

		addBoardItem(player, boardItem);
	}

	/**
	 * Adds a task and the reward.
	 * 
	 * @param taskName name of the task to add
	 * @param reward   reward function to give to the player
	 * @param goal     the goal amount of the task
	 */
	public final void addTask(String taskName, Consumer<UUID> reward, Integer goal) {
		Task task = new Task(taskName, reward, goal);
		tasks.add(task);

		for (Player online : Bukkit.getOnlinePlayers()) {
			addTask(online.getUniqueId(), task);
		}
	}

	/**
	 * Increments a task by one success.
	 * 
	 * @param player   player to increment task of
	 * @param taskName taskname to increment
	 */
	public final void incrementTask(UUID player, String taskName) {
		Scoreboard scoreboard = boardsByPlayer.get(player);

		Optional<Task> task = Collections.find(tasks, Task::id, taskName);

		if (task.isPresent() && !task.get().isComplete(player)) {
			Score score = scoreboard.getObjective(DEFFAULT_BOARD_TASK_NAME).getScore(task.get().describe(player));
			score.getScoreboard().resetScores(task.get().describe(player));

			task.get().incrementTask(player);
			Score updatedScore = scoreboard.getObjective(DEFFAULT_BOARD_TASK_NAME)
					.getScore(task.get().describe(player));
			updatedScore.setScore(tasks.indexOf(task.get()));

			if (task.get().isComplete(player)) {
				task.get().giveReward(player);
			}
		}
	}

	void register(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective(DEFFAULT_BOARD_TASK_NAME, "dummy", "-- Your tasks --");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		boardsByPlayer.put(player.getUniqueId(), board);

		boardItems.forEach(task -> {
			addBoardItem(player.getUniqueId(), task);
		});
		tasks.forEach(task -> {
			addTask(player.getUniqueId(), task);
		});
		setBoardDisplay(player.getUniqueId());
	}

	private void addBoardItem(UUID player, BoardItem boardItem) {
		setBoardDisplay(player);
		Scoreboard scoreboard = boardsByPlayer.get(player);
		if (!boardItem.hasPlayer(player)) {
			boardItem.addPlayer(player);
		}
		Score score = scoreboard.getObjective(DEFFAULT_BOARD_TASK_NAME).getScore(boardItem.describe(player));
		score.setScore(boardItems.indexOf(boardItem) + tasks.size());
		setBoardDisplay(player);
	}

	private void addTask(UUID player, Task task) {
		Scoreboard scoreboard = boardsByPlayer.get(player);
		if (!task.hasPlayer(player)) {
			task.addPlayer(player);
		}
		Score score = scoreboard.getObjective(DEFFAULT_BOARD_TASK_NAME).getScore(task.describe(player));
		score.setScore(tasks.indexOf(task));
		setBoardDisplay(player);
	}

	private void setBoardDisplay(UUID player) {
		Scoreboard scoreboard = boardsByPlayer.get(player);
		Player p = Bukkit.getPlayer(player);
		if (!boardItems.isEmpty() || !tasks.isEmpty()) {
			p.setScoreboard(scoreboard);
		} else {
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}
}

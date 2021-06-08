package me.jamoowns.moddingminecraft.taskkeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.jamoowns.moddingminecraft.common.fated.Collections;

public final class TaskKeeper {

	private class BoardItem implements IBoardItem {
		private String boardItem;

		private Map<UUID, Boolean> statusPerPlayer;

		BoardItem(String aBoardItem) {
			boardItem = aBoardItem;
			statusPerPlayer = new HashMap<>();
		}

		@Override
		public void addPlayer(UUID player) {
			statusPerPlayer.put(player, false);
		}

		@Override
		public String describe(UUID player) {
			return ChatColor.YELLOW + boardItem;
		}

		@Override
		public boolean hasPlayer(UUID player) {
			return statusPerPlayer.containsKey(player);
		}

		@Override
		public String id() {
			return boardItem;
		}
	}

	private interface IBoardItem {

		public boolean hasPlayer(UUID player);

		void addPlayer(UUID player);

		String describe(UUID player);

		String id();
	}

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

	private class Task implements IBoardItem {
		private String taskName;

		private Map<UUID, Integer> statusPerPlayer;

		private Consumer<UUID> reward;

		private Integer goal;

		Task(String aTaskName, Consumer<UUID> aReward, Integer aGoal) {
			taskName = aTaskName;
			statusPerPlayer = new HashMap<>();
			reward = aReward;
			goal = aGoal;
		}

		@Override
		public void addPlayer(UUID player) {
			statusPerPlayer.put(player, 0);
		}

		@Override
		public String describe(UUID player) {
			return ChatColor.GREEN + taskName + ChatColor.YELLOW + " - " + ChatColor.AQUA
					+ goalStatusDescription(player) + ChatColor.YELLOW + " - " + ChatColor.AQUA
					+ completeDescription(player);
		}

		public void giveReward(UUID player) {
			reward.accept(player);
		}

		@Override
		public boolean hasPlayer(UUID player) {
			return statusPerPlayer.containsKey(player);
		}

		@Override
		public String id() {
			return taskName;
		}

		public void incrementTask(UUID player) {
			if (statusPerPlayer.get(player) < goal) {
				statusPerPlayer.put(player, statusPerPlayer.get(player) + 1);
			}
		}

		public boolean isComplete(UUID player) {
			return statusPerPlayer.get(player) == goal;
		}

		private final String completeDescription(UUID player) {
			return isComplete(player) ? "COMPLETED" : "INCOMPLETE";
		}

		private final String goalStatusDescription(UUID player) {
			return statusPerPlayer.get(player) + "/" + goal;
		}
	}

	private Map<UUID, Scoreboard> boardsByPlayer;

	private final PlayerEventListener playerEventListener;

	private final List<BoardItem> boardItems;

	private final List<Task> tasks;

	private final String DEFFAULT_BOARD_TASK_NAME = "tasks";

	public TaskKeeper(JavaPlugin javaPlugin) {
		boardItems = new ArrayList<>();
		tasks = new ArrayList<>();

		boardsByPlayer = new HashMap<>();

		for (Player online : Bukkit.getOnlinePlayers()) {
			register(online);
		}

		playerEventListener = new PlayerEventListener(this);
		javaPlugin.getServer().getPluginManager().registerEvents(playerEventListener, javaPlugin);
	}

	public final void addBoardItem(String aBoardItem) {
		BoardItem boardItem = new BoardItem(aBoardItem);
		boardItems.add(boardItem);

		for (Player online : Bukkit.getOnlinePlayers()) {
			addBoardItem(online.getUniqueId(), boardItem);
		}
	}

	public final void addBoardItem(UUID player, String aBoardItem) {
		BoardItem boardItem = new BoardItem(aBoardItem);
		boardItems.add(boardItem);

		addBoardItem(player, boardItem);
	}

	public final void addTask(String taskName, Consumer<UUID> reward, Integer goal) {
		Task task = new Task(taskName, reward, goal);
		tasks.add(task);

		for (Player online : Bukkit.getOnlinePlayers()) {
			addTask(online.getUniqueId(), task);
		}
	}

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

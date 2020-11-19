package me.jamoowns.moddingminecraft.taskkeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import me.jamoowns.fated.Collections;

public final class TaskKeeper {

	private Map<UUID, Scoreboard> boardsByPlayer;

	private final JavaPlugin javaPlugin;

	private final PlayerEventListener playerEventListener;

	private final List<BoardItem> boardItems;
	private final List<Task> tasks;

	public TaskKeeper(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		boardItems = new ArrayList<>();
		tasks = new ArrayList<>();

		boardsByPlayer = new HashMap<>();

		for (Player online : Bukkit.getOnlinePlayers()) {
			register(online);
		}

		playerEventListener = new PlayerEventListener(this);
		javaPlugin.getServer().getPluginManager().registerEvents(playerEventListener, javaPlugin);
	}

	public final void addTask(String taskName, Consumer<UUID> reward, Integer goal) {
		Task task = new Task(taskName, reward, goal);
		tasks.add(task);

		for (Player online : Bukkit.getOnlinePlayers()) {
			addTask(online.getUniqueId(), task);
		}
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

	public final void incrementTask(UUID player, String taskName) {
		Scoreboard scoreboard = boardsByPlayer.get(player);

		Task task = Collections.find(tasks, Task::id, taskName).get();

		if (!task.isComplete(player)) {
			Score score = scoreboard.getObjective("tasks").getScore(task.describe(player));
			score.getScoreboard().resetScores(task.describe(player));

			task.incrementTask(player);
			Score updatedScore = scoreboard.getObjective("tasks").getScore(task.describe(player));
			updatedScore.setScore(tasks.indexOf(task));

			if (task.isComplete(player)) {
				task.reward.accept(player);
			}
		}
	}

	void register(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("tasks", "dummy", "-- Your tasks --");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		player.setScoreboard(board);

		boardsByPlayer.put(player.getUniqueId(), board);

		boardItems.forEach(task -> {
			addBoardItem(player.getUniqueId(), task);
		});
		tasks.forEach(task -> {
			addTask(player.getUniqueId(), task);
		});
	}

	private void addTask(UUID player, Task task) {
		Scoreboard scoreboard = boardsByPlayer.get(player);
		if (!task.hasPlayer(player)) {
			task.addPlayer(player);
		}
		Score score = scoreboard.getObjective("tasks").getScore(task.describe(player));
		score.setScore(tasks.indexOf(task));
	}

	private void addBoardItem(UUID player, BoardItem boardItem) {
		Scoreboard scoreboard = boardsByPlayer.get(player);
		if (!boardItem.hasPlayer(player)) {
			boardItem.addPlayer(player);
		}
		Score score = scoreboard.getObjective("tasks").getScore(boardItem.describe(player));
		score.setScore(boardItems.indexOf(boardItem) + tasks.size());
	}

	private interface IBoardItem {

		public boolean hasPlayer(UUID player);

		void addPlayer(UUID player);

		String describe(UUID player);

		String id();
	}

	private class BoardItem implements IBoardItem {
		private String boardItem;

		private Map<UUID, Boolean> statusPerPlayer;

		BoardItem(String aBoardItem) {
			boardItem = aBoardItem;
			statusPerPlayer = new HashMap<>();
		}

		public String id() {
			return boardItem;
		}

		public boolean hasPlayer(UUID player) {
			return statusPerPlayer.containsKey(player);
		}

		public void addPlayer(UUID player) {
			statusPerPlayer.put(player, false);
		}

		public String describe(UUID player) {
			return ChatColor.YELLOW + boardItem;
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

		public boolean hasPlayer(UUID player) {
			return statusPerPlayer.containsKey(player);
		}

		public void addPlayer(UUID player) {
			statusPerPlayer.put(player, 0);
		}

		public void incrementTask(UUID player) {
			if (statusPerPlayer.get(player) < goal) {
				statusPerPlayer.put(player, statusPerPlayer.get(player) + 1);
			}
		}

		public String describe(UUID player) {
			return ChatColor.GREEN + taskName + ChatColor.YELLOW + " - " + ChatColor.AQUA
					+ goalStatusDescription(player) + ChatColor.YELLOW + " - " + ChatColor.AQUA
					+ completeDescription(player);
		}

		public boolean isComplete(UUID player) {
			return statusPerPlayer.get(player) == goal;
		}

		public String id() {
			return taskName;
		}

		public void giveReward(UUID player) {
			reward.accept(player);
		}

		private final String goalStatusDescription(UUID player) {
			return statusPerPlayer.get(player) + "/" + goal;
		}

		private final String completeDescription(UUID player) {
			return isComplete(player) ? "COMPLETED" : "INCOMPLETE";
		}
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
}

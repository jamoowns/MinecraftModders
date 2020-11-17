package me.jamoowns.moddingminecraft.taskkeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

	private final List<IBoardItem> boardItems;

	public TaskKeeper(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		boardItems = new ArrayList<>();

		boardsByPlayer = new HashMap<>();

		for (Player online : Bukkit.getOnlinePlayers()) {
			register(online);
		}

		playerEventListener = new PlayerEventListener(this);
		javaPlugin.getServer().getPluginManager().registerEvents(playerEventListener, javaPlugin);
	}

	public final void addTask(String taskName) {
		Task task = new Task(taskName);
		boardItems.add(task);

		for (Player online : Bukkit.getOnlinePlayers()) {
			addTask(online.getUniqueId(), task);
		}
	}

	public final void addBoardItem(String boardItem) {
		IBoardItem task = new BoardItem(boardItem);
		boardItems.add(task);

		for (Player online : Bukkit.getOnlinePlayers()) {
			addTask(online.getUniqueId(), task);
		}
	}

	public final void addBoardItem(UUID player, String boardItem) {
		IBoardItem task = new BoardItem(boardItem);
		boardItems.add(task);

		addTask(player, task);
	}

	public final void updateTask(UUID player, String taskName, Boolean completed) {
		Scoreboard scoreboard = boardsByPlayer.get(player);

		IBoardItem boardItem = Collections.find(boardItems, IBoardItem::id, taskName).get();

		Score score = scoreboard.getObjective("tasks").getScore(boardItem.describe(player));
		score.getScoreboard().resetScores(boardItem.describe(player));

		if (boardItem instanceof Task) {
			((Task) boardItem).complete(player, completed);
		}
		Score updatedScore = scoreboard.getObjective("tasks").getScore(boardItem.describe(player));
		updatedScore.setScore(boardItems.indexOf(boardItem));
	}

	void register(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("tasks", "dummy", "-- Your tasks --");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		player.setScoreboard(board);

		boardsByPlayer.put(player.getUniqueId(), board);

		boardItems.forEach(task -> {
			addTask(player.getUniqueId(), task);
		});
	}

	private void addTask(UUID player, IBoardItem task) {
		Scoreboard scoreboard = boardsByPlayer.get(player);
		if (!task.hasPlayer(player)) {
			task.addPlayer(player);
		}
		Score score = scoreboard.getObjective("tasks").getScore(task.describe(player));
		score.setScore(boardItems.indexOf(task));
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

		private Map<UUID, Boolean> statusPerPlayer;

		Task(String aTaskName) {
			taskName = aTaskName;
			statusPerPlayer = new HashMap<>();
		}

		public boolean hasPlayer(UUID player) {
			return statusPerPlayer.containsKey(player);
		}

		public void addPlayer(UUID player) {
			statusPerPlayer.put(player, false);
		}

		public void complete(UUID player, Boolean completed) {
			statusPerPlayer.put(player, completed);
		}

		public String describe(UUID player) {
			return ChatColor.GREEN + taskName + ChatColor.YELLOW + " - " + ChatColor.AQUA
					+ completeDescription(statusPerPlayer.get(player));
		}

		public String id() {
			return taskName;
		}

		private final String completeDescription(Boolean isCompleted) {
			return isCompleted ? "COMPLETED" : "INCOMPLETE";
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

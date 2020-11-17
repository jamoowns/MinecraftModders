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

	private final List<Task> tasks;

	public TaskKeeper(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		tasks = new ArrayList<>();

		boardsByPlayer = new HashMap<>();

		for (Player online : Bukkit.getOnlinePlayers()) {
			register(online);
		}

		playerEventListener = new PlayerEventListener(this);
		javaPlugin.getServer().getPluginManager().registerEvents(playerEventListener, javaPlugin);
	}

	public final void addTask(String taskName) {
		Task task = new Task(taskName);
		tasks.add(task);

		for (Player online : Bukkit.getOnlinePlayers()) {
			addTask(online.getUniqueId(), task);
		}
	}

	public final void updateTask(UUID player, String taskName, Boolean completed) {
		Scoreboard scoreboard = boardsByPlayer.get(player);

		Task task = Collections.find(tasks, Task::taskName, taskName).get();

		Score score = scoreboard.getObjective("tasks").getScore(task.describe(player));
		score.getScoreboard().resetScores(task.describe(player));

		task.complete(player, completed);
		Score updatedScore = scoreboard.getObjective("tasks").getScore(task.describe(player));
		updatedScore.setScore(tasks.indexOf(task));
	}

	void register(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = board.registerNewObjective("tasks", "dummy", "-- Your tasks --");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		player.setScoreboard(board);

		boardsByPlayer.put(player.getUniqueId(), board);

		tasks.forEach(task -> {
			addTask(player.getUniqueId(), task);
		});
	}

	private void addTask(UUID player, Task task) {
		Scoreboard scoreboard = boardsByPlayer.get(player);
		if (!task.hasPlayer(player)) {
			task.addPlayer(player);
			Score score = scoreboard.getObjective("tasks").getScore(task.describe(player));
			score.setScore(tasks.indexOf(task));
		}
	}

	private class Task {
		private String taskName;

		private Map<UUID, Boolean> statusPerPlayer;

		Task(String aTaskName) {
			taskName = aTaskName;
			statusPerPlayer = new HashMap<>();
		}

		public boolean hasPlayer(UUID player) {
			return statusPerPlayer.containsKey(player);
		}

		void addPlayer(UUID player) {
			statusPerPlayer.put(player, false);
		}

		void complete(UUID player, Boolean completed) {
			statusPerPlayer.put(player, completed);
		}

		String describe(UUID player) {
			return ChatColor.GREEN + taskName + ChatColor.YELLOW + " - " + ChatColor.AQUA
					+ completeDescription(statusPerPlayer.get(player));
		}

		String taskName() {
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

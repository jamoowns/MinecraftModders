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
import org.bukkit.event.player.PlayerQuitEvent;
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

	public final void addTask(String taskName, Boolean completed) {
		Task task = new Task(taskName);
		tasks.add(task);

		for (Player online : Bukkit.getOnlinePlayers()) {
			Scoreboard scoreboard = boardsByPlayer.get(online.getUniqueId());
			task.addPlayer(online.getUniqueId());
			Score score = scoreboard.getObjective("tasks").getScore(task.describe(online.getUniqueId()));
			score.setScore(tasks.indexOf(task));
		}
	}

	public final void updateTask(UUID player, String taskName, Boolean completed) {
		Scoreboard scoreboard = boardsByPlayer.get(player);

		Task task = Collections.find(tasks, Task::taskName, taskName).get();

		task.complete(player, completed);
		Score score = scoreboard.getObjective("tasks").getScore(task.describe(player));
		score.getScoreboard().resetScores(task.describe(player));

		score.setScore(tasks.indexOf(task));
	}

	void register(Player player) {
		if (!boardsByPlayer.containsKey(player.getUniqueId())) {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective objective = board.registerNewObjective("tasks", "dummy", "-- Your tasks --");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);

			player.setScoreboard(board);

			boardsByPlayer.put(player.getUniqueId(), board);

			tasks.forEach(task -> {
				Scoreboard scoreboard = boardsByPlayer.get(player.getUniqueId());
				task.addPlayer(player.getUniqueId());
				Score score = scoreboard.getObjective("tasks").getScore(task.describe(player.getUniqueId()));
				score.setScore(tasks.indexOf(task));
			});
		}
	}

	void unregister(Player player) {
		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		boardsByPlayer.remove(player.getUniqueId());
	}

	private class Task {
		private String taskName;

		private Map<UUID, Boolean> statusPerPlayer;

		Task(String aTaskName) {
			taskName = aTaskName;
			statusPerPlayer = new HashMap<>();
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

		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			taskKeeper.unregister(event.getPlayer());
		}
	}
}

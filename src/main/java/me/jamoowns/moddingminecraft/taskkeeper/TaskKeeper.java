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

public final class TaskKeeper {

	private Map<UUID, Scoreboard> boardsByPlayer;

	private final JavaPlugin javaPlugin;

	private final PlayerEventListener playerEventListener;

	private final List<Task> tasks;

	private final Map<String, Task> taskByTaskName;

	public TaskKeeper(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		tasks = new ArrayList<>();
		taskByTaskName = new HashMap<>();

		boardsByPlayer = new HashMap<>();

		for (Player online : Bukkit.getOnlinePlayers()) {
			register(online);
		}

		playerEventListener = new PlayerEventListener(this);
		javaPlugin.getServer().getPluginManager().registerEvents(playerEventListener, javaPlugin);
	}

	private final String completeDescription(Boolean isCompleted) {
		return isCompleted ? "COMPLETED" : "INCOMPLETE";
	}

	public final void addTask(String taskName, Boolean completed) {
		Task task = new Task(taskName, completed);
		tasks.add(task);
		taskByTaskName.put(task.taskName, task);

		for (Player online : Bukkit.getOnlinePlayers()) {
			Scoreboard scoreboard = boardsByPlayer.get(online.getUniqueId());
			Score score = scoreboard.getObjective("tasks").getScore(task.describe());
			score.setScore(tasks.indexOf(task));
		}
	}

	public final void updateTask(UUID player, String taskName, Boolean completed) {
		Scoreboard scoreboard = boardsByPlayer.get(player);
		Task oldTask = taskByTaskName.get(taskName);

		Task task = new Task(taskName, completed);
		tasks.set(tasks.indexOf(oldTask), new Task(taskName, completed));
		Score score = scoreboard.getObjective("tasks").getScore(task.describe());
		score.getScoreboard().resetScores(oldTask.describe());

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
				Score score = scoreboard.getObjective("tasks").getScore(task.describe());
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
		private Boolean completed;

		Task(String aTaskName, Boolean aCompleted) {
			taskName = aTaskName;
			completed = aCompleted;
		}

		String describe() {
			return ChatColor.GREEN + taskName + ChatColor.YELLOW + " - " + ChatColor.AQUA
					+ completeDescription(completed);
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

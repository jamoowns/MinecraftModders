package me.jamoowns.moddingminecraft.taskkeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.bukkit.scoreboard.ScoreboardManager;

public final class TaskKeeper {

	private Scoreboard board;

	private final JavaPlugin javaPlugin;

	private final PlayerEventListener playerEventListener;

	private final List<Task> tasks;

	private final Map<String, Task> taskByTaskName;

	private final Objective objective;

	public TaskKeeper(JavaPlugin aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		tasks = new ArrayList<>();
		taskByTaskName = new HashMap<>();

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		objective = board.registerNewObjective("test", "dummy", "-- Your tasks --");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		for (Player online : Bukkit.getOnlinePlayers()) {
			online.setScoreboard(board);
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

		Score score = objective.getScore(ChatColor.GREEN + taskName + ChatColor.YELLOW + " - " + ChatColor.AQUA
				+ completeDescription(completed));
		score.setScore(tasks.indexOf(task));
	}

	public final void updateTask(String taskName, Boolean completed) {
		Task task = taskByTaskName.get(taskName);

		tasks.set(tasks.indexOf(task), new Task(taskName, completed));
	}

	void register(Player player) {
		player.setScoreboard(board);
	}

	private class Task {
		private String taskName;
		private Boolean completed;

		Task(String aTaskName, Boolean aCompleted) {
			taskName = aTaskName;
			completed = aCompleted;
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

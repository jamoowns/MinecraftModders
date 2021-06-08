package me.jamoowns.moddingminecraft.taskkeeper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.ChatColor;

public class Task implements IBoardItem {
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
		return ChatColor.GREEN + taskName + ChatColor.YELLOW + " - " + ChatColor.AQUA + goalStatusDescription(player)
				+ ChatColor.YELLOW + " - " + ChatColor.AQUA + completeDescription(player);
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

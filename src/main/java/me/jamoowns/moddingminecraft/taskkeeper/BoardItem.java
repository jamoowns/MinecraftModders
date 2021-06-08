package me.jamoowns.moddingminecraft.taskkeeper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;

public class BoardItem implements IBoardItem {
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

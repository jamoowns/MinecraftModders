package me.jamoowns.moddingminecraft.minigames.mgSettings;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public final class Lobby {
	// TODO:Expand

	private HashMap<Player, PlayerInfo> lobby;
	private int maxSize;

	public Lobby() {
		lobby = new HashMap<Player, PlayerInfo>();
		maxSize = 16;
	}

	public void AddToLobby(Player p) {
		PlayerInfo pInfo = new PlayerInfo(p.getInventory().getContents(), p.getBedSpawnLocation());
		lobby.put(p, pInfo);
		p.getInventory().clear();
		p.updateInventory();
	}

	public int MaxSize() {
		return maxSize;
	}

	public void RemoveAllFromLobby() {
		if (!lobby.isEmpty()) {
			for (Map.Entry<Player, PlayerInfo> entry : lobby.entrySet()) {
				Player p = entry.getKey();
				RestorePlayerInfo(p);
			}
			lobby.clear();
		}
	}

	public void RemoveFromLobby(Player p) {
		if (!lobby.isEmpty() && lobby.containsKey(p)) {
			RestorePlayerInfo(p);
		}
	}

	public int Size() {
		return lobby.size();
	}

	private void RestorePlayerInfo(Player p) {
		p.getInventory().clear();
		p.getInventory().setContents(lobby.get(p).getInventory());
		p.setBedSpawnLocation(lobby.get(p).getBedSpawn());
		p.updateInventory();
		lobby.remove(p);
	}

}

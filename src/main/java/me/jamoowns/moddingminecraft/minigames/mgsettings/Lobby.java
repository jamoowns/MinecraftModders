package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Lobby {
	// TODO:Expand More

	private HashMap<UUID, PlayerInfo> lobby;
	private int maxSize;

	public Lobby() {
		lobby = new HashMap<UUID, PlayerInfo>();
		maxSize = 16;
	}

	public void AddToLobby(Player p) {
		PlayerInfo pInfo = new PlayerInfo(p.getInventory().getContents(), p.getBedSpawnLocation());
		lobby.put(p.getUniqueId(), pInfo);
		p.getInventory().clear();
		p.updateInventory();
	}

	public int MaxSize() {
		return maxSize;
	}

	public boolean PlayerInLobby(UUID uuid) {
		return lobby.containsKey(uuid);
	}

	public void RemoveAllFromLobby() {
		if (!lobby.isEmpty()) {
			for (Map.Entry<UUID, PlayerInfo> entry : lobby.entrySet()) {
				UUID pUUID = entry.getKey();
				RestorePlayerInfo(Bukkit.getPlayer(pUUID));
			}
			lobby.clear();
		}
	}

	public void RemoveFromLobby(Player p) {
		if (!lobby.isEmpty() && lobby.containsKey(p.getUniqueId())) {
			RestorePlayerInfo(p);
		}
	}

	public int Size() {
		return lobby.size();
	}

	private void RestorePlayerInfo(Player p) {
		p.getInventory().clear();
		p.getInventory().setContents(lobby.get(p.getUniqueId()).getInventory());
		p.setBedSpawnLocation(lobby.get(p.getUniqueId()).getBedSpawn());
		p.updateInventory();
		lobby.remove(p.getUniqueId());
	}

}

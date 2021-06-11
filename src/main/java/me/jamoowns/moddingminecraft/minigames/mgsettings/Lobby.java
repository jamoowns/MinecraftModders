package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Lobby {

	private HashMap<UUID, PlayerInfo> lobby;

	private int maxSize;

	public Lobby() {
		lobby = new HashMap<UUID, PlayerInfo>();
		maxSize = 16;
	}

	public void addToLobby(Player p) {
		PlayerInfo pInfo = new PlayerInfo(p.getInventory().getContents(), p.getBedSpawnLocation());
		lobby.put(p.getUniqueId(), pInfo);
		p.getInventory().clear();
		p.updateInventory();
	}

	public int maxSize() {
		return maxSize;
	}

	public boolean playerInLobby(UUID uuid) {
		return lobby.containsKey(uuid);
	}

	public void removeAllFromLobby() {
		if (!lobby.isEmpty()) {
			for (Map.Entry<UUID, PlayerInfo> entry : lobby.entrySet()) {
				UUID pUUID = entry.getKey();
				restorePlayerInfo(Bukkit.getPlayer(pUUID));
			}
			lobby.clear();
		}
	}

	public void removeFromLobby(Player p) {
		if (!lobby.isEmpty() && lobby.containsKey(p.getUniqueId())) {
			restorePlayerInfo(p);
			lobby.remove(p.getUniqueId());
		}
	}

	public int size() {
		return lobby.size();
	}

	private void restorePlayerInfo(Player p) {
		p.getInventory().clear();
		p.getInventory().setContents(lobby.get(p.getUniqueId()).getInventory());
		p.setBedSpawnLocation(lobby.get(p.getUniqueId()).getBedSpawn());
		p.updateInventory();
	}
}

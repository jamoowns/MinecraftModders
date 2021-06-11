package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class LobbyListener implements IGameEventListener {

	private static final int MAX_LOBBY_SIZE = 16;

	private HashMap<UUID, PlayerInfo> lobby;

	public LobbyListener() {
		lobby = new HashMap<UUID, PlayerInfo>();
	}

	public void addToLobby(Player p) {
		PlayerInfo pInfo = new PlayerInfo(p.getInventory().getContents(), p.getBedSpawnLocation());
		lobby.put(p.getUniqueId(), pInfo);
		p.getInventory().clear();
		p.updateInventory();
	}

	@Override
	public void cleanup() {
		removeAllFromLobby();
	}

	public int maxSize() {
		return MAX_LOBBY_SIZE;
	}

	@EventHandler
	public final void onQuitEvent(PlayerQuitEvent event) {
		// This doesn't work if server crashes
		if (playerInLobby(event.getPlayer().getUniqueId())) {
			removeFromLobby(event.getPlayer());
		}
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

	public void sendLobbyMessage(String msg) {
		for (UUID uuid : lobby.keySet()) {
			Broadcaster.sendGameInfo(Bukkit.getPlayer(uuid), msg);
		}
	}

	public void sendPlayerMessage(Player p, String msg) {
		if (playerInLobby(p.getUniqueId())) {
			Broadcaster.sendGameInfo(p, msg);
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

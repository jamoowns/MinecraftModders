package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.ArrayList;
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

	public final void addToLobby(Player p) {
		PlayerInfo pInfo = new PlayerInfo(p.getInventory().getContents(), p.getBedSpawnLocation());
		lobby.put(p.getUniqueId(), pInfo);
		p.getInventory().clear();
		p.updateInventory();
	}

	@Override
	public final void cleanup() {
		removeAllFromLobby();
	}

	public final int maxSize() {
		return MAX_LOBBY_SIZE;
	}

	@EventHandler
	public final void onQuitEvent(PlayerQuitEvent event) {
		// This doesn't work if server crashes
		if (playerInLobby(event.getPlayer().getUniqueId())) {
			removeFromLobby(event.getPlayer());
		}
	}

	public final boolean playerInLobby(UUID uuid) {
		return lobby.containsKey(uuid);
	}

	public final ArrayList<Player> playerList() {
		ArrayList<Player> plist = new ArrayList<Player>();
		for (UUID uuid : lobby.keySet()) {
			plist.add(Bukkit.getPlayer(uuid));
		}
		return plist;
	}

	public final void removeAllFromLobby() {
		if (!lobby.isEmpty()) {
			for (Map.Entry<UUID, PlayerInfo> entry : lobby.entrySet()) {
				UUID pUUID = entry.getKey();
				restorePlayerInfo(Bukkit.getPlayer(pUUID));
			}
			lobby.clear();
		}
	}

	public final void removeFromLobby(Player p) {
		if (!lobby.isEmpty() && lobby.containsKey(p.getUniqueId())) {
			restorePlayerInfo(p);
			lobby.remove(p.getUniqueId());
		}
	}

	public final void sendLobbyMessage(String msg) {
		for (UUID uuid : lobby.keySet()) {
			Broadcaster.sendGameInfo(Bukkit.getPlayer(uuid), msg);
		}
	}

	public final void sendPlayerMessage(Player p, String msg) {
		if (playerInLobby(p.getUniqueId())) {
			Broadcaster.sendGameInfo(p, msg);
		}
	}

	public final int size() {
		return lobby.size();
	}

	private void restorePlayerInfo(Player p) {
		p.getInventory().clear();
		p.getInventory().setContents(lobby.get(p.getUniqueId()).getInventory());
		p.setBedSpawnLocation(lobby.get(p.getUniqueId()).getBedSpawn());
		p.updateInventory();
	}
}

package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;

public final class LobbyListener implements IGameEventListener {

	private static final int MAX_LOBBY_SIZE = 16;
	private int MIN_LOBBY_SIZE = 2;

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

	public void celebrateFireworks(ArrayList<FireworkMeta> fireworks, World world) {
		for (UUID uuid : lobby.keySet()) {
			for (int j = 0; j < fireworks.size(); j++) {
				Firework fw2 = (Firework) world.spawnEntity(Bukkit.getPlayer(uuid).getLocation().clone(),
						EntityType.FIREWORK);
				fw2.setFireworkMeta(fireworks.get(j));
			}
		}
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

	public int getMIN_LOBBY_SIZE() {
		return MIN_LOBBY_SIZE;
	}

	public void setMIN_LOBBY_SIZE(int mIN_LOBBY_SIZE) {
		MIN_LOBBY_SIZE = mIN_LOBBY_SIZE;
	}
}

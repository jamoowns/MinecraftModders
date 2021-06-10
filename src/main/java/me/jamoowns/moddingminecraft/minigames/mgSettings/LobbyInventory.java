package me.jamoowns.moddingminecraft.minigames.mgSettings;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class LobbyInventory {
//TODO:turn this into a full on lobby settings not just inventory
	private HashMap<Player, ItemStack[]> oldInvs;

	public LobbyInventory() {
		oldInvs = new HashMap<Player, ItemStack[]>();
	}

	public void AddInventory(Player p) {
		oldInvs.put(p, p.getInventory().getContents());
		p.getInventory().clear();
		p.updateInventory();
	}

	public int LobbySize() {
		return oldInvs.size();
	}

	public void RestoreAllInventory() {
		if (!oldInvs.isEmpty()) {
			for (Map.Entry<Player, ItemStack[]> entry : oldInvs.entrySet()) {
				Player player = entry.getKey();
				ItemStack[] savedInventory = entry.getValue();

				player.getInventory().clear();
				player.getInventory().setContents(savedInventory);
				player.updateInventory();
			}
			oldInvs.clear();
		}
	}

	public void RestoreInventory(Player p) {
		if (!oldInvs.isEmpty() && oldInvs.containsKey(p)) {
			p.getInventory().clear();
			p.getInventory().setContents(oldInvs.get(p));
			p.updateInventory();
			oldInvs.remove(p);
		}
	}

}

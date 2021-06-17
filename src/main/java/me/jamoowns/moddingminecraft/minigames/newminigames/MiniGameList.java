package me.jamoowns.moddingminecraft.minigames.newminigames;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.jamoowns.moddingminecraft.extras.TableGenerator;
import me.jamoowns.moddingminecraft.extras.TableGenerator.Alignment;

public class MiniGameList {

	private ArrayList<MiniGame> miniGameList;

	private ArrayList<Integer> miniGameIDList;

	public MiniGameList() {

		miniGameList = new ArrayList<MiniGame>();
		miniGameIDList = new ArrayList<Integer>();
	}

	public final void addMiniGame(MiniGame miniGame) {
		miniGameList.add(miniGame);
		miniGameIDList.add(miniGame.getMgID());
	}

	public void getGameList(Player player) {
		TableGenerator tg = new TableGenerator(Alignment.LEFT, Alignment.RIGHT, Alignment.LEFT, Alignment.CENTER);

		for (int i = 0; i < miniGameList.size(); i++) {
			tg.addRow("" + miniGameList.get(i).getMgID(), miniGameList.get(i).getMgName(),
					Bukkit.getPlayer(miniGameList.get(i).getMgHost()).getName(), "(1/16)");
		}

		for (String line : tg.generate()) {
			player.sendMessage(line);
		}
	}

	public final int getNewID() {
		return checkNewID(miniGameList.size());
	}

	public final void removeMiniGame(MiniGame miniGame) {
		miniGameList.remove(miniGame);
	}

	public final int size() {
		return miniGameList.size();
	}

	private int checkNewID(int id) {

		if (miniGameIDList.contains(id)) {

			return checkNewID(id++);
		} else {
			return id;
		}
	}
}

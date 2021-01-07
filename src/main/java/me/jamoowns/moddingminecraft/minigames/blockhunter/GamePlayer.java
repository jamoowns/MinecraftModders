package me.jamoowns.moddingminecraft.minigames.blockhunter;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;

public final class GamePlayer {

	private UUID playerId;

	private Location standLocation;

	private Material chosenBlock;

	private GamePlayer targetPlayer;

	private boolean hasFoundBlock;

	public GamePlayer(UUID aPlayer) {
		playerId = aPlayer;
		hasFoundBlock = false;
	}

	public final Material chosenBlock() {
		return chosenBlock;
	}

	public final void chosenBlock(Material mat) {
		chosenBlock = mat;
	}

	public final void clearChosenBlock() {
		chosenBlock = null;
	}

	public final void clearStand() {
		standLocation = null;
	}

	public final void foundBlock(boolean found) {
		hasFoundBlock = found;
	}

	public final boolean hasFoundBlock() {
		return hasFoundBlock;
	}

	public final boolean hasStandPlaced() {
		return standLocation != null;
	}

	public final UUID playerId() {
		return playerId;
	}

	public final void setStand(Location loc) {
		standLocation = loc;
	}

	public final void setTargetPlayer(GamePlayer aTargetPlayer) {
		targetPlayer = aTargetPlayer;
	}

	public final Location standLocation() {
		return standLocation.clone();
	}

	public final GamePlayer targetPlayer() {
		return targetPlayer;
	}
}

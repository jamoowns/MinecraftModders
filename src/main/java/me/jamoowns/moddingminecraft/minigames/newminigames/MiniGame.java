package me.jamoowns.moddingminecraft.minigames.newminigames;

import java.util.UUID;

import me.jamoowns.moddingminecraft.minigames.mgsettings.LobbyListener;

public class MiniGame {

	private int mgID;
	private String mgName;
	private UUID mgHost;
	private LobbyListener lobby;

	public MiniGame(int id, String name, UUID host) {
		mgID = id;
		setMgName(name);
		setMgHost(host);
		lobby = new LobbyListener();
	}

	public UUID getMgHost() {
		return mgHost;
	}

	public int getMgID() {
		return mgID;
	}

	public String getMgName() {
		return mgName;
	}

	public void setMgHost(UUID mgHost) {
		this.mgHost = mgHost;
	}

	public void setMgID(int mgID) {
		this.mgID = mgID;
	}

	public void setMgName(String mgName) {
		this.mgName = mgName;
	}

}

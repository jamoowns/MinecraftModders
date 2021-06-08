package me.jamoowns.moddingminecraft.taskkeeper;

import java.util.UUID;

public interface IBoardItem {
	public boolean hasPlayer(UUID player);

	void addPlayer(UUID player);

	String describe(UUID player);

	String id();
}

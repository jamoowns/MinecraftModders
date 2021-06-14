package me.jamoowns.moddingminecraft.listener;

import org.bukkit.event.Listener;

public interface IGameEventListener extends Listener {

	public void onDisabled();

	public void onEnabled();

	public void onServerStop();
}

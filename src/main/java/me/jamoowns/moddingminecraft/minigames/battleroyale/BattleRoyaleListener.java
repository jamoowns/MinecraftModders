package me.jamoowns.moddingminecraft.minigames.battleroyale;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory.KitLevel;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameCore;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameKit;

public final class BattleRoyaleListener implements IGameEventListener {

	private ModdingMinecraft javaPlugin;
	private GameCore gameCore;

	public BattleRoyaleListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		GameKit gameKit = Armory.offense(KitLevel.AVERAGE).combine(Armory.defence(KitLevel.AVERAGE))
				.combine(Armory.food(KitLevel.LOW));

		gameCore = new GameCore(javaPlugin, "royale", "Battle Royale", 5, 5, gameKit, 2);
	}

	@EventHandler
	public final void onBlockDamageEvent(BlockDamageEvent event) {
		gameCore.GoalBlockTouched(event.getPlayer(), event.getBlock().getLocation());
	}

	@Override
	public final void onDisabled() {
		gameCore.cleanup();
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
	}

	@Override
	public final void onServerStop() {
		gameCore.cleanup();
	}
}

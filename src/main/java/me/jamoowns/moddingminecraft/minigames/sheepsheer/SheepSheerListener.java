package me.jamoowns.moddingminecraft.minigames.sheepsheer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory.KitLevel;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameCore;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameKit;

public class SheepSheerListener implements IGameEventListener {

	private ModdingMinecraft javaPlugin;
	private GameCore gameCore;

	public SheepSheerListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;

		GameKit gameKit = Armory.offense(KitLevel.AVERAGE).combine(Armory.defence(KitLevel.AVERAGE))
				.combine(Armory.food(KitLevel.LOW).addContraband(new ItemStack(Material.SHEARS, 1)));

		gameCore = new GameCore(javaPlugin, "sheepsheer", "Sheep Sheer", 5, 5, gameKit, 1);
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}

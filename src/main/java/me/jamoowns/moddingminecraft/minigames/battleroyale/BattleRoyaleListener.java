package me.jamoowns.moddingminecraft.minigames.battleroyale;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.observable.ObservableProperty;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory.KitLevel;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameCore;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameKit;

public final class BattleRoyaleListener implements IGameEventListener {

	private ModdingMinecraft javaPlugin;

	private GameCore gameCore;

	private ObservableProperty<Boolean> gameEnabled;

	public BattleRoyaleListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		gameEnabled = new ObservableProperty<Boolean>(false);
		GameKit gameKit = Armory.offense(KitLevel.AVERAGE).combine(Armory.defence(KitLevel.AVERAGE))
				.combine(Armory.food(KitLevel.LOW));

		gameCore = new GameCore(javaPlugin, "royale", "Battle Royale", 5, 5, gameKit, 2);
	}

	@Override
	public ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return gameEnabled;
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

	private void createGoalItem() {
		CustomItem goalItem = new CustomItem("Goal Block", Material.DIAMOND_BLOCK);

		goalItem.setBlockPlaceEvent(event -> {
			GoalCheck(event);
		});

		gameCore.setGoalBlock(goalItem);
		javaPlugin.customItems().silentRegister(goalItem);
	}

	private void GoalCheck(BlockPlaceEvent event) {
		if (gameCore.isPlaying()) {
			Location playerHome = gameCore.getPlayerHomeLoc(event.getPlayer().getUniqueId());
			if (event.getBlockPlaced().getLocation().distance(playerHome) < 7) {
				event.getItemInHand().setAmount(0);
				event.getBlock().setType(Material.AIR);
				Integer currentScore = gameCore.getPlayerScoreId(event.getPlayer().getUniqueId());
				Integer updatedScore = currentScore + 1;
				Location scoreLocation = playerHome.clone().add(0, updatedScore, 0);
				event.getBlock().getWorld().getBlockAt(scoreLocation).setType(gameCore.getGoalMat());
				gameCore.setPlayerScoreId(event.getPlayer().getUniqueId(), updatedScore);

				boolean hasWon = gameCore.checkForVictory(event.getPlayer());
				if (!hasWon) {
					gameCore.sendLobbyMsg(event.getPlayer().getDisplayName() + " has scored a point!");
					gameCore.resetGoalBlock();
				}
			} else {
				gameCore.sendPlayerMsg(event.getPlayer(), "You must place that closer to your homebase");

				event.setCancelled(true);
			}
		}
	}
}

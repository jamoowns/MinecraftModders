package me.jamoowns.moddingminecraft.minigames.sheepsheer;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.common.chat.Broadcaster;
import me.jamoowns.moddingminecraft.common.observable.ObservableProperty;
import me.jamoowns.moddingminecraft.common.observable.ReadOnlyObservableProperty;
import me.jamoowns.moddingminecraft.customitems.CustomItem;
import me.jamoowns.moddingminecraft.listener.IGameEventListener;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory;
import me.jamoowns.moddingminecraft.minigames.mgsettings.Armory.KitLevel;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameCore;
import me.jamoowns.moddingminecraft.minigames.mgsettings.GameKit;

public class SheepSheerListener implements IGameEventListener {

	private ModdingMinecraft javaPlugin;

	private GameCore gameCore;
	private ArrayList<Sheep> sheep;
	private final Random RANDOM;

	private ObservableProperty<Boolean> gameEnabled;

	public SheepSheerListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		gameEnabled = new ObservableProperty<Boolean>(false);

		sheep = new ArrayList<Sheep>();
		RANDOM = new Random();
		GameKit gameKit = Armory.offense(KitLevel.AVERAGE).combine(Armory.defence(KitLevel.AVERAGE))
				.combine(Armory.food(KitLevel.LOW).addContraband(new ItemStack(Material.SHEARS, 1)));

		gameCore = new GameCore(javaPlugin, "sheepsheer", "Sheep Sheer", 5, 1, gameKit, 1, false);
		createGoalItem();
		createGoalStand();

	}

	@Override
	public final ReadOnlyObservableProperty<Boolean> gameEnabled() {
		return gameEnabled;
	}

	@Override
	public final void onDisabled() {
		gameCore.cleanup();
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
	}

	@EventHandler
	public final void onEntityDeathEvent(EntityDeathEvent event) {
		if (event.getEntity() instanceof Sheep) {
			Sheep sheepEnt = (Sheep) event.getEntity();

			Broadcaster.broadcastGameInfo(sheepEnt.toString());
		}

	}

	@Override
	public final void onServerStop() {
		gameCore.cleanup();
	}

	private void buildPen(Location location) {
		location.getBlock().setType(Material.AIR);
		Location pencentre = location.clone();

		for (int i = -8; i < 8; i++) {
			for (int j = -8; j < 8; j++) {
				pencentre.clone().add(i, -1, j).getBlock().setType(Material.GRASS_BLOCK);
			}
		}
		for (int i = -8; i < 8; i++) {
			for (int j = -8; j < 8; j++) {
				for (int z = 0; z < 8; z++) {
					pencentre.clone().add(i, z, j).getBlock().setType(Material.AIR);
				}
			}
		}
		for (int i = -8; i < 8; i++) {
			for (int j = -8; j < 8; j++) {
				if ((i == -8 || i == 7) || (j == -8 || j == 7)) {
					pencentre.clone().add(i, 0, j).getBlock().setType(Material.BIRCH_FENCE);
				}
			}
		}

		for (int j = 0; j < 8; j++) {
			Sheep shee = location.getWorld().spawn(location, Sheep.class);
			shee.setColor(DyeColor.WHITE);
			shee.setCustomName("");
			shee.setCustomNameVisible(false);
			sheep.add(shee);
		}
		Sheep shee = location.getWorld().spawn(location, Sheep.class);

		shee.setColor(DyeColor.WHITE);
		shee.setCustomName("§dSwagg");
		shee.setCustomNameVisible(true);
		sheep.add(shee);
	}

	private void createGoalItem() {
		CustomItem goalItem = new CustomItem("Goal Block", Material.EMERALD_BLOCK);

		goalItem.setBlockPlaceEvent(event -> {
			GoalCheck(event);
		});

		gameCore.setGoalBlock(goalItem);
		javaPlugin.customItems().silentRegister(goalItem);

	}

	private void createGoalStand() {
		CustomItem goalStandItem = new CustomItem("SHEER ME", Material.WHITE_WOOL);

		goalStandItem.setBlockPlaceEvent(event -> {
			if (gameCore.isSetup()) {
				gameCore.AddGoal(event.getPlayer(), event.getBlock().getLocation());
				buildPen(event.getBlock().getLocation());
			}
		});

		gameCore.setGoalBlockStand(goalStandItem);
		javaPlugin.customItems().silentRegister(goalStandItem);
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
					makeNewJeb();
				}
			} else {
				gameCore.sendPlayerMsg(event.getPlayer(), "You must place that closer to your homebase");

				event.setCancelled(true);
			}
		}
	}

	private void makeNewJeb() {
		for (int i = 0; i < sheep.size(); i++) {
			if (sheep.get(i).getName() == "jeb_") {
				sheep.get(i).setCustomName("");
			}
		}

		Sheep newjeb = sheep.get(RANDOM.nextInt(sheep.size()));
		newjeb.setColor(DyeColor.WHITE);
		newjeb.setCustomName("jeb_");
		newjeb.setCustomNameVisible(false);
	}

	private void shearevent(PlayerShearEntityEvent event) {
		if (sheep.contains(event.getEntity()) && event.getEntity().getCustomName().equals("jeb_")) {
			gameCore.GivePlayerGoalBlock(event.getPlayer());
		}
	}
}

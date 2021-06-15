package me.jamoowns.moddingminecraft.minigames.sheepsheer;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
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
	private ArrayList<Material> oldBlocks;
	private ArrayList<Location> oldBlockLocs;
	private Sheep sheerablesheep;
	private final Random RANDOM;
	private int first = 0;

	private ObservableProperty<Boolean> gameEnabled;

	public SheepSheerListener(ModdingMinecraft aJavaPlugin) {
		javaPlugin = aJavaPlugin;
		gameEnabled = new ObservableProperty<Boolean>(false);

		oldBlockLocs = new ArrayList<Location>();
		oldBlocks = new ArrayList<Material>();
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
		return IGameEventListener.ALWAYS_ENABLED;
	}

	@Override
	public final void onDisabled() {
		for (int i = 0; i < oldBlocks.size(); i++) {
			oldBlockLocs.get(i).getBlock().setType(oldBlocks.get(i));
		}
		sheep.forEach(l -> l.remove());
		sheep.clear();
		gameCore.cleanup();
	}

	@Override
	public final void onEnabled() {
		/* Empty. */
	}

	@Override
	public final void onServerStop() {
		for (int i = 0; i < oldBlocks.size(); i++) {
			oldBlockLocs.get(i).getBlock().setType(oldBlocks.get(i));
		}
		sheep.forEach(l -> l.remove());
		sheep.clear();
		gameCore.cleanup();
	}

	@EventHandler
	public void onSheepEatGrass(EntityChangeBlockEvent event) {
		if (event.getEntity() instanceof Sheep && sheep.contains(event.getEntity())) {
			event.setCancelled(true);
			sheep.forEach(l -> l.setSheared(false));
		}
	}

	private void buildPen(Location location) {
		location.getBlock().setType(Material.AIR);
		Location pencentre = location.clone();

		for (int i = -8; i < 8; i++) {
			for (int j = -8; j < 8; j++) {
				Location temploc = pencentre.clone().add(i, -1, j);
				Block tempBlock = temploc.getBlock();
				oldBlockLocs.add(temploc);
				oldBlocks.add(tempBlock.getType());
				tempBlock.setType(Material.GRASS_BLOCK);
			}
		}
		for (int i = -8; i < 8; i++) {
			for (int j = -8; j < 8; j++) {
				for (int z = 0; z < 8; z++) {
					Location temploc = pencentre.clone().add(i, z, j);
					Block tempBlock = temploc.getBlock();
					oldBlockLocs.add(temploc);
					oldBlocks.add(tempBlock.getType());
					tempBlock.setType(Material.AIR);
				}
			}
		}
		for (int i = -8; i < 8; i++) {
			for (int j = -8; j < 8; j++) {
				if ((i == -8 || i == 7) || (j == -8 || j == 7)) {
					Location temploc = pencentre.clone().add(i, 0, j);
					Block tempBlock = temploc.getBlock();
					oldBlockLocs.add(temploc);
					oldBlocks.add(tempBlock.getType());
					tempBlock.setType(Material.BIRCH_FENCE);
				}
			}
		}

		for (int j = 0; j < 8; j++) {
			Sheep shee = location.getWorld().spawn(location, Sheep.class);
			shee.setColor(DyeColor.WHITE);
			shee.setSheared(true);
			shee.setHealth(8);
			sheep.add(shee);
		}
		Sheep shee = location.getWorld().spawn(location, Sheep.class);
		shee.setColor(DyeColor.WHITE);
		shee.setSheared(true);
		shee.setHealth(8);
		sheerablesheep = shee;
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

		int newjeb = RANDOM.nextInt(sheep.size());

		for (int i = 0; i < sheep.size(); i++) {
			Sheep shee = sheep.get(i);
			if (i == newjeb) {
				shee.setColor(DyeColor.PINK);
				shee.setSheared(true);
				shee.setHealth(8);
				sheerablesheep = shee;
			} else {
				shee.setColor(DyeColor.WHITE);
				shee.setSheared(true);
				shee.setHealth(8);
			}
		}

	}

	@EventHandler
	private void onSheepShear(PlayerShearEntityEvent event) {
		if (event.getEntity() instanceof Sheep && sheep.contains(event.getEntity())) {
			event.setCancelled(true);
			sheep.forEach(l -> l.setSheared(true));
			Sheep sheepEnt = (Sheep) event.getEntity();
			if (sheerablesheep.equals(sheepEnt) && first == 1) {
				gameCore.GivePlayerGoalBlock(event.getPlayer());
			} else {
				makeNewJeb();
				first = 1;
			}
		}
	}
}

package me.jamoowns.moddingminecraft.minigames.mgsettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import me.jamoowns.moddingminecraft.ModdingMinecraft;
import me.jamoowns.moddingminecraft.customitems.CustomItem;

public class Goal {

	private static Integer GOAL_SCORE = 5;
	private static Integer GOAL_STAND_LOCATIONS = 5;
	private static Location goalLocation;

	private final Random RANDOM;

	private final Vector ABOVE;
	private List<Location> goalStands;
	private CustomItem goalBlockStand;
	private CustomItem goalBlock;

	public Goal(ModdingMinecraft aJavaPlugin, int GameScore, int GoalLocs) {

		RANDOM = new Random();
		ABOVE = new Vector(0, 1, 0);
		GOAL_SCORE = GameScore;
		GOAL_STAND_LOCATIONS = GoalLocs;
		goalStands = new ArrayList<Location>();

	}

	public void celebrateFireworks(ArrayList<FireworkMeta> fireworks, World world) {
		for (int i = 0; i < getGoalStands().size(); i++) {
			for (int j = 0; i < fireworks.size(); i++) {
				Firework fw2 = (Firework) world.spawnEntity(getGoalStands().get(i).clone(), EntityType.FIREWORK);
				fw2.setFireworkMeta(fireworks.get(j));
			}
		}
	}

	public void cleanup() {
		getGoalStands().forEach(l -> l.getBlock().setType(Material.AIR));
		getGoalStands().clear();
	}

	public CustomItem getGoalBlock() {
		return goalBlock;
	}

	public CustomItem getGoalBlockStand() {
		return goalBlockStand;
	}

	public Location getGoalLocation() {
		return goalLocation;
	}

	public int getGoalScore() {
		return GOAL_SCORE;
	}

	public List<Location> getGoalStands() {
		return goalStands;
	}

	public ItemStack goalItem() {
		return goalBlock.asItem();
	}

	public int goalLocMaxCount() {
		return GOAL_STAND_LOCATIONS;
	}

	public void resetGoal() {
		goalLocation = getGoalStands().get(RANDOM.nextInt(getGoalStands().size())).clone().add(ABOVE);
		goalLocation.getWorld().getBlockAt(goalLocation).setType(getGoalBlock().material());
	}

	public void setGoalBlock(CustomItem customItem) {
		goalBlock = customItem;
	}

	public void setGoalBlockStand(CustomItem goalBlockStand) {
		this.goalBlockStand = goalBlockStand;
	}

	public void setGoalStands(List<Location> goalStands) {
		this.goalStands = goalStands;
	}

}

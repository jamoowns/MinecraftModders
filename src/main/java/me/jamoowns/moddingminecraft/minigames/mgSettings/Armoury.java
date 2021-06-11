package me.jamoowns.moddingminecraft.minigames.mgSettings;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Armoury {
	// TODO:add Premade classes e.g archer, warior
	private ArrayList<ItemStack[]> Items;

	public Armoury(int AttackLevel, int DefenceLevel, int FoodLevel) {
		Items = new ArrayList<ItemStack[]>();
		setAttack(AttackLevel);
		setDefence(DefenceLevel);
		setFood(FoodLevel);
	}

	public ArrayList<ItemStack[]> ArmouryTemplate(int Type) {
		Items = new ArrayList<ItemStack[]>();
		setTemplate(Type);
		return Items;
	}

	public ArrayList<ItemStack[]> getItems() {
		return Items;
	}

	public void setItems(ArrayList<ItemStack[]> items) {
		Items = items;
	}

	private void setAttack(int attackLevel) {
		switch (attackLevel) {
		case 0:
			ItemStack[] temp0 = { (new ItemStack(Material.WOODEN_SWORD, 1)), (new ItemStack(Material.WOODEN_AXE, 1)) };
			Items.add(temp0);
			break;
		case 1:
			ItemStack[] temp1 = { (new ItemStack(Material.IRON_SWORD, 1)), (new ItemStack(Material.IRON_AXE, 1)) };
			Items.add(temp1);
			break;
		case 2:
			ItemStack[] temp2 = { (new ItemStack(Material.DIAMOND_SWORD, 1)),
					(new ItemStack(Material.DIAMOND_AXE, 1)) };
			Items.add(temp2);
			break;
		case 3:
			ItemStack[] temp3 = { (new ItemStack(Material.NETHERITE_SWORD, 1)),
					(new ItemStack(Material.NETHERITE_AXE, 1)) };
			Items.add(temp3);
			break;
		default:
		}

	}

	private void setDefence(int defenceLevel) {
		switch (defenceLevel) {
		case 0:
			ItemStack[] temp0 = { (new ItemStack(Material.LEATHER_BOOTS, 1)),
					(new ItemStack(Material.LEATHER_CHESTPLATE, 1)), (new ItemStack(Material.LEATHER_LEGGINGS, 1)),
					(new ItemStack(Material.LEATHER_HELMET, 1)) };
			Items.add(temp0);
			break;
		case 1:
			ItemStack[] temp1 = { (new ItemStack(Material.SHIELD, 1)), (new ItemStack(Material.IRON_BOOTS, 1)),
					(new ItemStack(Material.IRON_CHESTPLATE, 1)), (new ItemStack(Material.IRON_LEGGINGS, 1)),
					(new ItemStack(Material.IRON_HELMET, 1)) };
			Items.add(temp1);
			break;
		case 2:
			ItemStack[] temp2 = { (new ItemStack(Material.SHIELD, 1)), (new ItemStack(Material.DIAMOND_BOOTS, 1)),
					(new ItemStack(Material.DIAMOND_CHESTPLATE, 1)), (new ItemStack(Material.DIAMOND_LEGGINGS, 1)),
					(new ItemStack(Material.DIAMOND_HELMET, 1)) };
			Items.add(temp2);
			break;
		case 3:
			ItemStack[] temp3 = { (new ItemStack(Material.SHIELD, 1)), (new ItemStack(Material.NETHERITE_BOOTS, 1)),
					(new ItemStack(Material.NETHERITE_CHESTPLATE, 1)), (new ItemStack(Material.NETHERITE_LEGGINGS, 1)),
					(new ItemStack(Material.NETHERITE_HELMET, 1)) };
			Items.add(temp3);
			break;
		default:
		}

	}

	private void setFood(int foodLevel) {
		switch (foodLevel) {
		case 0:
			ItemStack[] temp0 = { (new ItemStack(Material.POTATO, 32)) };
			Items.add(temp0);
			break;
		case 1:
			ItemStack[] temp1 = { (new ItemStack(Material.COOKED_BEEF, 32)) };
			Items.add(temp1);
			break;
		case 2:
			ItemStack[] temp2 = { (new ItemStack(Material.GOLDEN_CARROT, 32)) };
			Items.add(temp2);
			break;
		default:
		}

	}

	private void setTemplate(int type) {
		switch (type) {
		case 0:
			// Archer
			ItemStack[] temp0 = { (new ItemStack(Material.LEATHER_BOOTS, 1)),
					(new ItemStack(Material.LEATHER_CHESTPLATE, 1)), (new ItemStack(Material.LEATHER_LEGGINGS, 1)),
					(new ItemStack(Material.LEATHER_HELMET, 1)), (new ItemStack(Material.BOW, 1)),
					(new ItemStack(Material.ARROW, 64)), (new ItemStack(Material.ARROW, 64)),
					(new ItemStack(Material.ARROW, 64)), (new ItemStack(Material.ARROW, 64)),
					(new ItemStack(Material.BAKED_POTATO, 32)) };
			Items.add(temp0);
			break;
		case 1:
			// Soldier
			ItemStack[] temp1 = { (new ItemStack(Material.IRON_SWORD, 1)), (new ItemStack(Material.IRON_BOOTS, 1)),
					(new ItemStack(Material.IRON_CHESTPLATE, 1)), (new ItemStack(Material.IRON_LEGGINGS, 1)),
					(new ItemStack(Material.IRON_HELMET, 1)), (new ItemStack(Material.GOLDEN_CARROT, 32)) };
			Items.add(temp1);
			break;
		case 2:
			// Warrior
			ItemStack[] temp2 = { (new ItemStack(Material.DIAMOND_AXE, 1)), (new ItemStack(Material.DIAMOND_BOOTS, 1)),
					(new ItemStack(Material.DIAMOND_CHESTPLATE, 1)), (new ItemStack(Material.DIAMOND_LEGGINGS, 1)),
					(new ItemStack(Material.DIAMOND_HELMET, 1)), (new ItemStack(Material.COOKED_BEEF, 32)) };
			Items.add(temp2);
			break;
		case 3:
			// Knight
			ItemStack[] temp3 = { (new ItemStack(Material.NETHERITE_SWORD, 1)), (new ItemStack(Material.SHIELD, 1)),
					(new ItemStack(Material.NETHERITE_BOOTS, 1)), (new ItemStack(Material.NETHERITE_CHESTPLATE, 1)),
					(new ItemStack(Material.NETHERITE_LEGGINGS, 1)), (new ItemStack(Material.NETHERITE_HELMET, 1)),
					(new ItemStack(Material.BAKED_POTATO, 32)) };
			Items.add(temp3);
			break;
		default:
		}
	}

}

package mctmods.smelteryio.blocks.meta;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.IStringSerializable;

public enum EnumMachine implements IStringSerializable {
	FUEL_CONTROLLER ("fuel_controller", EnumRarity.COMMON, "", "", 0, 2, 3, 15, 64),
	CASTING_MACHINE ("casting_machine", EnumRarity.COMMON, "", "", 0, 2, 3, 15, 64);

	private String name;
	private EnumRarity rarity;
	private String recipeOreDict1;
	private String recipeOreDict2;
	private int light;
	private int harvestLevel;
	private float hardness;
	private float resistance;
	private int maxSize;

	EnumMachine (String name, EnumRarity rarity, String recipeOreDict1, String recipeOreDict2, int light, int harvestLevel, float hardness, float resistance, int maxSize) {
		this.name = name;
		this.rarity = rarity;
		this.recipeOreDict1 = recipeOreDict1;
		this.recipeOreDict2 = recipeOreDict2;
		this.light = light;
		this.harvestLevel = harvestLevel;
		this.hardness = hardness;
		this.resistance = resistance;
		this.maxSize = maxSize;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public EnumRarity getRarity() {
		return this.rarity;
	}

	public String getRecipeOreDict1() {
		return this.recipeOreDict1;
	}

	public String getRecipeOreDict2() {
		return this.recipeOreDict2;
	}

	public int getLight() {
		return this.light;
	}

	public int getHarvestLevel() {
		return this.harvestLevel;
	}

	public float getHardness() {
		return this.hardness;
	}

	public float getResistance() {
		return this.resistance;
	}

 	public int getMaxSize() {
 		return this.maxSize;
 	}

}
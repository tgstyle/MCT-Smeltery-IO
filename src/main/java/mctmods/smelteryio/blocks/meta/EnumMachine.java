package mctmods.smelteryio.blocks.meta;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum EnumMachine implements IStringSerializable {
	FUEL_CONTROLLER     ("fuel_controller",     EnumRarity.COMMON, "", "", 0, 2, 3.0F, 15.0F, 64),
	CASTING_MACHINE     ("casting_machine",     EnumRarity.COMMON, "", "", 0, 2, 3.0F, 15.0F, 64),
	SMELTERY_INPUT      ("smeltery_input",      EnumRarity.COMMON, "", "", 0, 2, 3.0F, 15.0F, 64),
	ADVANCED_DRAIN      ("advanced_drain",      EnumRarity.COMMON, "", "", 0, 2, 3.0F, 15.0F, 64);

	private final String name;
	private final EnumRarity rarity;
	private final String recipeOreDict1;
	private final String recipeOreDict2;
	private final int light;
	private final int harvestLevel;
	private final float hardness;
	private final float resistance;
	private final int maxSize;

	EnumMachine(String name, EnumRarity rarity, String recipeOreDict1, String recipeOreDict2,
				int light, int harvestLevel, float hardness, float resistance, int maxSize) {
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

	@Override @Nonnull public String getName() {
		return name;
	}

	public EnumRarity getRarity() {
		return rarity;
	}

	public String getRecipeOreDict1() {
		return recipeOreDict1;
	}

	public String getRecipeOreDict2() {
		return recipeOreDict2;
	}

	public int getLight() {
		return light;
	}

	public int getHarvestLevel() {
		return harvestLevel;
	}

	public float getHardness() {
		return hardness;
	}

	public float getResistance() {
		return resistance;
	}

	public int getMaxSize() {
		return maxSize;
	}
}

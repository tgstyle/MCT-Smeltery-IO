package mctmods.smelteryio.items.meta;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.IStringSerializable;

public enum EnumUpgrade implements IStringSerializable {
	UPGRADE_BASE ("upgrade_base", EnumRarity.COMMON, "", "", 64),
	UPGRADE_SLOT1 ("upgrade_slot1", EnumRarity.COMMON, "", "", 8),
	UPGRADE_SLOT2 ("upgrade_slot2", EnumRarity.COMMON, "", "", 8),
	UPGRADE_SLOT3 ("upgrade_slot3", EnumRarity.COMMON, "", "", 8),
	UPGRADE_SLOT4 ("upgrade_slot4", EnumRarity.COMMON, "", "", 8),
	UPGRADE_BASIN ("upgrade_basin", EnumRarity.COMMON, "", "", 1),
	UPGRADE_SPEED ("upgrade_speed", EnumRarity.COMMON, "", "", 8),
	UPGRADE_REDSTONE ("upgrade_redstone", EnumRarity.COMMON, "", "", 1);

	private String name;
	private EnumRarity rarity;
	private String recipeOreDict1;
	private String recipeOreDict2;
	private int maxSize;

	EnumUpgrade (String name, EnumRarity rarity, String recipeOreDict1, String recipeOreDict2, int maxSize) {
		this.name = name;
		this.rarity = rarity;
		this.recipeOreDict1 = recipeOreDict1;
		this.recipeOreDict2 = recipeOreDict2;
		this.maxSize = maxSize;
	}

 	@Override
 	public String getName() {
 		return name;
 	}

	@Override
	public String toString() {
		return getName();
	}

 	public String getRecipeOreDict1() {
	   	return recipeOreDict1;
	}

 	public String getRecipeOreDict2() {
	   	return recipeOreDict2;
	}

 	public EnumRarity getRarity() {
	   	return rarity;
	}

 	public int getMaxSize() {
 		return maxSize;
 	}

}
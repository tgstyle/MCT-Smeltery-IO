package mctmods.smelteryio.registry;

import mctmods.smelteryio.blocks.meta.EnumMachine;
import mctmods.smelteryio.items.meta.EnumUpgrade;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RegistryDict {

	public static void registerDictionary() {

		// Blocks
		for (EnumMachine variant : EnumMachine.values()) {
			if (variant.getRecipeOreDict1() != null && !variant.getRecipeOreDict1().isEmpty()) {
				OreDictionary.registerOre(variant.getRecipeOreDict1(), new ItemStack(Registry.MACHINE, 1, variant.ordinal()));
			}
			if (variant.getRecipeOreDict2() != null && !variant.getRecipeOreDict2().isEmpty()) {
				OreDictionary.registerOre(variant.getRecipeOreDict2(), new ItemStack(Registry.MACHINE, 1, variant.ordinal()));
			}
		}

		// Items
		for (EnumUpgrade variant : EnumUpgrade.values()) {
			if (variant.getRecipeOreDict1() != null && !variant.getRecipeOreDict1().isEmpty()) {
				OreDictionary.registerOre(variant.getRecipeOreDict1(), new ItemStack(Registry.UPGRADE, 1, variant.ordinal()));
			}
			if (variant.getRecipeOreDict2() != null && !variant.getRecipeOreDict2().isEmpty()) {
				OreDictionary.registerOre(variant.getRecipeOreDict2(), new ItemStack(Registry.UPGRADE, 1, variant.ordinal()));
			}
		}

		// Compatibility
		if (OreDictionary.getOres("itemCoal").isEmpty()) {
			OreDictionary.registerOre("itemCoal", new ItemStack(Items.COAL, 1));
		}
	}

}
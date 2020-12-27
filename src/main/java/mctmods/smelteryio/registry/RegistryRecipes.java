package mctmods.smelteryio.registry;

import mctmods.smelteryio.SmelteryIO;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;

public class RegistryRecipes {
	public static void registerRecipes() {
		registerRecipe();
	}

	static ItemStack coal = new ItemStack(Items.COAL, 1);
	static ItemStack hopper = new ItemStack(Blocks.HOPPER, 1);
	static ItemStack furnace = new ItemStack(Blocks.FURNACE, 1);
	static ItemStack ice = new ItemStack(Blocks.ICE, 1);
	static ItemStack obsidian = new ItemStack(Blocks.OBSIDIAN ,1);
	static ItemStack repeater = new ItemStack(Items.REPEATER ,1);
	static ItemStack sugar = new ItemStack(Items.SUGAR, 1);
	static ItemStack emerald = new ItemStack(Items.EMERALD, 1);
	static ItemStack snowball = new ItemStack(Items.SNOWBALL, 1);
	static ItemStack casting_table = new ItemStack(TinkerSmeltery.castingBlock, 1, BlockCasting.CastingType.TABLE.getMeta());
	static ItemStack casting_basin = new ItemStack(TinkerSmeltery.castingBlock, 1, BlockCasting.CastingType.BASIN.getMeta());
	static ItemStack seared_brick_block = new ItemStack(TinkerSmeltery.searedBlock, 1, 3);
	static ItemStack seared_brick = new ItemStack(TinkerCommons.materials, 1, 0);
	static ItemStack smeltery_machine = new ItemStack(TinkerSmeltery.smelteryIO, 1, 0);
	static ItemStack fuel_controller = new ItemStack(Registry.MACHINE, 1, 0);
	static ItemStack casting_machine = new ItemStack(Registry.MACHINE, 1, 1);
	static ItemStack smeltery_input = new ItemStack(Registry.MACHINE, 1, 2);
	static ItemStack advanced_drain = new ItemStack(Registry.MACHINE, 1, 3);
	static ItemStack upgrade_base = new ItemStack(Registry.UPGRADE, 1, 0);
	static ItemStack upgrade_slot1 = new ItemStack(Registry.UPGRADE, 1, 1);
	static ItemStack upgrade_slot2 = new ItemStack(Registry.UPGRADE, 1, 2);
	static ItemStack upgrade_slot3 = new ItemStack(Registry.UPGRADE, 1, 3);
	static ItemStack upgrade_slot4 = new ItemStack(Registry.UPGRADE, 1, 4);
	static ItemStack upgrade_basin = new ItemStack(Registry.UPGRADE ,1 ,5);
	static ItemStack upgrade_speed = new ItemStack(Registry.UPGRADE, 1, 6);
	static ItemStack upgrade_redstone = new ItemStack(Registry.UPGRADE, 1, 7);
	static ItemStack powdered_fuel = new ItemStack(Registry.POWDERED_FUEL, 3);
	static ItemStack iceball = new ItemStack(Registry.ICEBALL, 1);

	private static void registerRecipe() {
		String ingotIronOrPlate = "plateIron";
		String ingotGoldOrPlate = "plateGold";
		String ingotGoldOrDustGold = "dustGold";
		String coalOrDustCoal = "dustCoal";
		if(OreDictionary.getOres("plateIton").isEmpty()) ingotIronOrPlate = "ingotIron";
		if(OreDictionary.getOres("plateGold").isEmpty()) ingotGoldOrPlate = "ingotGold";
		if(OreDictionary.getOres("dustGold").isEmpty()) ingotGoldOrDustGold = "ingotGold";
		if(OreDictionary.getOres("dustCoal").isEmpty()) coalOrDustCoal = "itemCoal";
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), advanced_drain, new Object[]{"AAA", "ABA","AAA", 'A', seared_brick, 'B', smeltery_machine}).setRegistryName("advanced_drain"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), fuel_controller, new Object[]{"AAA", "ABA","ACA", 'A', seared_brick_block, 'B', hopper, 'C', furnace}).setRegistryName("fuel_controller"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), casting_machine, new Object[]{"ADA", "BCB","ABA", 'A', seared_brick_block, 'B', ice, 'C', casting_table, 'D', smeltery_machine}).setRegistryName("casting_machine"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), smeltery_input, new Object[]{"ABA", "BCB","ABA", 'A', seared_brick_block, 'B', hopper, 'C', "chest"}).setRegistryName("smeltery_input"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), upgrade_base, true, new Object[]{"ACA", "CBC","ACA", 'A', "dyeBlue", 'B', ingotIronOrPlate, 'C', "paper"}).setRegistryName("upgrade_base"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), upgrade_slot1, true, new Object[]{"ABA", "BCB","ABA", 'A', "plankWood", 'B', "chest", 'C', upgrade_base}).setRegistryName("upgrade_slot1"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), upgrade_slot2, true, new Object[]{"ABA", "BCB","ABA", 'A', ingotIronOrPlate, 'B', "chest", 'C', upgrade_slot1}).setRegistryName("upgrade_slot2"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), upgrade_slot3, true, new Object[]{"ABA", "BCB","ABA", 'A', ingotGoldOrPlate, 'B', "chest", 'C', upgrade_slot2}).setRegistryName("upgrade_slot3"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), upgrade_slot4, true, new Object[]{"ABA", "BCB","ABA", 'A', "gemDiamond", 'B', "chest", 'C', upgrade_slot3}).setRegistryName("upgrade_slot4"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), upgrade_basin, true, new Object[]{"ABA", "BCB","ABA", 'A', obsidian, 'B', casting_basin, 'C', upgrade_slot4}).setRegistryName("upgrade_basin"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), upgrade_speed, true, new Object[]{"ABA", "BCB","ABA", 'A', emerald, 'B', sugar, 'C', upgrade_base}).setRegistryName("upgrade_speed"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), upgrade_redstone, true, new Object[]{"ABA", "CDC","ABA", 'A', "blockRedstone", 'B', repeater, 'C', "gemQuartz", 'D', upgrade_base}).setRegistryName("upgrade_redstone"));
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(new ResourceLocation(SmelteryIO.MODID), iceball, true, new Object[]{"AAA", "A A","AAA", 'A', snowball}).setRegistryName("iceball"));
		ForgeRegistries.RECIPES.register(new ShapelessOreRecipe(new ResourceLocation(SmelteryIO.MODID), powdered_fuel, coalOrDustCoal, "gunpowder", coalOrDustCoal, coalOrDustCoal, ingotGoldOrDustGold, coalOrDustCoal, coalOrDustCoal, coalOrDustCoal, coalOrDustCoal).setRegistryName("powdered_fuel"));
	}

}
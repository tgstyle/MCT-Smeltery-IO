package mctmods.smelteryio.items;

import mctmods.smelteryio.items.base.ItemBase;
import mctmods.smelteryio.library.util.ConfigSIO;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPowderedFuel extends ItemBase {
	private int maxSize = 64;

	public ItemPowderedFuel() {
 	 	super("powdered_fuel");
 	 	setMaxDamage(0);
 	 	setMaxStackSize(this.maxSize);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return this.maxSize;
	}

	@Override
	public int getItemBurnTime(ItemStack i) {
		int solidFuelBurnTime = ConfigSIO.powderedFuelBurnTime;
		return solidFuelBurnTime;
	}

	@SideOnly(Side.CLIENT)
 	public void initItemModels() {
   		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
 	}

}
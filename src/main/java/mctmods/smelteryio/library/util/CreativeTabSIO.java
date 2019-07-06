package mctmods.smelteryio.library.util;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.registry.RegistryBlock;

import net.minecraft.creativetab.CreativeTabs;

import net.minecraft.item.ItemStack;

public class CreativeTabSIO extends CreativeTabs {

 	public static final CreativeTabSIO SMELTERYIO_TAB = new CreativeTabSIO();

 	public CreativeTabSIO() {
 	 	super(SmelteryIO.MODID);
 	}

	@Override
	public ItemStack getTabIconItem() {
 	 	return new ItemStack(RegistryBlock.MACHINE, 1, 0);
	}

}

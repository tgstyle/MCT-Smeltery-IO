package mctmods.smelteryio.library.util;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.registry.Registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CreativeTabSIO extends CreativeTabs {
 	public static final CreativeTabSIO SMELTERYIO_TAB = new CreativeTabSIO();

 	public CreativeTabSIO() {
 	 	super(SmelteryIO.MODID);
 	}

	@Override @Nonnull public ItemStack createIcon() {
 	 	return new ItemStack(Registry.MACHINE, 1, 0);
	}
}

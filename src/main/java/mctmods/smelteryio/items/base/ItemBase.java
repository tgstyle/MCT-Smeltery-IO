package mctmods.smelteryio.items.base;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.library.util.CreativeTabSIO;

import net.minecraft.item.Item;

public class ItemBase extends Item {
 	public ItemBase(String registry) {
 	 	setRegistryName(SmelteryIO.MODID, registry);
 	 	setUnlocalizedName(getRegistryName().toString());
 	 	setCreativeTab(CreativeTabSIO.SMELTERYIO_TAB);
 	}

}
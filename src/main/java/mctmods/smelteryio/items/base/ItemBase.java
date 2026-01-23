package mctmods.smelteryio.items.base;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.library.util.CreativeTabSIO;

import net.minecraft.item.Item;

import java.util.Objects;

public class ItemBase extends Item {
 	public ItemBase(String registry) {
 	 	setRegistryName(SmelteryIO.MODID, registry);
 	 	setTranslationKey(Objects.requireNonNull(getRegistryName()).toString());
 	 	setCreativeTab(CreativeTabSIO.SMELTERYIO_TAB);
 	}
}

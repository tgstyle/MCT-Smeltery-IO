package mctmods.smelteryio.blocks.base;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.library.util.CreativeTabSIO;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

import java.util.Objects;

public class BlockBase extends Block {
	public BlockBase(Material material, MapColor mapColor, String registry) {
		super(material, mapColor);
		setRegistryName(SmelteryIO.MODID, registry);
		setTranslationKey(Objects.requireNonNull(getRegistryName()).toString());
		setCreativeTab(CreativeTabSIO.SMELTERYIO_TAB);
		setHarvestLevel("pickaxe", 0);
	}
}

package mctmods.smelteryio.itemblocks;

import mctmods.smelteryio.blocks.meta.EnumMachine;
import mctmods.smelteryio.itemblocks.base.ItemBlockBase;

import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockMachine extends ItemBlockBase {
	public ItemBlockMachine(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override @Nonnull
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey() + "." + EnumMachine.values()[stack.getMetadata() & 3].getName();
	}

	@SuppressWarnings("deprecation")
	@Override @Nonnull
	public EnumRarity getRarity(ItemStack stack) {
		return EnumMachine.values()[stack.getMetadata() & 3].getRarity();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return EnumMachine.values()[stack.getMetadata() & 3].getMaxSize();
	}

	@Override
	public int getMetadata(int damage) {
		return damage & 3;
	}
}

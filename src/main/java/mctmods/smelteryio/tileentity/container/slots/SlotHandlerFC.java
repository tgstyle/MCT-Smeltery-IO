package mctmods.smelteryio.tileentity.container.slots;

import javax.annotation.Nonnull;

import mctmods.smelteryio.tileentity.TileEntityFC;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotHandlerFC extends SlotItemHandler {
	private int tileID = TileEntityFC.TILEID;
	private int tileSlot;
	private int slotStackLimit;

	public SlotHandlerFC(IItemHandler itemHandler, int index, int xPosition, int yPosition, int stacksize) {
		super(itemHandler, index, xPosition, yPosition);
		this.tileSlot = index;
		this.slotStackLimit = stacksize;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		switch(tileSlot) {
		case TileEntityFC.SLOTFUEL:
			return (SlotHandlerItems.validForSlot(stack, TileEntityFC.SLOTFUEL, tileID));
		case TileEntityFC.SLOTUPGRADESPEED:
			return (SlotHandlerItems.validForSlot(stack, TileEntityFC.SLOTUPGRADESPEED, tileID));
		}
		return false;
	}

	@Override
	public int getSlotStackLimit() {
		return slotStackLimit;
	}

}
package mctmods.smelteryio.tileentity.container.slots;

import javax.annotation.Nonnull;

import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.container.ContainerFC;

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
		case ContainerFC.FUEL:
			return (SlotHandlerItems.validForSlot(stack, ContainerFC.FUEL, tileID));
		case ContainerFC.UPGRADESPEED:
			return (SlotHandlerItems.validForSlot(stack, ContainerFC.UPGRADESPEED, tileID));
		}
		return false;
	}

	@Override
	public int getSlotStackLimit() {
		return slotStackLimit;
	}

}
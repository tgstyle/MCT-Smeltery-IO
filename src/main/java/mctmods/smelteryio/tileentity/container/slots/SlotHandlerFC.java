package mctmods.smelteryio.tileentity.container.slots;

import javax.annotation.Nonnull;

import mctmods.smelteryio.tileentity.container.ContainerFC;
import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotHandlerFC extends SlotItemHandler {

	private int tileID;
	private int tileSlot;
	private int slotStackLimit;

	public SlotHandlerFC(IItemHandler itemHandler, int index, int xPosition, int yPosition, int stacksize, int tileid) {
		super(itemHandler, index, xPosition, yPosition);
		this.tileSlot = index;
		this.tileID = tileid;
		this.slotStackLimit = stacksize;
	}

	@Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
		switch(this.tileSlot) {
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

package mctmods.smelteryio.tileentity.container.slots;

import javax.annotation.Nonnull;

import mctmods.smelteryio.tileentity.TileEntityCM;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotHandlerCM extends SlotItemHandler {
	private int tileID = TileEntityCM.TILEID;
	private int tileSlot;
	private int slotStackLimit;

	public SlotHandlerCM(IItemHandler itemHandler, int index, int xPosition, int yPosition, int stacksize) {
		super(itemHandler, index, xPosition, yPosition);
		this.tileSlot = index;
		this.slotStackLimit = stacksize;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		switch(tileSlot) {
		case TileEntityCM.SLOTFUEL:
			return SlotHandlerItems.validForSlot(stack, TileEntityCM.SLOTFUEL, tileID);
		case TileEntityCM.SLOTCAST:
	   		return SlotHandlerItems.validForSlot(stack, TileEntityCM.SLOTCAST, tileID);
		case TileEntityCM.SLOTUPGRADE1:
			return SlotHandlerItems.validForSlot(stack, TileEntityCM.SLOTUPGRADE1, tileID);
		case TileEntityCM.SLOTUPGRADE2:
			return SlotHandlerItems.validForSlot(stack, TileEntityCM.SLOTUPGRADE2, tileID);
		case TileEntityCM.SLOTUPGRADESPEED:
			return SlotHandlerItems.validForSlot(stack, TileEntityCM.SLOTUPGRADESPEED, tileID);
		case TileEntityCM.SLOTOUTPUT:
			return SlotHandlerItems.validForSlot(stack, TileEntityCM.SLOTOUTPUT, tileID);
		case TileEntityCM.SLOTREDSTONE:
			return SlotHandlerItems.validForSlot(stack, TileEntityCM.SLOTREDSTONE, tileID);
		}
		return false;
	}

	@Override
	public int getSlotStackLimit() {
		return slotStackLimit;
	}

}
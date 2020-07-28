package mctmods.smelteryio.tileentity.container.slots;

import javax.annotation.Nonnull;

import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.container.ContainerCM;

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
		case ContainerCM.FUEL:
			return SlotHandlerItems.validForSlot(stack, ContainerCM.FUEL, tileID);
		case ContainerCM.CAST:
	   		return SlotHandlerItems.validForSlot(stack, ContainerCM.CAST, tileID);
		case ContainerCM.UPGRADE1:
			return SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADE1, tileID);
		case ContainerCM.UPGRADE2:
			return SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADE2, tileID);
		case ContainerCM.UPGRADESPEED:
			return SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADESPEED, tileID);
		case ContainerCM.OUTPUT:
			return SlotHandlerItems.validForSlot(stack, ContainerCM.OUTPUT, tileID);
		case ContainerCM.REDSTONE:
			return SlotHandlerItems.validForSlot(stack, ContainerCM.REDSTONE, tileID);
		}
		return false;
	}

	@Override
	public int getSlotStackLimit() {
		return slotStackLimit;
	}

}
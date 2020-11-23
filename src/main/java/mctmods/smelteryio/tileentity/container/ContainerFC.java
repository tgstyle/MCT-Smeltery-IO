package mctmods.smelteryio.tileentity.container;

import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.container.base.ContainerBase;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerFC;

import net.minecraft.inventory.IInventory;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerFC extends ContainerBase {
	private IItemHandler handler;

	public ContainerFC(IInventory playerInventory, final TileEntityFC tileEntity) {
		handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotHandlerFC(handler, TileEntityFC.SLOTUPGRADESPEED, 25, 34, 64));
		addSlotToContainer(new SlotHandlerFC(handler, TileEntityFC.SLOTFUEL, 134, 34, 64));
		addPlayerInventorySlotToContainer(playerInventory);
	}

	@Override
	protected int getSizeInventory() {
		return TileEntityFC.SLOTS_SIZE;
	}

}
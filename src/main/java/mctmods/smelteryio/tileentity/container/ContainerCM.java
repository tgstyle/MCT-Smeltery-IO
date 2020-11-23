package mctmods.smelteryio.tileentity.container;

import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.container.base.ContainerBase;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerCM;

import net.minecraft.inventory.IInventory;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerCM extends ContainerBase {
	private IItemHandler handler;

	public ContainerCM(IInventory playerInventory, final TileEntityCM tileEntity) {
		handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotHandlerCM(handler, TileEntityCM.SLOTFUEL, 56, 15, 64));
		addSlotToContainer(new SlotHandlerCM(handler, TileEntityCM.SLOTCAST, 56, 33, 1));
		addSlotToContainer(new SlotHandlerCM(handler, TileEntityCM.SLOTOUTPUT, 142, 33, 64));
		addSlotToContainer(new SlotHandlerCM(handler, TileEntityCM.SLOTUPGRADE1, 97, 12, 64));
		addSlotToContainer(new SlotHandlerCM(handler, TileEntityCM.SLOTUPGRADE2, 97, 33, 64));
		addSlotToContainer(new SlotHandlerCM(handler, TileEntityCM.SLOTUPGRADESPEED, 97, 54, 64));
		addSlotToContainer(new SlotHandlerCM(handler, TileEntityCM.SLOTOUTPUT, 142, 33, 64));
		addSlotToContainer(new SlotHandlerCM(handler, TileEntityCM.SLOTREDSTONE, 142, 54, 64));
		addPlayerInventorySlotToContainer(playerInventory);
	}

	@Override
	protected int getSizeInventory() {
		return TileEntityCM.SLOTS_SIZE;
	}

}
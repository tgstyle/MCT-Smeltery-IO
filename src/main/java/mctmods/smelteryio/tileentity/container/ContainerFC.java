package mctmods.smelteryio.tileentity.container;

import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.container.base.ContainerBase;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerFC;

import net.minecraft.inventory.IInventory;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerFC extends ContainerBase {
	public static final int UPGRADESPEED = 0, FUEL = 1;
	private int tileID = TileEntityFC.TILEID;
	private TileEntityFC tileEntity;
	private IItemHandler handler;

	public ContainerFC(IInventory playerInventory, final TileEntityFC tileentity) {
		tileEntity = tileentity;
		handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotHandlerFC(handler, UPGRADESPEED, 25, 34, 64, tileID));
		addSlotToContainer(new SlotHandlerFC(handler, FUEL, 134, 34, 64, tileID));
		addPlayerInventorySlotToContainer(playerInventory);
	}

}
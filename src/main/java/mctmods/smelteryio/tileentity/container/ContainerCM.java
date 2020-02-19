package mctmods.smelteryio.tileentity.container;

import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.container.base.ContainerBase;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerCM;

import net.minecraft.inventory.IInventory;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerCM extends ContainerBase {
	public static final int FUEL = 0, CAST = 1, UPGRADE1 = 2, UPGRADE2 = 3, UPGRADESPEED = 4, OUTPUT = 5, REDSTONE = 6;
	private int tileID = TileEntityCM.TILEID;
	private TileEntityCM tileEntity;
	private IItemHandler handler;

	public ContainerCM(IInventory playerInventory, final TileEntityCM tileentity) {
		tileEntity = tileentity;
		handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotHandlerCM(handler, FUEL, 56, 15, 64, tileID));
		addSlotToContainer(new SlotHandlerCM(handler, CAST, 56, 33, 1, tileID));
		addSlotToContainer(new SlotHandlerCM(handler, OUTPUT, 142, 33, 64, tileID));
		addSlotToContainer(new SlotHandlerCM(handler, UPGRADE1, 97, 12, 64, tileID));
		addSlotToContainer(new SlotHandlerCM(handler, UPGRADE2, 97, 33, 64, tileID));
		addSlotToContainer(new SlotHandlerCM(handler, UPGRADESPEED, 97, 54, 64, tileID));
		addSlotToContainer(new SlotHandlerCM(handler, OUTPUT, 142, 33, 64, tileID));
		addSlotToContainer(new SlotHandlerCM(handler, REDSTONE, 142, 54, 64, tileID));
		addPlayerInventorySlotToContainer(playerInventory);
	}

}
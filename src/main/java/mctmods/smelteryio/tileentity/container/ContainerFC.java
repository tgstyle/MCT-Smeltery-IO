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
		this.tileEntity = tileentity;
		this.handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		this.addSlotToContainer(new SlotHandlerFC(this.handler, UPGRADESPEED, 25, 34, 64, this.tileID));
		this.addSlotToContainer(new SlotHandlerFC(this.handler, FUEL, 79, 34, 64, this.tileID));

		this.addPlayerInventorySlotToContainer(playerInventory);
	}

}

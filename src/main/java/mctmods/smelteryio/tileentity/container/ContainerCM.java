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
		this.tileEntity = tileentity;
		this.handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		this.addSlotToContainer(new SlotHandlerCM(this.handler, FUEL, 56, 15, 64, this.tileID));
		this.addSlotToContainer(new SlotHandlerCM(this.handler, CAST, 56, 33, 1, this.tileID));
		this.addSlotToContainer(new SlotHandlerCM(this.handler, OUTPUT, 142, 33, 64, this.tileID));
		this.addSlotToContainer(new SlotHandlerCM(this.handler, UPGRADE1, 97, 12, 64, this.tileID));
		this.addSlotToContainer(new SlotHandlerCM(this.handler, UPGRADE2, 97, 33, 64, this.tileID));
		this.addSlotToContainer(new SlotHandlerCM(this.handler, UPGRADESPEED, 97, 54, 64, this.tileID));
		this.addSlotToContainer(new SlotHandlerCM(this.handler, OUTPUT, 142, 33, 64, this.tileID));
		this.addSlotToContainer(new SlotHandlerCM(this.handler, REDSTONE, 142, 54, 64, this.tileID));

		this.addPlayerInventorySlotToContainer(playerInventory);
	}

}

package mctmods.smelteryio.tileentity.container.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class ContainerBase extends Container {
	private static final double MAX_REACH_SQ = 64.0D;
	private TileEntity tileEntity;

	protected void bindTileEntity(TileEntity tileEntity) { this.tileEntity = tileEntity; }

	public void addPlayerInventorySlotToContainer(IInventory playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int row = 0; row < 9; ++row) {
			int x = 8 + row * 18;
			int y = 142;
			addSlotToContainer(new Slot(playerInventory, row, x, y));
		}
	}

	@Override @Nonnull public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();
			if (index < containerSlots) {
				if (!mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) { return ItemStack.EMPTY; }
			} else if (!mergeItemStack(itemstack1, 0, containerSlots, false)) { return ItemStack.EMPTY; }
			if (itemstack1.isEmpty()) { slot.putStack(ItemStack.EMPTY); }
			else { slot.onSlotChanged(); }
			if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}

	@Override public boolean canInteractWith(@Nonnull EntityPlayer player) {
		if (tileEntity == null) { return true; }
		if (tileEntity.isInvalid()) { return false; }
		BlockPos pos = tileEntity.getPos();
		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= MAX_REACH_SQ;
	}
}

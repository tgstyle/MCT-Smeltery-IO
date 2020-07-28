package mctmods.smelteryio.tileentity.container.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerBase extends Container {
	protected abstract int getSizeInventory();

	public void addPlayerInventorySlotToContainer(IInventory playerInventory) {
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 9; ++j){
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for(int row = 0; row < 9; ++row) {
			int x = 8 + row * 18;
			int y = 58 + 84;
			addSlotToContainer(new Slot(playerInventory, row, x, y));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		Slot slot = (Slot)inventorySlots.get(index);
		if(slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
		ItemStack sourceStack = slot.getStack();
		ItemStack copyOfSourceStack = sourceStack.copy();
		if(index >= 0 && index < 36) {
			if(!mergeItemStack(sourceStack, 36, 36 + getSizeInventory(), false)) return ItemStack.EMPTY;
		} else if(index >= 36 && index < 36 + getSizeInventory()) {
			if(!mergeItemStack(sourceStack, 0, 36, false)) return ItemStack.EMPTY;
		} else {
			return ItemStack.EMPTY;
		}
		if(sourceStack.getCount() == 0) {
			slot.putStack(ItemStack.EMPTY);
		} else {
			slot.onSlotChanged();
		}
		slot.onTake(player, sourceStack);
		return copyOfSourceStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
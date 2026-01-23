package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;

import mctmods.smelteryio.tileentity.base.TileEntityBase;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;

import net.minecraftforge.items.IItemHandler;

public class TileEntitySI extends TileEntityBase implements ITickable {
	public static final int SLOTS_SIZE = 1;
	public static final int SLOTITEMS = 0;

	public TileEntitySI() {
		super(SLOTS_SIZE);
	}

	@Override @Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return itemInventory.insertItem(slot, stack, simulate);
	}

	@Override @Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public void update() {
		if (world.isRemote) return;
		if (cooldown % 20 == 0) {
			getSmeltery();
			if (smeltery) transferItems();
		}
		if (update) {
			efficientMarkDirty();
			update = false;
		}
		cooldown = (cooldown + 1) % 20;
	}

	private void getSmeltery() {
		tileSmeltery = getMasterTile();
		if (tileSmeltery != null) {
			if (tileSmeltery.isActive()) {
				if (!smeltery) {
					notifyMasterOfChange();
					smeltery = true;
					update = true;
				}
			} else {
				if (smeltery) {
					notifyMasterOfChange();
					resetSI();
				}
			}
		} else {
			if (smeltery) {
				notifyMasterOfChange();
				resetSI();
			}
		}
	}

	private void resetSI() {
		smeltery = false;
		update = true;
		tileSmeltery = null;
	}

	private void transferItems() {
		if (tileSmeltery == null) return;
		IItemHandler handler = tileSmeltery.getItemHandler();
		if (handler == null) return;
		boolean transferred = false;
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack targetStack = handler.getStackInSlot(i);
			if (!targetStack.isEmpty()) continue;
			ItemStack sourceStack = itemInventory.getStackInSlot(SLOTITEMS);
			if (sourceStack.isEmpty()) break;
			if (!handler.isItemValid(i, sourceStack)) continue;
			ItemStack toInsert = sourceStack.copy();
			toInsert.setCount(1);
			ItemStack remainder = handler.insertItem(i, toInsert, false);
			if (remainder.isEmpty()) {
				itemInventory.extractItem(SLOTITEMS, 1, false);
				transferred = true;
			}
		}
		if (transferred) update = true;
	}
}

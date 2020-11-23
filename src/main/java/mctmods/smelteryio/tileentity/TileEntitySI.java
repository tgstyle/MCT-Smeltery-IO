package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;

import mctmods.smelteryio.tileentity.base.TileEntityBase;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;

public class TileEntitySI extends TileEntityBase implements ITickable{

	public static final int SLOTS_SIZE = 1;
	public static final int SLOTITEMS = 0;

	public TileEntitySI() {
		super(SLOTS_SIZE);
	}

	@Override
	public void update() {
		if(world.isRemote) {
			return;
		} else {
			if(cooldown % 20 == 0) {
				getSmeltery();
				if(smeltery) {
					for(int item = 0; item < tileSmeltery.getSizeInventory(); item++) {
						ItemStack stack = tileSmeltery.getStackInSlot(item);
						if(stack.isEmpty()) {
							ItemStack items = itemInventory.getStackInSlot(SLOTITEMS);
							boolean valid = tileSmeltery.getItemHandler().isItemValid(item, items);
							if(valid) {
								tileSmeltery.getItemHandler().insertItem(item, items, false);
								itemInventory.extractItem(SLOTITEMS, 1, false);
							}
						}
					}
				}
			}
			if(update) {
				efficientMarkDirty();
				update = false;
			}
		}
		cooldown = (cooldown + 1) % 20;
	}

	private void getSmeltery() {
		tileSmeltery = getMasterTile();
		if(tileSmeltery != null) {
			if(tileSmeltery.isActive()) {
				if(!smeltery) {
					notifyMasterOfChange();
					smeltery = true;
					update = true;
				}
			} else {
				if(smeltery) {
					resetSI();
				}
			}
		} else {
			if(smeltery) {
				resetSI();
			}
		}
	}

	private void resetSI() {
		smeltery = false;
		update = true;
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return itemInventory.insertItem(slot, stack, simulate);
	}

}
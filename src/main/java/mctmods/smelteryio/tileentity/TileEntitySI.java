package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mctmods.smelteryio.tileentity.base.TileEntityBase;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileEntitySI extends TileEntityBase implements ITickable {
	private int cooldown = 0;

	public TileEntitySI() { super(0); }

	@Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return smeltery && tileSmeltery != null && tileSmeltery.getItemHandler() != null;
		}
		return super.hasCapability(capability, facing);
	}

	@Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (smeltery && tileSmeltery != null) {
				IItemHandler target = tileSmeltery.getItemHandler();
				if (target != null) {
					return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new InputProxyHandler(target));
				}
			}
		}
		return super.getCapability(capability, facing);
	}

	@Override public void update() {
		if (world.isRemote) { return; }
		if (cooldown % 20 == 0) { getSmeltery(); }
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

	private static class InputProxyHandler implements IItemHandler {
		private final IItemHandler target;

		private InputProxyHandler(IItemHandler target) { this.target = target; }

		@Override public int getSlots() { return target.getSlots(); }

		@Override @Nonnull public ItemStack getStackInSlot(int slot) { return ItemStack.EMPTY; }

		@Override @Nonnull public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) { return target.insertItem(slot, stack, simulate); }

		@Override @Nonnull public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }

		@Override public int getSlotLimit(int slot) { return target.getSlotLimit(slot); }

		@Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return target.isItemValid(slot, stack); }
	}
}

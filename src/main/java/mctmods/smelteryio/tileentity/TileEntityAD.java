package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mctmods.smelteryio.tileentity.base.TileEntityBase;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import slimeknights.tconstruct.library.smeltery.SmelteryTank;

public class TileEntityAD extends TileEntityBase implements ITickable {
	public static final int SLOTS_SIZE = 0;

	public TileEntityAD() {
		super(SLOTS_SIZE);
	}

	@Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return smeltery && tileSmeltery != null && tileSmeltery.getTank() != null;
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return false;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && smeltery && tileSmeltery != null) {
			SmelteryTank masterTank = tileSmeltery.getTank();
			if (masterTank != null) {
				return (T) masterTank;
			}
		}
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return null;
		}
		return super.getCapability(capability, facing);
	}

	@Override public void update() {
		if (world.isRemote) { return; }

		if (cooldown % 20 == 0) {
			getSmeltery();
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
					resetAD();
				}
			}
		} else {
			if (smeltery) {
				notifyMasterOfChange();
				resetAD();
			}
		}
	}

	private void resetAD() {
		smeltery = false;
		update = true;
		tileSmeltery = null;
	}
}

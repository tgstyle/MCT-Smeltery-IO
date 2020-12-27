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
	private SmelteryTank tank;

	public TileEntityAD() {
		super(SLOTS_SIZE);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return capability == null;
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return tileSmelteryTank != null && tileSmelteryTank.getTank()!= null;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return null;
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if(tileSmelteryTank == null || tank == null) {
				return super.getCapability(capability, facing);
			}
			return (T) tank;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void update() {
		if(world.isRemote) {
			return;
		} else {
			if(cooldown % 20 == 0) {
				getSmeltery();
				if(smeltery) {
					if(tileSmelteryTank == null) {
						tileSmelteryTank = getMasterTile();
						tank = tileSmelteryTank.getTank();
						update = true;
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
					resetAD();
				}
			}
		} else {
			if(smeltery) {
				resetAD();
			}
		}
	}

	private void resetAD() {
		smeltery = false;
		update = true;
		tileSmelteryTank = null;
		tileSmeltery = null;
	}

}
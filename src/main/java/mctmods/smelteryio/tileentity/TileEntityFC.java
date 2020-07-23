package mctmods.smelteryio.tileentity;

import java.text.DecimalFormat;

import javax.annotation.Nonnull;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.tileentity.container.ContainerFC;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructure;

public class TileEntityFC extends TileEntityBase implements ITickable {
	public static final String TAG_FACING = "facing";
	public static final String TAG_PROGRESS = "progress";
	public static final String TAG_ACTIVE = "active";
	public static final String TAG_SMELTER = "smeltery";
	public static final String TAG_RATIO = "ratio";
	public static final String TAG_TARGET_TEMP = "targetTemp";
	public static final String TAG_CURRENT_TEMP = "currentTemp";
	public static final String TAG_SMELTERY_TEMP = "smelteryTemp";
	public static final String TAG_AT_CAPACITY = "atCapacity";
	public static final int TILEID = 0;
	public static final int PROGRESS = 250;
	private static final int SLOTS_SIZE = 2;
	private static final double FUEL_RATIO = ConfigSIO.fuelControllerRatio;
	private double ratio = 0.01;
	private int targetTemp = 0;
	private int currentTemp = 0;
	private int smelteryTemp;
	private boolean heatingItem = false;
	private boolean atCapacity = false;

	public TileEntityFC() {
		super(SLOTS_SIZE);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		facing = EnumFacing.getFront(compound.getInteger(TAG_FACING));
		progress = compound.getInteger(TAG_PROGRESS);
		active = (compound.getBoolean(TAG_ACTIVE));
		smeltery = (compound.getBoolean(TAG_SMELTER));
		ratio = compound.getDouble(TAG_RATIO);
		targetTemp = compound.getInteger(TAG_TARGET_TEMP);
		currentTemp = compound.getInteger(TAG_CURRENT_TEMP);
		smelteryTemp = compound.getInteger(TAG_SMELTERY_TEMP);
		atCapacity = compound.getBoolean(TAG_AT_CAPACITY);
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_FACING, facing.getIndex());
		compound.setInteger(TAG_PROGRESS, progress);
		compound.setBoolean(TAG_ACTIVE, active);
		compound.setBoolean(TAG_SMELTER, smeltery);
		compound.setDouble(TAG_RATIO, ratio);
		compound.setInteger(TAG_TARGET_TEMP, targetTemp);
		compound.setInteger(TAG_CURRENT_TEMP, currentTemp);
		compound.setInteger(TAG_SMELTERY_TEMP, smelteryTemp);
		compound.setBoolean(TAG_AT_CAPACITY, atCapacity);
		return super.writeToNBT(compound);
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if(slot == ContainerFC.FUEL) {
			if(SlotHandlerItems.validForSlot(stack, ContainerFC.FUEL, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		if(slot == ContainerFC.UPGRADESPEED) {
			if(SlotHandlerItems.validForSlot(stack, ContainerFC.UPGRADESPEED, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public void update() {
		if(world.isRemote) return;
		update = false;
		if(cooldown % 2 == 0 && getSmeltery()) {
		 	updateSmelteryHeatingState();
		 	calculateRatio();
		  	calculateTemperature();
		  	burnSolidFuel();
		}
		cooldown = (cooldown + 1) % 20;
		if(activeCount != 0) {
			activeCount--;
			active = true;
		} else if(progress == 0 && active) {
			update = true;
			active = false;
		}
		if(update) efficientMarkDirty();
	}

	private boolean getSmeltery() {
		if(tileSmeltery == null) tileSmeltery = getMasterTile();
		if(tileSmeltery != null && tileSmeltery.getTank() != null) {
			if(!smeltery) {
				smelteryTemp = getFluidFuelTemp();
				setSmelteryTemp(smelteryTemp);
				notifyMasterOfChange();
				update = true;
			}
			smeltery = true;
		} else {
			if(smeltery) {
				smeltery = false;
				tileSmeltery = null;
				update = true;
			}
			if(active) { 
				activeCount = 0;
				progress = 0;
				targetTemp = 0;
				currentTemp = 0;
				update = true;
			}
		}
		return smeltery;
	}

	private void updateSmelteryHeatingState() {
		heatingItem = false;
		atCapacity = false;
		if(tileSmeltery.isActive()) {
			for(int item = 0; item < tileSmeltery.getSizeInventory(); item++) {
				ItemStack stack = tileSmeltery.getStackInSlot(item);
				if(!stack.isEmpty()) {
					if(tileSmeltery.hasFuel()) {
						if(tileSmeltery.canHeat(item)) {
							int temp = tileSmeltery.getTemperature(item);
							int neededTemp = tileSmeltery.getTempRequired(item);
							float progress = tileSmeltery.getProgress(item);
							if(temp != 0 && temp <= neededTemp) heatingItem = true;
							if(!heatingItem && progress >= 2) atCapacity = true;
						}
					}
				}
			}
		}
		if(heatingItem || atCapacity && activeCount != 0) update = true;
		if(activeCount == 0 && atCapacity) {
			atCapacity = false;
			update = true;
		}
	}

	private void calculateRatio() {
		ItemStack speedStack = itemInventory.getStackInSlot(ContainerFC.UPGRADESPEED);
		int stackSize1 = speedStack.getCount();
		if(stackSize1 != upgradeSize1) {
			ratio = 0.01;
			if(speedStack != ItemStack.EMPTY) {
				ratio = (double) speedStack.getCount() / FUEL_RATIO;
				DecimalFormat df = new DecimalFormat("#.##");
				ratio = Double.parseDouble(df.format(ratio));
			}
			upgradeSize1 = stackSize1;
			update = true;
		}
	}

	private void calculateTemperature() {
		if(!heatingItem) return;
		smelteryTemp = getFluidFuelTemp();
		int fuelTempRatio = (int) (getBurnTime() * ratio) / 2;
		int fuelTempSolid = (((fuelTempRatio + 99) / 100) * 100) + smelteryTemp;
		if(fuelTempSolid >= 200000) fuelTempSolid = 200000;
		targetTemp = fuelTempSolid;
	}

	private void burnSolidFuel() {
		if(currentTemp == 0 && heatingItem && itemInventory.getStackInSlot(ContainerFC.FUEL) != ItemStack.EMPTY && !atCapacity) {
			currentTemp = targetTemp;
			consumeItemStack(ContainerFC.FUEL, 1);
			update = true;
		} if(currentTemp != 0) {
			if(targetTemp != tileSmeltery.getTemperature()) {
				setSmelteryTemp(targetTemp);
				notifyMasterOfChange();
			}
			progress = (progress + 1) % PROGRESS;
			activeCount = progress + 10;
			if(progress == 0) {
				targetTemp = smelteryTemp;
				currentTemp = 0;
				resetTemp();
			}
			update = true;
		}
	}

	private int getFluidFuelTemp() {
		FluidStack fluidStack = tileSmeltery.currentFuel;
		if(fluidStack != null) return fluidStack.getFluid().getTemperature() - 300; // convert to degrees celcius as done in Tinkers Construct
		return 0;
	}

	private int getBurnTime() {
		ItemStack solidFuel = itemInventory.getStackInSlot(ContainerFC.FUEL);
		if(solidFuel != ItemStack.EMPTY) {
			int burnTime = TileEntityFurnace.getItemBurnTime(solidFuel);
			if(burnTime > 0) return burnTime;
		}
		return 0;
	}

	private NBTTagCompound getNBT() {
		final NBTTagCompound nbt = new NBTTagCompound();
		if(tileSmeltery != null) {
			tileSmeltery.writeToNBT(nbt);
			return nbt;
		} else return null;
	}

	private void setSmelteryTempNBT(int temperature) {
		final NBTTagCompound nbt = getNBT();
		if(nbt == null) return;
		nbt.setInteger(TileHeatingStructure.TAG_TEMPERATURE, temperature);
		if(tileSmeltery != null) tileSmeltery.readFromNBT(nbt);
	}

	private void setSmelteryTemp(int temperature) {
	   	setSmelteryTempNBT(temperature);
	}

	public boolean atCapacity() {
		return atCapacity;
	}

	public void resetTemp() {
		setSmelteryTemp(smelteryTemp);
	}

	public int getFuelTemp() {
		if(currentTemp == 0) return smelteryTemp;
		else return targetTemp;
	}

	public int getCurrentTemp() {
		return currentTemp;
	}

	public boolean hasController() {
		return smeltery;
	}

	public boolean isReady() {
		if(progress != 0) return true;
		return false;
	}

	@SideOnly(Side.CLIENT)
	public double getRatio() {
		return ratio;
	}

	@SideOnly(Side.CLIENT)
	public int getGUIProgress(int pixel) {
		return (int) (((float)progress / (float)PROGRESS) * pixel);
	}

}
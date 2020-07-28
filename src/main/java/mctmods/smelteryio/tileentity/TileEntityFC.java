package mctmods.smelteryio.tileentity;

import java.text.DecimalFormat;

import javax.annotation.Nonnull;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.tileentity.base.TileEntityBase;
import mctmods.smelteryio.tileentity.container.ContainerFC;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerItems;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructure;

public class TileEntityFC extends TileEntityBase implements ITickable {
	public static final String TAG_RATIO = "ratio";
	public static final String TAG_TARGET_TEMP = "targetTemp";
	public static final String TAG_CURRENT_TEMP = "currentTemp";
	public static final String TAG_SMELTERY_TEMP = "smelteryTemp";
	public static final String TAG_AT_CAPACITY = "atCapacity";
	public static final int SLOTS_SIZE = 2;
	public static final int TILEID = 0;
	private static final int FUEL_CONTROLLER_SPEED = 2; 
	private static final double FUEL_RATIO = ConfigSIO.fuelControllerRatio;
	private double ratio = 0.01;
	private int targetTemp = 0;
	private int currentTemp = 0;
	private int smelteryTemp = 0;
	private boolean heatingItem = false;
	private boolean atCapacity = false;

	public TileEntityFC() {
		super(SLOTS_SIZE);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		ratio = compound.getDouble(TAG_RATIO);
		targetTemp = compound.getInteger(TAG_TARGET_TEMP);
		currentTemp = compound.getInteger(TAG_CURRENT_TEMP);
		smelteryTemp = compound.getInteger(TAG_SMELTERY_TEMP);
		atCapacity = compound.getBoolean(TAG_AT_CAPACITY);
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble(TAG_RATIO, ratio);
		compound.setInteger(TAG_TARGET_TEMP, targetTemp);
		compound.setInteger(TAG_CURRENT_TEMP, currentTemp);
		compound.setInteger(TAG_SMELTERY_TEMP, smelteryTemp);
		compound.setBoolean(TAG_AT_CAPACITY, atCapacity);
		super.writeToNBT(compound);
		return compound;
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		for(int slotNumber = 0; slotNumber < SLOTS_SIZE; slotNumber++) {
			if(SlotHandlerItems.validForSlot(stack, slotNumber, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public void update() {
		if(world.isRemote) {
			System.out.println("FC:" + activeCount + ":" + time);
			if(active && progress != 0) {
				activeCount = progress;
				progress = 0;
				cooldown = 1;
			} else if(active && cooldown % 2 == 0) {
				activeCount = (activeCount + FUEL_CONTROLLER_SPEED + speedStackSize) % (time + FUEL_CONTROLLER_SPEED);
			}
		} else {
			if(cooldown % 2 == 0) {
				if(active && time == 0) {
					active = false;
					update = true;
				}
				getSmeltery();
				if(smeltery && fueled) {
					updateSmelteryHeatingState();
					calculateRatio();
					calculateTemperature();
					canBurnSolidFuel();
					heatSmeltery();
				}
			}
		}
		if(update) {
			efficientMarkDirty();
			update = false;
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
				} else {
					if(!fueled) {
						for(BlockPos pos : tileSmeltery.tanks) {
							if(pos == tileSmeltery.currentTank) continue;
							IFluidTank tank = getTankAt(pos);
							if(tank != null && tank.getFluidAmount() > 0) {
								smelteryTemp = tank.getFluid().getFluid().getTemperature() - 300; // convert to degrees celcius as done in Tinkers Construct
								setSmelteryTemp(smelteryTemp);
								fueled = true;
								update = true;
					        }
					    }
					}
				}
			} else {
				if(smeltery) {
					notifyMasterOfChange();
					fcReset();
				}
			}
		} else {
			if(smeltery) {
				fcReset();
			}
		}
	}

	private void fcReset() {
		activeCount = 0;
		progress = 0;
		targetTemp = 0;
		currentTemp = 0;
		smelteryTemp = 0;
		fueled = false;
		smeltery = false;
		update = true;
	}

	private void updateSmelteryHeatingState() {
		heatingItem = false;
		if(progress == 0 ) {
			if(!tileSmeltery.isEmpty()) {
				for(int item = 0; item < tileSmeltery.getSizeInventory(); item++) {
					ItemStack stack = tileSmeltery.getStackInSlot(item);
					if(!stack.isEmpty()) {
						if(tileSmeltery.canHeat(item)) {
							int temp = tileSmeltery.getTemperature(item);
							int neededTemp = tileSmeltery.getTempRequired(item);
							float progress = tileSmeltery.getProgress(item);
							if(temp != 0 && temp <= neededTemp) {
								heatingItem = true;
								update = true;
								atCapacity = false;
							}
							if(!atCapacity && !heatingItem && progress >= 2) {
								atCapacity = true;
								update = true;
							}
						}
					}
				}
			}
		}
	}

	private void calculateRatio() {
		ItemStack upgrade1 = itemInventory.getStackInSlot(ContainerFC.UPGRADESPEED);
		int stackSize1 = upgrade1.getCount();
		if(stackSize1 != upgradeSize1) {
			ratio = 0.01;
			if(upgrade1 != ItemStack.EMPTY) {
				ratio = (double) stackSize1 / FUEL_RATIO;
				DecimalFormat df = new DecimalFormat("#.##");
				ratio = Double.parseDouble(df.format(ratio));
			}
			speedStackSize = 0;
			if(upgrade1.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 6))) speedStackSize += getSlotStackSize(upgrade1);
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

	private int getBurnTime() {
		ItemStack solidFuel = itemInventory.getStackInSlot(ContainerFC.FUEL);
		if(solidFuel != ItemStack.EMPTY) {
			int burnTime;
			burnTime = TileEntityFurnace.getItemBurnTime(solidFuel);
			if(burnTime > 0) return burnTime;
		}
		return 0;
	}

	private void canBurnSolidFuel() {
		if(!isReady && itemInventory.getStackInSlot(ContainerFC.FUEL) != ItemStack.EMPTY) {
			isReady = true;
			update = true;
		}
	}

	private void heatSmeltery() {
		if(isReady) {
			if(time != 0) {
				progress = (progress + FUEL_CONTROLLER_SPEED + speedStackSize) % (time + FUEL_CONTROLLER_SPEED);
			}
			if(time == 0) {
				if(currentTemp == 0 && heatingItem && !atCapacity) {
					currentTemp = targetTemp;
					time = getBurnTime();
					active = burnSolidFuel();
					update = true;
				} 
				if(currentTemp != 0) {
					if(targetTemp != tileSmeltery.getTemperature()) setSmelteryTemp(targetTemp);
				}
			} else if(progress >= time) {
				if(itemInventory.getStackInSlot(ContainerFC.FUEL) == ItemStack.EMPTY) isReady = false;
				if(getFluidFuelTemp() == 0) fueled = false;
				targetTemp = smelteryTemp;
				currentTemp = 0;
				time = 0;
				progress = 0;
				resetTemp();
				update = true;
			}
		}
	}

	private boolean burnSolidFuel() {
		consumeItemStack(ContainerFC.FUEL, 1);
		return true;
	}

	private int getFluidFuelTemp() {
		FluidStack fluidFuel = tileSmeltery.currentFuel;
		if(fluidFuel != null) return fluidFuel.getFluid().getTemperature() - 300; // convert to degrees celcius as done in Tinkers Construct
		return 0;
	}

	private NBTTagCompound getNBT() {
		final NBTTagCompound nbt = new NBTTagCompound();
		tileSmeltery.writeToNBT(nbt);
		return nbt;
	}

	private void setSmelteryTempNBT(int temperature) {
		final NBTTagCompound nbt = getNBT();
		if(nbt == null) return;
		nbt.setInteger(TileHeatingStructure.TAG_TEMPERATURE, temperature);
		tileSmeltery.readFromNBT(nbt);
		notifyMasterOfChange();
	}

	private void setSmelteryTemp(int temperature) {
	   	setSmelteryTempNBT(temperature);
	}

	public int getFuelTemp() {
		if(currentTemp == 0) return smelteryTemp;
		else return targetTemp;
	}

	public void resetTemp() {
		setSmelteryTemp(smelteryTemp);
	}

	@SideOnly(Side.CLIENT)
	public boolean atCapacity() {
		return atCapacity;
	}

	@SideOnly(Side.CLIENT)
	public boolean isHeatingSmeltery() {
		if(!atCapacity && currentTemp != 0) return true;
		return false;
	}

	@SideOnly(Side.CLIENT)
	public double getRatio() {
		return ratio;
	}

	@SideOnly(Side.CLIENT)
	public boolean isFueled() {
		return fueled;
	}

	@SideOnly(Side.CLIENT)
	public boolean isReady() {
		return isReady;
	}

	@SideOnly(Side.CLIENT)
	public boolean hasController() {
		return smeltery;
	}

	@SideOnly(Side.CLIENT)
	public boolean isActive() {
		return active;
	}

	@SideOnly(Side.CLIENT)
	public int getGUIProgress(int pixel) {
		return (int) (((float)activeCount / (float)time) * pixel);
	}

	public void guiOpen() {
		efficientMarkDirty();
	}

}
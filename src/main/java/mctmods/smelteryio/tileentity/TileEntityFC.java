package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.tileentity.base.TileEntityBase;
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
	public static final int SLOTS_SIZE = 2;
	public static final int SLOTUPGRADESPEED = 0, SLOTFUEL = 1;
	public static final String TAG_RATIO = "ratio";
	public static final String TAG_TARGET_TEMP = "targetTemp";
	public static final String TAG_CURRENT_TEMP = "currentTemp";
	public static final String TAG_SMELTERY_TEMP = "smelteryTemp";
	public static final String TAG_AT_CAPACITY = "atCapacity";
	public static final String TAG_OWNER = "owner";
	public static final int TILEID = 0;
	private static final int FUEL_CONTROLLER_SPEED = ConfigSIO.fuelControllerSpeed;
	private static final double FUEL_RATIO = ConfigSIO.fuelControllerRatio;
	private double ratio = 0.01;
	private int targetTemp = 0;
	private int currentTemp = 0;
	private int smelteryTemp = 0;
	private boolean owner = false;
	private boolean heatingItem = false;
	private boolean atCapacity = false;

	public TileEntityFC() {
		super(SLOTS_SIZE);
	}

	@Override public void readFromNBT(NBTTagCompound compound) {
		ratio = compound.getDouble(TAG_RATIO);
		targetTemp = compound.getInteger(TAG_TARGET_TEMP);
		currentTemp = compound.getInteger(TAG_CURRENT_TEMP);
		smelteryTemp = compound.getInteger(TAG_SMELTERY_TEMP);
		atCapacity = compound.getBoolean(TAG_AT_CAPACITY);
		owner = compound.getBoolean(TAG_OWNER);
		super.readFromNBT(compound);
	}

	@Override @Nonnull public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setDouble(TAG_RATIO, ratio);
		compound.setInteger(TAG_TARGET_TEMP, targetTemp);
		compound.setInteger(TAG_CURRENT_TEMP, currentTemp);
		compound.setInteger(TAG_SMELTERY_TEMP, smelteryTemp);
		compound.setBoolean(TAG_AT_CAPACITY, atCapacity);
		compound.setBoolean(TAG_OWNER, owner);
		return super.writeToNBT(compound);
	}

	@Override @Nonnull public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (SlotHandlerItems.validForSlot(stack, slot, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		return super.insertItem(slot, stack, simulate);
	}

	@Override public void update() {
		if (world.isRemote) {
			if (active && progress != 0) {
				activeCount = progress;
				progress = 0;
				cooldown = 1;
			} else if (active && cooldown % 2 == 0) {
				activeCount = (activeCount + FUEL_CONTROLLER_SPEED + speedStackSize) % (time + FUEL_CONTROLLER_SPEED + speedStackSize);
			}
		} else {
			if (cooldown % 2 == 0) {
				if (active && time == 0) {
					active = false;
					update = true;
				}
				getSmeltery();
				if (smeltery && fueled) {
					updateSmelteryHeatingState();
					calculateRatio();
					canBurnSolidFuel();
					heatSmeltery();
				}
			}
			if (update) {
				efficientMarkDirty();
				update = false;
			}
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
				} else {
					if (!owner && !tileSmeltery.getName().contains("sio.smeltery.customname")) {
						resetSmelteryName("sio.smeltery.customname");
						notifyMasterOfChange();
						owner = true;
						update = true;
					} else if (owner && !fueled) {
						smelteryTemp = getFluidFuelTemp();
						setSmelteryTemp(smelteryTemp);
						update = true;
					}
				}
			} else {
				if (smeltery) {
					resetSmeltery();
					resetFC();
				}
			}
		} else {
			if (smeltery) {
				resetFC();
			}
		}
	}

	private void resetFC() {
		ratio = 0.01;
		targetTemp = 0;
		currentTemp = 0;
		smelteryTemp = 0;
		progress = 0;
		time = 0;
		activeCount = 0;
		upgradeSize1 = 0;
		speedStackSize = 0;
		owner = false;
		atCapacity = false;
		heatingItem = false;
		isReady = false;
		active = false;
		smeltery = false;
		fueled = false;
		update = true;
		tileSmeltery = null;
	}

	private void updateSmelteryHeatingState() {
		if (progress != 0) return;
		if (!tileSmeltery.isEmpty() && !heatingItem) {
			for (int item = 0; item < tileSmeltery.getSizeInventory(); item++) {
				ItemStack stack = tileSmeltery.getStackInSlot(item);
				if (stack.isEmpty()) continue;
				if (tileSmeltery.getTempRequired(item) <= 0) continue;
				if (!tileSmeltery.canHeat(item)) continue;
				int temp = tileSmeltery.getTemperature(item);
				int neededTemp = tileSmeltery.getTempRequired(item);
				float itemProgress = tileSmeltery.getProgress(item);
				if (temp <= neededTemp) {
					heatingItem = true;
					update = true;
				}
				if (!atCapacity && itemProgress >= 2) {
					atCapacity = true;
					update = true;
				}
			}
		} else if (atCapacity) {
			atCapacity = false;
			update = true;
		}
	}

	private void calculateRatio() {
		ItemStack upgrade1 = itemInventory.getStackInSlot(SLOTUPGRADESPEED);
		int stackSize1 = upgrade1.getCount();
		if (stackSize1 == upgradeSize1) return;
		ratio = 0.01;
		if (!upgrade1.isEmpty()) {
			ratio = (double) stackSize1 / FUEL_RATIO;
		}
		speedStackSize = 0;
		if (upgrade1.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 6))) speedStackSize += getSlotStackSize(upgrade1);
		upgradeSize1 = stackSize1;
		calculateTemperature();
		update = true;
	}

	private void calculateTemperature() {
		smelteryTemp = getFluidFuelTemp();
		int fuelTempRatio = (int) (getBurnTime() * ratio) / 2;
		int fuelTempSolid = (((fuelTempRatio + 99) / 100) * 100) + smelteryTemp;
		if (fuelTempSolid >= 200000) fuelTempSolid = 200000;
		targetTemp = fuelTempSolid;
	}

	private int getBurnTime() {
		ItemStack solidFuel = itemInventory.getStackInSlot(SLOTFUEL);
		if (!solidFuel.isEmpty()) {
			int burnTime = TileEntityFurnace.getItemBurnTime(solidFuel);
			if (burnTime > 0) return burnTime;
		}
		return 0;
	}

	private void canBurnSolidFuel() {
		if (!isReady && !itemInventory.getStackInSlot(SLOTFUEL).isEmpty()) {
			isReady = true;
			update = true;
		} else if (isReady && time == 0 && progress == 0 && itemInventory.getStackInSlot(SLOTFUEL).isEmpty()) {
			isReady = false;
			update = true;
		}
	}

	private void heatSmeltery() {
		if (!isReady) return;
		if (time != 0) progress = (progress + FUEL_CONTROLLER_SPEED + speedStackSize) % (time + FUEL_CONTROLLER_SPEED + speedStackSize);
		if (time == 0 && progress == 0) {
			if (currentTemp == 0 && heatingItem) {
				calculateTemperature();
				time = getBurnTime();
				active = burnSolidFuel();
				currentTemp = targetTemp;
				update = true;
			}
		} else if (progress >= time) {
			smelteryTemp = getFluidFuelTemp();
			setSmelteryTemp(smelteryTemp);
			targetTemp = smelteryTemp;
			currentTemp = 0;
			time = 0;
			progress = 0;
			heatingItem = false;
			update = true;
		}
		setSmelteryTemp(targetTemp);
	}

	private boolean burnSolidFuel() {
		consumeItemStack(SLOTFUEL, 1);
		return true;
	}

	private int getFluidFuelTemp() {
		int maxTemp = 0;
		fueled = false;
		for (BlockPos pos : tileSmeltery.tanks) {
			IFluidTank tank = getTankAt(pos);
			if (tank == null) continue;
			FluidStack fluid = tank.getFluid();
			if (fluid == null || fluid.amount <= 0) continue;
			int fluidTemp = fluid.getFluid().getTemperature() - 300;
			if (fluidTemp > maxTemp) maxTemp = fluidTemp;
			fueled = true;
		}
		return maxTemp;
	}

	private void setSmelteryTemp(int temperature) {
		if (tileSmeltery == null || temperature == tileSmeltery.getTemperature()) return;
		setSmelteryTempNBT(temperature);
	}

	private void setSmelteryTempNBT(int temperature) {
		NBTTagCompound nbt = getNBT();
		nbt.setInteger(TileHeatingStructure.TAG_TEMPERATURE, temperature);
		int fuelValue = (temperature > 0) ? Integer.MAX_VALUE : 0;
		nbt.setInteger(TileHeatingStructure.TAG_FUEL, fuelValue);
		nbt.setBoolean(TileHeatingStructure.TAG_NEEDS_FUEL, fuelValue == 0);
		tileSmeltery.readFromNBT(nbt);
		notifyMasterOfChange();
	}

	private NBTTagCompound getNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		tileSmeltery.writeToNBT(nbt);
		return nbt;
	}

	public int getFuelTemp() {
		return currentTemp == 0 ? smelteryTemp : targetTemp;
	}

	public void resetSmeltery() {
		resetSmelteryName("gui.smeltery.name");
		resetSmelteryTemp();
		notifyMasterOfChange();
	}

	public void resetSmelteryTemp() {
		setSmelteryTemp(smelteryTemp);
	}

	public void resetSmelteryName(String name) {
		tileSmeltery.setCustomName(name);
	}

	@SideOnly(Side.CLIENT)
	public boolean getOwner() {
		return owner;
	}

	@SideOnly(Side.CLIENT)
	public boolean atCapacity() {
		return atCapacity;
	}

	@SideOnly(Side.CLIENT)
	public boolean isHeatingSmeltery() { return !atCapacity && currentTemp != 0; }

	@SideOnly(Side.CLIENT)
	public double getRatio() {
		return ratio;
	}

	public void guiOpen() {
		efficientMarkDirty();
	}
}

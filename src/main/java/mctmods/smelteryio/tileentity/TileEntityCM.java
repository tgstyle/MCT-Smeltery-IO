package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.library.util.recipes.CMRecipeHandler;
import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.tileentity.base.TileEntityBase;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerItems;

import mctmods.smelteryio.tileentity.fuildtank.TileEntityFluidTank;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;

public class TileEntityCM extends TileEntityBase implements ITickable, TileEntityFluidTank.TankListener {
	public static final int SLOTS_SIZE = 7;
	public static final int SLOTFUEL = 0, SLOTCAST = 1, SLOTUPGRADE1 = 2, SLOTUPGRADE2 = 3, SLOTUPGRADESPEED = 4, SLOTOUTPUT = 5, SLOTREDSTONE = 6;
	public static final String TAG_LOCK_SLOTS = "currentLockSlots";
	public static final String TAG_OUTPUT_ITEM_STACK = "targetItemStack";
	public static final String TAG_BURN_COUNT = "burnCount";
	public static final String TAG_REDSTONE = "controlledByRedstone";
	public static final String TAG_MODE = "currentMode";
	public static final String TAG_OUTPUT_STACK_SIZE = "outputStackSize";
	public static final String TAG_SPEED_STACK_SIZE = "speedStackSize";
	public static final int TILEID = 1;
	public static final int TANK_CAPACITY = 10368;
	public static final int CAST = 0;
	public static final int BASIN = 1;
	private static final int FUEL_SNOW_AMOUNT_BASIN = ConfigSIO.snowballBasinAmount;
	private static final int FUEL_SNOW_AMOUNT_CAST = ConfigSIO.snowballCastingAmount;
	private static final int FUEL_ICE_AMOUNT_BASIN = ConfigSIO.iceballBasinAmount;
	private static final int FUEL_ICE_AMOUNT_CAST = ConfigSIO.iceballCastingAmount;
	private static final int FUEL_ICE_BASIN_AMOUNT = ConfigSIO.iceballAmountBasin;
	private static final int FUEL_ICE_CAST_AMOUNT = ConfigSIO.iceballAmountCasting;
	private static final int CASTING_MACHINE_SPEED = ConfigSIO.castingMachineSpeed;
	private int outputStackSize = 0;
	private int speedStackSize = 0;
	private int currentMode = CAST;
	private int lastMode;
	private int burnCount = 0;
	private boolean slotsLocked = true;
	private boolean controlledByRedstone = false;
	private ItemStack targetItemStack = ItemStack.EMPTY;
	private ItemStack lastCast = ItemStack.EMPTY;
	private ItemStack cast;
	private FluidStack lastCastFluid;
	private FluidStack castFluid;
	private ICastingRecipe currentRecipe;

	public TileEntityFluidTank tank = new TileEntityFluidTank(TANK_CAPACITY, this);

	public TileEntityCM() {
		super(SLOTS_SIZE);
	}

	@Override public void readFromNBT(NBTTagCompound compound) {
		slotsLocked = compound.getBoolean(TAG_LOCK_SLOTS);
		targetItemStack = new ItemStack(compound.getCompoundTag(TAG_OUTPUT_ITEM_STACK));
		burnCount = compound.getInteger(TAG_BURN_COUNT);
		controlledByRedstone = compound.getBoolean(TAG_REDSTONE);
		currentMode = compound.getInteger(TAG_MODE);
		outputStackSize = compound.getInteger(TAG_OUTPUT_STACK_SIZE);
		speedStackSize = compound.getInteger(TAG_SPEED_STACK_SIZE);
		tank.readFromNBT(compound);
		super.readFromNBT(compound);
	}

	@Override @Nonnull public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean(TAG_LOCK_SLOTS, slotsLocked);
		NBTTagCompound tagItemStack = new NBTTagCompound();
		targetItemStack.writeToNBT(tagItemStack);
		compound.setTag(TAG_OUTPUT_ITEM_STACK, tagItemStack);
		compound.setInteger(TAG_BURN_COUNT, burnCount);
		compound.setBoolean(TAG_REDSTONE, controlledByRedstone);
		compound.setInteger(TAG_MODE, currentMode);
		compound.setInteger(TAG_OUTPUT_STACK_SIZE, outputStackSize);
		compound.setInteger(TAG_SPEED_STACK_SIZE, speedStackSize);
		tank.writeToNBT(compound);
		super.writeToNBT(compound);
		return compound;
	}

	@Override @Nonnull public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (SlotHandlerItems.validForSlot(stack, slot, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		return super.insertItem(slot, stack, simulate);
	}

	@Override @Nonnull public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot == SLOTOUTPUT) return itemInventory.extractItem(slot, amount, simulate);
		if (!slotsLocked) {
			if (getCurrentFluid() == null && !isActive()) {
				for (int slotNumber = 1; slotNumber < SLOTS_SIZE - 2; slotNumber++) {
					if (slot == slotNumber) return itemInventory.extractItem(slot, amount, simulate);
				}
			}
		}
		return super.extractItem(slot, amount, simulate);
	}

	@Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override public <T> @Nullable T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return (T) tank;
		return super.getCapability(capability, facing);
	}

	@Override
	public void update() {
		if (world.isRemote) {
			if (active && progress != 0) {
				activeCount = progress;
				progress = 0;
				cooldown = 1;
			} else if (active && canWork() && cooldown % 2 == 0) {
				activeCount = (activeCount + CASTING_MACHINE_SPEED + speedStackSize) % (time + CASTING_MACHINE_SPEED + speedStackSize);
			}
		} else {
			if (cooldown % 2 == 0) {
				if (active && time == 0) {
					active = false;
					update = true;
				}
				getSmeltery();
				if (smeltery) inputFluid();
				checkUpgradeSlots();
				canBurnSolidFuel();
				if (isChanged()) updateRecipe();
				if (canWork()) {
					doCasting();
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
				}
			} else {
				if (smeltery) {
					notifyMasterOfChange();
					resetCM();
				}
			}
		} else {
			if (smeltery) {
				resetCM();
			}
		}
	}

	private void resetCM() {
		smeltery = false;
		update = true;
		tileSmeltery = null;
	}

	private void inputFluid() {
		if (tank.getFluidAmount() >= tank.getCapacity()) return;
		if (tileSmeltery == null) return;
		SmelteryTank sourceTank = tileSmeltery.getTank();
		if (sourceTank == null) return;
		FluidStack sourceFluid = sourceTank.getFluid();
		if (sourceFluid == null) return;
		int canFill = tank.fill(sourceFluid, false);
		if (canFill <= 0) return;
		int transferAmount = Math.min(canFill, 144);
		FluidStack drained = sourceTank.drain(transferAmount, true);
		if (drained != null && drained.amount > 0) {
			tank.fill(drained, true);
		}
	}

	private boolean isChanged() {
		cast = itemInventory.getStackInSlot(SLOTCAST);
		castFluid = tank.getFluid();
		if (lastMode != currentMode) {
			burnCount = 0;
			time = 0;
		}
		boolean changed = (lastMode != currentMode)
				|| (lastCastFluid != null && castFluid == null)
				|| (lastCastFluid == null && castFluid != null)
				|| (castFluid != null && !castFluid.isFluidEqual(lastCastFluid))
				|| (castFluid != null && !FluidStack.areFluidStackTagsEqual(lastCastFluid, castFluid))
				|| !ItemStack.areItemsEqual(lastCast, cast)
				|| !ItemStack.areItemStackTagsEqual(lastCast, cast);
		if (changed) {
			lastMode = currentMode;
			lastCastFluid = castFluid;
			lastCast = cast;
		}
		return changed;
	}

	private void updateRecipe() {
		if (currentMode == CAST) currentRecipe = CMRecipeHandler.findTableCastingRecipe(cast, castFluid);
		else if (currentMode == BASIN) currentRecipe = CMRecipeHandler.findBasinCastingRecipe(cast, castFluid);
		update = true;
	}

	public boolean canWork() {
		if (!controlledByRedstone) return true;
		boolean blockPowered = world.isBlockPowered(pos);
		return !blockPowered;
	}

	private void checkUpgradeSlots() {
		ItemStack upgrade1 = itemInventory.getStackInSlot(SLOTUPGRADE1);
		ItemStack upgrade2 = itemInventory.getStackInSlot(SLOTUPGRADE2);
		ItemStack upgrade3 = itemInventory.getStackInSlot(SLOTUPGRADESPEED);
		ItemStack upgrade4 = itemInventory.getStackInSlot(SLOTREDSTONE);
		int oldOutput = outputStackSize;
		int oldMode = currentMode;
		int oldSpeed = speedStackSize;
		boolean oldRed = controlledByRedstone;
		outputStackSize = 0;
		currentMode = CAST;
		if (!upgrade1.isEmpty() || !upgrade2.isEmpty()) {
			outputStackSize += getSlotStackSize(upgrade1);
			outputStackSize += getSlotStackSize(upgrade2);
			if (outputStackSize > 64) outputStackSize = 64;
			if (upgrade1.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 5)) || upgrade2.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 5))) {
				currentMode = BASIN;
			}
		}
		speedStackSize = 0;
		if (!upgrade3.isEmpty() && upgrade3.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 6))) speedStackSize += getSlotStackSize(upgrade3);
		controlledByRedstone = !upgrade4.isEmpty() && upgrade4.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 7));
		if (oldOutput != outputStackSize || oldMode != currentMode || oldSpeed != speedStackSize || oldRed != controlledByRedstone) {
			update = true;
		}
	}

	private void canBurnSolidFuel() {
		if (!isReady && !itemInventory.getStackInSlot(SLOTFUEL).isEmpty()) {
			isReady = true;
			update = true;
		} else if (isReady && time == 0 && progress == 0 && burnCount == 0 && itemInventory.getStackInSlot(SLOTFUEL).isEmpty()) {
			isReady = false;
			update = true;
		}
	}

	private boolean burnSolidFuel() {
		if (burnCount != 0) return true;
		ItemStack fuel = itemInventory.getStackInSlot(SLOTFUEL);
		if (fuel.isEmpty()) return false;
		int fuelAmount = (currentMode == CAST) ? FUEL_SNOW_AMOUNT_CAST : FUEL_SNOW_AMOUNT_BASIN;
		boolean fuelIce = fuel.isItemEqual(new ItemStack(Registry.ICEBALL));
		if (fuelIce) fuelAmount = (currentMode == CAST) ? FUEL_ICE_AMOUNT_CAST : FUEL_ICE_AMOUNT_BASIN;
		if (fuel.getCount() >= fuelAmount) {
			consumeItemStack(SLOTFUEL, fuelAmount);
			burnCount = fuelIce ? ((currentMode == CAST) ? FUEL_ICE_CAST_AMOUNT : FUEL_ICE_BASIN_AMOUNT) : 1;
			return true;
		}
		return false;
	}

	private void doCasting() {
		if (isReady) {
			if (time != 0) progress = (progress + CASTING_MACHINE_SPEED + speedStackSize) % (time + CASTING_MACHINE_SPEED + speedStackSize);
			if (time == 0 && progress == 0) {
				if (targetItemStack.isEmpty() && currentRecipe != null) {
					if (castFluid != null && castFluid.amount >= currentRecipe.getFluidAmount()) {
						targetItemStack = getResult(cast, castFluid);
						if (!targetItemStack.isEmpty() && canOutput() && burnSolidFuel()) {
							if (currentRecipe.consumesCast()) itemInventory.extractItem(SLOTCAST, 1, false);
							tank.drain(currentRecipe.getFluidAmount(), true);
							time = currentRecipe.getTime() / 2;
							burnCount--;
							active = true;
							update = true;
						} else {
							targetItemStack = ItemStack.EMPTY;
						}
					}
				}
			} else if (progress >= time) {
				itemInventory.insertItem(SLOTOUTPUT, targetItemStack, false);
				targetItemStack = ItemStack.EMPTY;
				time = 0;
				progress = 0;
				update = true;
			}
		}
	}

	private ItemStack getResult(ItemStack cast, FluidStack fluidStack) {
		if (fluidStack != null) return currentRecipe.getResult(cast, fluidStack.getFluid());
		return ItemStack.EMPTY;
	}

	private boolean canOutput() {
		ItemStack outputSlot = itemInventory.getStackInSlot(SLOTOUTPUT);
		return (outputSlot.isEmpty() || (outputSlot.isItemEqual(targetItemStack) && ItemStack.areItemStackTagsEqual(outputSlot, targetItemStack))) && outputStackSize - outputSlot.getCount() > 0;
	}

	public FluidStack getCurrentFluid() {
		return tank.getFluid();
	}

	public int getFluidAmount() {
		if (tank.getFluid() != null) return tank.getFluid().amount;
		return 0;
	}

	@Override
	public void TankContentsChanged() {
		this.markContainingBlockForUpdate(null);
	}

	public void emptyTank() {
		tank.drain(getFluidAmount(), true);
	}

	public void slotsLocked() {
		slotsLocked = !slotsLocked;
	}

	@SideOnly(Side.CLIENT)
	public FluidTank getTank() {
		return tank;
	}

	@SideOnly(Side.CLIENT)
	public boolean isSlotsLocked() {
		return slotsLocked;
	}

	@SideOnly(Side.CLIENT)
	public int getOutputStackSize() {
		return outputStackSize;
	}

	@SideOnly(Side.CLIENT)
	public boolean isControlledByRedstone() {
		return controlledByRedstone;
	}

	@SideOnly(Side.CLIENT)
	public int getCurrentMode() {
		return currentMode;
	}

	@SideOnly(Side.CLIENT)
	public int getGUIFluidBarHeight(int pixel) {
		return (int) (((float) tank.getFluidAmount() / (float) TANK_CAPACITY) * pixel);
	}

	public void guiOpen() {
		efficientMarkDirty();
	}
}

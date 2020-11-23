package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.library.util.recipes.CMRecipeHandler;
import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.tileentity.base.TileEntityBase;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerItems;
import mctmods.smelteryio.tileentity.fuildtank.TileEntityFluidTank;
import mctmods.smelteryio.tileentity.fuildtank.TileEntityFluidTank.TankListener;

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

public class TileEntityCM extends TileEntityBase implements ITickable, TankListener {

	public static final int SLOTS_SIZE = 7;
	public static final int SLOTFUEL = 0, SLOTCAST = 1, SLOTUPGRADE1 = 2, SLOTUPGRADE2 = 3, SLOTUPGRADESPEED = 4, SLOTOUTPUT = 5, SLOTREDSTONE = 6;
	public static final String TAG_CAN_CAST = "canCast";
	public static final String TAG_MODE = "currentMode";
	public static final String TAG_OUTPUT_STACK_SIZE = "outputStackSize";
	public static final String TAG_LOCK_SLOTS = "currentLockSlots";
	public static final String TAG_OUTPUT_ITEM_STACK = "targetItemStack";
	public static final String TAG_REDSTONE = "controlledByRedstone";
	public static final String TAG_POWERED = "blockPowered";
	public static final String TAG_BURN_COUNT = "burnCount";
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
	private int currentMode = CAST;
	private int lastMode;
	private int fuelAmount;
	private int burnCount = 0;
	private boolean slotsLocked = true;
	private boolean controlledByRedstone = false;
	private boolean blockPowered = false;
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

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		currentMode = compound.getInteger(TAG_MODE);
		outputStackSize = compound.getInteger(TAG_OUTPUT_STACK_SIZE);
		slotsLocked = compound.getBoolean(TAG_LOCK_SLOTS);
		targetItemStack = new ItemStack(compound.getCompoundTag(TAG_OUTPUT_ITEM_STACK));
		controlledByRedstone = compound.getBoolean(TAG_REDSTONE);
		blockPowered = compound.getBoolean(TAG_POWERED);
		burnCount = compound.getInteger(TAG_BURN_COUNT);
		tank.readFromNBT(compound);
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_MODE, currentMode);
		compound.setInteger(TAG_OUTPUT_STACK_SIZE, outputStackSize);
		compound.setBoolean(TAG_LOCK_SLOTS, slotsLocked);
		NBTTagCompound tagItemStack = new NBTTagCompound();
		tagItemStack = targetItemStack.writeToNBT(tagItemStack);
		compound.setTag(TAG_OUTPUT_ITEM_STACK, tagItemStack);
		compound.setBoolean(TAG_REDSTONE, controlledByRedstone);
		compound.setBoolean(TAG_POWERED, blockPowered);
		compound.setInteger(TAG_BURN_COUNT, burnCount);
		tank.writeToNBT(compound);
		super.writeToNBT(compound);
		return compound;
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if(SlotHandlerItems.validForSlot(stack, slot, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(slot == SLOTOUTPUT) return itemInventory.extractItem(slot, amount, simulate);
		if(!slotsLocked) {
			if(getCurrentFluid() == null && !isActive()) {
				for(int slotNumber = 1; slotNumber < SLOTS_SIZE - 2; slotNumber++) {
					if(slot == slotNumber) return itemInventory.extractItem(slot, amount, simulate);
				}
			}
		}
		return super.extractItem(slot, amount, simulate);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return (T) tank;
		return super.getCapability(capability, facing);
	}

	@Override
	public void update() {
		if(world.isRemote) {
			if(active && progress != 0) {
				activeCount = progress;
				progress = 0;
				cooldown = 1;
			} else if(active && cooldown % 2 == 0) {
				activeCount = (activeCount + CASTING_MACHINE_SPEED + speedStackSize) % (time + CASTING_MACHINE_SPEED + speedStackSize);
			}
		} else {
			if(cooldown % 2 == 0) {
				if(active && time == 0) {
					active = false;
					update = true;
				}
				getSmeltery();
				if(smeltery) inputFluid();
			   	if(isChanged()) updateRecipe();
				if(canWork()) {
					checkUpgradeSlots();
					canBurnSolidFuel();
					doCasting();
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
			}  else {
				if(smeltery) {
					notifyMasterOfChange();
					resetCM();
				}
			}
		} else {
			if(smeltery) {
				resetCM();
			}
		}
	}

	private void resetCM() {
		smeltery = false;
		update = true;
	}

	private void inputFluid() {
		if(tileSmeltery.getTank().getFluid() == null) return;
		if(tank.getFluidAmount() == tank.getCapacity()) return;
		FluidStack out = tileSmeltery.getTank().getFluid();
		int accepted = tank.fill(out, false);
		if(accepted == 0) return;
		FluidStack transfer = new FluidStack(out, Math.min(accepted, 144));
		tileSmeltery.getTank().drain(transfer, true);
		tank.fill(transfer, true);
	}

	private boolean isChanged() {
		cast = itemInventory.getStackInSlot(SLOTCAST);
		castFluid = tank.getFluid();
		if(lastMode != currentMode) {
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
		if(changed) {
			lastMode = currentMode;
			lastCastFluid = castFluid;
			lastCast = cast;
		}
		return changed;
	}

	private void updateRecipe() {
		if(currentMode == CAST) currentRecipe = CMRecipeHandler.findTableCastingRecipe(cast, castFluid);
		else if(currentMode == BASIN) currentRecipe = CMRecipeHandler.findBasinCastingRecipe(cast, castFluid);
		update = true;
	}

	public boolean canWork() {
		blockPowered = world.isBlockPowered(pos);
		if(!controlledByRedstone) return true;
		return !blockPowered;
	}

	private void checkUpgradeSlots() {
		ItemStack upgrade1 = itemInventory.getStackInSlot(SLOTUPGRADE1);
		ItemStack upgrade2 = itemInventory.getStackInSlot(SLOTUPGRADE2);
		ItemStack upgrade3 = itemInventory.getStackInSlot(SLOTUPGRADESPEED);
		ItemStack upgrade4 = itemInventory.getStackInSlot(SLOTREDSTONE);
		int stackSize1 = upgrade1.getCount();
		int stackSize2 = upgrade2.getCount();
		int stackSize3 = upgrade3.getCount();
		int stackSize4 = upgrade4.getCount();
		if(stackSize1 != upgradeSize1 || stackSize2 != upgradeSize2) {
			outputStackSize = 0;
		   	currentMode = CAST;
			if(upgrade1 != ItemStack.EMPTY || upgrade2 != ItemStack.EMPTY) {
				outputStackSize += getSlotStackSize(upgrade1);
				outputStackSize += getSlotStackSize(upgrade2);
				// Should not happen, but just in case!
				if(outputStackSize > 64) outputStackSize = 64;
				if(upgrade1.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 5)) || upgrade2.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 5))) {
					currentMode = BASIN;
				}
			}
			upgradeSize1 = stackSize1;
			upgradeSize2 = stackSize2;
			update = true;
		}
		if(stackSize3 != upgradeSize3) {
			speedStackSize = 0;
			if(upgrade3.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 6))) speedStackSize += getSlotStackSize(upgrade3);
			upgradeSize3 = stackSize3;
			update = true;
		}
		if(stackSize4 != upgradeSize4) {
			controlledByRedstone = false;
			if(upgrade4.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 7))) {
				controlledByRedstone = true;
			}
			upgradeSize4 = stackSize4;
			update = true;
		}
	}

	private void canBurnSolidFuel() {
		if(!isReady && itemInventory.getStackInSlot(SLOTFUEL) != ItemStack.EMPTY) {
			isReady = true;
			update = true;
		} else if(isReady && time == 0 && progress == 0 && burnCount == 0 && itemInventory.getStackInSlot(SLOTFUEL) == ItemStack.EMPTY) {
			isReady = false;
			update = true;
		}
	}

	private void doCasting() {
		if(isReady) {
			if(time != 0) progress = (progress + CASTING_MACHINE_SPEED + speedStackSize) % (time + CASTING_MACHINE_SPEED + speedStackSize);
			if(time == 0 && progress == 0) {
				if(targetItemStack.isEmpty() && currentRecipe != null) {
					if(castFluid != null && castFluid.amount >= currentRecipe.getFluidAmount()){
						targetItemStack = getResult(cast, castFluid);
						if(!targetItemStack.isEmpty() && canOutput() && burnSolidFuel()){
							if(currentRecipe.consumesCast()) itemInventory.extractItem(SLOTCAST, 1, false);
							tank.drain(currentRecipe.getFluidAmount(), true);
							time = currentRecipe.getTime() * 2;
							burnCount--;
							active = true;
							update = true;
						} else {
							targetItemStack = ItemStack.EMPTY;
						}
					}
				}
			} else if(progress >= time) {
				itemInventory.insertItem(SLOTOUTPUT, targetItemStack, false);
				targetItemStack = ItemStack.EMPTY;
				time = 0;
				progress = 0;
				update = true;
			}
		}
	}

	private ItemStack getResult(ItemStack cast, FluidStack fluidStack) {
		if(fluidStack != null) return currentRecipe.getResult(cast, fluidStack.getFluid());
		return ItemStack.EMPTY;
	}

	private boolean canOutput() {
		ItemStack outputSlot = itemInventory.getStackInSlot(SLOTOUTPUT);
		return (outputSlot.isEmpty() || (outputSlot.isItemEqual(targetItemStack) && ItemStack.areItemStackTagsEqual(outputSlot, targetItemStack))) && outputStackSize - outputSlot.getCount() > 0;
	}

	private boolean burnSolidFuel() {
		if(burnCount != 0) return true;
		ItemStack fuel = itemInventory.getStackInSlot(SLOTFUEL);
		if(fuel == ItemStack.EMPTY) return fueled = false;
		fuelAmount = 1;
		if(currentMode == CAST) fuelAmount = FUEL_SNOW_AMOUNT_CAST;
	   	if(currentMode == BASIN) fuelAmount = FUEL_SNOW_AMOUNT_BASIN;
	   	boolean fuelIce = false;
	   	if(fuel.isItemEqual(new ItemStack(Registry.ICEBALL))) fuelIce = true;
	   	if(fuelIce && currentMode == CAST) fuelAmount = FUEL_ICE_AMOUNT_CAST;
		if(fuelIce && currentMode == BASIN) fuelAmount = FUEL_ICE_AMOUNT_BASIN;
		if(burnCount == 0 && fuel.getCount() >= fuelAmount) {
			consumeItemStack(SLOTFUEL, fuelAmount);
			burnCount = 1;
			if(fuelIce && currentMode == CAST) burnCount = FUEL_ICE_CAST_AMOUNT;
			if(fuelIce && currentMode == BASIN) burnCount = FUEL_ICE_BASIN_AMOUNT;
			return fueled = true;
		}
		return fueled = false;
	}

	public FluidStack getCurrentFluid() {
		return tank.getFluid();
	}

	public int getFluidAmount() {
		if(tank.getFluid() != null) return tank.getFluid().amount;
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
		if(slotsLocked) slotsLocked = false;
		else slotsLocked = true;	
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
		return (int) (((float)tank.getFluidAmount() / (float)TANK_CAPACITY) * pixel);
	}

	public void guiOpen() {
		efficientMarkDirty();
	}

}
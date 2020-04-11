package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.library.util.recipes.CMRecipeHandler;
import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.tileentity.container.ContainerCM;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerItems;
import mctmods.smelteryio.tileentity.fuildtank.TileEntityFluidTank;
import mctmods.smelteryio.tileentity.fuildtank.TileEntityFluidTank.TankListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.smeltery.ICastingRecipe;

public class TileEntityCM extends TileEntitySlotHandler implements ITickable, TankListener {
	private EnumFacing facing = EnumFacing.NORTH;
	public static final String TAG_FACING = "facing";
	public static final String TAG_PROGRESS = "progress";
	public static final String TAG_ACTIVE = "active";
	public static final String TAG_FUEL = "fueled";
	public static final String TAG_CAN_CAST = "canCast";
	public static final String TAG_MODE = "currentMode";
	public static final String TAG_OUTPUT_STACK_SIZE = "outputStackSize";
	public static final String TAG_SPEED_STACK_SIZE = "speedStackSize";
	public static final String TAG_LOCK_SLOTS = "currentLockSlots";
	public static final String TAG_OUTPUT_ITEM_STACK = "targetItemStack";
	public static final String TAG_REDSTONE = "controlledByRedstone";
	public static final String TAG_POWERED = "blockPowered";
	public static final int TILEID = 1;
	public static final int CAPACITY = 10368;
	public static final int CAST = 0;
	public static final int BASIN = 1;
	public boolean active = false;
	private static final int PROGRESS = 24;
	private static final int SLOTS_SIZE = 7;
	private static final int FUEL_AMOUNT_BASIN = ConfigSIO.snowballBasinAmount;
	private static final int FUEL_AMOUNT_CAST = ConfigSIO.snowballCastingAmount;
	private static final int CASTING_MACHINE_SPEED = ConfigSIO.castingMachineSpeed;
	private int cooldown = 0;
	private int upgradeSize1 = 0;
	private int upgradeSize2 = 0;
	private int upgradeSize3 = 0;
	private int upgradeSize4 = 0;
	private int progress = 0;
	private int activeCount = 0;
	private int outputStackSize = 0;
	private int speedStackSize = 1;
	private int currentMode = CAST;
	private int fuelAmount;
	private int lastMode;
	private boolean canCast = false;
	private boolean fueled = false;
	private boolean slotsLocked = true;
	private boolean controlledByRedstone = false;
	private boolean blockPowered = false;
	private boolean update = false;
	private ItemStack targetItemStack = ItemStack.EMPTY;
	private ItemStack lastCast = ItemStack.EMPTY;
	private ItemStack cast;
	private FluidStack lastCastFluid;
	private FluidStack castFluid;
	private ICastingRecipe currentRecipe;

	public TileEntityFluidTank tank = new TileEntityFluidTank(CAPACITY, this);

	public TileEntityCM() {
		super(SLOTS_SIZE);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		facing = EnumFacing.getFront(compound.getInteger(TAG_FACING));
		progress = compound.getInteger(TAG_PROGRESS);
		active = (compound.getBoolean(TAG_ACTIVE));
		fueled = compound.getBoolean(TAG_FUEL);
		canCast = compound.getBoolean(TAG_CAN_CAST);
		currentMode = compound.getInteger(TAG_MODE);
		outputStackSize = compound.getInteger(TAG_OUTPUT_STACK_SIZE);
		speedStackSize = compound.getInteger(TAG_SPEED_STACK_SIZE);
		slotsLocked = compound.getBoolean(TAG_LOCK_SLOTS);
		targetItemStack = new ItemStack(compound.getCompoundTag(TAG_OUTPUT_ITEM_STACK));
		controlledByRedstone = compound.getBoolean(TAG_REDSTONE);
		blockPowered = compound.getBoolean(TAG_POWERED);
		tank.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setInteger(TAG_FACING, facing.getIndex());
		compound.setInteger(TAG_PROGRESS, progress);
		compound.setBoolean(TAG_ACTIVE, active);
		compound.setBoolean(TAG_FUEL, fueled);
		compound.setBoolean(TAG_CAN_CAST, canCast);
		compound.setInteger(TAG_MODE, currentMode);
		compound.setInteger(TAG_OUTPUT_STACK_SIZE, outputStackSize);
		compound.setInteger(TAG_SPEED_STACK_SIZE, speedStackSize);
		compound.setBoolean(TAG_LOCK_SLOTS, slotsLocked);
		compound.setBoolean(TAG_REDSTONE, controlledByRedstone);
		compound.setBoolean(TAG_POWERED, blockPowered);
		tank.writeToNBT(compound);
		NBTTagCompound tagItemStack = new NBTTagCompound();
		tagItemStack = targetItemStack.writeToNBT(tagItemStack);
		compound.setTag(TAG_OUTPUT_ITEM_STACK, tagItemStack);
		return compound;
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if(slot == ContainerCM.FUEL) {
			if(SlotHandlerItems.validForSlot(stack, ContainerCM.FUEL, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		if(slot == ContainerCM.CAST) {
			if(!SlotHandlerItems.validForSlot(stack, ContainerCM.CAST, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		if(slot == ContainerCM.UPGRADE1) {
			if(SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADE1, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		if(slot == ContainerCM.UPGRADE2) {
			if(SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADE2, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		if(slot == ContainerCM.UPGRADESPEED) {
			if(SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADESPEED, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		if(slot == ContainerCM.REDSTONE) {
			if(SlotHandlerItems.validForSlot(stack, ContainerCM.REDSTONE, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		if(slot == ContainerCM.OUTPUT) {
			if(!SlotHandlerItems.validForSlot(stack, ContainerCM.OUTPUT, TILEID)) return itemInventory.insertItem(slot, stack, simulate);
		}
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(slot == ContainerCM.OUTPUT) return itemInventory.extractItem(slot, amount, simulate);
		if(!slotsLocked) {
			if(getCurrentFluid() == null && progress == 0) {
				if(slot == ContainerCM.UPGRADE1) return itemInventory.extractItem(slot, amount, simulate);
				if(slot == ContainerCM.UPGRADE2) return itemInventory.extractItem(slot, amount, simulate);
				if(slot == ContainerCM.UPGRADESPEED) return itemInventory.extractItem(slot, amount, simulate);
				if(slot == ContainerCM.CAST) return itemInventory.extractItem(slot, amount, simulate);
			}
		}
		return super.extractItem(slot, amount, simulate);
	}

	@Override
	public void update() {
		if(world.isRemote) return;
		update = false;
		if(cooldown % 2 == 0) {
			if(isChanged()) updateRecipe();
			if(canWork()) {
				checkUpgradeSlots();
				if(canCast()) {
					doCasting();
				}
			}
		}
		cooldown = (cooldown + 2) % 200;
		if(activeCount != 0) {
			activeCount--;
			active = true;
		} else if(activeCount == 0 && progress == 0 && active) {
			update = true;
			active = false;
		}
		if(update) efficientMarkDirty();
	}

	public EnumFacing getFacing() {
		return facing;
	}

	public void setFacing(EnumFacing facing) {
		this.facing = facing;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.getNbtCompound());
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

	private boolean isChanged() {
		cast = itemInventory.getStackInSlot(ContainerCM.CAST);
		castFluid = tank.getFluid();
		boolean changed = (lastMode != currentMode)
				|| (lastCastFluid != null && castFluid == null)
				|| (lastCastFluid == null && castFluid != null)
				|| (castFluid != null && !castFluid.isFluidEqual(lastCastFluid))
				|| (castFluid != null && !FluidStack.areFluidStackTagsEqual(lastCastFluid, castFluid))
				|| !ItemStack.areItemsEqual(lastCast, cast)
				|| !ItemStack.areItemStackTagsEqual(lastCast, cast);
		lastMode = currentMode;
		lastCastFluid = castFluid;
		lastCast = cast;
		return changed;
	}

	private void updateRecipe() {
		if(currentMode == CAST) currentRecipe = CMRecipeHandler.findTableCastingRecipe(cast, castFluid);
		else if(currentMode == BASIN) currentRecipe = CMRecipeHandler.findBasinCastingRecipe(cast, castFluid);
		update = true;
	}

	private void checkUpgradeSlots() {
		ItemStack upgrade1 = itemInventory.getStackInSlot(ContainerCM.UPGRADE1);
		ItemStack upgrade2 = itemInventory.getStackInSlot(ContainerCM.UPGRADE2);
		ItemStack upgrade3 = itemInventory.getStackInSlot(ContainerCM.UPGRADESPEED);
		ItemStack upgrade4 = itemInventory.getStackInSlot(ContainerCM.REDSTONE);
		int stackSize1 = upgrade1.getCount();
		int stackSize2 = upgrade2.getCount();
		int stackSize3 = upgrade3.getCount();
		int stackSize4 = upgrade4.getCount();
		checkUpgrades(upgrade1, stackSize1, upgrade2, stackSize2, upgrade3, stackSize3, upgrade4, stackSize4);
	}

	private void checkUpgrades(ItemStack itemStack1, int stackSize1, ItemStack itemStack2, int stackSize2, ItemStack itemStack3, int stackSize3, ItemStack itemStack4, int stackSize4) {
		if(stackSize1 != upgradeSize1 || stackSize2 != upgradeSize2) {
			outputStackSize = 0;
		   	currentMode = CAST;
		   	fuelAmount = FUEL_AMOUNT_CAST;
			if(itemStack1 != ItemStack.EMPTY || itemStack2 != ItemStack.EMPTY) {
				outputStackSize += getSlotStackSize(itemStack1);
				outputStackSize += getSlotStackSize(itemStack2);
				// Should not happen, but just in case!
				if(outputStackSize > 64) outputStackSize = 64;
				if(itemStack1.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 5)) || itemStack2.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 5))) {
					currentMode = BASIN;
					fuelAmount = FUEL_AMOUNT_BASIN;
				}
			}
			upgradeSize1 = stackSize1;
			upgradeSize2 = stackSize2;
			update = true;
		}
		if(stackSize3 != upgradeSize3) {
			speedStackSize = 1;
			if(itemStack3.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 6))) {
				speedStackSize += getSlotStackSize(itemStack3);
				if(speedStackSize == 0) speedStackSize = 1;
			}
			upgradeSize3 = stackSize3;
			update = true;
		}
		if(stackSize4 != upgradeSize4) {
			controlledByRedstone = false;
			if(itemStack4.isItemEqual(new ItemStack(Registry.UPGRADE, 1, 7))) {
				controlledByRedstone = true;
			}
			upgradeSize4 = stackSize4;
			update = true;
		}
	}

	private int getSlotStackSize(ItemStack itemStack) {
		int size = 0;
		int meta = itemStack.getItemDamage();
		int count = itemStack.getCount();
		switch(meta) {
		case 1:
			size = count * 1;
			break;
		case 2:
			size = count * 2;
			break;
		case 3:
			size = count * 3;
			break;
		case 4:
			size = count * 4;
			break;
		case 6:
			size = count * 1;
			break;
		}
		return size;
	}

	public boolean canWork() {
		blockPowered = world.isBlockPowered(pos);
		if(!controlledByRedstone) return true;
		return !blockPowered;
	}

	private boolean canCast() {
		canCast = false;
		if(outputStackSize != 0) {
		   	if(progress != 0 || itemInventory.getStackInSlot(ContainerCM.FUEL).getCount() >= fuelAmount) {
		   		canCast = true;
			}
		}
		return canCast;
	}

	private void doCasting() {
		if(progress == 0) {
			if(targetItemStack.isEmpty() && currentRecipe != null) {
				if(castFluid != null && castFluid.amount >= currentRecipe.getFluidAmount()){
					targetItemStack = getResult(cast, castFluid);
					if(!targetItemStack.isEmpty() && canOutput() && burnSolidFuel()){
						if(currentRecipe.consumesCast()) itemInventory.extractItem(ContainerCM.CAST, 1, false);
						tank.drain(currentRecipe.getFluidAmount(), true);
						progress = speedStackSize * CASTING_MACHINE_SPEED;
						if(currentMode == BASIN) progress = progress / 3;
						if(progress == 0) progress = 1;
						update = true;
					} else {
						targetItemStack = ItemStack.EMPTY;
					}
				}
			}
		} else {
			if(progress >= PROGRESS - 1) {
				itemInventory.insertItem(ContainerCM.OUTPUT, targetItemStack, false);
				targetItemStack = ItemStack.EMPTY;
				progress = 0;
				update = true;
			} else {
				progress = (progress + 1) % PROGRESS;
				activeCount = progress + 5;
				update = true;
			}
		}
	}

	private boolean canOutput() {
		ItemStack outputSlot = itemInventory.getStackInSlot(ContainerCM.OUTPUT);
		return (outputSlot.isEmpty() || (outputSlot.isItemEqual(targetItemStack) && ItemStack.areItemStackTagsEqual(outputSlot, targetItemStack))) && outputStackSize - outputSlot.getCount() > 0;
	}

	private ItemStack getResult(ItemStack cast, FluidStack fluidStack) {
		if(fluidStack != null) return currentRecipe.getResult(cast, fluidStack.getFluid());
		return ItemStack.EMPTY;
	}
	
	private boolean burnSolidFuel() {
		fueled = false;
		if(itemInventory.getStackInSlot(ContainerCM.FUEL).getCount() >= fuelAmount) {
			consumeItemStack(ContainerCM.FUEL, fuelAmount);
   			fueled = true;
   			return fueled;
		}
		if(itemInventory.getStackInSlot(ContainerCM.FUEL) == ItemStack.EMPTY && progress != 0) {
			fueled = true;
			return fueled;
		}
		return fueled;
	}

	public boolean isFueled() {
		return fueled;
	}

	public boolean isReady() {
		if(canCast && fueled) return true;
		return false;
	}

	public int getCurrentMode() {
		return currentMode;
	}

	public boolean isControlledByRedstone() {
		return controlledByRedstone;
	}

	public boolean isBlockPowered() {
		return blockPowered;
	}

	public boolean isSlotsLocked() {
		return slotsLocked;
	}

	public int isProgressing() {
		return progress;
	}

	public FluidTank getTank() {
		return tank;
	}

	public FluidStack getCurrentFluid() {
		return tank.getFluid();
	}

	public int getFluidAmount() {
		if(tank.getFluid() != null) return tank.getFluid().amount;
		return 0;
	}

	public void emptyTank() {
		tank.drain(getFluidAmount(), true);
	}

	public void slotsLocked() {
		if(slotsLocked) slotsLocked = false;
		else slotsLocked = true;
		
	}

	public int getOutputStackSize() {
		return outputStackSize;
	}

	@Override
	public void TankContentsChanged() {
		this.markContainingBlockForUpdate(null);
	}

	@SideOnly(Side.CLIENT)
	public int getGUIFluidBarHeight(int pixel) {
		return (int) (((float)tank.getFluidAmount() / (float)CAPACITY) * pixel);
	}

	@SideOnly(Side.CLIENT)
	public int getGUIProgress(int pixel) {
		return (int) (((float)progress / (float)PROGRESS) * pixel);
	}

}
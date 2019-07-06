package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mctmods.smelteryio.library.util.recipes.CMRecipeHandler;
import mctmods.smelteryio.registry.RegistryItem;
import mctmods.smelteryio.tileentity.container.ContainerCM;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerItems;
import mctmods.smelteryio.tileentity.fuildtank.TileEntityFluidTank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.smeltery.ICastingRecipe;

public class TileEntityCM extends TileEntityItemHandler implements ITickable {
	
	private EnumFacing facing = EnumFacing.NORTH;

    public static final String TAG_FACING = "facing";
    public static final String TAG_PROGRESS = "progress";
    public static final String TAG_ACTIVE_COUNT = "activeCount";
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

    private static final int PROGRESS = 24;
    private static final int SLOTS_SIZE = 7;
    private static final int FUEL_AMOUNT_BASIN = 9;
    private static final int FUEL_AMOUNT_CAST = 1;

    private FluidTank tank;

    private int progress;
    private int fuelAmount;
    private int currentMode = CAST;
    private int outputStackSize = 0;
    private int speedStackSize = 0;
    private int cooldown = 0;
    private int lastMode;
	private int activeCount;

    private boolean canCast = false;
    private boolean fueled = false;
    private boolean slotsLocked = true;
    private boolean controlledByRedstone = false;
    private boolean blockPowered = false;

    private ItemStack targetItemStack = ItemStack.EMPTY;
    private ItemStack lastCast = ItemStack.EMPTY;
    private FluidStack lastFluidStack;
    private ICastingRecipe currentRecipe;

	public TileEntityCM() {
        super(SLOTS_SIZE);
        this.tank = new TileEntityFluidTank(this, CAPACITY);
    }

    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot == ContainerCM.FUEL) {
        	if (SlotHandlerItems.validForSlot(stack, ContainerCM.FUEL, TILEID)) {
        		return this.itemInventory.insertItem(slot, stack, simulate);
        	}
        }
    	if (slot == ContainerCM.CAST) {
        	if (!SlotHandlerItems.validForSlot(stack, ContainerCM.CAST, TILEID)) {
        		return this.itemInventory.insertItem(slot, stack, simulate);
        	}
        }
    	if (slot == ContainerCM.UPGRADE1) {
        	if (SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADE1, TILEID)) {
        		return this.itemInventory.insertItem(slot, stack, simulate);
        	}
        }
    	if (slot == ContainerCM.UPGRADE2) {
        	if (SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADE2, TILEID)) {
        		return this.itemInventory.insertItem(slot, stack, simulate);
        	}
        }
        if (slot == ContainerCM.UPGRADESPEED) {
        	if (SlotHandlerItems.validForSlot(stack, ContainerCM.UPGRADESPEED, TILEID)) {
        		return this.itemInventory.insertItem(slot, stack, simulate);
        	}
        }
    	if (slot == ContainerCM.REDSTONE) {
        	if (SlotHandlerItems.validForSlot(stack, ContainerCM.REDSTONE, TILEID)) {
        		return this.itemInventory.insertItem(slot, stack, simulate);
        	}
        }
    	if (slot == ContainerCM.OUTPUT) {
        	if (!SlotHandlerItems.validForSlot(stack, ContainerCM.OUTPUT, TILEID)) {
        		return this.itemInventory.insertItem(slot, stack, simulate);
        	}
        }
        return super.insertItem(slot, stack, simulate);
    }

	@Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == ContainerCM.OUTPUT) {
            return this.itemInventory.extractItem(slot, amount, simulate);
        }
        if (!slotsLocked) {
        	if (getCurrentFluid() == null && this.progress == 0) {
        		if (slot == ContainerCM.UPGRADE1) {
        			return this.itemInventory.extractItem(slot, amount, simulate);
        		}
        		if (slot == ContainerCM.UPGRADE2) {
        			return this.itemInventory.extractItem(slot, amount, simulate);
            	}
        		if (slot == ContainerCM.UPGRADESPEED) {
        			return this.itemInventory.extractItem(slot, amount, simulate);
        		}
        		if (slot == ContainerCM.CAST) {
        			return this.itemInventory.extractItem(slot, amount, simulate);
            	}
        	}
        }
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public void update() {
    	if (!this.hasWorld()) return;
    	if (world.isRemote) return;
		if (this.cooldown % 2 == 0) {
			this.blockPowered = world.isBlockPowered(pos);
			if (isChanged()) {
				updateRecipe();
			}
			if (canWork()) {
				checkUpgrade();
				if (canCast() && burnSolidFuel()) {
					doCasting();
				}
			}
		}
		activeCount();
		sendUpdates();
		this.cooldown = (this.cooldown + 2) % 200;
	}

	@Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.facing = EnumFacing.getFront(compound.getInteger(TAG_FACING));
        this.progress = compound.getInteger(TAG_PROGRESS);
        this.activeCount = (compound.getInteger(TAG_ACTIVE_COUNT));
        this.fueled = compound.getBoolean(TAG_FUEL);
        this.canCast = compound.getBoolean(TAG_CAN_CAST);
        this.currentMode = compound.getInteger(TAG_MODE);
        this.outputStackSize = compound.getInteger(TAG_OUTPUT_STACK_SIZE);
        this.speedStackSize = compound.getInteger(TAG_SPEED_STACK_SIZE);
        this.slotsLocked = compound.getBoolean(TAG_LOCK_SLOTS);
        this.targetItemStack = new ItemStack(compound.getCompoundTag(TAG_OUTPUT_ITEM_STACK));
        this.controlledByRedstone = compound.getBoolean(TAG_REDSTONE);
        this.blockPowered = compound.getBoolean(TAG_POWERED);
        this.tank.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger(TAG_FACING, facing.getIndex());
        compound.setInteger(TAG_PROGRESS, this.progress);
        compound.setInteger(TAG_ACTIVE_COUNT, this.activeCount);
        compound.setBoolean(TAG_FUEL, this.fueled);
        compound.setBoolean(TAG_CAN_CAST, this.canCast);
        compound.setInteger(TAG_MODE, this.currentMode);
        compound.setInteger(TAG_OUTPUT_STACK_SIZE, this.outputStackSize);
        compound.setInteger(TAG_SPEED_STACK_SIZE, this.speedStackSize);
        compound.setBoolean(TAG_LOCK_SLOTS, this.slotsLocked);
        compound.setBoolean(TAG_REDSTONE, this.controlledByRedstone);
        compound.setBoolean(TAG_POWERED, this.blockPowered);
        this.tank.writeToNBT(compound);

        NBTTagCompound tagItemStack = new NBTTagCompound();
        tagItemStack = this.targetItemStack.writeToNBT(tagItemStack);
        compound.setTag(TAG_OUTPUT_ITEM_STACK, tagItemStack);

        return compound;
    }
    
	public EnumFacing getFacing() {
		return facing;
	}

	public void setFacing(EnumFacing facing) {
		this.facing = facing;
	}
    
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)	{
		return (oldState.getBlock() != newState.getBlock());
	}

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
    	super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());
    }

	private void sendUpdates() {
		if (activeCount != 0) {
       		markDirty();
       		world.markBlockRangeForRenderUpdate(pos, pos);
       	}
		world.notifyBlockUpdate(pos, getState(), getState(), 3);
	}

	private IBlockState getState() {
		return world.getBlockState(pos);
	}

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) this.tank;
        return super.getCapability(capability, facing);
    }

    private boolean isChanged() {
        FluidStack fluidStack = this.tank.getFluid();
        ItemStack itemStack = this.itemInventory.getStackInSlot(ContainerCM.CAST);

        boolean changed = (this.lastMode != this.currentMode)
                || (this.lastFluidStack != null && fluidStack == null)
                || (this.lastFluidStack == null && fluidStack != null)
                || (fluidStack != null && !fluidStack.isFluidEqual(this.lastFluidStack))
                || (fluidStack != null && !FluidStack.areFluidStackTagsEqual(this.lastFluidStack, fluidStack))
                || !ItemStack.areItemsEqual(this.lastCast, itemStack)
                || !ItemStack.areItemStackTagsEqual(this.lastCast, itemStack);

        this.lastMode = this.currentMode;
        this.lastFluidStack = fluidStack;
        this.lastCast = itemStack;

        return changed;
    }

    private void updateRecipe() {
        ItemStack cast = this.itemInventory.getStackInSlot(ContainerCM.CAST);
        FluidStack fluidStack = this.tank.getFluid();
        if (this.currentMode == BASIN) {
        	this.currentRecipe = CMRecipeHandler.findBasinCastingRecipe(cast, fluidStack);
        } else if (this.currentMode == CAST) {
        	this.currentRecipe = CMRecipeHandler.findTableCastingRecipe(cast, fluidStack);
        }
    }

    private void checkUpgrade() {
        ItemStack upgrade1 = this.itemInventory.getStackInSlot(ContainerCM.UPGRADE1);
        ItemStack upgrade2 = this.itemInventory.getStackInSlot(ContainerCM.UPGRADE2);
        ItemStack upgrade3 = this.itemInventory.getStackInSlot(ContainerCM.UPGRADESPEED);
        ItemStack upgrade4 = this.itemInventory.getStackInSlot(ContainerCM.REDSTONE);
        this.speedStackSize = 0;
        this.outputStackSize = 0;
        this.controlledByRedstone = false;
        checkUpgrades(upgrade1);
        checkUpgrades(upgrade2);
        checkSpeedUpgrades(upgrade3);
        checkRedstone(upgrade4);
        if (this.speedStackSize == 0) {
            this.speedStackSize = 1;
        }
        // Should not happen, but just in case!
        if (this.outputStackSize > 64) {
            this.outputStackSize = 64;
        }
    }

    private void checkUpgrades(ItemStack itemStack) {
        if (itemStack != ItemStack.EMPTY && itemStack.getItem().equals(RegistryItem.UPGRADE)) {
            this.outputStackSize += getSlotStackSize(itemStack);
            if (itemStack.isItemEqual(new ItemStack(RegistryItem.UPGRADE, 1, 5))) {
                this.currentMode = BASIN;
            }
        }
    }

    private void checkSpeedUpgrades(ItemStack itemStack) {
        if (itemStack != ItemStack.EMPTY && itemStack.isItemEqual(new ItemStack(RegistryItem.UPGRADE, 1, 6))) {
                this.speedStackSize += getSlotStackSize(itemStack);
        }
    }
    
    private void checkRedstone(ItemStack itemStack) {
        if (itemStack.isItemEqual(new ItemStack(RegistryItem.UPGRADE, 1, 7))) {
            this.controlledByRedstone = true;
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
        if (!this.controlledByRedstone)
            return true;
        else
            return !this.blockPowered;
    }

    private boolean canCast() {
    	this.canCast = false;	
        if (this.outputStackSize != 0) {
           	if (this.progress != 0 || this.itemInventory.getStackInSlot(ContainerCM.FUEL) != ItemStack.EMPTY) {
           		this.canCast = true;
        	}
        }
        return this.canCast;
	}

	private boolean burnSolidFuel() {
		if (this.currentMode != BASIN) {
			this.fuelAmount = FUEL_AMOUNT_CAST;			
		} else {
			this.fuelAmount = FUEL_AMOUNT_BASIN;	
		}
    	if (this.progress >= PROGRESS - 1) {
    		this.fueled = false;
    	}
    	if (this.itemInventory.getStackInSlot(ContainerCM.FUEL).getCount() >= fuelAmount) {
    		if (this.fueled == false) {
    			consumeItemStack(ContainerCM.FUEL, this.fuelAmount);
   				this.fueled = true;
    		}
    	}
    	if (itemInventory.getStackInSlot(ContainerCM.FUEL) == ItemStack.EMPTY && this.progress != 0 && this.progress >= PROGRESS - 1) {
    		this.fueled = true;
    	}
    	return this.fueled;
    }

    private ItemStack getResult(ItemStack cast, FluidStack fluidStack) {
        if (fluidStack != null)
            return this.currentRecipe.getResult(cast, fluidStack.getFluid());
        else
            return ItemStack.EMPTY;
    }

    private void doCasting() {
        if (this.progress == 0){
            if (this.targetItemStack.isEmpty() && currentRecipe != null) {
                ItemStack cast = this.itemInventory.getStackInSlot(ContainerCM.CAST);
                FluidStack fluidStack = this.tank.getFluid();
                if (fluidStack != null && fluidStack.amount >= currentRecipe.getFluidAmount()){
                    this.targetItemStack = getResult(cast, fluidStack);
                    if (!this.targetItemStack.isEmpty() && canOutput()){
                        if (currentRecipe.consumesCast()) {
                            this.itemInventory.extractItem(ContainerCM.CAST, 1, false);
                        }
                        this.tank.drain(currentRecipe.getFluidAmount(), true);
                        this.progress = this.speedStackSize * 2;
                    } else {
                        this.targetItemStack = ItemStack.EMPTY;
                    }
                }
            }
        } else {
            if (this.progress >= PROGRESS - 1) {
                this.itemInventory.insertItem(ContainerCM.OUTPUT, this.targetItemStack, false);
                this.targetItemStack = ItemStack.EMPTY;
                this.progress = 0;
            } else {
                this.progress = (this.progress + 1) % PROGRESS;
                this.activeCount = this.progress + 5;
            }
        }
    }

    private boolean canOutput() {
    	ItemStack outputSlot = this.itemInventory.getStackInSlot(ContainerCM.OUTPUT);
        return (outputSlot.isEmpty() || (outputSlot.isItemEqual(this.targetItemStack) && ItemStack.areItemStackTagsEqual(outputSlot, this.targetItemStack))) && outputStackSize - outputSlot.getCount() > 0;
    }
    
    public int activeCount() {
        if (this.activeCount != 0) {
        	this.activeCount--;
        }
		return this.activeCount;
    }

    public boolean isFueled() {
    	return this.fueled;
    }
    
    public boolean isReady() {
    	if (this.canCast && this.fueled) {
    	return true;
    	}
		return false;
    }

    public int getCurrentMode() {
        return this.currentMode;
    }

    public boolean isControlledByRedstone() {
        return this.controlledByRedstone;
    }

    public boolean isBlockPowered() {
        return this.blockPowered;
    }

    public boolean isSlotsLocked() {
    	return this.slotsLocked;
    }

    public int isProgressing() {
    	return this.progress;
    }

    public FluidTank getTank() {
        return this.tank;
    }

    public FluidStack getCurrentFluid() {
        return this.tank.getFluid();
    }

    public int getFluidAmount() {
        if (tank.getFluid() != null) {
            return tank.getFluid().amount;
        }
        return 0;
    }

    public void emptyTank() {
        this.tank.drain(getFluidAmount(), true);
    }

    public void slotsLocked() {
    	if (slotsLocked) {
    		this.slotsLocked = false;
    	} else {
    		this.slotsLocked = true;
        }
    }

    public int getOutputStackSize() {
        return this.outputStackSize;
    }

    @SideOnly(Side.CLIENT)
    public int getGUIFluidBarHeight(int pixel) {
    	return (int) (((float)this.tank.getFluidAmount() / (float)CAPACITY) * pixel);
    }

    @SideOnly(Side.CLIENT)
    public int getGUIProgress(int pixel) {
        return (int) (((float)this.progress / (float)PROGRESS) * pixel);
    }

}

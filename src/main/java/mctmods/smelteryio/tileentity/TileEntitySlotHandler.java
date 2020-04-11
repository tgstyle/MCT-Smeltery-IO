package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class TileEntitySlotHandler extends TileSmelteryComponent {
	private final int itemSlotsSize;
	private ItemStackHandler itemInventoryIO;
	protected ItemStackHandler itemInventory;

	protected TileEntitySlotHandler(int itemSlots) {
		itemSlotsSize = itemSlots;
		itemInventory = new ItemStackHandler(itemSlotsSize) {
			@Override
			protected void onContentsChanged(int itemSlots) {
				TileEntitySlotHandler.this.efficientMarkDirty();
				TileEntitySlotHandler.this.onSlotChange(itemSlots);
			}
		};

		itemInventoryIO = new ItemStackHandler(itemSlotsSize) {
			@Override
			protected void onContentsChanged(int itemSlots) {
				TileEntitySlotHandler.this.efficientMarkDirty();
			}

			@Override
			public void setStackInSlot(int itemSlots, @Nonnull ItemStack stack) {
				itemInventory.setStackInSlot(itemSlots, stack);
			}

			@Override
			public int getSlots() {
				return itemInventory.getSlots();
			}

			@Override
			@Nonnull
			public ItemStack getStackInSlot(int itemSlots) {
				return itemInventory.getStackInSlot(itemSlots);
			}

			@Override
			public ItemStack insertItem(int itemSlots, @Nonnull ItemStack stack, boolean simulate) {
				return TileEntitySlotHandler.this.insertItem(itemSlots, stack, simulate);
			}

			@Override
			public ItemStack extractItem(int itemSlots, int amount, boolean simulate) {
				return TileEntitySlotHandler.this.extractItem(itemSlots, amount, simulate);
			}
		};
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("itemInventory", itemInventory.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		itemInventory.deserializeNBT(compound.getCompoundTag("itemInventory"));
		super.readFromNBT(compound);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(facing == null) {
				return (T) itemInventory;
			} else {
				return (T) itemInventoryIO;
			}
		}
		return super.getCapability(capability, facing);
	}

	public ItemStack insertItem(int itemSlots, @Nonnull ItemStack stack, boolean simulate) {
		return stack;
	}

	public ItemStack extractItem(int itemSlots, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	protected void onSlotChange(int itemSlots) {
	}

	protected void consumeItemStack(int slotId, int amount) {
		itemInventory.extractItem(slotId, amount, false);
	}

	public void efficientMarkDirty() {
		world.getChunkFromBlockCoords(getPos()).markDirty();
		this.markContainingBlockForUpdate(null);
	}

	public void markContainingBlockForUpdate(@Nullable IBlockState newState) {
		markBlockForUpdate(getPos(), newState);
	}

	public void markBlockForUpdate(BlockPos pos, @Nullable IBlockState newState) {
		IBlockState state = world.getBlockState(pos);
		if(newState == null) newState = state;
		world.notifyBlockUpdate(pos, state, newState, 3);
		world.notifyNeighborsOfStateChange(pos, newState.getBlock(), true);
	}

}
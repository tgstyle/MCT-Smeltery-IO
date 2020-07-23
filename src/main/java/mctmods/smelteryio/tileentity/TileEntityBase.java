package mctmods.smelteryio.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class TileEntityBase extends TileSmelteryComponent {
	public EnumFacing facing = EnumFacing.NORTH;
	public int progress = 0;
	public int activeCount = 0;
	public int cooldown = 0;
	public int upgradeSize1 = 0;
	public int upgradeSize2 = 0;
	public int upgradeSize3 = 0;
	public int upgradeSize4 = 0;
	public boolean active = false;
	public boolean update = false;
	public boolean smeltery = false;
	public TileSmeltery tileSmeltery;

	private final int itemSlotsSize;
	private ItemStackHandler itemInventoryIO;
	protected ItemStackHandler itemInventory;

	protected TileEntityBase(int itemSlots) {
		itemSlotsSize = itemSlots;
		itemInventory = new ItemStackHandler(itemSlotsSize) {
			@Override
			protected void onContentsChanged(int itemSlots) {
				TileEntityBase.this.efficientMarkDirty();
				TileEntityBase.this.onSlotChange(itemSlots);
			}
		};
		itemInventoryIO = new ItemStackHandler(itemSlotsSize) {
			@Override
			protected void onContentsChanged(int itemSlots) {
				TileEntityBase.this.efficientMarkDirty();
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
				return TileEntityBase.this.insertItem(itemSlots, stack, simulate);
			}

			@Override
			public ItemStack extractItem(int itemSlots, int amount, boolean simulate) {
				return TileEntityBase.this.extractItem(itemSlots, amount, simulate);
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

	public EnumFacing getFacing() {
		return facing;
	}

	public void setFacing(EnumFacing facing) {
		this.facing = facing;
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

	public TileSmeltery getMasterTile() {
		TileSmeltery tileSmeltery = null;
		BlockPos masterPos = getMasterPosition();
		World world = getWorld();
		if(getHasMaster() && masterPos != null && world.getTileEntity(masterPos) instanceof TileSmeltery) tileSmeltery = (TileSmeltery) world.getTileEntity(masterPos);
		return tileSmeltery;
	}

}
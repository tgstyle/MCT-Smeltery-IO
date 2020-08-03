package mctmods.smelteryio.tileentity.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class TileEntityBase extends TileSmelteryComponent {
	public static final String TAG_FACING = "facing";
	public static final String TAG_IS_READY = "isReady";
	public static final String TAG_ACTIVE = "active";
	public static final String TAG_PROGRESS = "progress";
	public static final String TAG_SMELTER = "smeltery";
	public static final String TAG_FUELED = "fueled";
	public static final String TAG_TIME = "time";
	public static final String TAG_SPEED_STACK_SIZE = "speedStackSize";
	public EnumFacing facing = EnumFacing.NORTH;
	public int progress = 0;
	public int time = 0;
	public int activeCount = 0;
	public int cooldown = 0;
	public int upgradeSize1 = 0;
	public int upgradeSize2 = 0;
	public int upgradeSize3 = 0;
	public int upgradeSize4 = 0;
	public int speedStackSize = 0;
	public boolean isReady = false;
	public boolean active = false;
	public boolean smeltery = false;
	public boolean fueled = false;
	public boolean update = false;
	public TileSmeltery tileSmeltery;
	private final int itemSlotsSize;
	private ItemStackHandler itemInventoryIO;
	protected ItemStackHandler itemInventory;

	protected TileEntityBase(int itemSlots) {
		itemSlotsSize = itemSlots;
		itemInventory = new ItemStackHandler(itemSlotsSize) {};
		itemInventoryIO = new ItemStackHandler(itemSlotsSize) {
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
	public void readFromNBT(NBTTagCompound compound) {
		facing = EnumFacing.getFront(compound.getInteger(TAG_FACING));
		isReady = compound.getBoolean(TAG_IS_READY);
		active = compound.getBoolean(TAG_ACTIVE);
		progress = compound.getInteger(TAG_PROGRESS);
		time = compound.getInteger(TAG_TIME);
		smeltery = compound.getBoolean(TAG_SMELTER);
		fueled = compound.getBoolean(TAG_FUELED);
		speedStackSize = compound.getInteger(TAG_SPEED_STACK_SIZE);
		itemInventory.deserializeNBT(compound.getCompoundTag("itemInventory"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_FACING, facing.getIndex());
		compound.setBoolean(TAG_IS_READY, isReady);
		compound.setBoolean(TAG_ACTIVE, active);
		compound.setInteger(TAG_PROGRESS, progress);
		compound.setInteger(TAG_TIME, time);
		compound.setBoolean(TAG_SMELTER, smeltery);
		compound.setBoolean(TAG_FUELED, fueled);
		compound.setInteger(TAG_SPEED_STACK_SIZE, speedStackSize);
		compound.setTag("itemInventory", itemInventory.serializeNBT());
		super.writeToNBT(compound);
		return compound;
	}

	@Override
	public boolean hasCapability(final Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
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

	public int getSlotStackSize(ItemStack itemStack) {
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
			size = count * 2;
			break;
		}
		return size;
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

	public IFluidTank getTankAt(BlockPos pos) {
		TileEntity tileEntity = getWorld().getTileEntity(pos);
		if(tileEntity instanceof TileTank) return ((TileTank) tileEntity).getInternalTank();
		return null;
	}

	public boolean isActive() {
		return active;
	}

	public boolean hasController() {
		return smeltery;
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
	public int getGUIProgress(int pixel) {
		return (int) (((float)activeCount / (float)time) * pixel);
	}

}
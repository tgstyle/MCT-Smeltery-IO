package mctmods.smelteryio.tileentity;

import java.text.DecimalFormat;

import javax.annotation.Nonnull;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.tileentity.container.ContainerFC;
import mctmods.smelteryio.tileentity.container.slots.SlotHandlerItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructure;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class TileEntityFC extends TileEntitySmelteryItemHandler implements ITickable {
	private EnumFacing facing = EnumFacing.NORTH;
	public static final String TAG_FACING = "facing";
	public static final String TAG_PROGRESS = "progress";
	public static final String TAG_ACTIVE_COUNT = "activeCount";
	public static final String TAG_RATIO = "ratio";
	public static final String TAG_TARGET_TEMP = "targetTemp";
	public static final String TAG_CURRENT_TEMP = "currentTemp";
	public static final String TAG_SMELTERY_TEMP = "smelteryTemp";
	public static final String TAG_AT_CAPACITY = "atCapacity";
	public static final int TILEID = 0;
	public static final int PROGRESS = 250;
	private static final int SLOTS_SIZE = 2;
	private static final double FUEL_RATIO = ConfigSIO.fuelControllerRatio;
	private TileSmeltery tileSmeltery;
	private double ratio = 0.01;
	private int cooldown = 0;
	private int upgradeSize1 = 0;
	private int progress = 0;
	private int activeCount = 0;
	private int targetTemp = 0;
	private int currentTemp = 0;
	private int smelteryTemp;
	private boolean heatingItem = false;
	private boolean atCapacity = false;
	private boolean update = false;

	public TileEntityFC() {
		super(SLOTS_SIZE);
	}

	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if(slot == ContainerFC.FUEL) {
			if(SlotHandlerItems.validForSlot(stack, ContainerFC.FUEL, TILEID)) return this.itemInventory.insertItem(slot, stack, simulate);
		}
		if(slot == ContainerFC.UPGRADESPEED) {
			if(SlotHandlerItems.validForSlot(stack, ContainerFC.UPGRADESPEED, TILEID)) return this.itemInventory.insertItem(slot, stack, simulate);
		}
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.facing = EnumFacing.getFront(compound.getInteger(TAG_FACING));
		this.progress = compound.getInteger(TAG_PROGRESS);
		this.activeCount = (compound.getInteger(TAG_ACTIVE_COUNT));
		this.ratio = compound.getDouble(TAG_RATIO);
		this.targetTemp = compound.getInteger(TAG_TARGET_TEMP);
		this.currentTemp = compound.getInteger(TAG_CURRENT_TEMP);
		this.smelteryTemp = compound.getInteger(TAG_SMELTERY_TEMP);
		this.atCapacity = compound.getBoolean(TAG_AT_CAPACITY);
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger(TAG_FACING, facing.getIndex());
		compound.setInteger(TAG_PROGRESS, this.progress);
		compound.setInteger(TAG_ACTIVE_COUNT, this.activeCount);
		compound.setDouble(TAG_RATIO, this.ratio);
		compound.setInteger(TAG_TARGET_TEMP, this.targetTemp);
		compound.setInteger(TAG_CURRENT_TEMP, this.currentTemp);
		compound.setInteger(TAG_SMELTERY_TEMP, this.smelteryTemp);
		compound.setBoolean(TAG_AT_CAPACITY, this.atCapacity);
		return super.writeToNBT(compound);
	}

	@Override
	public void update() {
		if(world.isRemote) return;
		this.update = false;
		if(cooldown % 2 == 0) {
			if(this instanceof TileSmelteryComponent) {
				tileSmeltery = getMasterTile();
		   		if(tileSmeltery != null && tileSmeltery.getTank() != null) {
		   			updateSmelteryHeatingState();
		   			calculateRatio();
		   			calculateTemperature();
		   			burnSolidFuel();
		   		}
			}
		}
		activeCount();
		this.cooldown = (this.cooldown + 1) % 20;
		if(this.update) saveUpdates();
	}

	public EnumFacing getFacing() {
		return this.facing;
	}

	public void setFacing(EnumFacing facing) {
		this.facing = facing;
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

	private IBlockState getState() {
		return world.getBlockState(pos);
	}

	public void markBlockForUpdate(BlockPos pos) {
		world.notifyBlockUpdate(pos, getState(), getState(), 3);
		world.notifyNeighborsOfStateChange(pos, getState().getBlock(), true);
	}

	private void saveUpdates() {
		efficientMarkDirty();
		markBlockForUpdate(getPos());
	}

	private void updateSmelteryHeatingState() {
		this.heatingItem = false;
		this.atCapacity = false;
		for(int item = 0; item < tileSmeltery.getSizeInventory(); item++) {
			ItemStack stack = tileSmeltery.getStackInSlot(item);
		    if(!stack.isEmpty()) {
		    	if(tileSmeltery.hasFuel()) {
		    		if(tileSmeltery.canHeat(item)) {
		    			int temp = tileSmeltery.getTemperature(item);
		    			int neededTemp = tileSmeltery.getTempRequired(item);
		    			float progress = tileSmeltery.getProgress(item);
		    			if(temp != 0 && temp <= neededTemp) this.heatingItem = true;
		    			if(!this.heatingItem && progress >= 2) this.atCapacity = true;
		    		}
		    	}
		    }
		}
		if(this.heatingItem || this.atCapacity && this.activeCount != 0) this.update = true;
		if(this.activeCount == 0 && this.atCapacity) {
			this.atCapacity = false;
			this.update = true;
		}
	}

	private void calculateRatio() {
		ItemStack speedStack = itemInventory.getStackInSlot(ContainerFC.UPGRADESPEED);
		int stackSize1 = speedStack.getCount();
		if(stackSize1 != upgradeSize1) {
			this.ratio = 0.01;
			if(speedStack != ItemStack.EMPTY) {
				this.ratio = (double) speedStack.getCount() / FUEL_RATIO;
				DecimalFormat df = new DecimalFormat("#.##");
				this.ratio = Double.parseDouble(df.format(this.ratio));
			}
			this.upgradeSize1 = stackSize1;
			this.update = true;
		}
	}

	private void calculateTemperature() {
		if(!this.heatingItem) return;
		this.smelteryTemp = getFluidFuelTemp();
		int fuelTempRatio = (int) (getBurnTime() * this.ratio) / 2;
		int fuelTempSolid = (((fuelTempRatio + 99) / 100) * 100) + this.smelteryTemp;
		if(fuelTempSolid >= 200000) fuelTempSolid = 200000;
		this.targetTemp = fuelTempSolid;
	}

	private void burnSolidFuel() {
		if(this.currentTemp == 0 && this.heatingItem && itemInventory.getStackInSlot(ContainerFC.FUEL) != ItemStack.EMPTY && !this.atCapacity) {
			this.currentTemp = this.targetTemp;
			consumeItemStack(ContainerFC.FUEL, 1);
			this.update = true;
		} if(this.currentTemp != 0) {
			if(this.targetTemp != tileSmeltery.getTemperature()) setSmelteryTemp(this.targetTemp);
			this.progress = (this.progress + 1) % PROGRESS;
			this.activeCount = this.progress + 10;
			if(this.progress == 0) {
				this.targetTemp = this.smelteryTemp;
				this.currentTemp = 0;
				resetTemp();
			}
			this.update = true;
		}
	}

	public TileSmeltery getMasterTile() {
		TileSmeltery tileSmeltery = null;
		BlockPos masterPos = getMasterPosition();
		World world = getWorld();
		if(getHasMaster() && masterPos != null && world.getTileEntity(masterPos) instanceof TileSmeltery) tileSmeltery = (TileSmeltery) world.getTileEntity(masterPos);
		return tileSmeltery;
	}

	private int getFluidFuelTemp() {
		if(tileSmeltery == null) return 0;
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

	public int activeCount() {
		if(this.activeCount != 0) this.activeCount--;
		return this.activeCount;
	}

	public boolean isReady() {
		if(this.progress != 0) return true;
		return false;
	}

	public boolean atCapacity() {
		return this.atCapacity;
	}

	public void resetTemp() {
		setSmelteryTemp(this.smelteryTemp);
	}

	public int getFuelTemp() {
		if(currentTemp == 0) return this.smelteryTemp;
		else return this.targetTemp;
	}

	public int getCurrentTemp() {
		return this.currentTemp;
	}

	@SideOnly(Side.CLIENT)
	public double getRatio() {
		return this.ratio;
	}

	@SideOnly(Side.CLIENT)
	public int getGUIProgress(int pixel) {
		return (int) (((float)this.progress / (float)PROGRESS) * pixel);
	}

}
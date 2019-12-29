package mctmods.smelteryio.tileentity.fuildtank;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class TileEntityFluidTank extends FluidTank {
	public TileEntityFluidTank(final TileEntity tileEntity, final int capacity) {
		super(capacity);
		tile = tileEntity;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		notifyBlockUpdate();
		tile.markDirty();
		return super.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		notifyBlockUpdate();
		tile.markDirty();
		return super.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		notifyBlockUpdate();
		tile.markDirty();
		return super.drain(maxDrain, doDrain);
	}

	private void notifyBlockUpdate(){
		World world = tile.getWorld();
		BlockPos pos = tile.getPos();
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
	}

}
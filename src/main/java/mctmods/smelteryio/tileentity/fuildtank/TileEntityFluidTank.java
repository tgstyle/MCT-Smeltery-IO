package mctmods.smelteryio.tileentity.fuildtank;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityFluidTank extends FluidTank {
	public interface TankListener {
		void TankContentsChanged();
	}

	TankListener listener;

	public TileEntityFluidTank(int capacity, @Nonnull TankListener listener) {
		this(null, capacity, listener);
	}

	public TileEntityFluidTank(@Nullable FluidStack fluidStack, int capacity, @Nonnull TankListener listener) {
		super(fluidStack, capacity);
		this.listener = listener;
	}

	public TileEntityFluidTank(Fluid fluid, int amount, int capacity, @Nonnull TankListener listener) {
		this(new FluidStack(fluid, amount), capacity, listener);
	}

	@Override
	protected void onContentsChanged() {
		listener.TankContentsChanged();
		super.onContentsChanged();
	}

}
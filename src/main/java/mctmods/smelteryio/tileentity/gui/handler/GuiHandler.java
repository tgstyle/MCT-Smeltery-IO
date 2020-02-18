package mctmods.smelteryio.tileentity.gui.handler;

import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.container.ContainerCM;
import mctmods.smelteryio.tileentity.container.ContainerFC;
import mctmods.smelteryio.tileentity.container.base.ContainerBase;
import mctmods.smelteryio.tileentity.gui.GuiCM;
import mctmods.smelteryio.tileentity.gui.GuiFC;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static final int FUEL_CONTROLLER = 0, CASTING_MACHINE = 1;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case FUEL_CONTROLLER:
			return new ContainerFC(player.inventory, (TileEntityFC) world.getTileEntity(new BlockPos(x, y, z)));
		case CASTING_MACHINE:
			return new ContainerCM(player.inventory, (TileEntityCM) world.getTileEntity(new BlockPos(x, y, z)));
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
		case FUEL_CONTROLLER:
			return new GuiFC((ContainerBase) getServerGuiElement(ID, player, world, x, y, z), (TileEntityFC) tileEntity);
		case CASTING_MACHINE:
			return new GuiCM((ContainerBase) getServerGuiElement(ID, player, world, x, y, z), (TileEntityCM) tileEntity);
		default:
			return null;
		}
	}

}
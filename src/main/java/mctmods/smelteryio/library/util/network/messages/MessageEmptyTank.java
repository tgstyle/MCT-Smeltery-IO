package mctmods.smelteryio.library.util.network.messages;

import io.netty.buffer.ByteBuf;

import mctmods.smelteryio.library.util.network.messages.base.MessageBase;
import mctmods.smelteryio.tileentity.TileEntityCM;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.network.ByteBufUtils;

public class MessageEmptyTank extends MessageBase<MessageEmptyTank> {
	private NBTTagCompound data;

	public MessageEmptyTank() {

	}

	public MessageEmptyTank(BlockPos pos) {
		data = new NBTTagCompound();
		data.setInteger("x", pos.getX());
		data.setInteger("y", pos.getY());
		data.setInteger("z", pos.getZ());
	}

	@Override
	public void handleClientSide(MessageEmptyTank message, EntityPlayer player) {
	}

	@Override
	public void handleServerSide(MessageEmptyTank message, EntityPlayer player) {
		int x = message.data.getInteger("x");
		int y = message.data.getInteger("y");
		int z = message.data.getInteger("z");
		TileEntity tileEntity = player.world.getTileEntity(new BlockPos(x, y, z));
		if(tileEntity instanceof TileEntityCM) ((TileEntityCM) tileEntity).emptyTank();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data);
	}

}
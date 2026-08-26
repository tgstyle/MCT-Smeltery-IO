package mctmods.smelteryio.network.messages;

import mctmods.smelteryio.network.messages.base.MessageBase;
import mctmods.smelteryio.tileentity.TileEntityCM;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class MessageEmptyTank extends MessageBase<MessageEmptyTank> {
	private long pos;

	public MessageEmptyTank() {}

	public MessageEmptyTank(BlockPos pos) { this.pos = pos.toLong(); }

	@Override public void handleClientSide(MessageEmptyTank message, EntityPlayer player) {}

	@Override public void handleServerSide(MessageEmptyTank message, EntityPlayer player) {
		BlockPos target = BlockPos.fromLong(message.pos);
		if (!player.world.isBlockLoaded(target)) { return; }
		if (player.getDistanceSq(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D) > MAX_REACH_SQ) { return; }
		TileEntity tileEntity = player.world.getTileEntity(target);
		if (tileEntity instanceof TileEntityCM) { ((TileEntityCM) tileEntity).emptyTank(); }
	}

	@Override public void fromBytes(ByteBuf buf) { pos = buf.readLong(); }

	@Override public void toBytes(ByteBuf buf) { buf.writeLong(pos); }
}

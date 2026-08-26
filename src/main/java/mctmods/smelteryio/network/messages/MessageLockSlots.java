package mctmods.smelteryio.network.messages;

import mctmods.smelteryio.network.messages.base.MessageBase;
import mctmods.smelteryio.tileentity.TileEntityCM;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class MessageLockSlots extends MessageBase<MessageLockSlots> {
	private long pos;

	public MessageLockSlots() {}

	public MessageLockSlots(BlockPos pos) { this.pos = pos.toLong(); }

	@Override public void handleClientSide(MessageLockSlots message, EntityPlayer player) {}

	@Override public void handleServerSide(MessageLockSlots message, EntityPlayer player) {
		BlockPos target = BlockPos.fromLong(message.pos);
		if (!player.world.isBlockLoaded(target)) { return; }
		if (player.getDistanceSq(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D) > MAX_REACH_SQ) { return; }
		TileEntity tileEntity = player.world.getTileEntity(target);
		if (tileEntity instanceof TileEntityCM) { ((TileEntityCM) tileEntity).slotsLocked(); }
	}

	@Override public void fromBytes(ByteBuf buf) { pos = buf.readLong(); }

	@Override public void toBytes(ByteBuf buf) { buf.writeLong(pos); }
}

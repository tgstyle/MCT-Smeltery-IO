package mctmods.smelteryio.library.util.network.messages.base;

import mctmods.smelteryio.SmelteryIO;

import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class MessageBase<REQ extends IMessage> implements IMessage, IMessageHandler<REQ, REQ> {
	public MessageBase() {
	}

	public abstract void handleClientSide(REQ message, EntityPlayer player);
	public abstract void handleServerSide(REQ message, EntityPlayer player);

	@Override
	public REQ onMessage(REQ message, MessageContext context) {
		if(context.side == Side.SERVER) {
			handleServerSide(message, context.getServerHandler().player);
		} else {
			handleClientSide(message, SmelteryIO.proxy.getPlayerEntity());
		}
		return null;
	}

}
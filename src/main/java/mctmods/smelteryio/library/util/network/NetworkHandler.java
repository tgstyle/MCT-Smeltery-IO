package mctmods.smelteryio.library.util.network;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.library.util.network.messages.MessageEmptyTank;
import mctmods.smelteryio.library.util.network.messages.MessageLockSlots;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SmelteryIO.MODID);
	public static int discriminator = 0;

	public static void registerNetwork() {
		INSTANCE.registerMessage(MessageEmptyTank.class, MessageEmptyTank.class, discriminator++, Side.SERVER);
		INSTANCE.registerMessage(MessageLockSlots.class, MessageLockSlots.class, discriminator++, Side.SERVER);
	}

	public static void sendToServer(IMessage message) {
		INSTANCE.sendToServer(message);
	}
}

package mctmods.smelteryio.registry;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.tileentity.gui.handler.GuiHandler;

import net.minecraftforge.fml.common.network.NetworkRegistry;

public class RegistryGUI {

	public static void registerGUI() {
		NetworkRegistry.INSTANCE.registerGuiHandler(SmelteryIO.instance, new GuiHandler());
	}

}
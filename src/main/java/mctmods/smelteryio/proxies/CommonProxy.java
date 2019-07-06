package mctmods.smelteryio.proxies;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.library.util.network.NetworkHandler;
import mctmods.smelteryio.registry.RegistryBlock;
import mctmods.smelteryio.registry.RegistryDict;
import mctmods.smelteryio.registry.RegistryGUI;
import mctmods.smelteryio.registry.RegistryRecipes;
import mctmods.smelteryio.registry.RegistryTE;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

 	public void preInit(FMLPreInitializationEvent event) {
 		ConfigSIO.syncConfig();
 		RegistryTE.registerTileEntities();
 		NetworkHandler.registerNetwork();
	}

 	public void init(FMLInitializationEvent event) {
 		RegistryDict.registerDictionary();
 		RegistryRecipes.registerRecipes();
	}

 	public void postInit(FMLPostInitializationEvent event) {
		RegistryBlock.registerTConstruct();
		RegistryGUI.registerGUI();
	}

}

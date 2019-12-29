package mctmods.smelteryio;

import mctmods.smelteryio.library.util.ConfigSIO;
import mctmods.smelteryio.library.util.network.NetworkHandler;
import mctmods.smelteryio.proxies.CommonProxy;
import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.registry.RegistryDict;
import mctmods.smelteryio.registry.RegistryGUI;
import mctmods.smelteryio.registry.RegistryRecipes;
import mctmods.smelteryio.registry.RegistryTE;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
	modid = SmelteryIO.MODID,
	name = SmelteryIO.MODNAME,
	version = SmelteryIO.VERSION,
	acceptedMinecraftVersions = "[1.12.2,1.13)",
	dependencies=
			"required-after:forge@[14.23.0.2486,);" +
			"required-after:tconstruct@[1.12.2-2.7.3.30,);" +
			"after:waila;" +
			"after:jei;")

@EventBusSubscriber
public class SmelteryIO {

 	public static final String MODID = "mctsmelteryio";
 	public static final String MODNAME = "MCT Smeltery IO";
 	public static final String VERSION = "${version}";

 	public static Logger logger = LogManager.getLogger(MODID);

 	@SidedProxy(clientSide = "mctmods.smelteryio.proxies.ClientProxy", serverSide = "mctmods.smelteryio.proxies.CommonProxy")
 	public static CommonProxy proxy;
 	public static Configuration config;

	@Instance(MODID)
	public static SmelteryIO instance = new SmelteryIO();

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		Registry.registerBlocks(event.getRegistry());
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		Registry.registerItems(event.getRegistry());
		Registry.registerItemBlocks(event.getRegistry());
		RegistryDict.registerDictionaryBlocks();
		RegistryDict.registerDictionaryItems();
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		proxy.registerRenders();
	}

 	@EventHandler
 	public void preInit(FMLPreInitializationEvent event) {
 		config = new Configuration(event.getSuggestedConfigurationFile());

 		ConfigSIO.syncConfig();
 		RegistryTE.registerTileEntities();
 		NetworkHandler.registerNetwork();
 		RegistryRecipes.registerRecipes();

 		proxy.preInit();
 	}

 	@EventHandler
 	public void init(FMLInitializationEvent event) {
 		proxy.init();
 	}

 	@EventHandler
 	public void postInit(FMLPostInitializationEvent event) {
		Registry.registerTConstruct();
		RegistryGUI.registerGUI();

 	 	proxy.postInit();
 	}

}
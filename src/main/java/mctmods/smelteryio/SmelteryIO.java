package mctmods.smelteryio;

import mctmods.smelteryio.library.util.LoggerSIO;
import mctmods.smelteryio.proxies.ClientProxy;
import mctmods.smelteryio.proxies.CommonProxy;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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

public class SmelteryIO {

 	public static final String MODID = "mctsmelteryio";
 	public static final String MODNAME = "MCT Smeltery IO";
 	public static final String VERSION = "${version}";

 	@SidedProxy(clientSide = "mctmods.smelteryio.proxies.ClientProxy", serverSide = "mctmods.smelteryio.proxies.CommonProxy")
 	public static CommonProxy proxy;
 	public static ClientProxy proxyClient;
 	public static Configuration config;

	@Instance(MODID)
	public static SmelteryIO instance = new SmelteryIO();

 	@EventHandler
 	public void preInit(FMLPreInitializationEvent event) {

 		LoggerSIO.logger = event.getModLog();

 		config = new Configuration(event.getSuggestedConfigurationFile());

 		proxy.preInit(event);
 	}

 	@EventHandler
 	public void init(FMLInitializationEvent event) {

 		proxy.init(event);
 	}

 	@EventHandler
 	public void postInit(FMLPostInitializationEvent event) {

 	 	proxy.postInit(event);
 	}

}

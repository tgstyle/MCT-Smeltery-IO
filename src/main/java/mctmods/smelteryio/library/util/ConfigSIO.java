package mctmods.smelteryio.library.util;

import mctmods.smelteryio.SmelteryIO;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigSIO {

	public static int powderedFuelBurnTime = 20000;

	public static void syncConfig() {
		Configuration config = SmelteryIO.config;
		try {
			config.load();

	 	 	Property powderedFuelBurnTimeProperty = config.get(Configuration.CATEGORY_GENERAL,
	 	 	 	 	"powderedFuelBurnTime",
	 	 	 	 	"20000",
	 	 	 	 	"The burn time of Powdered Fuel");

	 	 	powderedFuelBurnTime = powderedFuelBurnTimeProperty.getInt();
	 	} catch (Exception e) {
	 		LoggerSIO.error("Config Error %d" + e);
	 	} finally {
	 	 	if (config.hasChanged()) {
	 	 		config.save();
	 	 	}
	 	}
	}

}

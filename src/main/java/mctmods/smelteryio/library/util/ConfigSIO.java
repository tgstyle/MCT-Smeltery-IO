package mctmods.smelteryio.library.util;

import mctmods.smelteryio.SmelteryIO;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigSIO {

	public static int powderedFuelBurnTime;
	public static int snowballBasinAmount;
	public static int snowballCastingAmount;
	public static int castingMachineSpeed;
	public static double fuelControllerRatio;

	private static int powderedFuelBurnTimeCheck = 20000;
	private static int snowballBasinAmountCheck = 8;
	private static int snowballCastingAmountCheck = 1;
	private static int castingMachineSpeedCheck = 2;
	private static double fuelControllerRatioCheck = 4.44;

	public static void syncConfig() {
		Configuration config = SmelteryIO.config;
		try {
			config.load();

	 	 	Property powderedFuelBurnTimeProperty = config.get(Configuration.CATEGORY_GENERAL,
	 	 	 	 	"powderedFuelBurnTime",
	 	 	 	 	"20000",
	 	 	 	 	"The burn time of Powdered Fuel (Valid value 1600-200000) (Default = 20000)");

	 	 	powderedFuelBurnTimeCheck = powderedFuelBurnTimeProperty.getInt();
	 	 	
	 	 	Property snowballBasinAmountProperty = config.get(Configuration.CATEGORY_GENERAL,
	 	 	 	 	"snowballBasinAmount",
	 	 	 	 	"8",
	 	 	 	 	"The Amount of Snowballs used for Basin mode (Valid value 1-16) (Default = 8)");
	 	 	

	 	 	snowballBasinAmountCheck = snowballBasinAmountProperty.getInt();

	 	 	Property snowballCastingAmountProperty = config.get(Configuration.CATEGORY_GENERAL,
	 	 	 	 	"snowballCastingAmount",
	 	 	 	 	"1",
	 	 	 	 	"The Amount of Snowballs used for Casting mode (Valid value 1-16) (Default = 1)");

	 	 	snowballCastingAmountCheck = snowballCastingAmountProperty.getInt();
	 	 	
	 	 	Property castingMachineSpeedProperty = config.get(Configuration.CATEGORY_GENERAL,
	 	 	 	 	"castingMachineSpeed",
	 	 	 	 	"2",
	 	 	 	 	"Casting Machine Speed Upgrade Multiplier for Basin/Casting (Valid value 1-4) (Default = 2)");

	 	 	castingMachineSpeedCheck = castingMachineSpeedProperty.getInt();
	 	 	
	 	 	Property fuelControllerRatioProperty = config.get(Configuration.CATEGORY_GENERAL,
	 	 	 	 	"fuelControllerRatio",
	 	 	 	 	"4.44",
	 	 	 	 	"Fuel Controller Ratio for Temperature/Speed Upgrades (Valid value 1.0-4.44) (Default = 4.44)");

	 	 	fuelControllerRatioCheck = fuelControllerRatioProperty.getDouble();

	 	} catch (Exception e) {
	 		SmelteryIO.logger.error("Config Error %d" + e);
	 	} finally {
	 	 	if (config.hasChanged()) {
	 	 		config.save();
	 	 	}
	 	}

 	 	if (powderedFuelBurnTimeCheck < 1600) {
 	 		powderedFuelBurnTimeCheck = 1600;
 	 	} else if (powderedFuelBurnTimeCheck > 200000) {
 	 		powderedFuelBurnTimeCheck = 200000;
 	 	}
 	 	if (snowballBasinAmountCheck < 1) {
 	 		snowballBasinAmountCheck = 1;
 	 	} else if (snowballBasinAmountCheck > 16) {
 	 		snowballBasinAmountCheck = 16;
 	 	}
 	 	if (snowballCastingAmountCheck < 1) {
 	 		snowballCastingAmountCheck = 1;
 	 	} else if (snowballCastingAmountCheck > 16) {
 	 		snowballCastingAmountCheck = 16;
 	 	}
 	 	if (castingMachineSpeedCheck < 0) {
 	 		castingMachineSpeedCheck = 1;
 	 	} else if (castingMachineSpeedCheck > 4) {
 	 		castingMachineSpeedCheck = 4;
 	 	}
 	 	if (fuelControllerRatioCheck < 1.0) {
 	 		fuelControllerRatioCheck = 1.0;
 	 	} else if (fuelControllerRatioCheck > 4.44) {
 	 		fuelControllerRatioCheck = 4.44;
 	 	}
 	 	
 	 	powderedFuelBurnTime = powderedFuelBurnTimeCheck;
 	 	snowballBasinAmount = snowballBasinAmountCheck;
 	 	snowballCastingAmount = snowballCastingAmountCheck;
 	 	castingMachineSpeed = castingMachineSpeedCheck;
 	 	fuelControllerRatio = fuelControllerRatioCheck;

	}

}
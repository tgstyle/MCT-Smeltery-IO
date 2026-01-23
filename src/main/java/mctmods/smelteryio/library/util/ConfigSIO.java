package mctmods.smelteryio.library.util;

import mctmods.smelteryio.SmelteryIO;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigSIO {
	public static final String CATEGORY_CASTING_MACHINE = "casting machine";
	public static final String CATEGORY_FUEL_CONTROLLER = "fuel controller";

	public static int powderedFuelBurnTime;
	public static int snowballBasinAmount;
	public static int snowballCastingAmount;
	public static int iceballBasinAmount;
	public static int iceballCastingAmount;
	public static int iceballAmountBasin;
	public static int iceballAmountCasting;
	public static int castingMachineSpeed;
	public static int fuelControllerSpeed;
	public static int iceBallStackSize;
	public static double fuelControllerRatio;

	private static int powderedFuelBurnTimeCheck = 24000;
	private static int snowballBasinAmountCheck = 8;
	private static int snowballCastingAmountCheck = 1;
	private static int iceballBasinAmountCheck = 1;
	private static int iceballCastingAmountCheck = 1;
	private static int iceballAmountBasinCheck = 1;
	private static int iceballAmountCastingCheck = 8;
	private static int castingMachineSpeedCheck = 2;
	private static int fuelControllerSpeedCheck = 8;
	private static int iceBallStackSizeCheck = 16;
	private static double fuelControllerRatioCheck = 4.44;

	public static void syncConfig() {
		Configuration config = SmelteryIO.config;

		try {
			config.load();
	 	 	Property powderedFuelBurnTimeProperty = config.get(Configuration.CATEGORY_GENERAL,
	 	 	 	"powderedFuelBurnTime",
	 	 	 	String.valueOf(powderedFuelBurnTimeCheck),
	 	 	 	"The burn time of Powdered Fuel (Valid value 1600-200000) (Default = 24000)");
	 	 	powderedFuelBurnTimeCheck = powderedFuelBurnTimeProperty.getInt();
	 	 	if(powderedFuelBurnTimeCheck < 1600) {
	 	 		powderedFuelBurnTimeCheck = 1600;
	 	 		powderedFuelBurnTimeProperty.setValue(powderedFuelBurnTimeCheck);
	 	 	} else if(powderedFuelBurnTimeCheck > 200000) {
	 	 		powderedFuelBurnTimeCheck = 200000;
	 	 		powderedFuelBurnTimeProperty.setValue(powderedFuelBurnTimeCheck);
	 	 	}
	 	 	powderedFuelBurnTime = powderedFuelBurnTimeCheck;
	 	 	
	 	 	Property iceBallStackProperty = config.get(Configuration.CATEGORY_GENERAL,
		 	 	 	"iceBallStackSize",
		 	 	 	String.valueOf(iceBallStackSizeCheck),
		 	 	 	"Ice Ball stack Size (Valid value 1-64) (Default = 16)");
	 	 	iceBallStackSizeCheck = iceBallStackProperty.getInt();
		 	if(iceBallStackSizeCheck < 0) {
		 		iceBallStackSizeCheck = 1;
		 		iceBallStackProperty.setValue(iceBallStackSizeCheck);
		 	} else if(iceBallStackSizeCheck > 64) {
		 		iceBallStackSizeCheck = 64;
		 		iceBallStackProperty.setValue(iceBallStackSizeCheck);
		 	}
		 	iceBallStackSize = iceBallStackSizeCheck;

	 	 	Property snowballBasinAmountProperty = config.get(CATEGORY_CASTING_MACHINE,
	 	 	 	"snowballBasinAmount",
	 	 	 	String.valueOf(snowballBasinAmountCheck),
	 	 	 	"The Amount of Snowballs used for Basin mode (Valid value 1-16) (Default = 8)");
	 	 	snowballBasinAmountCheck = snowballBasinAmountProperty.getInt();
	 	 	if(snowballBasinAmountCheck < 1) {
	 	 		snowballBasinAmountCheck = 1;
	 	 		snowballBasinAmountProperty.setValue(snowballBasinAmountCheck);
	 	 	} else if(snowballBasinAmountCheck > 16) {
	 	 		snowballBasinAmountCheck = 16;
	 	 		snowballBasinAmountProperty.setValue(snowballBasinAmountCheck);
	 	 	}
	 	 	snowballBasinAmount = snowballBasinAmountCheck;

	 	 	Property snowballCastingAmountProperty = config.get(CATEGORY_CASTING_MACHINE,
	 	 	 	 "snowballCastingAmount",
	 	 	 	 String.valueOf(snowballCastingAmountCheck),
	 	 	 	 "The Amount of Snowballs used for Casting mode (Valid value 1-16) (Default = 1)");
	 	 	snowballCastingAmountCheck = snowballCastingAmountProperty.getInt();
	 	 	if(snowballCastingAmountCheck < 1) {
	 	 		snowballCastingAmountCheck = 1;
	 	 		snowballCastingAmountProperty.setValue(snowballCastingAmountCheck);
	 	 	} else if(snowballCastingAmountCheck > 16) {
	 	 		snowballCastingAmountCheck = 16;
	 	 		snowballCastingAmountProperty.setValue(snowballCastingAmountCheck);
	 	 	}
	 	 	snowballCastingAmount = snowballCastingAmountCheck;

	 	 	Property iceballBasinAmountProperty = config.get(CATEGORY_CASTING_MACHINE,
		 	 	"iceballBasinAmount",
		 	 	String.valueOf(iceballBasinAmountCheck),
		 	 	"The Amount of Iceballs used for Basin mode (Valid value 1-16) (Default = 1)");
		 	iceballBasinAmountCheck = iceballBasinAmountProperty.getInt();
		 	if(iceballBasinAmountCheck < 1) {
		 		iceballBasinAmountCheck = 1;
		 		iceballBasinAmountProperty.setValue(iceballBasinAmountCheck);
		 	} else if(iceballBasinAmountCheck > 16) {
		 		iceballBasinAmountCheck = 16;
		 		iceballBasinAmountProperty.setValue(iceballBasinAmountCheck);
		 	}
		 	iceballBasinAmount = iceballBasinAmountCheck;

		 	Property iceballCastingAmountProperty = config.get(CATEGORY_CASTING_MACHINE,
		 	 	 "iceballCastingAmount",
		 	 	 String.valueOf(iceballCastingAmountCheck),
		 	 	 "The Amount of Iceballs used for Casting mode (Valid value 1-16) (Default = 1)");
		 	iceballCastingAmountCheck = iceballCastingAmountProperty.getInt();
		 	if(iceballCastingAmountCheck < 1) {
		 		iceballCastingAmountCheck = 1;
		 		iceballCastingAmountProperty.setValue(iceballCastingAmountCheck);
		 	} else if(iceballCastingAmountCheck > 16) {
		 		iceballCastingAmountCheck = 16;
		 		iceballCastingAmountProperty.setValue(iceballCastingAmountCheck);
		 	}
		 	iceballCastingAmount = iceballCastingAmountCheck;

	 	 	Property iceballAmountBasinProperty = config.get(CATEGORY_CASTING_MACHINE,
		 	 	"iceballAmountBasin",
		 	 	String.valueOf(iceballAmountBasinCheck),
		 	 	"The Amount of Casts per Iceball used for Basin mode (Valid value 1-16) (Default = 1)");
		 	iceballAmountBasinCheck = iceballAmountBasinProperty.getInt();
		 	if(iceballAmountBasinCheck < 1) {
		 		iceballAmountBasinCheck = 1;
		 		iceballAmountBasinProperty.setValue(iceballAmountBasinCheck);
		 	} else if(iceballAmountBasinCheck > 16) {
		 		iceballAmountBasinCheck = 16;
		 		iceballAmountBasinProperty.setValue(iceballAmountBasinCheck);
		 	}
		 	iceballAmountBasin = iceballAmountBasinCheck;

		 	Property iceballAmountCastingProperty = config.get(CATEGORY_CASTING_MACHINE,
		 	 	 "iceballAmountCasting",
		 	 	 String.valueOf(iceballAmountCastingCheck),
		 	 	 "The Amount of Casts per Iceball used for Casting mode (Valid value 1-16) (Default = 8)");
		 	iceballAmountCastingCheck = iceballAmountCastingProperty.getInt();
		 	if(iceballAmountCastingCheck < 1) {
		 		iceballAmountCastingCheck = 1;
		 		iceballAmountCastingProperty.setValue(iceballAmountCastingCheck);
		 	} else if(iceballAmountCastingCheck > 16) {
		 		iceballAmountCastingCheck = 16;
		 		iceballAmountCastingProperty.setValue(iceballAmountCastingCheck);
		 	}
		 	iceballAmountCasting = iceballAmountCastingCheck;

	 	 	Property castingMachineSpeedProperty = config.get(CATEGORY_CASTING_MACHINE,
	 	 	 	"castingMachineSpeed",
	 	 	 	String.valueOf(castingMachineSpeedCheck),
	 	 	 	"Casting Machine Progress Speed (Valid value 1-4) (Default = 2)");
	 	 	castingMachineSpeedCheck = castingMachineSpeedProperty.getInt();
	 	 	if(castingMachineSpeedCheck < 0) {
	 	 		castingMachineSpeedCheck = 1;
	 	 		castingMachineSpeedProperty.setValue(castingMachineSpeedCheck);
	 	 	} else if(castingMachineSpeedCheck > 4) {
	 	 		castingMachineSpeedCheck = 4;
	 	 		castingMachineSpeedProperty.setValue(castingMachineSpeedCheck);
	 	 	}
	 	 	castingMachineSpeed = castingMachineSpeedCheck;

	 	 	Property fuelControllerSpeedProperty = config.get(CATEGORY_FUEL_CONTROLLER,
	 	 	 	"fuelControllerSpeed",
	 	 	 	String.valueOf(fuelControllerSpeedCheck),
	 	 	 	"Fuel Controller Progress Speed (Valid value 2-16) (Default = 8)");
	 	 	fuelControllerSpeedCheck = fuelControllerSpeedProperty.getInt();
	 	 	if(fuelControllerSpeedCheck < 0) {
	 	 		fuelControllerSpeedCheck = 2;
	 	 		fuelControllerSpeedProperty.setValue(fuelControllerSpeedCheck);
	 	 	} else if(fuelControllerSpeedCheck > 16) {
	 	 		fuelControllerSpeedCheck = 16;
	 	 		fuelControllerSpeedProperty.setValue(fuelControllerSpeedCheck);
	 	 	}
	 	 	fuelControllerSpeed = fuelControllerSpeedCheck;

	 	 	Property fuelControllerRatioProperty = config.get(CATEGORY_FUEL_CONTROLLER,
	 	 	 	"fuelControllerRatio",
	 	 	 	String.valueOf(fuelControllerRatioCheck),
	 	 	 	"Fuel Controller Ratio for Temperature/Speed Upgrades (Valid value 1.0-4.44) (Default = 4.44)");
	 	 	fuelControllerRatioCheck = fuelControllerRatioProperty.getDouble();
	 	 	if(fuelControllerRatioCheck < 1.0) {
	 	 		fuelControllerRatioProperty.setValue(fuelControllerRatioCheck);
	 	 		fuelControllerRatioCheck = 1.0;
	 	 	} else if(fuelControllerRatioCheck > 4.44) {
	 	 		fuelControllerRatioCheck = 4.44;
	 	 		fuelControllerRatioProperty.setValue(fuelControllerRatioCheck);
	 	 	}
	 	 	fuelControllerRatio = fuelControllerRatioCheck;

	 	} catch (Exception e) {
            SmelteryIO.logger.error("Config Error %d{}", String.valueOf(e));
	 	} finally {
	 	 	if(config.hasChanged()) {
	 	 		config.save();
	 	 	}
	 	}
	}

}
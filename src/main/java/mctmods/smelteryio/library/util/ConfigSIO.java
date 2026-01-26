package mctmods.smelteryio.library.util;

import mctmods.smelteryio.SmelteryIO;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigSIO {
	public static final String CATEGORY_CASTING_MACHINE = "casting machine";
	public static final String CATEGORY_FUEL_CONTROLLER = "fuel controller";

	public static int powderedFuelBurnTime;
	public static int castingMachineSpeed;
	public static int fuelControllerSpeed;
	public static int iceBallStackSize;
	public static double fuelControllerRatio;

	public static String[] customCoolants;

	private static int powderedFuelBurnTimeCheck = 24000;
	private static int castingMachineSpeedCheck = 2;
	private static int fuelControllerSpeedCheck = 8;
	private static int iceBallStackSizeCheck = 16;
	private static double fuelControllerRatioCheck = 4.44;

	private static final String[] defaultCoolants = new String[] {
			"minecraft:snowball:0;1,8,1,1;C",
			"smelteryio:iceball:0;1,1,8,1;C"
	};

	public static void syncConfig() {
		Configuration config = SmelteryIO.config;

		try {
			config.load();

			Property powderedFuelBurnTimeProperty = config.get(Configuration.CATEGORY_GENERAL,
					"powderedFuelBurnTime",
					String.valueOf(powderedFuelBurnTimeCheck),
					"The burn time of Powdered Fuel (Valid value 1600-200000) (Default = 24000)");
			powderedFuelBurnTimeCheck = powderedFuelBurnTimeProperty.getInt();
			if (powderedFuelBurnTimeCheck < 1600) {
				powderedFuelBurnTimeCheck = 1600;
				powderedFuelBurnTimeProperty.setValue(powderedFuelBurnTimeCheck);
			} else if (powderedFuelBurnTimeCheck > 200000) {
				powderedFuelBurnTimeCheck = 200000;
				powderedFuelBurnTimeProperty.setValue(powderedFuelBurnTimeCheck);
			}
			powderedFuelBurnTime = powderedFuelBurnTimeCheck;

			Property iceBallStackProperty = config.get(Configuration.CATEGORY_GENERAL,
					"iceBallStackSize",
					String.valueOf(iceBallStackSizeCheck),
					"Ice Ball stack Size (Valid value 1-64) (Default = 16)");
			iceBallStackSizeCheck = iceBallStackProperty.getInt();
			if (iceBallStackSizeCheck < 1) {
				iceBallStackSizeCheck = 1;
				iceBallStackProperty.setValue(iceBallStackSizeCheck);
			} else if (iceBallStackSizeCheck > 64) {
				iceBallStackSizeCheck = 64;
				iceBallStackProperty.setValue(iceBallStackSizeCheck);
			}
			iceBallStackSize = iceBallStackSizeCheck;

			Property castingMachineSpeedProperty = config.get(CATEGORY_CASTING_MACHINE,
					"castingMachineSpeed",
					String.valueOf(castingMachineSpeedCheck),
					"Casting Machine Progress Speed (Valid value 1-4) (Default = 2)");
			castingMachineSpeedCheck = castingMachineSpeedProperty.getInt();
			if (castingMachineSpeedCheck < 1) {
				castingMachineSpeedCheck = 1;
				castingMachineSpeedProperty.setValue(castingMachineSpeedCheck);
			} else if (castingMachineSpeedCheck > 4) {
				castingMachineSpeedCheck = 4;
				castingMachineSpeedProperty.setValue(castingMachineSpeedCheck);
			}
			castingMachineSpeed = castingMachineSpeedCheck;

			Property fuelControllerSpeedProperty = config.get(CATEGORY_FUEL_CONTROLLER,
					"fuelControllerSpeed",
					String.valueOf(fuelControllerSpeedCheck),
					"Fuel Controller Progress Speed (Valid value 2-16) (Default = 8)");
			fuelControllerSpeedCheck = fuelControllerSpeedProperty.getInt();
			if (fuelControllerSpeedCheck < 2) {
				fuelControllerSpeedCheck = 2;
				fuelControllerSpeedProperty.setValue(fuelControllerSpeedCheck);
			} else if (fuelControllerSpeedCheck > 16) {
				fuelControllerSpeedCheck = 16;
				fuelControllerSpeedProperty.setValue(fuelControllerSpeedCheck);
			}
			fuelControllerSpeed = fuelControllerSpeedCheck;

			Property fuelControllerRatioProperty = config.get(CATEGORY_FUEL_CONTROLLER,
					"fuelControllerRatio",
					String.valueOf(fuelControllerRatioCheck),
					"Fuel Controller Ratio for Temperature/Speed Upgrades (Valid value 1.0-4.44) (Default = 4.44)");
			fuelControllerRatioCheck = fuelControllerRatioProperty.getDouble();
			if (fuelControllerRatioCheck < 1.0) {
				fuelControllerRatioCheck = 1.0;
				fuelControllerRatioProperty.setValue(fuelControllerRatioCheck);
			} else if (fuelControllerRatioCheck > 4.44) {
				fuelControllerRatioCheck = 4.44;
				fuelControllerRatioProperty.setValue(fuelControllerRatioCheck);
			}
			fuelControllerRatio = fuelControllerRatioCheck;

			Property customCoolantsProperty = config.get(CATEGORY_CASTING_MACHINE,
					"customCoolants",
					defaultCoolants,
					"Custom cooling items for the Casting Machine.\n" +
							"Format: modid:itemname[:meta];consume_table,consume_basin,casts_table,casts_basin[;C|S]\n" +
							"Use '*' for wildcard metadata. All values must be >=1.\n" +
							"C=circle slot BG (default), S=square slot BG.\n" +
							"Examples:\n" +
							"minecraft:packed_ice:0;1,2,4,2;S\n" +
							"thermalfoundation:material:1025:*;1,1,10,5;C");
			customCoolants = customCoolantsProperty.getStringList();

		} catch (Exception e) {
			SmelteryIO.logger.error("Config Error: {}", e);
		} finally {
			if (config.hasChanged()) {
				config.save();
			}
		}
	}
}

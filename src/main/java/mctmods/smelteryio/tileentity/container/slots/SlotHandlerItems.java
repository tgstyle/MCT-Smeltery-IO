package mctmods.smelteryio.tileentity.container.slots;

import java.util.ArrayList;

import mctmods.smelteryio.items.ItemUpgrade;
import mctmods.smelteryio.items.meta.EnumUpgrade;
import mctmods.smelteryio.tileentity.container.ContainerCM;
import mctmods.smelteryio.tileentity.container.ContainerFC;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotHandlerItems {

    @SuppressWarnings("serial")
    static ArrayList<EnumUpgrade> allowUpgradeList = new ArrayList<EnumUpgrade>() {{
        add(EnumUpgrade.UPGRADE_SLOT1);
        add(EnumUpgrade.UPGRADE_SLOT2);
        add(EnumUpgrade.UPGRADE_SLOT3);
        add(EnumUpgrade.UPGRADE_SLOT4);
        add(EnumUpgrade.UPGRADE_BASIN);
    }};

    @SuppressWarnings("serial")
	static ArrayList<EnumUpgrade> allowSpeedList = new ArrayList<EnumUpgrade>() {{
        add(EnumUpgrade.UPGRADE_SPEED);
    }};

    @SuppressWarnings("serial")
	static ArrayList<EnumUpgrade> allowRedstoneList = new ArrayList<EnumUpgrade>() {{
        add(EnumUpgrade.UPGRADE_REDSTONE);
    }};

    public static boolean validForSlot(ItemStack stack, int slot, int tileID) {
    	if (stack == null || stack.isEmpty()) {
    		return false;
    	}
    	switch(tileID) {
		// Fuel Controller
    	case 0:
			if (slot == ContainerFC.FUEL) {
				if (TileEntityFurnace.isItemFuel(stack)) {
					return true;
				}
			}
			if (slot == ContainerFC.UPGRADESPEED) {
				for (EnumUpgrade type : allowSpeedList) {
					if (stack.getMetadata() == type.getMeta()) {
						return true;
					}
				}
	        }
			break;
		// Casting Machine
		case 1:
			if (slot == ContainerCM.FUEL) {
				if (stack.getItem() instanceof ItemSnowball) {
					return true;
				}
			}
			if (slot == ContainerCM.CAST) {
				if (stack.getItem() instanceof ItemUpgrade) {
					return true;
				}
				if (stack.getItem() instanceof ItemSnowball) {
					return true;
				}
	    	}
			if (slot == ContainerCM.UPGRADE1 || slot == ContainerCM.UPGRADE2) {
				for (EnumUpgrade type : allowUpgradeList) {
			        if (stack.getMetadata() == type.getMeta()) {
			            return true;
			        }
			    }
	    	}
			if (slot == ContainerCM.UPGRADESPEED) {
				for (EnumUpgrade type : allowSpeedList) {
					if (stack.getMetadata() == type.getMeta()) {
						return true;
					}
				}
	        }
			if (slot == ContainerCM.REDSTONE) {
				for (EnumUpgrade type : allowRedstoneList) {
			        if (stack.getMetadata() == type.getMeta()) {
			            return true;
			        }
			    }
	    	}
			if (slot == ContainerCM.OUTPUT) {
				return true;
			}
			break;
    	}
    	return false;
    }

}

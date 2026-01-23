package mctmods.smelteryio.tileentity.container.slots;

import java.util.ArrayList;

import mctmods.smelteryio.items.meta.EnumUpgrade;
import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.TileEntityFC;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotHandlerItems {
	static ArrayList<EnumUpgrade> allowUpgradeList = new ArrayList<EnumUpgrade>() {{
		add(EnumUpgrade.UPGRADE_SLOT1);
		add(EnumUpgrade.UPGRADE_SLOT2);
		add(EnumUpgrade.UPGRADE_SLOT3);
		add(EnumUpgrade.UPGRADE_SLOT4);
		add(EnumUpgrade.UPGRADE_BASIN);
	}};

	static ArrayList<EnumUpgrade> allowSpeedList = new ArrayList<EnumUpgrade>() {{
		add(EnumUpgrade.UPGRADE_SPEED);
	}};

	static ArrayList<EnumUpgrade> allowRedstoneList = new ArrayList<EnumUpgrade>() {{
		add(EnumUpgrade.UPGRADE_REDSTONE);
	}};

	public static boolean validForSlot(ItemStack stack, int slot, int tileID) {
		if(stack == null || stack.isEmpty()) {
			return false;
		}
		switch(tileID) {
		// Fuel Controller
		case 0:
			if(slot == TileEntityFC.SLOTFUEL) {
				if(TileEntityFurnace.isItemFuel(stack)) return true;
			}
			if(slot == TileEntityFC.SLOTUPGRADESPEED) {
				for(EnumUpgrade type : allowSpeedList) {
					if(stack.getItem() == Registry.UPGRADE && stack.getMetadata() == type.ordinal()) return true;
				}
			}
			break;
		// Casting Machine
		case 1:
			if(slot == TileEntityCM.SLOTFUEL) {
				if(stack.getItem() == Items.SNOWBALL || stack.getItem() == Registry.ICEBALL) return true;
			}
			if(slot == TileEntityCM.SLOTCAST) {
                return stack.getItem() != Registry.UPGRADE && stack.getItem() != Items.SNOWBALL && stack.getItem() != Registry.ICEBALL;
            }
			if(slot == TileEntityCM.SLOTUPGRADE1 || slot == TileEntityCM.SLOTUPGRADE2) {
				for(EnumUpgrade type : allowUpgradeList) {
					if(stack.getItem() == Registry.UPGRADE && stack.getMetadata() == type.ordinal()) return true;
				}
			}
			if(slot == TileEntityCM.SLOTUPGRADESPEED) {
				for(EnumUpgrade type : allowSpeedList) {
					if(stack.getItem() == Registry.UPGRADE && stack.getMetadata() == type.ordinal()) return true;
				}
			}
			if(slot == TileEntityCM.SLOTREDSTONE) {
				for(EnumUpgrade type : allowRedstoneList) {
					if(stack.getItem() == Registry.UPGRADE && stack.getMetadata() == type.ordinal()) return true;
				}
			}
			if(slot == TileEntityCM.SLOTOUTPUT) {
				return false;
			}
			break;
		}
		return false;
	}
}

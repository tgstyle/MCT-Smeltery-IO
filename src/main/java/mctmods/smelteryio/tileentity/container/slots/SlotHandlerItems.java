package mctmods.smelteryio.tileentity.container.slots;

import mctmods.smelteryio.items.meta.EnumUpgrade;
import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.util.CoolantHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class SlotHandlerItems {
	public static boolean validForSlot(ItemStack stack, int slot, int tileID) {
		if (stack == null || stack.isEmpty()) { return false; }

		switch (tileID) {
			case 0:
				if (slot == TileEntityFC.SLOTFUEL) { return TileEntityFurnace.isItemFuel(stack); }
				if (slot == TileEntityFC.SLOTUPGRADESPEED) { return isUpgrade(stack, EnumUpgrade.UPGRADE_SPEED); }
				break;
			case 1:
				if (slot == TileEntityCM.SLOTFUEL) { return isCoolant(stack); }
				if (slot == TileEntityCM.SLOTCAST) { return stack.getItem() != Registry.UPGRADE && !isCoolant(stack); }
				if (slot == TileEntityCM.SLOTUPGRADE1 || slot == TileEntityCM.SLOTUPGRADE2) { return isUpgrade(stack, EnumUpgrade.UPGRADE_SLOT1) || isUpgrade(stack, EnumUpgrade.UPGRADE_SLOT2) || isUpgrade(stack, EnumUpgrade.UPGRADE_SLOT3) || isUpgrade(stack, EnumUpgrade.UPGRADE_SLOT4) || isUpgrade(stack, EnumUpgrade.UPGRADE_BASIN); }
				if (slot == TileEntityCM.SLOTUPGRADESPEED) { return isUpgrade(stack, EnumUpgrade.UPGRADE_SPEED); }
				if (slot == TileEntityCM.SLOTREDSTONE) { return isUpgrade(stack, EnumUpgrade.UPGRADE_REDSTONE); }
				if (slot == TileEntityCM.SLOTOUTPUT) { return false; }
				break;
		}
		return false;
	}

	private static boolean isUpgrade(ItemStack stack, EnumUpgrade type) { return stack.getItem() == Registry.UPGRADE && stack.getMetadata() == type.ordinal(); }

	public static boolean isCoolant(ItemStack stack) {
		for (int i = 0; i < CoolantHandler.COOLANTS.size(); i++) {
			if (CoolantHandler.COOLANTS.get(i).matches(stack)) { return true; }
		}
		return false;
	}
}

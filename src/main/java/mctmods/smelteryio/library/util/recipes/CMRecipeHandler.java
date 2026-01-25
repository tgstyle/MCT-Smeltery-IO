package mctmods.smelteryio.library.util.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;

public class CMRecipeHandler {
	public static ICastingRecipe findTableCastingRecipe(ItemStack cast, FluidStack fluidStack) {
		ICastingRecipe recipe = null;
		if (fluidStack != null) {
			recipe = TinkerRegistry.getTableCasting(cast, fluidStack.getFluid());
		}
		return recipe;
	}

	public static ICastingRecipe findBasinCastingRecipe(ItemStack cast, FluidStack fluidStack) {
		ICastingRecipe recipe = null;
		if (fluidStack != null) {
			recipe = TinkerRegistry.getBasinCasting(cast, fluidStack.getFluid());
		}
		return recipe;
	}
}

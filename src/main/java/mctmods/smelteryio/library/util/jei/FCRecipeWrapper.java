package mctmods.smelteryio.library.util.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

import net.minecraft.item.ItemStack;

public class FCRecipeWrapper implements IRecipeWrapper {
	protected final ItemStack input;

	public FCRecipeWrapper(ItemStack itemStack) {
		this.input = itemStack;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, this.input);
	}

}
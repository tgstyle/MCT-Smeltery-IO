package mctmods.smelteryio.library.util.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class CMRecipeHandler implements IRecipeWrapperFactory<CMRecipeWrapper> {
	@Override
	public IRecipeWrapper getRecipeWrapper(CMRecipeWrapper recipe) {
		return recipe;
	}

}
package mctmods.smelteryio.library.util.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

import javax.annotation.Nonnull;

public class CMRecipeHandler implements IRecipeWrapperFactory<CMRecipeWrapper> {
	@Override @Nonnull
	public IRecipeWrapper getRecipeWrapper(@Nonnull CMRecipeWrapper recipe) {
		return recipe;
	}
}

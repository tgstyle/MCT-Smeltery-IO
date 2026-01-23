package mctmods.smelteryio.library.util.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

import javax.annotation.Nonnull;

public class FCRecipeHandler implements IRecipeWrapperFactory<FCRecipeWrapper> {
	@Override @Nonnull public IRecipeWrapper getRecipeWrapper(@Nonnull FCRecipeWrapper recipe) {
		return recipe;
	}
}

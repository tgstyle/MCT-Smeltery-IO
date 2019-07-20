package mctmods.smelteryio.library.util.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

public class FCRecipeHandler implements IRecipeWrapperFactory<FCRecipeWrapper> {
    @Override
    public IRecipeWrapper getRecipeWrapper(FCRecipeWrapper recipe) {
        return recipe;
    }

}

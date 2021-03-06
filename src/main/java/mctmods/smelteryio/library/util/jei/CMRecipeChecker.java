package mctmods.smelteryio.library.util.jei;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import org.apache.commons.lang3.tuple.Triple;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;

public class CMRecipeChecker {
	private static CMRecipeWrapper recipeWrapper;

	public static List<CMRecipeWrapper> getCastingRecipes() {
		List<CMRecipeWrapper> recipes = new ArrayList<>();
		Map<Triple<Item, Item, Fluid>, List<ItemStack>> castDict = Maps.newHashMap();
		List<ICastingRecipe> allRecipes = Lists.newLinkedList();
		allRecipes.addAll(TinkerRegistry.getAllTableCastingRecipes());
		for(ICastingRecipe irecipe : allRecipes) {
			if(irecipe instanceof CastingRecipe) {
				CastingRecipe recipe = (CastingRecipe) irecipe;
				if(recipe.cast != null && recipe.getResult() != null && recipe.getResult().getItem() instanceof Cast) {
					Triple<Item, Item, Fluid> output = Triple.of(recipe.getResult().getItem(), Cast.getPartFromTag(recipe.getResult()), recipe.getFluid().getFluid());
					if(!castDict.containsKey(output)) {
						List<ItemStack> list = Lists.newLinkedList();
						castDict.put(output, list);
						recipeWrapper = new CMRecipeWrapper(list, recipe, JEIPlugin.castingCategory.castingTable);
						if(recipeWrapper.isValid(false)) {
							recipes.add(recipeWrapper);
						}
					}
					castDict.get(output).addAll(recipe.cast.getInputs());
				}
				else {
					recipeWrapper = new CMRecipeWrapper(recipe, JEIPlugin.castingCategory.castingTable);
					if(recipeWrapper.isValid(true)) {
						recipes.add(recipeWrapper);
					}
				}
			}
		}
		for(ICastingRecipe irecipe : TinkerRegistry.getAllBasinCastingRecipes()) {
			if(irecipe instanceof CastingRecipe) {
				CastingRecipe recipe = (CastingRecipe) irecipe;

				recipeWrapper = new CMRecipeWrapper(recipe, JEIPlugin.castingCategory.castingBasin);

				if(recipeWrapper.isValid(true)) {
					recipes.add(recipeWrapper);
				}
			}
		}
		return recipes;
	}

}
package mctmods.smelteryio.library.util.jei;

import javax.annotation.Nonnull;

import mctmods.smelteryio.registry.Registry;
import mctmods.smelteryio.tileentity.gui.GuiCM;
import mctmods.smelteryio.tileentity.gui.GuiFC;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
@mezz.jei.api.JEIPlugin
public class JEI implements IModPlugin {
	public static IJeiHelpers jeiHelpers;
	public static ICraftingGridHelper craftingGridHelper;
	public static IRecipeRegistry recipeRegistry;
	public static CMRecipeCategory castingMachineRecipeCategory;
	public static FCRecipeCategory fuelControllerRecipeCategory;

	@Override public void registerCategories(IRecipeCategoryRegistration registry) {
		final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

		fuelControllerRecipeCategory = new FCRecipeCategory(guiHelper);
		castingMachineRecipeCategory = new CMRecipeCategory(guiHelper);

		registry.addRecipeCategories(
				castingMachineRecipeCategory,
				fuelControllerRecipeCategory
		);
	}

	@Override public void register(@Nonnull IModRegistry registry) {
		jeiHelpers = registry.getJeiHelpers();

		registry.handleRecipes(FCRecipeWrapper.class, new FCRecipeHandler(), FCRecipeCategory.CATEGORY);
		registry.addRecipes(FCRecipeChecker.getFuel(), FCRecipeCategory.CATEGORY);

		registry.handleRecipes(CMRecipeWrapper.class, new CMRecipeHandler(), CMRecipeCategory.CATEGORY);
		registry.addRecipes(CMRecipeChecker.getCastingRecipes(), CMRecipeCategory.CATEGORY);

		registry.addRecipeClickArea(GuiFC.class, 102, 35, 18, 18, FCRecipeCategory.CATEGORY);
		registry.addRecipeClickArea(GuiCM.class, 117, 34, 22, 16, CMRecipeCategory.CATEGORY);

		registry.addRecipeCatalyst(new ItemStack(Registry.MACHINE, 1, 0), FCRecipeCategory.CATEGORY);
		registry.addRecipeCatalyst(new ItemStack(Registry.MACHINE, 1, 1), CMRecipeCategory.CATEGORY);
	}

	@Override public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
		recipeRegistry = jeiRuntime.getRecipeRegistry();
	}
}

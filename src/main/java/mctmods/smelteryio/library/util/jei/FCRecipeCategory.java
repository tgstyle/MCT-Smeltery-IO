package mctmods.smelteryio.library.util.jei;

import com.google.common.collect.Lists;

import java.util.List;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.registry.Registry;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public class FCRecipeCategory implements IRecipeCategory {
	public static String CATEGORY = SmelteryIO.MODID + ":" + "fuel_controller";
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SmelteryIO.MODID, "textures/gui/jei/fuel_controller.png");
	protected final IDrawable background;
	protected final IDrawableAnimated arrow;

	public FCRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, 140, 60);

		IDrawableStatic arrowDrawable = guiHelper.createDrawable(BACKGROUND_TEXTURE, 142, 23, 14, 14);
		arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 100, IDrawableAnimated.StartDirection.TOP, true);
	}

	@Override @Nonnull public String getUid() {
		return CATEGORY;
	}

	@Override @Nonnull public String getTitle() {
		return I18n.format(Registry.MACHINE.getTranslationKey() + ".fuel_controller.name");
	}

	@Override @Nonnull public String getModName() {
		return SmelteryIO.MODNAME;
	}

	@Override @Nonnull public IDrawable getBackground() {
		return background;
	}

	@Override public void drawExtras(@Nonnull Minecraft minecraft) {
		arrow.draw(minecraft, 64, 24);
	}

	@Override public void setRecipe(IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup items = recipeLayout.getItemStacks();
		items.init(0, true, 116, 21);
		items.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
		List<ItemStack> speedUpg = Lists.newLinkedList();
		speedUpg.add(new ItemStack(Registry.UPGRADE, 1, 6));
		items.init(1, false, 7, 21);
		items.set(1, speedUpg);
	}
}

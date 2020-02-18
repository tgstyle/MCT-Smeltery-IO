package mctmods.smelteryio.library.util.jei;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.library.smeltery.CastingRecipe;

public class CMRecipeWrapper implements IRecipeWrapper {
	protected final List<ItemStack> cast;
	protected final List<FluidStack> inputFluid;
	protected List<ItemStack> output;
	public final IDrawable castingBlock;
	private final CastingRecipe recipe;

	public CMRecipeWrapper(List<ItemStack> casts, CastingRecipe recipe, IDrawable castingBlock) {
		this.cast = casts;
		this.recipe = recipe;
		this.inputFluid = ImmutableList.of(recipe.getFluid());
		this.output = ImmutableList.of(recipe.getResult());
		this.castingBlock = castingBlock;
	}

	public CMRecipeWrapper(CastingRecipe recipe, IDrawable castingBlock) {
		if(recipe.cast != null) this.cast = recipe.cast.getInputs();
		else this.cast = ImmutableList.of();
		this.inputFluid = ImmutableList.of(recipe.getFluid());
		this.recipe = recipe;
		this.output = ImmutableList.of(recipe.getResult());
		this.castingBlock = castingBlock;
	}

	public boolean hasCast() {
		return this.recipe.cast != null;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, ImmutableList.of(cast));
		ingredients.setInputs(VanillaTypes.FLUID, inputFluid);
		ingredients.setOutputs(VanillaTypes.ITEM, lazyInitOutput());
	}

	public List<ItemStack> lazyInitOutput() {
		if(this.output == null) {
			if(this.recipe.getResult() == null) return ImmutableList.of();
			this.output = ImmutableList.of(this.recipe.getResult());
		}
		return this.output;
	}

	@Override
	public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		if(this.recipe.consumesCast()) {
			String s = I18n.format("gui.jei.casting.consume");
			int x = 55;
			x -= minecraft.fontRenderer.getStringWidth(s)/2;
			minecraft.fontRenderer.drawString(s, x, 40, 0xaa0000);
		}
	}

	public boolean isValid(boolean checkCast) {
		return !this.inputFluid.isEmpty()
				&& this.inputFluid.get(0) != null
				&& (!checkCast || !this.hasCast()
				|| (!this.cast.isEmpty()
				&& !this.cast.get(0).isEmpty()))
				&& !this.output.isEmpty()
				&& !this.output.get(0).isEmpty();
	}

}
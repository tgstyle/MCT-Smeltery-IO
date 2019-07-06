package mctmods.smelteryio.library.util.plugins.jei;

import com.google.common.collect.Lists;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.registry.RegistryBlock;
import mctmods.smelteryio.registry.RegistryItem;

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

import java.util.List;

@SuppressWarnings("rawtypes")
public class FCRecipeCategory implements IRecipeCategory {

    public static String CATEGORY = SmelteryIO.MODID + ":" + "fuel_controller";
    private static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SmelteryIO.MODID, "textures/gui/jei/fuel_controller.png");

    protected final IDrawable background;
    protected final IDrawableAnimated arrow;

    public FCRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, 140, 60);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(BACKGROUND_TEXTURE, 142, 23, 14, 14);
        this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 100, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public String getUid() {
        return CATEGORY;
    }

    @Override
    public String getTitle() {
        return I18n.format(RegistryBlock.MACHINE.getUnlocalizedName() + ".fuel_controller.name");
    }

    @Override
    public String getModName() {
        return SmelteryIO.MODNAME;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        this.arrow.draw(minecraft, 81, 24);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup items = recipeLayout.getItemStacks();

        items.init(0, true, 57, 22);
        items.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        List<ItemStack> speedUpg = Lists.newLinkedList();
        speedUpg.add(new ItemStack(RegistryItem.UPGRADE, 1, 6));

        items.init(2, false, 3, 22);
        items.set(2, speedUpg);
    }

}

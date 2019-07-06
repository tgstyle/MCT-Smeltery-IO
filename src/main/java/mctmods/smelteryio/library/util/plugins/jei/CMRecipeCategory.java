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
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.library.materials.Material;

import java.util.List;

@SuppressWarnings("rawtypes")
public class CMRecipeCategory implements IRecipeCategory {

    public static String CATEGORY = SmelteryIO.MODID + ":" + "casting_machine";
    private static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SmelteryIO.MODID, "textures/gui/jei/casting_machine.png");

    private final IDrawable background;
    protected final IDrawableAnimated arrow;

    public CMRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, 149, 63);

        IDrawableStatic arrowDrawable = guiHelper.createDrawable(BACKGROUND_TEXTURE, 151, 0, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 30, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public String getUid() {
        return CATEGORY;
    }

    @Override
    public String getTitle() {
        return I18n.format(RegistryBlock.MACHINE.getUnlocalizedName() + ".casting_machine.name");
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
        this.arrow.draw(minecraft, 71, 21);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup items = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

        List<FluidStack> input = ingredients.getInputs(VanillaTypes.FLUID).get(0);

        int cap = input.get(0).amount;

        items.init(0, true, 58, 25);
        items.init(1, false, 113, 24);
        items.set(ingredients);

        fluids.init(0, true, 3, 2, 12, 52, Material.VALUE_Block, false, null);
        fluids.set(ingredients);

        fluids.init(1, true, 3, 2, 12, 52, cap, false, null);
        fluids.set(1, input);

        items.init(0, true, 44, 19);
        items.init(1, false, 104, 20);
        items.set(ingredients);

        List<ItemStack> upgrade = Lists.newLinkedList();
        upgrade.add(new ItemStack(RegistryItem.UPGRADE, 1, 1));
        upgrade.add(new ItemStack(RegistryItem.UPGRADE, 1, 2));
        upgrade.add(new ItemStack(RegistryItem.UPGRADE, 1, 3));
        upgrade.add(new ItemStack(RegistryItem.UPGRADE, 1, 4));

        items.init(2, false, 129, 11);
        items.set(2, upgrade);

        items.init(3, false, 129, 29);
        if (recipeWrapper instanceof CMRecipeWrapper && ((CMRecipeWrapper)recipeWrapper).hasCast()){
            items.set(3, upgrade);
        } else {
            items.set(3, new ItemStack(RegistryItem.UPGRADE, 1, 5));
        }

        fluids.init(0, true, 3, 2, 12, 52, Material.VALUE_Block, false, null);
    }

}

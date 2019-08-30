package mctmods.smelteryio.library.util.jei;

import com.google.common.collect.Lists;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.library.util.ConfigSIO;
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
import net.minecraft.init.Items;
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
        this.arrow.draw(minecraft, 103, 24);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup items = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

        List<FluidStack> input = ingredients.getInputs(VanillaTypes.FLUID).get(0);

        int cap = input.get(0).amount;

        fluids.init(0, true, 5, 5, 12, 52, Material.VALUE_Block, false, null);
        fluids.set(ingredients);

        fluids.init(1, true, 5, 5, 12, 52, cap, false, null);
        fluids.set(1, input);

        items.init(0, true, 41, 22);
        items.init(1, false, 127, 22);
        items.set(ingredients);

        List<ItemStack> upgrade = Lists.newLinkedList();
        upgrade.add(new ItemStack(RegistryItem.UPGRADE, 1, 1));
        upgrade.add(new ItemStack(RegistryItem.UPGRADE, 1, 2));
        upgrade.add(new ItemStack(RegistryItem.UPGRADE, 1, 3));
        upgrade.add(new ItemStack(RegistryItem.UPGRADE, 1, 4));

        items.init(2, true, 41, 4);
        if (recipeWrapper instanceof CMRecipeWrapper && ((CMRecipeWrapper)recipeWrapper).hasCast()){
        	items.set(2, new ItemStack(Items.SNOWBALL, ConfigSIO.snowballCastingAmount));
        } else {
        	items.set(2, new ItemStack(Items.SNOWBALL, ConfigSIO.snowballBasinAmount));
        }

        items.init(3, false, 82, 1);
        items.set(3, upgrade);

        items.init(4, false, 82, 22);
        if (recipeWrapper instanceof CMRecipeWrapper && ((CMRecipeWrapper)recipeWrapper).hasCast()){
            items.set(4, upgrade);
        } else {
            items.set(4, new ItemStack(RegistryItem.UPGRADE, 1, 5));
        }

        fluids.init(0, true, 5, 5, 12, 52, Material.VALUE_Block, false, null);
    }

}

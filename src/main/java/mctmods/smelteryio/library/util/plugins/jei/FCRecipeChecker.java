package mctmods.smelteryio.library.util.plugins.jei;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.List;

public class FCRecipeChecker {

    public static List<FCRecipeWrapper> getFuel() {
        List<FCRecipeWrapper> list = Lists.newLinkedList();
        for (Item item : Item.REGISTRY) {
            ItemStack itemstack = new ItemStack(item);
            if (TileEntityFurnace.isItemFuel(itemstack)){
                list.add(new FCRecipeWrapper(itemstack));
            }
        }
        return list;
    }

}

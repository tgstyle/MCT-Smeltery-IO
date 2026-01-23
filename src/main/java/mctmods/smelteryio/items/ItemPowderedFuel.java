package mctmods.smelteryio.items;

import mctmods.smelteryio.items.base.ItemBase;
import mctmods.smelteryio.library.util.ConfigSIO;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemPowderedFuel extends ItemBase {
	private final int maxSize = 64;

	public ItemPowderedFuel() {
 	 	super("powdered_fuel");
 	 	setMaxDamage(0);
 	 	setMaxStackSize(maxSize);
	}

	@Override public int getItemStackLimit(@Nonnull ItemStack stack) {
		return maxSize;
	}

	@Override public int getItemBurnTime(@Nonnull ItemStack i) { return ConfigSIO.powderedFuelBurnTime; }

	@SideOnly(Side.CLIENT)
 	public void initItemModels() {
   		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
 	}
}

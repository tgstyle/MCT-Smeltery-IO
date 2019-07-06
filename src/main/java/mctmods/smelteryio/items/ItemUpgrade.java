package mctmods.smelteryio.items;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import mctmods.smelteryio.items.base.ItemBase;
import mctmods.smelteryio.items.meta.EnumUpgrade;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUpgrade extends ItemBase {

	public ItemUpgrade() {
 	 	super("upgrade");
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (this.isInCreativeTab(tab)) {
			for (EnumUpgrade type : EnumUpgrade.values()) {
				list.add(new ItemStack(this, 1, type.ordinal()));
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
 	 	return super.getUnlocalizedName() + "." + EnumUpgrade.values()[stack.getMetadata()].getName();
 	}

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumUpgrade.values()[stack.getMetadata()].getRarity();
    }

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return EnumUpgrade.values()[stack.getMetadata()].getMaxSize();
	}

 	@Override
 	public int getMetadata(int damage) {
 	 	return damage;
 	}

	@SideOnly(Side.CLIENT)
	public boolean isShiftKeyDown() {
 	 	return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
 	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (this.isShiftKeyDown()) {
			switch (stack.getItemDamage()) {
				case 0:
					tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.upgrade_base"));
					tooltip.add(TextFormatting.GREEN + I18n.format("sio.tooltips.upgrade_base.usage"));
					break;
				case 1:
					tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.upgrade_slot1"));
					tooltip.add(TextFormatting.GREEN + I18n.format("sio.tooltips.upgrade_slot1.usage"));
					break;
				case 2:
					tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.upgrade_slot2"));
					tooltip.add(TextFormatting.GREEN + I18n.format("sio.tooltips.upgrade_slot2.usage"));
					break;
				case 3:
					tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.upgrade_slot3"));
					tooltip.add(TextFormatting.GREEN + I18n.format("sio.tooltips.upgrade_slot3.usage"));
					break;
				case 4:
					tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.upgrade_slot4"));
					tooltip.add(TextFormatting.GREEN + I18n.format("sio.tooltips.upgrade_slot4.usage"));
					break;
				case 5:
					tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.upgrade_basin"));
					tooltip.add(TextFormatting.GREEN + I18n.format("sio.tooltips.upgrade_basin.usage"));
					break;
				case 6:
					tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.upgrade_speed"));
					tooltip.add(TextFormatting.GREEN + I18n.format("sio.tooltips.upgrade_speed.usage"));
					break;
				case 7:
					tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.upgrade_redstone"));
					tooltip.add(TextFormatting.GREEN + I18n.format("sio.tooltips.upgrade_redstone.usage"));
					break;
			}
		} else {
			tooltip.add(TextFormatting.GOLD + I18n.format("sio.tooltips.common.holdshift"));
		}
	}

	@SideOnly(Side.CLIENT)
 	public void initItemModels() {
 		for (EnumUpgrade variant : EnumUpgrade.values()) {
 			ModelLoader.setCustomModelResourceLocation(this, variant.ordinal(), new ModelResourceLocation(this.getRegistryName() + "/" + variant.getName(), "inventory"));
 		}
 	}

}

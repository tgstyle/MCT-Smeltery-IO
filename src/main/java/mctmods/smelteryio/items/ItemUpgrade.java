package mctmods.smelteryio.items;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

import org.lwjgl.input.Keyboard;

public class ItemUpgrade extends ItemBase {
	public ItemUpgrade() {
		super("upgrade");
		setHasSubtypes(true);
	}

	@Override public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
		if(isInCreativeTab(tab)) {
			for(EnumUpgrade type : EnumUpgrade.values()) {
				list.add(new ItemStack(this, 1, type.ordinal()));
			}
		}
	}

	@Override @Nonnull public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey() + "." + EnumUpgrade.values()[stack.getMetadata()].getName();
	}

	@SuppressWarnings("deprecation")
	@Override @Nonnull public EnumRarity getRarity(ItemStack stack) {
		return EnumUpgrade.values()[stack.getMetadata()].getRarity();
	}

	@Override public int getItemStackLimit(ItemStack stack) {
		return EnumUpgrade.values()[stack.getMetadata()].getMaxSize();
	}

	@Override public int getMetadata(int damage) {
		return damage;
	}

	@SideOnly(Side.CLIENT)
	public boolean isShiftKeyDown() {
		return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
	}

	@SideOnly(Side.CLIENT)
	@Override public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
		if(isShiftKeyDown()) {
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
		for(EnumUpgrade variant : EnumUpgrade.values()) {
			ModelLoader.setCustomModelResourceLocation(this, variant.ordinal(), new ModelResourceLocation(getRegistryName() + "/" + variant.getName(), "inventory"));
		}
	}
}

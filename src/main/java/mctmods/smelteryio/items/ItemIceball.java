package mctmods.smelteryio.items;

import mctmods.smelteryio.entity.EntityIceball;
import mctmods.smelteryio.items.base.ItemBase;
import mctmods.smelteryio.library.util.ConfigSIO;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ItemIceball extends ItemBase {
	private final int maxSize = ConfigSIO.iceBallStackSize;

	public ItemIceball() {
		super("iceball");
		setMaxDamage(0);
		setMaxStackSize(maxSize);
	}

	@Override public int getItemStackLimit(@Nonnull ItemStack stack) {
		return maxSize;
	}

	@Override @Nonnull public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if (!playerIn.capabilities.isCreativeMode) {
			itemstack.shrink(1);
		}
		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
				SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
				0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!worldIn.isRemote) {
			EntityIceball entityiceball = new EntityIceball(worldIn, playerIn);
			entityiceball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entityiceball);
		}

		playerIn.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));
		return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
	}

	@SideOnly(Side.CLIENT)
	public void initItemModels() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Objects.requireNonNull(getRegistryName()), "inventory"));
	}
}

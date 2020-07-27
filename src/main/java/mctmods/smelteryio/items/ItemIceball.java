package mctmods.smelteryio.items;

import mctmods.smelteryio.entity.EntityIceball;
import mctmods.smelteryio.items.base.ItemBase;

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

public class ItemIceball extends ItemBase {
	private int maxSize = 16;

	public ItemIceball() {
 	 	super("iceball");
 	 	setMaxDamage(0);
 	 	setMaxStackSize(maxSize);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return maxSize;
	}

    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if(!playerIn.capabilities.isCreativeMode) itemstack.shrink(1);
        worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if(!worldIn.isRemote) {
        	EntityIceball entityiceball = new EntityIceball(worldIn, playerIn);
        	entityiceball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(entityiceball);
        }
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    }

	@SideOnly(Side.CLIENT)
 	public void initItemModels() {
   		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
 	}

}
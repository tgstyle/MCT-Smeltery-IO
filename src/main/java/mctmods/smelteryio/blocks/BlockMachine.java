package mctmods.smelteryio.blocks;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.blocks.base.BlockBaseTE;
import mctmods.smelteryio.blocks.meta.EnumMachine;
import mctmods.smelteryio.tileentity.TileEntityAD;
import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.TileEntitySI;
import mctmods.smelteryio.tileentity.base.TileEntityBase;
import mctmods.smelteryio.tileentity.gui.handler.GuiHandler;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockMachine extends BlockBaseTE {
	public static final PropertyEnum<EnumMachine> VARIANT = PropertyEnum.create("block", EnumMachine.class);
	public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyInteger ACTIVE = PropertyInteger.create("active", 1, 4);

	public BlockMachine() { super(Material.IRON, MapColor.GRAY, "machine"); }

	@Override @Nonnull protected BlockStateContainer createBlockState() {
		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		builder.add(VARIANT);
		builder.add(ACTIVE);
		builder.add(FACING);
		return builder.build();
	}

	@Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
		for (EnumMachine variant : EnumMachine.values()) list.add(new ItemStack(this, 1, variant.ordinal()));
	}

	@SuppressWarnings("deprecation")
	@Override @Nonnull public IBlockState getStateFromMeta(int meta) {
		int variantIndex = meta & 3;
		int facingIndex = (meta >> 2) & 3;
		EnumFacing facing = EnumFacing.HORIZONTALS[facingIndex];
		return getDefaultState().withProperty(VARIANT, EnumMachine.values()[variantIndex]).withProperty(FACING, facing);
	}

	@Override public int getMetaFromState(IBlockState state) {
		int variant = state.getValue(VARIANT).ordinal();
		int facing = state.getValue(FACING).getHorizontalIndex();
		return variant | (facing << 2);
	}

	@Override public int damageDropped(@Nonnull IBlockState state) { return state.getValue(VARIANT).ordinal(); }

	@Override @Nonnull public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facingBlock, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, @Nonnull EnumHand hand) {
		EnumFacing facing = placer.getHorizontalFacing().getOpposite();
		return getDefaultState().withProperty(VARIANT, EnumMachine.values()[meta & 3]).withProperty(FACING, facing).withProperty(ACTIVE, 1);
	}

	@SuppressWarnings("deprecation")
	@Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
		int mode = 1;
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFC || te instanceof TileEntityCM) {
			TileEntityBase base = (TileEntityBase) te;
			boolean active = base.isActive();
			boolean hasController = base.hasController();
			mode = hasController ? (active ? 4 : 3) : (active ? 2 : 1);
		}
		return state.withProperty(ACTIVE, mode);
	}

	@Override public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
		int variant = meta & 3;
		switch (variant) {
			case 0: return new TileEntityFC();
			case 1: return new TileEntityCM();
			case 2: return new TileEntitySI();
			case 3: return new TileEntityAD();
		}
		return null;
	}

	@Override public boolean onBlockActivated(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntity tileEntity = world.getTileEntity(pos);
			int variant = state.getValue(VARIANT).ordinal();
			if (variant == 0 && tileEntity instanceof TileEntityFC) {
				((TileEntityFC)tileEntity).guiOpen();
				player.openGui(SmelteryIO.instance, GuiHandler.FUEL_CONTROLLER, world, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
			if (variant == 1 && tileEntity instanceof TileEntityCM) {
				((TileEntityCM)tileEntity).guiOpen();
				player.openGui(SmelteryIO.instance, GuiHandler.CASTING_MACHINE, world, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		return false;
	}

	@Override public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity != null) {
			if (tileEntity instanceof TileEntityFC && ((TileEntityFC)tileEntity).getMasterTile() != null) ((TileEntityFC)tileEntity).resetSmeltery();
			if (((TileEntityBase)tileEntity).getMasterTile() != null) ((TileEntityBase)tileEntity).notifyMasterOfChange();
			IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if (handler != null) {
				for (int slot = 0; slot < handler.getSlots(); slot++) {
					ItemStack stack = handler.getStackInSlot(slot);
					if (!stack.isEmpty()) InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
		}
		super.breakBlock(world, pos, state);
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntityFC && ((TileEntityBase)tileEntity).active) {
			EnumFacing facing = state.getValue(FACING);
			double d0 = pos.getX() + 0.5D;
			double d1 = pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
			double d2 = pos.getZ() + 0.5D;
			double d4 = rand.nextDouble() * 0.6D - 0.3D;
			if (rand.nextDouble() < 0.1D) world.playSound(d0, pos.getY(), d2, net.minecraft.init.SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			switch (facing) {
				case WEST: world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D); world.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D); break;
				case EAST: world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D); world.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D); break;
				case NORTH: world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D); world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D); break;
				case SOUTH: world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D); world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D); break;
			}
		}
		if (tileEntity instanceof TileEntityCM && ((TileEntityBase)tileEntity).active) {
			world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.25F, 0.1F, false);
			world.spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + 0.5D, pos.getY() + 0.99D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.99D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.0D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.99D, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.0D, 0.0D, 0.0D, 0.0D);
		}
	}

	@SideOnly(Side.CLIENT)
	public void initItemBlockModels() {
		ModelLoader.setCustomStateMapper(this, new BlockStateMachine());
		for (EnumMachine variant : EnumMachine.values()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), variant.ordinal(), new ModelResourceLocation(SmelteryIO.MODID + ":" + variant.getName(), "inventory"));
		}
	}

	@SideOnly(Side.CLIENT)
	public static class BlockStateMachine extends StateMapperBase {
		@Override @Nonnull protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			String block = state.getValue(VARIANT).getName();
			String builder = ACTIVE.getName() + "=" + state.getValue(ACTIVE) + "," + FACING.getName() + "=" + state.getValue(FACING).getName();
			ResourceLocation baseLocation = new ResourceLocation(SmelteryIO.MODID + ":" + block);
			return new ModelResourceLocation(baseLocation, builder);
		}
	}
}

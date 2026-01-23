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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;

public class BlockMachine extends BlockBaseTE {
	public static final PropertyEnum<EnumMachine> VARIANT = PropertyEnum.create("block", EnumMachine.class);
	public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing");
	public static final PropertyInteger ACTIVE = PropertyInteger.create("active", 1, 4);

	public BlockMachine() {
		super(Material.IRON, MapColor.GRAY, "machine");
	}

	@Override @Nonnull protected BlockStateContainer createBlockState() {
		BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
		builder.add(VARIANT);
		builder.add(ACTIVE);
		builder.add(FACING);
		return builder.build();
	}

	@Override public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list)	{
		for(EnumMachine variant : EnumMachine.values()) list.add(new ItemStack(this, 1, variant.ordinal()));
	}

	@SuppressWarnings("deprecation")
	@Override @Nonnull public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, EnumMachine.values()[meta]);
	}

	@Override public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Override public int damageDropped(@Nonnull IBlockState state) {
		return getMetaFromState(state);
	}

	public String getRecipeOreDict1(IBlockState state) {
		return state.getValue(VARIANT).getRecipeOreDict1();
	}

	public String getRecipeOreDict2(IBlockState state) {
		return state.getValue(VARIANT).getRecipeOreDict2();
	}

	@Override public int getLightValue(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
		return state.getValue(VARIANT).getLight();
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return state.getValue(VARIANT).getHarvestLevel();
	}

	@SuppressWarnings("deprecation")
	@Override public float getBlockHardness(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
		return state.getValue(VARIANT).getHardness();
	}

	@Override public float getExplosionResistance(World world, @Nonnull BlockPos pos, Entity exploder, @Nonnull Explosion explosion) {
		return world.getBlockState(pos).getValue(VARIANT).getResistance() / 5F;
	}

	public EnumFacing getFacing(IBlockAccess world, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TileEntityBase) return ((TileEntityBase)tileEntity).getFacing();
		return null;
	}

	public void setFacing(IBlockAccess world, BlockPos pos, EnumFacing facing, IBlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TileEntityBase) ((TileEntityBase)tileEntity).setFacing(facing);
	}

	@Override
	public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
		setFacing(world, pos, EnumFacing.getDirectionFromEntityLiving(pos, placer), state);
	}

	@SuppressWarnings("deprecation")
	@Override @Nonnull public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		int meta = getMetaFromState(state);
		if(tileEntity instanceof TileEntityFC || tileEntity instanceof TileEntityCM) {
			int mode = 1;
			if(((TileEntityBase)tileEntity).isActive() && ((TileEntityBase)tileEntity).hasController()) mode = 4;
			if(!((TileEntityBase)tileEntity).isActive() && ((TileEntityBase)tileEntity).hasController()) mode = 3;
			if(((TileEntityBase)tileEntity).isActive() && !((TileEntityBase)tileEntity).hasController()) mode = 2;
			if(!((TileEntityBase)tileEntity).isActive() && !((TileEntityBase)tileEntity).hasController()) mode = 1;
			return state.withProperty(VARIANT, EnumMachine.values()[meta]).withProperty(ACTIVE, mode).withProperty(FACING, getFacing(world, pos, state));
		}
		return state.withProperty(VARIANT, EnumMachine.values()[meta]).withProperty(FACING, getFacing(world, pos, state));
	}

	@Override public TileEntity createNewTileEntity(@Nonnull World world, int meta) {
		switch(meta) {
		case 0:
			return new TileEntityFC();
		case 1:
			return new TileEntityCM();
		case 2:
			return new TileEntitySI();
		case 3:
			return new TileEntityAD();
		}
		return null;
	}

	@Override public boolean onBlockActivated(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			TileEntity tileEntity = world.getTileEntity(pos);
			int meta = getMetaFromState(state);
			switch(meta) {
			case 0:
                assert tileEntity != null;
                ((TileEntityFC)tileEntity).guiOpen();
				player.openGui(SmelteryIO.instance, GuiHandler.FUEL_CONTROLLER, world, pos.getX(), pos.getY(), pos.getZ());
				return true;
			case 1:
                assert tileEntity != null;
                ((TileEntityCM)tileEntity).guiOpen();
				player.openGui(SmelteryIO.instance, GuiHandler.CASTING_MACHINE, world, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		return false;
	}

	@Override public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);
		ItemStack stack;
        assert tileEntity != null;
        IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		if(tileEntity instanceof TileEntityFC) {
			if(((TileEntityFC)tileEntity).getMasterTile() != null) ((TileEntityFC)tileEntity).resetSmeltery();
		}
		if(((TileEntityBase)tileEntity).getMasterTile() != null) ((TileEntityBase)tileEntity).notifyMasterOfChange();
		if(handler != null) {
			for(int slot = 0; slot < handler.getSlots(); slot++) {
				stack = handler.getStackInSlot(slot);
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
			}
		}
		world.removeTileEntity(pos);
		super.breakBlock(world, pos, state);
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TileEntityFC) {
			if(((TileEntityBase)tileEntity).active) {
				EnumFacing enumfacing = getFacing(world, pos, state);
				double d0 = (double)pos.getX() + 0.5D;
				double d1 = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
				double d2 = (double)pos.getZ() + 0.5D;
				double d4 = rand.nextDouble() * 0.6D - 0.3D;
				if(rand.nextDouble() < 0.1D) world.playSound((double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				switch (enumfacing) {
					case WEST:
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
						world.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
						break;
					case EAST:
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
						world.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52D, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
						break;
					case NORTH:
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
						world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52D, 0.0D, 0.0D, 0.0D);
						break;
					case SOUTH:
						world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
						world.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52D, 0.0D, 0.0D, 0.0D);
					default:
						break;
				}
			}
		}
		if(tileEntity instanceof TileEntityCM) {
			if(((TileEntityBase)tileEntity).active) {
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.25F, 0.1F, false);
				world.spawnParticle(Objects.requireNonNull(EnumParticleTypes.getParticleFromId(12)), pos.getX() + .5D , pos.getY() + .99D, pos.getZ() + .5D , 0.0, 0.0, 0.0);
				world.spawnParticle(Objects.requireNonNull(EnumParticleTypes.getParticleFromId(11)), pos.getX() + .99D, pos.getY() + .5D , pos.getZ() + .5D , 0.0, 0.0, 0.0);
				world.spawnParticle(Objects.requireNonNull(EnumParticleTypes.getParticleFromId(11)), pos.getX() + .0D , pos.getY() + .5D , pos.getZ() + .5D , 0.0, 0.0, 0.0);
				world.spawnParticle(Objects.requireNonNull(EnumParticleTypes.getParticleFromId(11)), pos.getX() + .5D , pos.getY() + .5D , pos.getZ() + .99D, 0.0, 0.0, 0.0);
				world.spawnParticle(Objects.requireNonNull(EnumParticleTypes.getParticleFromId(11)), pos.getX() + .5D , pos.getY() + .5D , pos.getZ() + .0D , 0.0, 0.0, 0.0);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void initItemBlockModels() {
		for(EnumMachine variant : EnumMachine.values()) {
			ModelLoader.setCustomStateMapper(this, new BlockStateMachine());
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), variant.ordinal(), new ModelResourceLocation(SmelteryIO.MODID + ":" + variant.getName()));
		}
	}

	@SideOnly(Side.CLIENT)
	public static class BlockStateMachine extends StateMapperBase {
		@Override @Nonnull protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			String block = state.getValue(VARIANT).getName();
            String builder = ACTIVE.getName() + "=" + state.getValue(ACTIVE) + "," + FACING.getName() + "=" + state.getValue(FACING);
			ResourceLocation baseLocation = new ResourceLocation(SmelteryIO.MODID + ":" + block);
			return new ModelResourceLocation(baseLocation, builder);
		}
	}
}

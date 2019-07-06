package mctmods.smelteryio.blocks;

import java.util.Random;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.blocks.meta.EnumMachine;
import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.gui.handler.GuiHandler;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
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

import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class BlockMachine extends BlockBaseTE {

	public static final PropertyEnum<EnumMachine> VARIANT = PropertyEnum.create("blocks", EnumMachine.class);
	public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing");
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockMachine() {
        super(Material.IRON, MapColor.GRAY, "machine");
    }

    @Override
    protected BlockStateContainer createBlockState() {
    	BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
    	builder.add(VARIANT);
    	builder.add(FACING);
    	builder.add(ACTIVE);
    	return builder.build();
    }

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)	{
		for (EnumMachine variant : EnumMachine.values()) {
			list.add(new ItemStack(this, 1, variant.getMeta()));
		}
    }

	@Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumMachine.values()[meta]);
    }

	@Override
    public int getMetaFromState(IBlockState state) {
    	return ((EnumMachine)state.getValue(VARIANT)).getMeta();
    }

    @Override
    public int damageDropped(IBlockState state) {
    	return getMetaFromState(state);
    }

    public String getRecipeOreDict1(IBlockState state) {
    	return ((EnumMachine)state.getValue(VARIANT)).getRecipeOreDict1();
    }

    public String getRecipeOreDict2(IBlockState state) {
    	return ((EnumMachine)state.getValue(VARIANT)).getRecipeOreDict2();
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ((EnumMachine)state.getValue(VARIANT)).getLight();
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return ((EnumMachine)state.getValue(VARIANT)).getHarvestLevel();
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        return ((EnumMachine)state.getValue(VARIANT)).getHardness();
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        return ((EnumMachine)world.getBlockState(pos).getValue(VARIANT)).getResistance() / 5F;
    }

	public EnumFacing getFacing(IBlockAccess world, BlockPos pos, IBlockState state) {
		int meta = ((EnumMachine)state.getValue(VARIANT)).getMeta();
		switch(meta) {
		case 0:
			final TileEntityFC tileEntity0 = (TileEntityFC)world.getTileEntity(pos);
			return tileEntity0 != null ? tileEntity0.getFacing() : EnumFacing.NORTH;
		case 1:
			final TileEntityCM tileEntity1 = (TileEntityCM)world.getTileEntity(pos);
			return tileEntity1 != null ? tileEntity1.getFacing() : EnumFacing.NORTH;
		default:
			return null;
		}
	}

	public void setFacing(IBlockAccess world, BlockPos pos, EnumFacing facing, IBlockState state) {
		int meta = ((EnumMachine)state.getValue(VARIANT)).getMeta();
		switch(meta) {
		case 0:
			final TileEntityFC tileEntity0 = (TileEntityFC)world.getTileEntity(pos);
			tileEntity0.setFacing(facing);
			break;
		case 1:
			final TileEntityCM tileEntity1 = (TileEntityCM)world.getTileEntity(pos);
			tileEntity1.setFacing(facing);
			break;
		}
	}

    @Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		setFacing(world, pos, EnumFacing.getDirectionFromEntityLiving(pos, placer), state);
	}

    @Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
    	boolean active = false;
    	int meta = ((EnumMachine)state.getValue(VARIANT)).getMeta();
		switch(meta) {
		case 0:
			TileEntityFC tileEntity0 = (TileEntityFC)world.getTileEntity(pos);
			if (tileEntity0.activeCount() !=0) {active = true;}
			return state.withProperty(FACING, getFacing(world, pos, state)).withProperty(ACTIVE, active);
		case 1:
			TileEntityCM tileEntity1 = (TileEntityCM)world.getTileEntity(pos);
			if (tileEntity1.activeCount() !=0) {active = true;}
			return state.withProperty(FACING, getFacing(world, pos, state)).withProperty(ACTIVE, active);
		}
		return state;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch(meta) {
		case 0:
			return new TileEntityFC();
		case 1:
			return new TileEntityCM();
		default:
			return null;
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
		int meta = getMetaFromState(state);
		switch(meta) {
		case 0:
			if (!world.isRemote) {
                if (isActive(world, pos)) {
                	player.openGui(SmelteryIO.instance, GuiHandler.FUEL_CONTROLLER, world, pos.getX(), pos.getY(), pos.getZ());
                }
			}
			return true;
		case 1:
			if (!world.isRemote) {
                player.openGui(SmelteryIO.instance, GuiHandler.CASTING_MACHINE, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		default:
			return false;
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		int meta = getMetaFromState(state);
		switch(meta) {
		case 0:
			TileEntityFC tileEntity0 = (TileEntityFC)world.getTileEntity(pos);
			IItemHandler handler0 = tileEntity0.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			for (int slot = 0; slot < handler0.getSlots(); slot++) {
				ItemStack stack = handler0.getStackInSlot(slot);
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
			}
			if (world.isRemote) {
				if (tileEntity0 instanceof TileSmelteryComponent) {
					((TileSmelteryComponent)tileEntity0).notifyMasterOfChange();
				}
				if (tileEntity0 instanceof TileEntityFC) {
					((TileEntityFC)tileEntity0).resetTemp();
				}
			}
			break;
		case 1:
			TileEntityCM tileEntity1 = (TileEntityCM)world.getTileEntity(pos);
			IItemHandler handler1 = tileEntity1.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			for (int slot = 0; slot < handler1.getSlots(); slot++) {
				ItemStack stack = handler1.getStackInSlot(slot);
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
			}
			break;
		}
		world.removeTileEntity(pos);
		super.breakBlock(world, pos, state);
	}

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (world.isRemote) {
            return true;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileSmelteryComponent) {
            ((TileSmelteryComponent)tileEntity).notifyMasterOfChange();
        }
        if (tileEntity instanceof TileEntityFC) {
            ((TileEntityFC)tileEntity).resetTemp();
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    private boolean isActive(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityFC) {
            TileEntityFC tileEntityFC = (TileEntityFC)tileEntity;
            TileSmeltery tileSmeltery = tileEntityFC.getMasterTile();
            if (tileSmeltery != null) {
                return tileEntityFC.getHasMaster() && tileSmeltery.isActive();
            }

        }
        return false;
    }

    @SuppressWarnings("incomplete-switch")
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
    	if (state.getValue(VARIANT).getName() == "fuel_controller") {
    		TileEntityFC tileEntity = (TileEntityFC)world.getTileEntity(pos);
    		if (tileEntity.activeCount() !=0) {
    			EnumFacing enumfacing = getFacing(world, pos, state);
    			double d0 = (double)pos.getX() + 0.5D;
    			double d1 = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
    			double d2 = (double)pos.getZ() + 0.5D;
    			double d4 = rand.nextDouble() * 0.6D - 0.3D;

    			if (rand.nextDouble() < 0.1D) {
    				world.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
    			}
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
    			}
    		}
    	}
    	if (state.getValue(VARIANT).getName() == "casting_machine") {
    		TileEntityCM tileEntity = (TileEntityCM)world.getTileEntity(pos);
    		if (tileEntity.activeCount() !=0) {	
    			world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.25F, 0.1F, false);
				world.spawnParticle(EnumParticleTypes.getParticleFromId(12), pos.getX() + .5D , pos.getY() + .99D, pos.getZ() + .5D , 0.0, 0.0, 0.0);
				world.spawnParticle(EnumParticleTypes.getParticleFromId(11), pos.getX() + .99D, pos.getY() + .5D , pos.getZ() + .5D , 0.0, 0.0, 0.0);
				world.spawnParticle(EnumParticleTypes.getParticleFromId(11), pos.getX() + .0D , pos.getY() + .5D , pos.getZ() + .5D , 0.0, 0.0, 0.0);
				world.spawnParticle(EnumParticleTypes.getParticleFromId(11), pos.getX() + .5D , pos.getY() + .5D , pos.getZ() + .99D, 0.0, 0.0, 0.0);
				world.spawnParticle(EnumParticleTypes.getParticleFromId(11), pos.getX() + .5D , pos.getY() + .5D , pos.getZ() + .0D , 0.0, 0.0, 0.0);
    		}
    	}	
    }

    @SideOnly(Side.CLIENT)
    public void initItemBlockModels() {
		for (EnumMachine variant : EnumMachine.values()) {
			ModelLoader.setCustomStateMapper(this, new BlockStateMachine());
    		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), variant.getMeta(), new ModelResourceLocation(Item.getItemFromBlock(this).getRegistryName(), "blocks=" + variant.getName()));
		}
    }

    @SideOnly(Side.CLIENT)
    public class BlockStateMachine extends StateMapperBase {

		@Override
    	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			String block = ((EnumMachine)state.getValue(VARIANT)).getName();
			StringBuilder builder = new StringBuilder();
			
			
			builder.append(ACTIVE.getName());
			builder.append("=");
			builder.append(state.getValue(ACTIVE));
			builder.append(",");
			builder.append(FACING.getName());
			builder.append("=");
			builder.append(state.getValue(FACING));

			ResourceLocation baseLocation = new ResourceLocation(SmelteryIO.MODID + ":" + block);

			return new ModelResourceLocation(baseLocation, builder.toString());
    	}
    }

}

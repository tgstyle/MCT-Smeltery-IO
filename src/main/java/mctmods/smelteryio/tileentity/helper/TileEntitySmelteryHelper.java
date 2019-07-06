package mctmods.smelteryio.tileentity.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructure;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;

public class TileEntitySmelteryHelper {

    private World world;
    private BlockPos scPos;

    public TileEntitySmelteryHelper(World world, BlockPos scPos) {
        this.world = world;
        this.scPos = scPos;
    }

    public void setTemp(int temp) {
        if (this.world != null && scPos != null) {
            TileEntity tileEntity = this.world.getTileEntity(scPos);
            if (tileEntity instanceof TileSmeltery) {
                TileSmeltery tileSC = (TileSmeltery) tileEntity;
                try {
                    @SuppressWarnings("rawtypes")
                    Class[] oParam = new Class[2];
                    oParam[0] = Integer.TYPE;
                    oParam[1] = Integer.TYPE;
                    Object[] mParam = {new Integer(0), new Integer(temp)};
                    Method addFuelMethod = TileHeatingStructure.class.getDeclaredMethod("addFuel", oParam);
                    addFuelMethod.setAccessible(true);
                    addFuelMethod.invoke(tileSC, mParam);
                } catch (NoSuchMethodException exception) {
                    exception.printStackTrace();
                } catch (SecurityException exception) {
                    exception.printStackTrace();
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                } catch (IllegalArgumentException exception) {
                    exception.printStackTrace();
                } catch (InvocationTargetException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

}

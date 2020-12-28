package mctmods.smelteryio.registry;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.tileentity.TileEntityAD;
import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.TileEntitySI;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class RegistryTE {

	@SuppressWarnings("deprecation")
	public static void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityFC.class, SmelteryIO.MODID + "TileEntityFC");
		GameRegistry.registerTileEntity(TileEntityCM.class, SmelteryIO.MODID + "TileEntityCM");
		GameRegistry.registerTileEntity(TileEntitySI.class, SmelteryIO.MODID + "TileEntitySI");
		GameRegistry.registerTileEntity(TileEntityAD.class, SmelteryIO.MODID + "TileEntityAD");
	}

}
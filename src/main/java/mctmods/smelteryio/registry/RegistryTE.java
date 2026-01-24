package mctmods.smelteryio.registry;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.tileentity.TileEntityAD;
import mctmods.smelteryio.tileentity.TileEntityCM;
import mctmods.smelteryio.tileentity.TileEntityFC;
import mctmods.smelteryio.tileentity.TileEntitySI;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class RegistryTE {
	public static void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityFC.class, new ResourceLocation(SmelteryIO.MODID, "fc"));
		GameRegistry.registerTileEntity(TileEntityCM.class, new ResourceLocation(SmelteryIO.MODID, "cm"));
		GameRegistry.registerTileEntity(TileEntitySI.class, new ResourceLocation(SmelteryIO.MODID, "si"));
		GameRegistry.registerTileEntity(TileEntityAD.class, new ResourceLocation(SmelteryIO.MODID, "ad"));
	}
}

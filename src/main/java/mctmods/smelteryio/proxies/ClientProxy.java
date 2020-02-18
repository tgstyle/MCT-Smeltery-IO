package mctmods.smelteryio.proxies;

import mctmods.smelteryio.registry.Registry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenders() {
		Registry.initModels();
	}

	@Override
 	public void preInit() {
 	 	super.preInit();
	}

	@Override
 	public void init() {
 	 	super.init();
	}

	@Override
 	public void postInit() {
 	 	super.postInit();
	}

	@Override
	public EntityPlayer getPlayerEntity(){
		return Minecraft.getMinecraft().player;
	}

}
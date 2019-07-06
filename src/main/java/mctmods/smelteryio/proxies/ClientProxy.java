package mctmods.smelteryio.proxies;

import mctmods.smelteryio.registry.RegistryBlock;
import mctmods.smelteryio.registry.RegistryItem;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
 	public void preInit(FMLPreInitializationEvent event) {
 	 	super.preInit(event);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
 	public void init(FMLInitializationEvent event) {
 	 	super.init(event);
	}

	@Override
 	public void postInit(FMLPostInitializationEvent event) {
 	 	super.postInit(event);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {

		// blocks
		RegistryBlock.initModels();

		// items
		RegistryItem.initModels();

	}

    public EntityPlayer getPlayerEntity(){
        return Minecraft.getMinecraft().player;
    }

}

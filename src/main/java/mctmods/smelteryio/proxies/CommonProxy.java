package mctmods.smelteryio.proxies;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.entity.EntityIceball;
import mctmods.smelteryio.registry.Registry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CommonProxy {
	public void registerRenders() {
	}

 	public void preInit() {
 		EntityRegistry.registerModEntity(new ResourceLocation(SmelteryIO.MODID, "textures/items/iceball.png"), EntityIceball.class, Registry.ICEBALL.getUnlocalizedName(), 0, SmelteryIO.instance, 64, 10, true);
	}

 	public void init() {
	}

 	public void postInit() {
	}

	public EntityPlayer getPlayerEntity(){
		return null;
	}

}
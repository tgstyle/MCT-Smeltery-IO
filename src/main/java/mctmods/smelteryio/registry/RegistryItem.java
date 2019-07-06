package mctmods.smelteryio.registry;

import java.util.HashSet;
import java.util.Set;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.items.ItemPowderedFuel;
import mctmods.smelteryio.items.ItemUpgrade;

import net.minecraft.item.Item;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@GameRegistry.ObjectHolder(SmelteryIO.MODID)
public class RegistryItem {

 	@GameRegistry.ObjectHolder("powdered_fuel")
 	public static final ItemPowderedFuel POWDERED_FUEL = new ItemPowderedFuel();

 	@GameRegistry.ObjectHolder("upgrade")
 	public static final ItemUpgrade UPGRADE = new ItemUpgrade();

 	private static final Item[] item = {
 		new ItemPowderedFuel(),
 		new ItemUpgrade()
   };


   @EventBusSubscriber
   public static class RegistrationHandler {
 	   public static final Set<Item> ITEMS = new HashSet<>();

 	   @SubscribeEvent
 	   public static void registerItem(RegistryEvent.Register<Item> event) {
 	 	   final IForgeRegistry<Item> registry = event.getRegistry();
 	 	   for (Item iS : item) {
 	 	 	   registry.register(iS);
 	 	 	   ITEMS.add(iS);
 	 	   }
 	   }
   }

   @SideOnly(Side.CLIENT)
   public static void initModels() {
 		POWDERED_FUEL.initItemModels();
 		UPGRADE.initItemModels();
 	}

}

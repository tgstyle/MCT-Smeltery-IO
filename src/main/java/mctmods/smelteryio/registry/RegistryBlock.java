package mctmods.smelteryio.registry;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.blocks.BlockMachine;
import mctmods.smelteryio.itemblocks.ItemBlockMachine;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import slimeknights.tconstruct.smeltery.TinkerSmeltery;

@GameRegistry.ObjectHolder(SmelteryIO.MODID)
public class RegistryBlock {

	@GameRegistry.ObjectHolder("machine")
 	public static final BlockMachine MACHINE = new BlockMachine();

 	private static final Block[] blocks = {
 		MACHINE
 	};

	 private static final ItemBlock[] itemBlocks = {
 		new ItemBlockMachine(MACHINE)
 	};

	public static void registerTConstruct () {

		ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
 	 	for (Block block : TinkerSmeltery.validSmelteryBlocks) {
 	 	 	  builder.add(block);
 	 	}
 	 	builder.add(RegistryBlock.MACHINE);
 	 	TinkerSmeltery.validSmelteryBlocks = builder.build();
	}

	@EventBusSubscriber
 	public static class RegistrationHandler {
 	 	public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();

 	 	@SubscribeEvent
 	 	public static void registerBlocks(RegistryEvent.Register<Block> event) {
 	 	 	final IForgeRegistry<Block> registry = event.getRegistry();
 	 	 	registry.registerAll(blocks);
 	 	}

 	 	@SubscribeEvent
 	 	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
 	 	 	final IForgeRegistry<Item> registry = event.getRegistry();
 	 	 	for (ItemBlock iB : itemBlocks) {
 	 	 	 	registry.register(iB.setRegistryName(iB.getBlock().getRegistryName()));
 	 	 	 	ITEM_BLOCKS.add(iB);
 	 	 	}
 	 	}

 	}

 	@SideOnly(Side.CLIENT)
 	public static void initModels() {
 		MACHINE.initItemBlockModels();
 	}

}

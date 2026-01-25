package mctmods.smelteryio.registry;

import com.google.common.collect.ImmutableSet;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.blocks.BlockMachine;
import mctmods.smelteryio.entity.EntityIceball;
import mctmods.smelteryio.itemblocks.ItemBlockMachine;
import mctmods.smelteryio.items.ItemIceball;
import mctmods.smelteryio.items.ItemPowderedFuel;
import mctmods.smelteryio.items.ItemUpgrade;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.Objects;

@GameRegistry.ObjectHolder(SmelteryIO.MODID)
public class Registry {
	// blocks
	public static final BlockMachine MACHINE = new BlockMachine();

	// items
	public static final ItemPowderedFuel POWDERED_FUEL = new ItemPowderedFuel();
	public static final ItemIceball ICEBALL = new ItemIceball();
	public static final ItemUpgrade UPGRADE = new ItemUpgrade();

	// blocks
	private static final Block[] block = { MACHINE };

	// items
	private static final Item[] item = {
			POWDERED_FUEL,
			ICEBALL,
			UPGRADE
	};

	// itemblocks
	private static final ItemBlock[] itemblock = { new ItemBlockMachine(MACHINE) };

	public static void registerBlocks(IForgeRegistry<Block> registry) {
		for (Block block : block) {
			registry.register(block);
			SmelteryIO.logger.info("Added block: {}", block.getRegistryName());
		}
	}

	public static void registerItems(IForgeRegistry<Item> registry) {
		for (Item item : item) {
			registry.register(item);
			SmelteryIO.logger.info("Added item: {}", item.getRegistryName());
		}
	}

	public static void registerItemBlocks(IForgeRegistry<Item> registry) {
		for (ItemBlock item_block : itemblock) {
			registry.register(item_block.setRegistryName(Objects.requireNonNull(item_block.getBlock().getRegistryName())));
			SmelteryIO.logger.info("Added itemblock: {}", item_block.getBlock().getRegistryName());
		}
	}

	public static void registerEntities() {
		EntityIceball.registerEntity();
	}

	public static void registerTConstruct() {
		ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
		for (Block block : TinkerSmeltery.validSmelteryBlocks) {
			builder.add(block);
		}
		builder.add(MACHINE);
		TinkerSmeltery.validSmelteryBlocks = builder.build();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		// blocks
		MACHINE.initItemBlockModels();
		// items
		POWDERED_FUEL.initItemModels();
		ICEBALL.initItemModels();
		UPGRADE.initItemModels();
		// entities
		EntityIceball.registerRenderer();
	}
}

package com.aetherium;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
	private ModBlocks() {}

	public static final Block AETHERITE_RESIDUE_ORE = register("aetherite_residue_ore", Block::new,
			BlockBehaviour.Properties.of().strength(4.5F, 6.0F).sound(SoundType.DEEPSLATE).requiresCorrectToolForDrops());
	public static final Block NETHER_CATALYST_VEIN = register("nether_catalyst_vein", Block::new,
			BlockBehaviour.Properties.of().strength(3.5F, 6.0F).sound(SoundType.NETHER_ORE).requiresCorrectToolForDrops());
	public static final Block END_STABILIZER_DEPOSIT = register("end_stabilizer_deposit", Block::new,
			BlockBehaviour.Properties.of().strength(4.0F, 9.0F).sound(SoundType.STONE).requiresCorrectToolForDrops());

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory,
			BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Aetherium.id(name));
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Aetherium.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.setId(blockKey)));
		Registry.register(BuiltInRegistries.ITEM, itemKey,
				new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey)));
		return block;
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(tab -> {
			tab.accept(AETHERITE_RESIDUE_ORE.asItem());
			tab.accept(NETHER_CATALYST_VEIN.asItem());
			tab.accept(END_STABILIZER_DEPOSIT.asItem());
		});
	}
}

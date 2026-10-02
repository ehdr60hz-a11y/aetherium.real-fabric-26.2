package com.aetherium;

import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public final class ModItems {
	private ModItems() {}

	public static final Item RAW_AETHERITE_RESIDUE = simple("raw_aetherite_residue");
	public static final Item FRACTURED_RESIDUE = simple("fractured_residue");
	public static final Item PURIFIED_AETHERITE = simple("purified_aetherite");
	public static final Item NETHER_CATALYST = simple("nether_catalyst");
	public static final Item END_STABILIZER = simple("end_stabilizer");

	public static final Item DENSE_ALLOY = simple("dense_alloy");
	public static final Item LIGHT_ALLOY = simple("light_alloy");
	public static final Item CONDUCTIVE_ALLOY = simple("conductive_alloy");
	public static final Item STABLE_ALLOY = simple("stable_alloy");
	public static final Item RESONANT_ALLOY = simple("resonant_alloy");

	public static final Item RESONANCE_EXTRACTOR = register("resonance_extractor", Item::new,
			new Item.Properties().pickaxe(ToolMaterial.DIAMOND, 1.0F, -2.8F));

	private static Item simple(String name) {
		return register(name, Item::new, new Item.Properties());
	}

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Aetherium.id(name));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(tab -> {
			tab.accept(RAW_AETHERITE_RESIDUE);
			tab.accept(FRACTURED_RESIDUE);
			tab.accept(PURIFIED_AETHERITE);
			tab.accept(NETHER_CATALYST);
			tab.accept(END_STABILIZER);
			tab.accept(DENSE_ALLOY);
			tab.accept(LIGHT_ALLOY);
			tab.accept(CONDUCTIVE_ALLOY);
			tab.accept(STABLE_ALLOY);
			tab.accept(RESONANT_ALLOY);
		});
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
				.register(tab -> tab.accept(RESONANCE_EXTRACTOR));
	}
}

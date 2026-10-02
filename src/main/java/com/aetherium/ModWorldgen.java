package com.aetherium;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModWorldgen {
	private ModWorldgen() {}

	private static ResourceKey<PlacedFeature> placed(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, Aetherium.id(name));
	}

	public static void initialize() {
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
				GenerationStep.Decoration.UNDERGROUND_ORES, placed("aetherite_residue_ore"));
		BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.BASALT_DELTAS),
				GenerationStep.Decoration.UNDERGROUND_DECORATION, placed("nether_catalyst_vein"));
		BiomeModifications.addFeature(
				BiomeSelectors.foundInTheEnd().and(BiomeSelectors.excludeByKey(Biomes.THE_END)),
				GenerationStep.Decoration.UNDERGROUND_ORES, placed("end_stabilizer_deposit"));
	}
}

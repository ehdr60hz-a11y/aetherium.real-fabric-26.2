package com.aetherium;

import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class AetheriumArmorMaterial {
    private AetheriumArmorMaterial() {}

    public static final int BASE_DURABILITY = 80;
    public static final ResourceKey<EquipmentAsset> ASSET_KEY =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Aetherium.id("aetherium_vessel"));
    public static final TagKey<Item> REPAIR_TAG =
            TagKey.create(BuiltInRegistries.ITEM.key(), Aetherium.id("repairs_aetherium_vessel"));

    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            BASE_DURABILITY,
            Map.of(
                    ArmorType.HELMET, 4,
                    ArmorType.CHESTPLATE, 9,
                    ArmorType.LEGGINGS, 7,
                    ArmorType.BOOTS, 4
            ),
            18,
            SoundEvents.ARMOR_EQUIP_IRON,
            4.0F,
            0.20F,
            REPAIR_TAG,
            ASSET_KEY
    );
}

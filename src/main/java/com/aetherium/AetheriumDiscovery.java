package com.aetherium;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public final class AetheriumDiscovery {
    public static final ResourceKey<Item> ANCIENT_MEMORY_FRAGMENT_KEY =
            ResourceKey.create(Registries.ITEM, Aetherium.id("ancient_memory_fragment"));

    public static final Item ANCIENT_MEMORY_FRAGMENT = Registry.register(
            BuiltInRegistries.ITEM,
            ANCIENT_MEMORY_FRAGMENT_KEY,
            new Item(new Item.Properties().setId(ANCIENT_MEMORY_FRAGMENT_KEY))
    );

    private AetheriumDiscovery() {}

    public static void initialize() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (pos.getY() > -32) return;
            if (!state.is(Blocks.DEEPSLATE) && !state.is(Blocks.TUFF)) return;
            if (has(serverPlayer, ANCIENT_MEMORY_FRAGMENT)) return;

            if (serverPlayer.getRandom().nextInt(64) != 0) return;

            serverPlayer.getInventory().add(new ItemStack(ANCIENT_MEMORY_FRAGMENT));
            serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.fragment_found"));
            serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.fragment_hint"));
        });
    }

    public static boolean has(ServerPlayer player, Item item) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).is(item)) return true;
        }
        return false;
    }

    public static boolean consume(ServerPlayer player, Item item) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(item)) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static void give(ServerPlayer player, Item item) {
        player.getInventory().add(new ItemStack(item));
    }
}

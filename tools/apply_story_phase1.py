#!/usr/bin/env python3
"""Applies Aetherium Story/Discovery Phase 1 after the legacy generator has created the project.

This first layer intentionally does not reveal Aetherium in Creative. A player working in
very deep stone can discover an ancient fragment. Later phases will turn fragments into a
multi-stage research chain.
"""
from pathlib import Path
import json, struct, zlib

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "src/main/java/com/aetherium"
RES = ROOT / "src/main/resources"

DISCOVERY_JAVA = r'''package com.aetherium;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/** First layer of the Aetherium discovery chain. The material is never named here. */
public final class AetheriumDiscovery {
    private static final ResourceKey<Item> ANCIENT_FRAGMENT_KEY =
            ResourceKey.create(Registries.ITEM, Aetherium.id("ancient_memory_fragment"));

    public static final Item ANCIENT_MEMORY_FRAGMENT = Registry.register(
            BuiltInRegistries.ITEM,
            ANCIENT_FRAGMENT_KEY,
            new Item.Properties().setId(ANCIENT_FRAGMENT_KEY)
    );

    private AetheriumDiscovery() {}

    public static void initialize() {
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            if (pos.getY() > -40) return;
            if (!state.is(Blocks.DEEPSLATE) && !state.is(Blocks.TUFF)) return;

            for (int slot = 0; slot < serverPlayer.getInventory().getContainerSize(); slot++) {
                if (serverPlayer.getInventory().getItem(slot).is(ANCIENT_MEMORY_FRAGMENT)) return;
            }

            ItemStack fragment = new ItemStack(ANCIENT_MEMORY_FRAGMENT);
            if (serverPlayer.getInventory().add(fragment)) {
                serverPlayer.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable("message.aetherium.fragment_found")
                );
            }
        });
    }
}
'''


def write_png(path: Path):
    path.parent.mkdir(parents=True, exist_ok=True)
    w = h = 16
    px = []
    for y in range(h):
        row = []
        for x in range(w):
            transparent = not (3 <= x <= 12 and 2 <= y <= 13)
            if transparent:
                row.append((0, 0, 0, 0))
                continue
            edge = x in (3, 12) or y in (2, 13)
            if edge:
                c = (42, 44, 50, 255)
            else:
                # Aged graphite/stone fragment with one almost-hidden teal mark.
                c = (76, 78, 82, 255)
            if (x, y) in {(7, 6), (8, 6), (8, 7), (8, 8)}:
                c = (49, 112, 105, 255)
            row.append(c)
        px.append(row)

    raw = b"".join(b"\x00" + b"".join(bytes(p) for p in row) for row in px)
    def chunk(kind, data):
        return struct.pack(">I", len(data)) + kind + data + struct.pack(">I", zlib.crc32(kind + data) & 0xffffffff)
    data = b"\x89PNG\r\n\x1a\n"
    data += chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    data += chunk(b"IDAT", zlib.compress(raw, 9))
    data += chunk(b"IEND", b"")
    path.write_bytes(data)


def patch_json(path: Path, updates: dict):
    data = json.loads(path.read_text(encoding="utf-8")) if path.exists() else {}
    data.update(updates)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main():
    SRC.mkdir(parents=True, exist_ok=True)
    RES.mkdir(parents=True, exist_ok=True)

    (SRC / "AetheriumDiscovery.java").write_text(DISCOVERY_JAVA, encoding="utf-8")

    main_java = SRC / "Aetherium.java"
    text = main_java.read_text(encoding="utf-8")
    if "AetheriumDiscovery.initialize();" not in text:
        text = text.replace("\t\tModWorldgen.initialize();", "\t\tModWorldgen.initialize();\n\t\tAetheriumDiscovery.initialize();")
        main_java.write_text(text, encoding="utf-8")

    patch_json(
        RES / "assets/aetherium/lang/en_us.json",
        {
            "item.aetherium.ancient_memory_fragment": "Ancient Memory Fragment",
            "message.aetherium.fragment_found": "Something ancient was embedded in the stone."
        },
    )
    patch_json(
        RES / "assets/aetherium/lang/fa_ir.json",
        {
            "item.aetherium.ancient_memory_fragment": "تکه‌خاطره‌ی باستانی",
            "message.aetherium.fragment_found": "چیزی باستانی در دل سنگ پنهان بود."
        },
    )
    patch_json(
        RES / "assets/aetherium/models/item/ancient_memory_fragment.json",
        {
            "parent": "minecraft:item/generated",
            "textures": {"layer0": "aetherium:item/ancient_memory_fragment"},
        },
    )
    patch_json(
        RES / "assets/aetherium/items/ancient_memory_fragment.json",
        {"model": {"type": "minecraft:model", "model": "aetherium:item/ancient_memory_fragment"}},
    )
    write_png(RES / "assets/aetherium/textures/item/ancient_memory_fragment.png")

    print("Aetherium Story Phase 1 applied: first hidden discovery clue added.")


if __name__ == "__main__":
    main()

package com.aetherium;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class AetheriumExpansion {
    private AetheriumExpansion() {}

    public static final Block ANCIENT_RESEARCH_BRICKS = registerBlock(
            "ancient_research_bricks", Block::new,
            BlockBehaviour.Properties.of().strength(5.0F, 12.0F));

    public static final Block RESEARCH_CONSOLE = registerBlock(
            "research_console", p -> new LoreBlock(p, Stage.RESEARCH),
            BlockBehaviour.Properties.of().strength(4.0F, 10.0F).lightLevel(s -> 3));

    public static final Block RESONANCE_FURNACE = registerBlock(
            "resonance_furnace", p -> new LoreBlock(p, Stage.FURNACE),
            BlockBehaviour.Properties.of().strength(5.0F, 12.0F).lightLevel(s -> 5));

    public static final Block CATALYST_ALTAR = registerBlock(
            "catalyst_altar", p -> new LoreBlock(p, Stage.CATALYST),
            BlockBehaviour.Properties.of().strength(6.0F, 18.0F).lightLevel(s -> 5));

    public static final Block STABILIZATION_PILLAR = registerBlock(
            "stabilization_pillar", p -> new LoreBlock(p, Stage.STABILIZER),
            BlockBehaviour.Properties.of().strength(6.0F, 18.0F).lightLevel(s -> 7));

    public static final Block AETHERIC_ASSEMBLY_STATION = registerBlock(
            "aetheric_assembly_station", p -> new LoreBlock(p, Stage.ASSEMBLY),
            BlockBehaviour.Properties.of().strength(6.0F, 18.0F).lightLevel(s -> 6));

    public static final Block AETHERIUM_FRAME = registerBlock(
            "aetherium_frame", Block::new,
            BlockBehaviour.Properties.of().strength(7.0F, 24.0F));

    public static final Block AETHERIUM_BLOCK = registerBlock(
            "aetherium_block", Block::new,
            BlockBehaviour.Properties.of().strength(8.0F, 30.0F).lightLevel(s -> 4));

    public static final Item RESEARCH_FRAGMENT = simple("research_fragment");
    public static final Item CIPHER_PLATE = simple("cipher_plate");
    public static final Item CATALYST_SHARD = simple("catalyst_shard");
    public static final Item STABILIZER_SHARD = simple("stabilizer_shard");
    public static final Item AETHERIUM_BLUEPRINT = simple("aetherium_blueprint");
    public static final Item AETHERIC_CORE = simple("aetheric_core");
    public static final Item RESONANT_CELL = simple("resonant_cell");

    public static final Item VESSEL_HELMET = register("aetherium_vessel_helmet",
            Item::new, new Item.Properties().humanoidArmor(AetheriumArmorMaterial.INSTANCE,
                    net.minecraft.world.item.equipment.ArmorType.HELMET)
                    .durability(net.minecraft.world.item.equipment.ArmorType.HELMET.getDurability(AetheriumArmorMaterial.BASE_DURABILITY)));
    public static final Item VESSEL_CHESTPLATE = register("aetherium_vessel_chestplate",
            Item::new, new Item.Properties().humanoidArmor(AetheriumArmorMaterial.INSTANCE,
                    net.minecraft.world.item.equipment.ArmorType.CHESTPLATE)
                    .durability(net.minecraft.world.item.equipment.ArmorType.CHESTPLATE.getDurability(AetheriumArmorMaterial.BASE_DURABILITY)));
    public static final Item VESSEL_LEGGINGS = register("aetherium_vessel_leggings",
            Item::new, new Item.Properties().humanoidArmor(AetheriumArmorMaterial.INSTANCE,
                    net.minecraft.world.item.equipment.ArmorType.LEGGINGS)
                    .durability(net.minecraft.world.item.equipment.ArmorType.LEGGINGS.getDurability(AetheriumArmorMaterial.BASE_DURABILITY)));
    public static final Item VESSEL_BOOTS = register("aetherium_vessel_boots",
            Item::new, new Item.Properties().humanoidArmor(AetheriumArmorMaterial.INSTANCE,
                    net.minecraft.world.item.equipment.ArmorType.BOOTS)
                    .durability(net.minecraft.world.item.equipment.ArmorType.BOOTS.getDurability(AetheriumArmorMaterial.BASE_DURABILITY)));

    public static final ToolMaterial AETHERIUM_TOOL_MATERIAL = new ToolMaterial(
            net.minecraft.tags.BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            2_000_000_000,
            12.0F,
            3.0F,
            30,
            AetheriumArmorMaterial.REPAIR_TAG
    );

    public static final Item AETHERIUM_EVERPICK = register(
            "aetherium_everpick",
            Item::new,
            new Item.Properties().pickaxe(AETHERIUM_TOOL_MATERIAL, 5.0F, -2.4F)
    );

    private static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> factory,
                                       BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Aetherium.id(name));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Aetherium.id(name));
        Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(properties.setId(key)));
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new net.minecraft.world.item.BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(itemKey)));
        return block;
    }

    private static Item simple(String name) {
        return register(name, Item::new, new Item.Properties());
    }

    private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Aetherium.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(tab -> {
            tab.accept(RESEARCH_FRAGMENT);
            tab.accept(CIPHER_PLATE);
            tab.accept(CATALYST_SHARD);
            tab.accept(STABILIZER_SHARD);
            tab.accept(AETHERIUM_BLUEPRINT);
            tab.accept(AETHERIC_CORE);
            tab.accept(RESONANT_CELL);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(tab -> {
            tab.accept(VESSEL_HELMET);
            tab.accept(VESSEL_CHESTPLATE);
            tab.accept(VESSEL_LEGGINGS);
            tab.accept(VESSEL_BOOTS);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(tab -> {
            tab.accept(ANCIENT_RESEARCH_BRICKS.asItem());
            tab.accept(AETHERIUM_FRAME.asItem());
            tab.accept(AETHERIUM_BLOCK.asItem());
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(tab -> {
            tab.accept(AETHERIUM_EVERPICK);
        });

        ServerTickEvents.END_SERVER_TICK.register(AetheriumExpansion::tickProgression);
    }

    private static final Set<UUID> OVERWORLD_STRUCTURES = new HashSet<>();
    private static final Set<UUID> NETHER_STRUCTURES = new HashSet<>();
    private static final Set<UUID> END_STRUCTURES = new HashSet<>();

    private static void tickProgression(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.level().dimension() == Level.OVERWORLD
                    && AetheriumDiscovery.has(player, AetheriumDiscovery.ANCIENT_MEMORY_FRAGMENT)
                    && !AetheriumDiscovery.has(player, RESEARCH_FRAGMENT)
                    && OVERWORLD_STRUCTURES.add(player.getUUID())) {
                buildResearchVault((ServerLevel) player.level(), player.blockPosition().offset(28, -6, 28));
                player.sendSystemMessage(Component.translatable("message.aetherium.vault_trace"));
            }

            if (player.level().dimension() == Level.NETHER
                    && AetheriumDiscovery.has(player, RESEARCH_FRAGMENT)
                    && !AetheriumDiscovery.has(player, CATALYST_SHARD)
                    && NETHER_STRUCTURES.add(player.getUUID())) {
                buildShrine((ServerLevel) player.level(), player.blockPosition().offset(24, 0, 16));
                player.sendSystemMessage(Component.translatable("message.aetherium.catalyst_trace"));
            }

            if (player.level().dimension() == Level.END
                    && AetheriumDiscovery.has(player, CATALYST_SHARD)
                    && !AetheriumDiscovery.has(player, STABILIZER_SHARD)
                    && END_STRUCTURES.add(player.getUUID())) {
                buildStabilizationSite((ServerLevel) player.level(), player.blockPosition().offset(24, 0, 16));
                player.sendSystemMessage(Component.translatable("message.aetherium.stabilizer_trace"));
            }
        }
    }

    private static void buildResearchVault(ServerLevel level, BlockPos origin) {
        for (int x = -5; x <= 5; x++) for (int y = -2; y <= 3; y++) for (int z = -5; z <= 5; z++) {
            BlockPos p = origin.offset(x, y, z);
            Block b = (x == -5 || x == 5 || z == -5 || z == 5 || y == -2 || y == 3)
                    ? ANCIENT_RESEARCH_BRICKS : net.minecraft.world.level.block.Blocks.AIR;
            level.setBlock(p, b.defaultBlockState(), 3);
        }
        level.setBlock(origin.offset(0, -1, 0), RESEARCH_CONSOLE.defaultBlockState(), 3);
        level.setBlock(origin.offset(0, -2, 0), AETHERIUM_FRAME.defaultBlockState(), 3);
    }

    private static void buildShrine(ServerLevel level, BlockPos origin) {
        for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) {
            level.setBlock(origin.offset(x, 0, z), ANCIENT_RESEARCH_BRICKS.defaultBlockState(), 3);
        }
        for (int y = 1; y <= 3; y++) {
            level.setBlock(origin.offset(0, y, 0), AETHERIUM_FRAME.defaultBlockState(), 3);
        }
        level.setBlock(origin.offset(0, 1, 0), CATALYST_ALTAR.defaultBlockState(), 3);
    }

    private static void buildStabilizationSite(ServerLevel level, BlockPos origin) {
        for (int x = -4; x <= 4; x++) for (int z = -4; z <= 4; z++) {
            if (Math.abs(x) == 4 || Math.abs(z) == 4) {
                level.setBlock(origin.offset(x, 0, z), AETHERIUM_FRAME.defaultBlockState(), 3);
            }
        }
        for (int y = 0; y <= 4; y++) {
            level.setBlock(origin.offset(0, y, 0), STABILIZATION_PILLAR.defaultBlockState(), 3);
        }
        level.setBlock(origin.offset(0, 1, 2), STABILIZATION_PILLAR.defaultBlockState(), 3);
        level.setBlock(origin.offset(0, 1, -2), STABILIZATION_PILLAR.defaultBlockState(), 3);
    }

    private enum Stage {
        RESEARCH, FURNACE, CATALYST, STABILIZER, ASSEMBLY
    }

    private static final class LoreBlock extends Block {
        private final Stage stage;

        private LoreBlock(BlockBehaviour.Properties properties, Stage stage) {
            super(properties);
            this.stage = stage;
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                    net.minecraft.world.entity.player.Player player,
                                                    BlockHitResult hit) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

            switch (stage) {
                case RESEARCH -> {
                    if (!AetheriumDiscovery.has(serverPlayer, AetheriumDiscovery.ANCIENT_MEMORY_FRAGMENT)) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.no_memory"));
                        return InteractionResult.SUCCESS;
                    }
                    if (!AetheriumDiscovery.has(serverPlayer, RESEARCH_FRAGMENT)) {
                        AetheriumDiscovery.give(serverPlayer, RESEARCH_FRAGMENT);
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.research_found"));
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.three_worlds"));
                    }
                }
                case FURNACE -> {
                    if (AetheriumDiscovery.has(serverPlayer, ModItems.RAW_AETHERITE_RESIDUE)) {
                        AetheriumDiscovery.consume(serverPlayer, ModItems.RAW_AETHERITE_RESIDUE);
                        AetheriumDiscovery.give(serverPlayer, ModItems.PURIFIED_AETHERITE);
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.purified"));
                    } else {
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.furnace_hint"));
                    }
                }
                case CATALYST -> {
                    if (!AetheriumDiscovery.has(serverPlayer, RESEARCH_FRAGMENT)) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.catalyst_locked"));
                        return InteractionResult.SUCCESS;
                    }
                    if (!AetheriumDiscovery.has(serverPlayer, CATALYST_SHARD)) {
                        AetheriumDiscovery.give(serverPlayer, CATALYST_SHARD);
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.catalyst_found"));
                    }
                }
                case STABILIZER -> {
                    if (!AetheriumDiscovery.has(serverPlayer, CATALYST_SHARD)) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.stabilizer_locked"));
                        return InteractionResult.SUCCESS;
                    }
                    if (!AetheriumDiscovery.has(serverPlayer, STABILIZER_SHARD)) {
                        AetheriumDiscovery.give(serverPlayer, STABILIZER_SHARD);
                        AetheriumDiscovery.give(serverPlayer, AETHERIUM_BLUEPRINT);
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.stabilizer_found"));
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.blueprint_complete"));
                    }
                }
                case ASSEMBLY -> {
                    boolean ready = AetheriumDiscovery.has(serverPlayer, AETHERIUM_BLUEPRINT)
                            && AetheriumDiscovery.has(serverPlayer, ModItems.RESONANT_ALLOY)
                            && AetheriumDiscovery.has(serverPlayer, AETHERIC_CORE);
                    if (!ready) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.assembly_locked"));
                        return InteractionResult.SUCCESS;
                    }
                    if (AetheriumDiscovery.consume(serverPlayer, ModItems.RESONANT_ALLOY)
                            && AetheriumDiscovery.consume(serverPlayer, AETHERIC_CORE)) {
                        AetheriumDiscovery.give(serverPlayer, VESSEL_CHESTPLATE);
                        serverPlayer.sendSystemMessage(Component.translatable("message.aetherium.assembly_complete"));
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
    }
}

// registries.h — COMPREHENSIVE STUB for TLS integration test
// Generated from source code analysis of all B_, I_, D_, W_ identifiers.
// Full version must be generated from Minecraft server JAR via build_registries.js
//
// To regenerate:
//   1. Download Minecraft server.jar (1.21.8) → notchian/server.jar
//   2. bash extract_registries.sh && node build_registries.js

#ifndef REGISTRIES_H
#define REGISTRIES_H

#include <stdint.h>
#include <stddef.h>

// ── Block IDs ───────────────────────────────────────────────────
#define B_DESPAWN_DISTANCE 0
#define B_RECURSE 1
#define B_air 2
#define B_allium 3
#define B_azure_bluet 4
#define B_bedrock 5
#define B_blue_orchid 6
#define B_bookshelf 7
#define B_cactus 8
#define B_chest 9
#define B_coal_block 10
#define B_coal_ore 11
#define B_cobblestone 12
#define B_cobblestone_slab 13
#define B_composter 14
#define B_copper_ore 15
#define B_crafting_table 16
#define B_dead_bush 17
#define B_diamond_block 18
#define B_diamond_ore 19
#define B_dirt 20
#define B_furnace 21
#define B_glass 22
#define B_glowstone 23
#define B_gold_block 24
#define B_gold_ore 25
#define B_grass_block 26
#define B_ice 27
#define B_iron_block 28
#define B_iron_ore 29
#define B_lava 30
#define B_lily_pad 31
#define B_moss_carpet 32
#define B_mud 33
#define B_oak_leaves 34
#define B_oak_log 35
#define B_oak_planks 36
#define B_oak_sapling 37
#define B_orange_tulip 38
#define B_oxeye_daisy 39
#define B_poppy 40
#define B_red_tulip 41
#define B_redstone_block 42
#define B_redstone_ore 43
#define B_sand 44
#define B_sandstone 45
#define B_short_grass 46
#define B_snow 47
#define B_snow_block 48
#define B_snowy_grass_block 49
#define B_stone 50
#define B_stone_slab 51
#define B_stripped_oak_log 52
#define B_torch 54
#define B_vine 55
#define B_water 56
#define B_white_tulip 57

// ── Item IDs ────────────────────────────────────────────────────
#define I_AUTH_WPA2_PSK 0
#define I_EVENT 1
#define I_EVENT_STA_DISCONNECTED 2
#define I_EVENT_STA_START 3
#define I_IF_STA 4
#define I_INIT_CONFIG_DEFAULT 5
#define I_MODE_STA 6
#define I_PASS 7
#define I_PS_NONE 8
#define I_SSID 9
#define I_apple 10
#define I_beef 11
#define I_bone_meal 12
#define I_cactus 13
#define I_charcoal 14
#define I_chest 15
#define I_chicken 16
#define I_coal 17
#define I_coal_block 18
#define I_cobblestone 19
#define I_cobblestone_slab 20
#define I_composter 21
#define I_cooked_beef 22
#define I_cooked_chicken 23
#define I_cooked_mutton 24
#define I_cooked_porkchop 25
#define I_copper_block 26
#define I_copper_ingot 27
#define I_crafting_table 28
#define I_diamond 29
#define I_diamond_axe 30
#define I_diamond_block 31
#define I_diamond_boots 32
#define I_diamond_chestplate 33
#define I_diamond_helmet 34
#define I_diamond_hoe 35
#define I_diamond_leggings 36
#define I_diamond_pickaxe 37
#define I_diamond_shovel 38
#define I_diamond_sword 39
#define I_furnace 40
#define I_glass 41
#define I_gold_block 42
#define I_gold_ingot 43
#define I_golden_axe 44
#define I_golden_boots 45
#define I_golden_chestplate 46
#define I_golden_helmet 47
#define I_golden_hoe 48
#define I_golden_leggings 49
#define I_golden_pickaxe 50
#define I_golden_shovel 51
#define I_golden_sword 52
#define I_iron_axe 53
#define I_iron_block 54
#define I_iron_boots 55
#define I_iron_chestplate 56
#define I_iron_helmet 57
#define I_iron_hoe 58
#define I_iron_ingot 59
#define I_iron_leggings 60
#define I_iron_pickaxe 61
#define I_iron_shovel 62
#define I_iron_sword 63
#define I_leather 64
#define I_leather_boots 65
#define I_leather_chestplate 66
#define I_leather_helmet 67
#define I_leather_leggings 68
#define I_lily_pad 69
#define I_moss_carpet 70
#define I_mutton 71
#define I_netherite_axe 72
#define I_netherite_boots 73
#define I_netherite_chestplate 74
#define I_netherite_helmet 75
#define I_netherite_hoe 76
#define I_netherite_ingot 77
#define I_netherite_leggings 78
#define I_netherite_pickaxe 79
#define I_netherite_shovel 80
#define I_netherite_sword 81
#define I_oak_button 82
#define I_oak_leaves 83
#define I_oak_log 84
#define I_oak_planks 85
#define I_oak_pressure_plate 86
#define I_oak_sapling 87
#define I_oak_slab 88
#define I_oak_wood 89
#define I_porkchop 90
#define I_raw_gold 91
#define I_raw_iron 92
#define I_redstone 93
#define I_redstone_block 94
#define I_rotten_flesh 95
#define I_sand 96
#define I_shears 97
#define I_short_grass 98
#define I_snow 99
#define I_snow_block 100
#define I_snowball 101
#define I_stick 102
#define I_stone 103
#define I_stone_axe 104
#define I_stone_hoe 105
#define I_stone_pickaxe 106
#define I_stone_shovel 107
#define I_stone_slab 108
#define I_stone_sword 109
#define I_sugar_cane 110
#define I_torch 112
#define I_wheat_seeds 113
#define I_white_wool 114
#define I_wooden_axe 115
#define I_wooden_hoe 116
#define I_wooden_pickaxe 117
#define I_wooden_shovel 118
#define I_wooden_sword 119

// ── Damage Type IDs ─────────────────────────────────────────────
#define D_BARN 0
#define D_BARRACKS 1
#define D_BRAND 2
#define D_CAPTAIN 3
#define D_CEMETERY 4
#define D_CHURCH 5
#define D_COLA 6
#define D_FARM 7
#define D_FEAST 8
#define D_FISHING_DOCK 9
#define D_FLOPPER 10
#define D_FLOW 11
#define D_GARDEN 12
#define D_HISTORY 13
#define D_HOUSE_LARGE 14
#define D_HOUSE_SMALL 15
#define D_INTERVAL 16
#define D_LIBRARY 17
#define D_MARKET 18
#define D_MAX 19
#define D_MINE_SHAFT 20
#define D_NONE 21
#define D_RATION 22
#define D_RELIC 23
#define D_RUINS 24
#define D_SEED 25
#define D_SMITHY 26
#define D_STABLE 27
#define D_STAFF 28
#define D_STEW 29
#define D_TAVERN 30
#define D_TEMPLE 31
#define D_TICKS 32
#define D_TO_DISK 33
#define D_TRADE_POST 34
#define D_VILLAGE 35
#define D_WATCHTOWER 36
#define D_WELL 37
#define D_WINDMILL 38
#define D_cactus 39
#define D_fall 40
#define D_generic 41
#define D_lava 42
#define D_on_fire 43

// ── Biome IDs ───────────────────────────────────────────────────
#define W_CHESTS 0
#define W_DISTANCE 1
#define W_beach 2
#define W_desert 3
#define W_mangrove_swamp 4
#define W_plains 5
#define W_snowy_plains 6

// ── Lookup tables ───────────────────────────────────────────────
extern uint16_t B_to_I[256];
extern uint16_t I_to_B[256];
extern uint16_t block_palette[256];
extern uint16_t network_block_palette[256];

// ── Binary blobs ────────────────────────────────────────────────
extern uint8_t registries_bin[1];
extern size_t registries_bin_size;
extern uint8_t tags_bin[1];
extern size_t tags_bin_size;

// I_to_B as inline function (used as I_to_B(*item))
static inline uint16_t I_to_B_func(uint16_t item) {
    if (item < 256) return I_to_B[item];
    return 0;
}

#endif

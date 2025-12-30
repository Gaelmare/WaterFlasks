/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Top level items must be static, the subclasses' fields must not be static.
 */

public class ConfigFlasks {

    public static ModConfigSpec.ConfigValue<Integer> LEATHER_CAPACITY;
    public static ModConfigSpec.ConfigValue<Integer> DAMAGE_FACTOR;
    public static ModConfigSpec.ConfigValue<Integer> IRON_CAPACITY;
    public static ModConfigSpec.BooleanValue THIRSTY_DRINK;
    public static ModConfigSpec.BooleanValue SHIFT_EMPTY;
    static ModConfigSpec SPEC;
    public static void register(ModContainer modContainer) {
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        COMMON_BUILDER.comment("Settings for Water Flasks");
        // todo: we need an unbreaking-like handler for durability tweaks, and no idea how to handle changes to capacities...
        LEATHER_CAPACITY = COMMON_BUILDER
                .comment("Liquid Capacity of Leather Flask (500 = 1/2 bucket = 5 drinks or 2 water bars) Min 100, Max MAXINT")
                .define("leatherCapacity",() -> 2000, // Default value as supplier
                        (value) -> {
                            // Validation function that returns true if valid
                            int capacity = (Integer) value;
                            return capacity >= 100;
                        });
        DAMAGE_FACTOR = COMMON_BUILDER
                .comment("Damage Capability of Flasks are Capacity/(this value), 0 = MAXINT uses")
                .define("damageFactor",() -> 5, // Default value as supplier
                        (value) -> {
                            // Validation function that returns true if valid
                            int capacity = (Integer) value;
                            return capacity >= 0;
                        }); // Max value);
        IRON_CAPACITY = COMMON_BUILDER
                .comment("Liquid Capacity of Iron Flask (1000 = 1 bucket = 10 drinks or 4 water bars) Min 100, Max MAXINT")
                .define("ironCapacity",() -> 2000, // Default value as supplier
                        (value) -> {
                            // Validation function that returns true if valid
                            int capacity = (Integer) value;
                            return capacity >= 100;
                        }); // Max value);
        THIRSTY_DRINK = COMMON_BUILDER
                .comment("Allow drinking when not thirsty? Could be useful or wasteful if that's not water in there... Default False")
                .define("thirstyDrinking", false);
        SHIFT_EMPTY = COMMON_BUILDER
                .comment("Empty flask when shift-right-clicked? Generally a useful feature, but does annoy a certain streamer... Default True")
                .define("shiftClickToEmpty", true);
        SPEC = COMMON_BUILDER.build();
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigFlasks.SPEC);

    }

}

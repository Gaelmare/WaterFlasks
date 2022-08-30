package org.labellum.mc.waterflasks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Top level items must be static, the subclasses' fields must not be static.
 */

public class ConfigFlasks {
    public static ForgeConfigSpec.IntValue LEATHER_CAPACITY;
    public static ForgeConfigSpec.IntValue DAMAGE_FACTOR;
    public static ForgeConfigSpec.BooleanValue ENABLE_IRON;
    public static ForgeConfigSpec.IntValue IRON_CAPACITY;

    public static void register() {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("Settings for Water Flasks");
        LEATHER_CAPACITY = COMMON_BUILDER
                .comment("Liquid Capacity of Leather Flask (500 = 1/2 bucket = 5 drinks or 2 water bars)")
                .defineInRange("leatherCapacity", 500, 100, Integer.MAX_VALUE);
        DAMAGE_FACTOR = COMMON_BUILDER
                .comment("Damage Capability of Flasks are Capacity/(this value), 0 = MAXINT uses")
                .defineInRange("leatherCapacity", 5, 0, Integer.MAX_VALUE);
        ENABLE_IRON = COMMON_BUILDER
                .comment("Liquid Capacity of Leather Flask (500 = 1/2 bucket = 5 drinks or 2 water bars)")
                .define("Enable Iron Flask", true);
        IRON_CAPACITY = COMMON_BUILDER
                .comment("Liquid Capacity of Iron Flask (1000 = 1 bucket = 10 drinks or 4 water bars)")
                .defineInRange("leatherCapacity", 2000, 100, Integer.MAX_VALUE);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
    }

}

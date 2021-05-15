package org.labellum.mc.waterflasks;

/*
    Copyright (c) 2019 Gaelmare

    This file is part of Waterflasks.

    Waterflasks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    WaterFlasks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WaterFlasks.  If not, see <https://www.gnu.org/licenses/>.
*/

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.util.Helpers;
import org.labellum.mc.waterflasks.item.ModItems;
import org.labellum.mc.waterflasks.proxy.CommonProxy;
import org.labellum.mc.waterflasks.recipe.ModRecipes;

@Mod(
        modid = Waterflasks.MOD_ID,
        name = Waterflasks.MOD_NAME,
        version = Waterflasks.VERSION,
        dependencies = Waterflasks.DEPENDENCIES
)
public class Waterflasks {

    public static final String MOD_ID = "waterflasks";
    public static final String MOD_NAME = "WaterFlasks";
    public static final String VERSION = "1.9";
    public static final String DEPENDENCIES = "required-after:" + TerraFirmaCraft.MOD_ID +
        "@[" + "1.0.0.127" + ",)";

    /**
     * Many thanks to Shadowfacts' 1.12.2 modding tutorial. Fingerprints from it remain...
     */

    @Mod.Instance(MOD_ID)
    public static Waterflasks INSTANCE;

    @GameRegistry.ObjectHolder("waterflasks:item.flaskbreak")
    public static final SoundEvent FLASK_BREAK = (SoundEvent) Helpers.getNull();


    @SidedProxy(serverSide = "org.labellum.mc.waterflasks.proxy.CommonProxy",
            clientSide = "org.labellum.mc.waterflasks.proxy.ClientProxy")
    public static CommonProxy proxy;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        System.out.println(MOD_NAME + " is loading");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // register loot function for use when we add bladder loot pools to TFC animals
        LootFunctionManager.registerFunction(new ApplyRequiredSkill.Serializer(new ResourceLocation(MOD_ID, "apply_req_skill")));
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        /**
         * Listen for the register event for creating custom items
         */
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            ModItems.register(event.getRegistry());
        }

        /**
         * Listen for the register event for models
         */
        @SubscribeEvent
        public static void registerItems(ModelRegistryEvent event) {
            ModItems.registerModels();
        }

        /**
         * Register Knapping Recipe
         */
        @SubscribeEvent
        public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event) {
            ModRecipes.registerKnapping(event);
        }

        /**
         * Register Anvil Recipe
         */
        @SubscribeEvent
        public static void onRegisterAnvilRecipeEvent(RegistryEvent.Register<AnvilRecipe> event) {
            ModRecipes.registerAnvil(event);
        }

        @SubscribeEvent
        public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
            ResourceLocation soundID = new ResourceLocation(MOD_ID, "item.flaskbreak");
            event.getRegistry().register((new SoundEvent(soundID)).setRegistryName(soundID));
        }

        @SubscribeEvent
        public static void onLootTableLoad(LootTableLoadEvent event)
        {
            if (event.getName().toString().startsWith("tfc:"))
            {
                switch (event.getName().toString().substring(4)) {
                    case "animals/cow":
                    case "animals/muskox":
                    case "animals/wildebeest":
                    case "animals/yak":
                    case "animals/zebu":
                        addPool(event, "animals/cow");
                        break;
                    case "animals/black_bear":
                    case "animals/grizzly_bear":
                    case "animals/polar_bear":
                        addPool(event, "animals/bear");
                        break;
                    case "animals/horse":
                    case "animals/camel":
                    case "animals/donkey":
                    case "animals/mule":
                        addPool(event, "animals/horse");
                        break;
                    case "animals/sheep":
                    case "animals/goat":
                    case "animals/alpaca":
                    case "animals/llama":
                        addPool(event, "animals/sheep");
                        break;
                    case "animals/deer":
                    case "animals/gazelle":
                        addPool(event, "animals/deer");
                        break;
                    default:
                        break;
                }
            }
        }

        private static void addPool(LootTableLoadEvent event, String tableName)
        {
            LootEntry entry = new LootEntryTable(new ResourceLocation("waterflasks:"+tableName),
                    1, 0, new LootCondition[0], "waterflasks_bladder_entry");

            LootPool newPool = new LootPool(new LootEntry [] {entry}, new LootCondition[0],
                    new RandomValueRange(1), new RandomValueRange(0), "waterflasks_bladder_pool");
            //weights here seemed screwy. Implemented own skill function, applied in json data
            event.getTable().addPool(newPool);
        }
    }
}

package org.labellum.mc.waterflasks.setup;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static void init(FMLCommonSetupEvent event)
    {

    }


    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
/*
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        System.out.println(MOD_NAME + " is loading");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // register loot function for use when we add bladder loot pools to TFC animals
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
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
    */
}

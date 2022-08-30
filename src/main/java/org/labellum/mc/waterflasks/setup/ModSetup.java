package org.labellum.mc.waterflasks.setup;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.labellum.mc.waterflasks.ApplyRequiredSkill;
import org.labellum.mc.waterflasks.item.ModItems;
import org.labellum.mc.waterflasks.proxy.CommonProxy;
import org.labellum.mc.waterflasks.recipe.ModRecipes;

import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    public static void init(FMLCommonSetupEvent event)
    {

    }

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

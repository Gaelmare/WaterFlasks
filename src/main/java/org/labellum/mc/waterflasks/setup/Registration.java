package org.labellum.mc.waterflasks.setup;

import net.minecraft.world.item.Item;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.labellum.mc.waterflasks.ConfigFlasks;
import org.labellum.mc.waterflasks.item.ItemIronFlask;
import org.labellum.mc.waterflasks.item.ItemLeatherFlask;

import static net.dries007.tfc.common.TFCItemGroup.FOOD;

import static net.dries007.tfc.common.TFCItemGroup.METAL;
import static org.labellum.mc.waterflasks.ConfigFlasks.GENERAL;
import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

public class Registration {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static void init()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
    }

    public static final Item.Properties MISC_PROPERTIES = new Item.Properties().tab(FOOD);
    public static final Item.Properties METAL_PROPERTIES = new Item.Properties().tab(METAL);

    public static RegistryObject<Item> leatherSide = ITEMS.register("leather_side",()->new Item(MISC_PROPERTIES));
    public static RegistryObject<Item> bladder = ITEMS.register("bladder",()->new Item(MISC_PROPERTIES));
    public static RegistryObject<Item> leatherFlask = ITEMS.register("leather_flask",() -> new ItemLeatherFlask(
            MISC_PROPERTIES.durability(GENERAL.damageFactor == 0 ? Integer.MAX_VALUE : ConfigFlasks.GENERAL.leatherCap / GENERAL.damageFactor)));
    public static RegistryObject<Item> brokenLeatherFlask = ITEMS.register("broken_leather_flask",()->new Item(MISC_PROPERTIES));
    public static RegistryObject<Item> unfinishedFlask = registerIronItem("unfinished_iron_flask");
    public static RegistryObject<Item> ironFlask = registerIronFlask("iron_flask");
    public static RegistryObject<Item> brokenIronFlask = registerIronItem("broken_iron_flask");

    private static RegistryObject<Item> registerIronItem(String name) {
        if (ConfigFlasks.GENERAL.enableIron)
        {
            return ITEMS.register(name, ()->new Item(METAL_PROPERTIES));
        }
        else return null;
    }

    private static RegistryObject<Item> registerIronFlask(String name) {
        if (ConfigFlasks.GENERAL.enableIron)
        {
            return ITEMS.register(name, () -> new ItemIronFlask(
                    METAL_PROPERTIES.durability(GENERAL.damageFactor == 0 ? Integer.MAX_VALUE : ConfigFlasks.GENERAL.ironCap / GENERAL.damageFactor)));
        }
        else return null;
    }
}

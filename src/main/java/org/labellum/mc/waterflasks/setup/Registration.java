package org.labellum.mc.waterflasks.setup;

import net.dries007.tfc.util.Helpers;
import net.minecraft.sounds.SoundEvent;
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
import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

public class Registration {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);


    public static void init()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        SOUNDS.register(bus);

    }

    public static final Item.Properties MISC_PROPERTIES = new Item.Properties().tab(FOOD);
    public static final Item.Properties METAL_PROPERTIES = new Item.Properties().tab(METAL);

    public static RegistryObject<Item> leatherSide = ITEMS.register("leather_side",()->new Item(MISC_PROPERTIES));
    public static RegistryObject<Item> bladder = ITEMS.register("bladder",()->new Item(MISC_PROPERTIES));
    public static RegistryObject<Item> leatherFlask = ITEMS.register("leather_flask",() -> new ItemLeatherFlask(
            MISC_PROPERTIES.durability(ConfigFlasks.DAMAGE_FACTOR.get() == 0 ? Integer.MAX_VALUE : ConfigFlasks.LEATHER_CAPACITY.get() / ConfigFlasks.DAMAGE_FACTOR.get())));
    public static RegistryObject<Item> brokenLeatherFlask = ITEMS.register("broken_leather_flask",()->new Item(MISC_PROPERTIES));
    public static RegistryObject<Item> unfinishedFlask = registerIronItem("unfinished_iron_flask");
    public static RegistryObject<Item> ironFlask = registerIronFlask("iron_flask");
    public static RegistryObject<Item> brokenIronFlask = registerIronItem("broken_iron_flask");

    public static final RegistryObject<SoundEvent> FLASK_BREAK = SOUNDS.register("item.flaskbreak", () -> new SoundEvent(Helpers.identifier("item.flaskbreak")));;

    private static RegistryObject<Item> registerIronItem(String name) {
        if (ConfigFlasks.ENABLE_IRON.get())
        {
            return ITEMS.register(name, ()->new Item(METAL_PROPERTIES));
        }
        else return null;
    }

    private static RegistryObject<Item> registerIronFlask(String name) {
        if (ConfigFlasks.ENABLE_IRON.get())
        {
            return ITEMS.register(name, () -> new ItemIronFlask(
                    METAL_PROPERTIES.durability(ConfigFlasks.DAMAGE_FACTOR.get() == 0 ? Integer.MAX_VALUE : ConfigFlasks.IRON_CAPACITY.get() / ConfigFlasks.DAMAGE_FACTOR.get())));
        }
        else return null;
    }
}

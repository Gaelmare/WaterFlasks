package org.labellum.mc.waterflasks.setup;

import net.dries007.tfc.util.Helpers;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.labellum.mc.waterflasks.ConfigFlasks;
import org.labellum.mc.waterflasks.item.FlaskItem;

import java.util.function.Supplier;

import static net.dries007.tfc.common.TFCItemGroup.*;
import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

public class Registration {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    public static final RegistryObject<AddItemChanceModifier.Serializer> ADD_ITEM = glmSerializer("add_item", AddItemChanceModifier.Serializer::new);

    public static void init()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        SOUNDS.register(bus);
        MODIFIER_SERIALIZERS.register(bus);
    }

    public static final RegistryObject<Item> LEATHER_SIDE = register("leather_side", MISC);
    public static final RegistryObject<Item> BLADDER = register("bladder", MISC);
    public static final RegistryObject<Item> BROKEN_LEATHER_FLASK = register("broken_leather_flask", MISC);
    public static final RegistryObject<Item> LEATHER_FLASK = register("leather_flask", () -> new FlaskItem(flaskProperties(), ConfigFlasks.LEATHER_CAPACITY, FlaskItem.DEFAULT_DRINK, BROKEN_LEATHER_FLASK));
    public static final RegistryObject<Item> UNFINISHED_FLASK = register("unfinished_iron_flask", MISC);
    public static final RegistryObject<Item> BROKEN_IRON_FLASK = register("broken_iron_flask", METAL);
    public static final RegistryObject<Item> IRON_FLASK = register("iron_flask", () -> new FlaskItem(flaskProperties(), ConfigFlasks.IRON_CAPACITY, FlaskItem.DEFAULT_DRINK, BROKEN_IRON_FLASK));

    public static final RegistryObject<SoundEvent> FLASK_BREAK = SOUNDS.register("item.flaskbreak", () -> new SoundEvent(Helpers.identifier("item.flaskbreak")));;

    // todo this may not work
    // todo we can also set a config-based capacity for our flask items.
    // we generally want to instantiate *new* properties per item, as the properties builder is mutable.
    private static Item.Properties flaskProperties()
    {
        return new Item.Properties().tab(MISC).durability(ConfigFlasks.DAMAGE_FACTOR.get() == 0 ? Integer.MAX_VALUE : ConfigFlasks.LEATHER_CAPACITY.get() / ConfigFlasks.DAMAGE_FACTOR.get());
    }

    private static RegistryObject<Item> register(String name, CreativeModeTab tab)
    {
        return ITEMS.register(name, () -> new Item(new Item.Properties().tab(tab)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier)
    {
        return ITEMS.register(name, supplier);
    }

    private static <T extends GlobalLootModifierSerializer<? extends IGlobalLootModifier>> RegistryObject<T> glmSerializer(String id, Supplier<T> modifier)
    {
        return MODIFIER_SERIALIZERS.register(id, modifier);
    }
}

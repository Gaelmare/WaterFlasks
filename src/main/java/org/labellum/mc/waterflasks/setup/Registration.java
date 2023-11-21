/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.setup;

import com.mojang.serialization.Codec;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.recipes.DelegateRecipe;
import net.dries007.tfc.util.SelfTests;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.labellum.mc.waterflasks.ConfigFlasks;
import org.labellum.mc.waterflasks.item.FlaskItem;

import java.util.function.Supplier;

import static net.dries007.tfc.common.TFCCreativeTabs.*;
import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

public class Registration {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    public static void init()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        SOUNDS.register(bus);
        MODIFIER_SERIALIZERS.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }

    public static final TagKey<Item> FLASKS = TagKey.create(ForgeRegistries.Keys.ITEMS, new ResourceLocation(MOD_ID, "flasks"));

    public static final RegistryObject<Codec<AddItemChanceModifier>> ADD_ITEM = glmSerializer("add_item", () -> AddItemChanceModifier.CODEC);

    public static final RegistryObject<Item> LEATHER_SIDE = register("leather_side");

    public static final RegistryObject<Item> BLADDER = register("bladder");
    public static final RegistryObject<Item> BROKEN_LEATHER_FLASK = register("broken_leather_flask");
    public static final RegistryObject<Item> LEATHER_FLASK = register("leather_flask", () -> new FlaskItem(leatherProperties(), ConfigFlasks.LEATHER_CAPACITY, FlaskItem.DEFAULT_DRINK, BROKEN_LEATHER_FLASK));
    public static final RegistryObject<Item> UNFINISHED_FLASK = register("unfinished_iron_flask");
    public static final RegistryObject<Item> BROKEN_IRON_FLASK = register("broken_iron_flask");
    public static final RegistryObject<Item> IRON_FLASK = register("iron_flask", () -> new FlaskItem(ironProperties(), ConfigFlasks.IRON_CAPACITY, FlaskItem.DEFAULT_DRINK, BROKEN_IRON_FLASK));

    public static final CreativeTabHolder FLASKTAB = register("flasks", () -> new ItemStack(LEATHER_FLASK.get()), Registration::fillTab);

    public static final RegistryObject<SoundEvent> FLASK_BREAK = SOUNDS.register("item.flaskbreak", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("waterflasks", "item.flaskbreak")));

    public static final RegistryObject<RecipeSerializer<?>> HEAL_FLASK_SERIALIZER = registerSerializer("heal_flask", () -> DelegateRecipe.Serializer.shaped(HealFlaskRecipe::new));

    private static void fillTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out) {
        accept(out, LEATHER_SIDE);
        accept(out, BLADDER);
        accept(out, BROKEN_LEATHER_FLASK);
        accept(out, LEATHER_FLASK);
        accept(out, UNFINISHED_FLASK);
        accept(out, BROKEN_IRON_FLASK);
        accept(out, IRON_FLASK);
    }

    // todo this may not work
    // todo we can also set a config-based capacity for our flask items.
    // we generally want to instantiate *new* properties per item, as the properties builder is mutable.
    private static Item.Properties leatherProperties()
    {
        return new Item.Properties().durability(100);
    }
    private static Item.Properties ironProperties()
    {
        return new Item.Properties().durability(400);
    }

    private static RegistryObject<Item> register(String name)
    {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> supplier)
    {
        return ITEMS.register(name, supplier);
    }

    private static CreativeTabHolder register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        final RegistryObject<CreativeModeTab> reg = CREATIVE_TABS.register(name, () -> CreativeModeTab.builder()
                .icon(icon)
                .title(Component.translatable("item.waterflasks.leather_flask"))
                .displayItems(displayItems)
                .build());
        return new CreativeTabHolder(reg, displayItems);
    }

    private static <T extends IGlobalLootModifier> RegistryObject<Codec<T>> glmSerializer(String id, Supplier<Codec<T>> modifier)
    {
        return MODIFIER_SERIALIZERS.register(id, modifier);
    }

    private static <S extends RecipeSerializer<?>> RegistryObject<S> registerSerializer(String name, Supplier<S> factory)
    {
        return RECIPE_SERIALIZERS.register(name, factory);
    }

    private static <T extends ItemLike, R extends Supplier<T>> void accept(CreativeModeTab.Output out, R reg)
    {
        if (reg.get().asItem() == Items.AIR)
        {
            TerraFirmaCraft.LOGGER.error("BlockItem with no Item added to creative tab: " + reg);
            SelfTests.reportExternalError();
            return;
        }
        out.accept(reg.get());
    }

}

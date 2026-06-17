/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.setup;

import com.mojang.serialization.MapCodec;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.recipes.RecipeSerializerImpl;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.util.SelfTests;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.component.fluid.FluidContainerHandler;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.labellum.mc.waterflasks.ConfigFlasks;
import org.labellum.mc.waterflasks.item.FlaskItem;

import java.util.function.Supplier;

import static org.labellum.mc.waterflasks.Waterflasks.MODID;

public class Registration {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public static final net.neoforged.neoforge.registries.DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = net.neoforged.neoforge.registries.DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static void init(IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
        SOUNDS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        MODIFIER_SERIALIZERS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    public static final TagKey<Item> FLASKS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, "flasks"));

    //public static final RegistryObject<Codec<AddItemChanceModifier>> ADD_ITEM = glmSerializer("add_item", () -> AddItemChanceModifier.CODEC);
    public static final Supplier<MapCodec<AddItemChanceModifier>> ADD_ITEM = MODIFIER_SERIALIZERS.register("add_item", () -> AddItemChanceModifier.CODEC);

    public static final DeferredItem<Item> LEATHER_SIDE = register("leather_side");

    public static final DeferredItem<Item> BLADDER = register("bladder");
    public static final DeferredItem<Item> BROKEN_LEATHER_FLASK = register("broken_leather_flask");
    public static final DeferredItem<Item> LEATHER_FLASK = register("leather_flask", () -> new FlaskItem(leatherProperties(), ConfigFlasks.LEATHER_CAPACITY, FlaskItem.DEFAULT_DRINK, BROKEN_LEATHER_FLASK, true));
    public static final DeferredItem<Item> UNFINISHED_FLASK = register("unfinished_iron_flask");
    public static final DeferredItem<Item> BROKEN_IRON_FLASK = register("broken_iron_flask");
    public static final DeferredItem<Item> IRON_FLASK = register("iron_flask", () -> new FlaskItem(ironProperties(), ConfigFlasks.IRON_CAPACITY, FlaskItem.DEFAULT_DRINK, BROKEN_IRON_FLASK, true));
    public static final DeferredItem<Item> UNFINISHED_RED_STEEL_FLASK = register("unfinished_red_steel_flask", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> RED_STEEL_FLASK = register("red_steel_flask", () -> new FlaskItem(redSteelProperties(), ConfigFlasks.IRON_CAPACITY, FlaskItem.DEFAULT_DRINK, UNFINISHED_RED_STEEL_FLASK, false));

    // Creates a creative tab with the id "waterflasks:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FLASKTAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.waterflasks")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> LEATHER_FLASK.get().getDefaultInstance())
            .displayItems(Registration::fillTab).build());


    public static final DeferredHolder<SoundEvent, SoundEvent> FLASK_BREAK = SOUNDS.register(
            "item.flaskbreak", // must match the resource location on the next line
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("waterflasks", "item.flaskbreak"))
    );

    public static final TFCRecipeSerializers.Id<HealFlaskRecipe> HEAL_FLASK_SERIALIZER = register("heal_flask", HealFlaskRecipe.CODEC, HealFlaskRecipe.STREAM_CODEC);

    //public static final RegistryObject<RecipeSerializer<?>> HEAL_FLASK_SERIALIZER = registerSerializer("heal_flask", () -> DelegateRecipe.Serializer.shaped(HealFlaskRecipe::new));

    private static void fillTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out) {
        accept(out, LEATHER_SIDE);
        accept(out, BLADDER);
        accept(out, BROKEN_LEATHER_FLASK);
        accept(out, LEATHER_FLASK);
        accept(out, UNFINISHED_FLASK);
        accept(out, BROKEN_IRON_FLASK);
        accept(out, IRON_FLASK);
        accept(out, UNFINISHED_RED_STEEL_FLASK);
        accept(out, RED_STEEL_FLASK);
    }

    // todo this may not work
    // todo we can also set a config-based capacity for our flask items.
    // we generally want to instantiate *new* properties per item, as the properties builder is mutable.
    private static Item.Properties leatherProperties()
    {
        return new Item.Properties();
    }
    private static Item.Properties ironProperties()
    {
        return new Item.Properties();
    }
    private static Item.Properties redSteelProperties()
    {
        return new Item.Properties().stacksTo(1).rarity(Rarity.EPIC);
    }

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerItem(
            Capabilities.FluidHandler.ITEM,
            (stack, ctx) -> stack.getItem() instanceof FluidContainerItem item
                ? new FluidContainerHandler(stack, item.containerInfo())
                : null,
            LEATHER_FLASK.get(), IRON_FLASK.get(), RED_STEEL_FLASK.get()
        );
    }

    private static DeferredItem<Item> register(String name)
    {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }

    private static <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier)
    {
        return ITEMS.register(name, supplier);
    }

    private static <R extends Recipe<?>> TFCRecipeSerializers.Id<R> register(String name, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> stream)
    {
        return register(name, new RecipeSerializerImpl<>(codec, stream));
    }

    private static <R extends Recipe<?>> TFCRecipeSerializers.Id<R> register(String name, RecipeSerializer<R> serializer)
    {
        return new TFCRecipeSerializers.Id<>(RECIPE_SERIALIZERS.register(name, () -> serializer));
    }

    //private static <S extends RecipeSerializer<?>> RegistryObject<S> registerSerializer(String name, Supplier<S> factory)
    //{
    //    return RECIPE_SERIALIZERS.register(name, factory);
    //}

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

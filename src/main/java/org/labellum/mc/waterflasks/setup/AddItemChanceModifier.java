package org.labellum.mc.waterflasks.setup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.JsonHelpers;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import org.labellum.mc.waterflasks.Waterflasks;

import java.util.List;

public class AddItemChanceModifier extends LootModifier {
    private final ItemStack item;
    private final double chance;


    protected AddItemChanceModifier(LootItemCondition[] conditions, ItemStack item, double chance)
    {
        super(conditions);
        this.item = item;
        this.chance = chance;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> loot, LootContext context)
    {
        if (context.getRandom().nextDouble() < chance)
            loot.add(item.copy());
        return loot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AddItemChanceModifier>
    {
        @Override
        public AddItemChanceModifier read(ResourceLocation location, JsonObject json, LootItemCondition[] conditions)
        {
            return new AddItemChanceModifier(conditions, JsonHelpers.getItemStack(json, "item"), JsonHelpers.getAsDouble(json, "chance", 1.0 ));
        }

        @Override
        public JsonObject write(AddItemChanceModifier instance)
        {
            JsonObject json = makeConditions(instance.conditions);
            json.add("item", codecToJson(ItemStack.CODEC, instance.item));
            json.add("chance", codecToJson(Codec.DOUBLE, instance.chance));
            return json;
        }

        public static <T> JsonElement codecToJson(Codec<T> codec, T instance)
        {
            return codec.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(false, Util.prefix("Error encoding: ", Waterflasks.LOGGER::error));
        }

    }
}

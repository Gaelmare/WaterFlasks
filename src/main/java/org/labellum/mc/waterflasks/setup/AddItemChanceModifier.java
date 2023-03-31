/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.setup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.dries007.tfc.util.JsonHelpers;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
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
        final Player player = context.getParamOrNull(LootContextParams.LAST_DAMAGE_PLAYER);

        // make attack damage 15 (slightly better than red steel axe) equivalent to an additional 50% chance of drop, so divide damage by 30
        double bonus = 0.0D;
        if(!(player == null)) {
            bonus = player.getAttributeValue(Attributes.ATTACK_DAMAGE) / 30.0D;
        }
        if (context.getRandom().nextDouble() < chance + bonus)
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

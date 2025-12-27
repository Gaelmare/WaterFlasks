/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddItemChanceModifier extends LootModifier {
    private final ItemStack item;
    private final double chance;

    protected AddItemChanceModifier(LootItemCondition[] conditions, ItemStack item, double chance)
    {
        super(conditions);
        this.item = item;
        this.chance = chance;
    }

    public static final Codec<AddItemChanceModifier> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
            .and(ItemStack.CODEC.fieldOf("item").forGetter(c -> c.item))
            .and(Codec.DOUBLE.optionalFieldOf("chance", 1d).forGetter(c -> c.chance)
            ).apply(instance, AddItemChanceModifier::new));

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context)
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

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return Registration.ADD_ITEM.get();
    }

}

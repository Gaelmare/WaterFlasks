/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.minecraft.world.item.crafting.CraftingBookCategory.EQUIPMENT;

public class HealFlaskRecipe extends ShapedRecipe
{
    public static final MapCodec<HealFlaskRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            // Copied from AdvancedShapedRecipe.Serializer.CODEC, as we want to avoid the "result" field as a strict item stack
            ShapedRecipePattern.MAP_CODEC.forGetter(c -> c.pattern),
            ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
            ItemStackProvider.CODEC.optionalFieldOf("remainder").forGetter(c -> c.remainder),
            Codec.INT.optionalFieldOf("input_row", 0).forGetter(c -> c.inputRow),
            Codec.INT.optionalFieldOf("input_column", 0).forGetter(c -> c.inputColumn)
    ).apply(i, HealFlaskRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HealFlaskRecipe> STREAM_CODEC = StreamCodec.composite(
            ShapedRecipePattern.STREAM_CODEC, c -> c.pattern,
            ItemStackProvider.STREAM_CODEC, c -> c.result,
            ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.remainder,
            ByteBufCodecs.VAR_INT, c -> c.inputRow,
            ByteBufCodecs.VAR_INT, c -> c.inputColumn,
            HealFlaskRecipe::new
    );

    private final ItemStackProvider result;
    private final Optional<ItemStackProvider> remainder;
    private final int inputSlot, inputRow, inputColumn;

    protected HealFlaskRecipe(ShapedRecipePattern pattern, ItemStackProvider result, Optional<ItemStackProvider> remainder, int inputRow, int inputColumn)
    {
        super("", EQUIPMENT, pattern, ItemStack.EMPTY);
        this.result = result;
        this.remainder = remainder;
        this.inputSlot = RecipeHelpers.dissolveRowColumn(inputRow, inputColumn, pattern.width());
        this.inputRow = inputRow;
        this.inputColumn = inputColumn;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        FluidStack fluid = FluidStack.EMPTY;
        ItemStack flask = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++)
        {
            ItemStack stack = input.getItem(i);
            if (Helpers.isItem(stack, Registration.FLASKS))
            {
                flask = stack;
                IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
                if (handler != null) {
                    fluid = handler.getFluidInTank(0);
                }
                break;
            }
        }
        // Build the output from our ItemStackProvider result, not the (empty) ShapedRecipe result
        final ItemStack result = this.result.getSingleStack(flask);
        if (!fluid.isEmpty())
        {
            final FluidStack fillFluid = fluid;
            IFluidHandler handler = result.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler != null) {
                handler.fill(fillFluid, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result.getStackDisplayOnly(ItemStack.EMPTY);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); ++i)
        {
            final ItemStack copy = input.getItem(i);
            if (copy.isDamageableItem() && Helpers.isItem(copy, TFCTags.Items.TOOLS_KNIFE))
            {
                final @Nullable Player player = RecipeHelpers.getCraftingPlayer();
                if (player != null)
                {
                    Helpers.damageItem(copy, player.level());
                }
                else
                {
                    Helpers.damageItem(copy);
                }
                items.set(i, copy);
                break;
            }
        }
        return items;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Registration.HEAL_FLASK_SERIALIZER.get();
    }

    @Override
    public CraftingBookCategory category() {
        return EQUIPMENT;
    }
}

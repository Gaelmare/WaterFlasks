/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.item;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.DiscreteFluidContainerItem;
import net.dries007.tfc.util.Drinkable;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.labellum.mc.waterflasks.fluids.FlaskFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.function.Supplier;

import static net.dries007.tfc.common.capabilities.food.TFCFoodData.MAX_THIRST;
import static org.labellum.mc.waterflasks.setup.Registration.*;

public class FlaskItem extends DiscreteFluidContainerItem {

    public static final int DEFAULT_DRINK = 100;
    public static final TagKey<Fluid> DRINKABLE = TagKey.create(Registry.FLUID_REGISTRY, Helpers.identifier("drinkables"));

    private final Supplier<Integer> capacity;
    private final Supplier<? extends Item> broken;
    private final int drink;

    public FlaskItem(Item.Properties prop, Supplier<Integer> capFunc, int drink, Supplier<? extends Item> broken) {

        super(prop, capFunc, DRINKABLE, false, false);
        this.capacity = capFunc;
        this.drink = drink;
        this.broken = broken;
    }

    public static int getCapacity(ItemStack stack) {
        return ((FlaskItem)stack.getItem()).capacity.get();
    }

    /**
     * Returns 1 - fraction full because model overrides are like that
     * @param stack Flask
     * @return fraction empty
     */
    public static float getEmptinessDisplay(ItemStack stack) {
        return 1.0f - getLiquidAmount(stack)/(float)getCapacity(stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new FlaskFluidHandler(stack, capacity.get(), DRINKABLE);
    }

    public static int getLiquidAmount(ItemStack stack) {
        return stack.getCapability(Capabilities.FLUID_ITEM).map(cap -> cap.getFluidInTank(0).getAmount()).orElse(0);
    }

    /**
     * Returns the packed int RGB value used to render the durability bar in the GUI.
     * Retrieves no-alpha RGB color from liquid to use in durability bar
     *
     * @param stack Stack to get color from
     * @return A packed RGB value for the durability colour (0x00RRGGBB)
     */
    @Override
    public int getBarColor(ItemStack stack)
    {
        return stack.getCapability(Capabilities.FLUID_ITEM).map(cap -> {
            FluidStack drained = cap.drain(capacity.get(), IFluidHandler.FluidAction.SIMULATE);
            if (!drained.isEmpty())
            {
                return drained.getFluid().getAttributes().getColor();
            }
            return super.getBarColor(stack);
        }).orElse(super.getBarColor(stack));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final IFluidHandler handler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if (handler == null)
        {
            return InteractionResultHolder.pass(stack);
        }
        else
        {
            // Do not use in creative game mode
            if(player.isCreative())
                return InteractionResultHolder.pass(stack);

            // If contains fluid, allow emptying with shift-right-click
            if(player.isCrouching())
            {
                handler.drain(capacity.get(), IFluidHandler.FluidAction.EXECUTE);
                Helpers.playSound(level, player.blockPosition(), SoundEvents.BUCKET_EMPTY);
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (FluidHelpers.transferBetweenWorldAndItem(stack, level, hit, player, hand, false, false, true))
            {
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            if (handler.getFluidInTank(0).isEmpty())
            {
                return afterFillFailed(handler, level, player, stack, hand);
            }
            else
            {
                //Try to Drink
                FoodData stats = player.getFoodData();
                if (stats instanceof TFCFoodData && ((TFCFoodData) stats).getThirst() >= MAX_THIRST) {
                    // Don't drink if not thirsty
                    return InteractionResultHolder.fail(player.getItemInHand(hand));
                }
                FluidStack cont = handler.drain(capacity.get(), IFluidHandler.FluidAction.SIMULATE);
               if (!cont.isEmpty() && cont.getAmount() >= drink) {
                    return afterEmptyFailed(handler, level, player, stack, hand);
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    @Nonnull
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, Level level, LivingEntity entity)
    {
        if (entity instanceof Player player)
        {
            stack.getCapability(Capabilities.FLUID_ITEM).ifPresent(handler -> {
                final FluidStack drained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
                if (drained.getAmount() >= drink) {
                    FluidStack fluidConsumed = handler.drain(drink, IFluidHandler.FluidAction.EXECUTE);
                    final Drinkable drinkable = Drinkable.get(fluidConsumed.getFluid());
                    if (drinkable != null)
                    {
                        drinkable.onDrink(player, fluidConsumed.getAmount());
                    }
                    // the consumer is triggered when the player breaks an item. So we always know when something actually broke!
                    stack.hurtAndBreak(1, player, p -> {
                        // vanilla requests that we do this. not sure why
                        p.broadcastBreakEvent(player.getUsedItemHand());
                        level.playSound(null, entity.getOnPos(), FLASK_BREAK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                        ItemHandlerHelper.giveItemToPlayer((Player) entity, new ItemStack(broken.get()));
                    });
                }
            });
        }
        return stack;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return PotionItem.EAT_DURATION;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack)
    {
        ItemStack items = new ItemStack(this);
        items.setDamageValue(stack.getDamageValue());
        return items;
    }

    @NotNull
    @Override
    protected InteractionResultHolder<ItemStack> afterEmptyFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        if (player.isCrouching())
        {
            level.playSound(player, player.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 0.5f, 1.2f);
            handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            return InteractionResultHolder.consume(stack);
        }
        final Drinkable drinkable = Drinkable.get(handler.getFluidInTank(0).getFluid());
        if (drinkable != null)
        {
            return ItemUtils.startUsingInstantly(level, player, hand);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        if (this.allowdedIn(category)) {
            items.add(new ItemStack(this));
            Iterator<Fluid> iterator = Helpers.getAllTagValues(DRINKABLE, ForgeRegistries.FLUIDS).iterator();

            while(true) {
                Fluid fluid;
                FlowingFluid flowing;
                do {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    fluid = iterator.next();
                    if (!(fluid instanceof FlowingFluid)) {
                        break;
                    }

                    flowing = (FlowingFluid)fluid;
                } while(flowing.getSource() != flowing);

                ItemStack stack = new ItemStack(this);
                Fluid finalFluid = fluid;
                stack.getCapability(Capabilities.FLUID_ITEM).ifPresent((c) ->
                        c.fill(new FluidStack(finalFluid, capacity.get()), IFluidHandler.FluidAction.EXECUTE));
                items.add(stack);
            }
        }
    }
}

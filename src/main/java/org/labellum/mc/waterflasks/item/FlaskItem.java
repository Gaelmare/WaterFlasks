/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.item;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.util.data.Drinkable;
import net.dries007.tfc.util.Helpers;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.labellum.mc.waterflasks.ConfigFlasks;

import java.util.function.Supplier;

import static org.labellum.mc.waterflasks.ConfigFlasks.SHIFT_EMPTY;
import static org.labellum.mc.waterflasks.setup.Registration.*;

public class FlaskItem extends FluidContainerItem {

    public static final int DEFAULT_DRINK = 100;

    private final Supplier<Integer> capacity;
    private final Supplier<? extends Item> broken;
    private final int drink;
    private final boolean breakable;

    public FlaskItem(Item.Properties prop, Supplier<Integer> capFunc, int drink, Supplier<? extends Item> broken, boolean breakable) {
        super(prop, capFunc, TFCTags.Fluids.USABLE_IN_JUG, false, () -> false);
        this.capacity = capFunc;
        this.drink = drink;
        this.broken = broken;
        this.breakable = breakable;
    }

    @Override
    public void onDestroyed(ItemEntity ie, DamageSource ds)
    {
        ie.level().playSound(null, ie.getOnPos(), FLASK_BREAK.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    public static int getCapacity(ItemStack stack) {
        return ((FlaskItem)stack.getItem()).capacity.get();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (!breakable) return 0;
        int factor = ConfigFlasks.DAMAGE_FACTOR.get();
        if (factor <= 0) return 0;
        return Math.max(1, capacity.get() / factor);
    }

    /**
     * Returns 1 - fraction full because model overrides are like that
     * @param stack Flask
     * @return fraction empty
     */
    public static float getEmptinessDisplay(ItemStack stack) {
        return 1.0f - getLiquidAmount(stack)/(float)getCapacity(stack);
    }

    public static int getLiquidAmount(ItemStack stack) {
        IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null) {
            return 0;
        }
        else {
            return handler.getFluidInTank(0).getAmount();
        }
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        final FluidStack fluid = FluidHelpers.getContainedFluid(stack);
        if (!fluid.isEmpty())
        {
            final int color = RenderHelpers.getFluidColor(fluid);
            final int r = FastColor.ARGB32.red(color);
            final int g = FastColor.ARGB32.green(color);
            final int b = FastColor.ARGB32.blue(color);
            return FastColor.ARGB32.color(0, r, g, b);
        }
        int maxDamage = getMaxDamage(stack);
        if (maxDamage > 0) {
            return Mth.hsvToRgb(Math.max(0.0F, (1.0F - (float) stack.getDamageValue() / (float) maxDamage) / 3.0F), 1.0F, 1.0F);
        }
        return 0x7BF600;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getMaxDamage(stack) > 0 || !FluidHelpers.getContainedFluid(stack).isEmpty();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int maxDamage = getMaxDamage(stack);
        if (maxDamage <= 0) {
            return FluidHelpers.getContainedFluid(stack).isEmpty() ? 0 : 13;
        }
        return Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) maxDamage);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null)
        {
            return InteractionResultHolder.pass(stack);
        }
        else
        {
            // Do not use in creative game mode
            if(player.isCreative())
                return InteractionResultHolder.pass(stack);

            // If contains fluid, allow emptying with shift-right-click if configured
            if(player.isCrouching() && SHIFT_EMPTY.get())
            {
                handler.drain(capacity.get(), IFluidHandler.FluidAction.EXECUTE);
                Helpers.playSound(level, player.blockPosition(), SoundEvents.BUCKET_EMPTY);
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            final BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
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
                if (!ConfigFlasks.THIRSTY_DRINK.get() && IPlayerInfo.get(player).getThirst() >= PlayerInfo.MAX_THIRST) {
                    // Don't drink if not thirsty
                    return InteractionResultHolder.fail(stack);
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
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack items = new ItemStack(this);
        items.setDamageValue(stack.getDamageValue());
        return items;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        if (entity instanceof Player player)
        {
            final @Nullable IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (handler != null)
            {
                final FluidStack drained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
                if (drained.getAmount() >= drink) {
                    FluidStack fluidConsumed = handler.drain(drink, IFluidHandler.FluidAction.EXECUTE);
                    final Drinkable drinkable = Drinkable.get(fluidConsumed.getFluid());
                    if (drinkable != null && !level.isClientSide)
                    {
                        drinkable.onDrink(player, fluidConsumed.getAmount());
                    }
                    Helpers.damageItem(stack, entity, EquipmentSlot.MAINHAND);
                }
            }
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 32;
    } // lavish copy indeed, magic numbers anyone?

    @Override
    protected InteractionResultHolder<ItemStack> afterEmptyFailed(IFluidHandler handler, Level level, Player player, ItemStack stack, InteractionHand hand)
    {
        if (player.isCrouching() && ConfigFlasks.SHIFT_EMPTY.get())
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
}

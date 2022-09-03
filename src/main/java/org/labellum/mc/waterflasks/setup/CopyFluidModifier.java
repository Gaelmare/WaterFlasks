package org.labellum.mc.waterflasks.setup;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public enum CopyFluidModifier implements ItemStackModifier.SingleInstance<org.labellum.mc.waterflasks.setup.CopyFluidModifier> {

    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        CraftingContainer inv = RecipeHelpers.getCraftingContainer();
        input.getCapability(Capabilities.FLUID_ITEM).ifPresent(handler -> {
            stack.getCapability(Capabilities.FLUID_ITEM).ifPresent(outhandle -> {
                stack.setDamageValue(0);
                FluidStack drained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                outhandle.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                //all of these inv accesses cause the changes to occur before crafting actually occurs!
                //inv.setItem(4, inv.getItem(4).hurt(1,null,null));
                //inv.removeItem(0, 1); //hacky way to remove input flask
                //inv.setItem(4, Helpers.damageCraftingItem(inv.getItem(4), 1).copy()); //horrific hack
                //inv.setChanged();
            });
        });

        return stack;
    }

    @Override
    public org.labellum.mc.waterflasks.setup.CopyFluidModifier instance()
    {
        return INSTANCE;
    }
}

/*package org.labellum.mc.waterflasks.setup;

import net.dries007.tfc.common.recipes.DamageInputsCraftingRecipe;
import net.dries007.tfc.common.recipes.IRecipeDelegate;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.IShapedRecipe;

public class DamageKnifeShapedRecipe extends DamageInputsCraftingRecipe<IShapedRecipe<CraftingContainer>> implements IRecipeDelegate.Shaped<CraftingContainer> {
    protected DamageKnifeShapedRecipe(ResourceLocation id, IShapedRecipe<CraftingContainer> recipe)
    {
        super(id, recipe);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); ++i)
        {
            ItemStack stack = inv.getItem(i);
            if (stack.isDamageableItem())
            {
                items.set(i, Helpers.damageCraftingItem(stack, 1).copy());
            }
        }
        return items;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Registration.DAMAGE_KNIFE_SHAPED_CRAFTING.get();
    }

}*/

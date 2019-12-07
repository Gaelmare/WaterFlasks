package org.labellum.mc.waterflasks.recipe;

import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import org.labellum.mc.waterflasks.item.ModItems;

public class ModRecipes {

	public static void registerKnapping(RegistryEvent.Register<KnappingRecipe> event) {
        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(ModItems.leatherSide),
                    "   XX", " X XX", "XXXXX", "XXXXX", " XXX ").setRegistryName("leather_side")
        );
	}
}
/*
   XX
 X XX
XXXXX
XXXXX
 XXX
*/
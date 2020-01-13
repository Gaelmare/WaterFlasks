package org.labellum.mc.waterflasks.recipe;

import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import org.labellum.mc.waterflasks.item.ModItems;

import static net.dries007.tfc.api.types.Metal.ItemType.SHEET;
import static net.dries007.tfc.util.forge.ForgeRule.*;
import static net.dries007.tfc.util.skills.SmithingSkill.Type.GENERAL;
import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

public class ModRecipes {

	public static void registerKnapping(RegistryEvent.Register<KnappingRecipe> event) {
        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(ModItems.leatherSide),
                    "  XX ", " XXX ", "XXXXX", " XXX ", "  X  ").setRegistryName("leather_side")
        );
	}
    /*
      XX
     XXX
    XXXXX
     XXX
      X
    */

    public static void registerAnvil(RegistryEvent.Register<AnvilRecipe> event) {
        event.getRegistry().registerAll(
           new AnvilRecipe(new ResourceLocation(MOD_ID,"unfinished_iron_flask"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON,SHEET))),
                   new ItemStack(ModItems.unfinishedFlask), Metal.WROUGHT_IRON.getTier(), GENERAL, PUNCH_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST)
        );
    }
}

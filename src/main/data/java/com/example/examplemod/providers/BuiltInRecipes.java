package com.example.examplemod.providers;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.item.ExampleModItems;
import mod.traister101.datagenutils.data.EnhancedRecipeProvider;
import mod.traister101.datagenutils.data.recipe.CraftingRecipeBuilder;
import mod.traister101.datagenutils.data.recipe.tfc.*;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.items.TFCItems.ItemId;
import net.dries007.tfc.common.recipes.WeldingRecipe.Behavior;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.Metal.ItemType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class BuiltInRecipes extends EnhancedRecipeProvider {

	public BuiltInRecipes(final PackOutput output, final CompletableFuture<Provider> registries,
			final AdditionalRecipeProvider... additionalRecipeProviders) {
		super(output, registries, additionalRecipeProviders);
	}

	@Override
	protected void buildRecipes(final RecipeOutput recipeOutput, final Provider holderLookup) {
		CraftingRecipeBuilder.shaped(ExampleModItems.EXAMPLE_ITEM, 11)
				.pattern("W", "S", "S") // Whole pattern at once
				.define('W', Ingredient.of(ItemTags.PLANKS))
				.define('S', Items.STICK) // You should use a tag for this, but it's an example recipe
				.unlockedBy("has_planks", has(ItemTags.PLANKS))
				.unlockedBy("has_sticks", has(Items.STICK))
				.save(recipeOutput); // We are using the default recipe name which is the items name (prepended by 'crafting/')

		AdvancedCraftingRecipeBuilder.shapeless("example_recipe_folder", ItemStackProvider.of(ExampleModItems.EXAMPLE_ITEM))
				.primaryIngredient(Ingredient.of(TFCTags.Items.BREAD))
				.requires(Ingredient.of(TFCTags.Items.BREAD))
				.requires(Ingredient.of(TFCTags.Items.TOOLS_KNIFE))
				.unlockedBy("has_bread", has(TFCTags.Items.BREAD))
				.unlockedBy("has_knife", has(TFCTags.Items.TOOLS_KNIFE))
				.damageInputs()
				// We set both a folder and a custom name so this recipe ends up in `data/example_mod/recipe/example_recipe_folder`
				// with the name of `custom_recipe_name`
				.save(recipeOutput, ExampleMod.location("custom_recipe_name"));

		AdvancedCraftingRecipeBuilder.shaped(ExampleModItems.EXAMPLE_ITEM, 1)
				.pattern("s")
				.pattern("S")
				.define('s', Tags.Items.RODS_WOODEN)
				.inputItem('S', TFCTags.Items.TOOLS_SAW, 1, 0) // Our saw is in the second row first column 'S'
				.damageInputs()
				.unlockedBy("has_sticks", has(Tags.Items.RODS_WOODEN))
				.unlockedBy("has_saw", has(TFCTags.Items.TOOLS_SAW))
				// This overload is not recommended but is certainly convenient for quick and dirty recipe overrides like for compatibility addons
				.save(recipeOutput.withConditions(new ModLoadedCondition("some_mod_id")), "tfc:override"); // (goes into tfc namespace)

		final var roseGoldIngot = TFCItems.METAL_ITEMS.get(Metal.ROSE_GOLD).get(ItemType.INGOT);
		AnvilRecipeBuilder.welding(ExampleModItems.EXAMPLE_ITEM, 15)
				.bronze() // Require bronze anvil tier
				.firstInput(Ingredient.of(roseGoldIngot))
				.secondInput(Ingredient.of(roseGoldIngot))
				.bonus(Behavior.COPY_BEST) // The bonus behavior
				.save(recipeOutput);

		KnappingRecipeBuilder.leather(ExampleModItems.EXAMPLE_ITEM.toStack(5))
				.pattern("X    ", " X   ", "  X  ", "   X ", "    X")
				.save(recipeOutput);
	}
}
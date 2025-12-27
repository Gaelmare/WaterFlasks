package com.example.examplemod.advancement;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.item.ExampleModItems;
import mod.traister101.datagenutils.data.AdvancementSubProvider;
import mod.traister101.datagenutils.data.util.*;

import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.AdvancementRewards.Builder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class ExampleAdvancements implements AdvancementSubProvider {

	@Override
	public void generate(final AdvancementOutput output, final Provider registries) {
		// Save a reference to our root advancement so we can easily make children
		final var root = AdvancementBuilder.root()
				.display(SimpleDisplayInfo.builder()
						.icon(ExampleModItems.EXAMPLE_ITEM)
						.title("Example title") // The advancement title, lang is automatically generated
						.description("Example description")
						.type(AdvancementType.TASK))
				.requirementsStrategy(Strategy.OR) // This is pointless since there's only one unlock criteria
				.addCriterion("unlocked_example_item_recipe",
						RecipeUnlockedTrigger.unlocked(ExampleMod.location("example_recipe_folder/custom_recipe_name")))
				.save(output, ExampleMod.location("example/root"));
		// Create a child of the root advancement
		AdvancementBuilder.child(root)
				.display(SimpleDisplayInfo.builder()
						.icon(Items.DIAMOND_BLOCK)
						.title("Example Title Diamond Block")
						.description("Craft an example item")
						.type(AdvancementType.CHALLENGE)
						.showToast(true))
				.rewards(Builder.loot(Blocks.DIAMOND_BLOCK.getLootTable())) // The unlock rewards, we roll the diamond block loot table
				.addCriterion("obtained_example_item", InventoryChangeTrigger.TriggerInstance.hasItems(ExampleModItems.EXAMPLE_ITEM))
				.save(output, ExampleMod.location("example/child"));
	}
}
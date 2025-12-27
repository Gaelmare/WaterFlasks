package com.example.examplemod.providers;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.item.ExampleModItems;
import mod.traister101.datagenutils.data.tfc.ItemHeatProvider;
import mod.traister101.datagenutils.data.util.tfc.TFCFluidHeat;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class BuiltInItemHeat extends ItemHeatProvider {

	public BuiltInItemHeat(final PackOutput output, final CompletableFuture<Provider> lookup) {
		super(output, ExampleMod.MOD_ID, lookup);
	}

	@Override
	protected void addData(final Provider provider) {
		// Adds a heat def and a melting recipe for 42 units of rose gold
		addAndMelt("example_heat_def", Ingredient.of(ExampleModItems.EXAMPLE_ITEM), TFCFluidHeat.ROSE_GOLD, 42);
	}
}
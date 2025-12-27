package com.example.examplemod.providers;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.item.ExampleModItems;
import mod.traister101.datagenutils.data.tfc.ItemSizeProvider;
import net.dries007.tfc.common.component.size.*;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class BuiltInItemSizes extends ItemSizeProvider {

	public BuiltInItemSizes(final PackOutput output, final CompletableFuture<Provider> lookup) {
		super(output, ExampleMod.MOD_ID, lookup);
	}

	@Override
	protected void addData(final Provider provider) {
		add("example_size_def", size(ExampleModItems.EXAMPLE_ITEM, Size.HUGE, Weight.VERY_HEAVY));
	}
}
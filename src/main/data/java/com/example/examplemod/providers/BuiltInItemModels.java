package com.example.examplemod.providers;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.item.ExampleModItems;
import com.google.common.base.Preconditions;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class BuiltInItemModels extends ItemModelProvider {

	public static final UncheckedModelFile ITEM_GENERATED = new UncheckedModelFile("item/generated");

	public BuiltInItemModels(final PackOutput packOutput, final ExistingFileHelper existingFileHelper) {
		super(packOutput, ExampleMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		// Usually you'd use
		// basicItem(ExampleModItems.EXAMPLE_ITEM.asItem());
		// but the example item uses the debug texture so we do
		getBuilder(ExampleModItems.EXAMPLE_ITEM).parent(ITEM_GENERATED).texture("layer0", mcLoc("block/debug"));
	}

	/**
	 * Vanilla doesn't handle `/` in registry names, as TFC uses them we should change this to behave in a more sensibleness way
	 */
	@Override
	public ItemModelBuilder getBuilder(final String path) {
		Preconditions.checkNotNull(path, "Path must not be null");
		final ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? mcLoc(path) : modLoc(path));
		existingFileHelper.trackGenerated(outputLoc, MODEL);
		return generatedModels.computeIfAbsent(outputLoc, factory);
	}

	public ItemModelBuilder getBuilder(final ItemLike itemLike) {
		return getBuilder(BuiltInRegistries.ITEM.getKey(itemLike.asItem()).toString());
	}

	/**
	 * Vanilla doesn't properly handle `/` in registry names
	 */
	private ResourceLocation extendWithFolder(final ResourceLocation location) {
		if (location.getPath().startsWith(folder)) return location;
		return location.withPrefix(folder + "/");
	}
}
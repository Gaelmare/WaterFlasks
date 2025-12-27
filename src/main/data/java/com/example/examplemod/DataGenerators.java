package com.example.examplemod;

import com.example.examplemod.providers.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ExampleMod.MOD_ID)
public final class DataGenerators {

	@SubscribeEvent
	private static void gatherData(final GatherDataEvent event) {
		final var generator = event.getGenerator();
		final var lookupProvider = event.getLookupProvider();
		final var existingFileHelper = event.getExistingFileHelper();
		final var packOutput = generator.getPackOutput();

		generator.addProvider(event.includeServer(), new BuiltInItemSizes(packOutput, lookupProvider));
		// The ItemHeatProvider generates extra recipes so we need to hang onto a reference to hand to our recipe provider
		final var builtInItemHeat = generator.addProvider(event.includeServer(), new BuiltInItemHeat(packOutput, lookupProvider));
		generator.addProvider(event.includeServer(), new BuiltInRecipes(packOutput, lookupProvider, builtInItemHeat));
		// The EnhancedAdvancementProvider generates extra language so we need to hang onto a reference to hand to our language provider
		final var builtInAdvancements = generator.addProvider(event.includeServer(),
				BuiltInAdvancements.create(packOutput, lookupProvider, existingFileHelper));

		generator.addProvider(event.includeClient(), new BuiltInLanguage(packOutput, builtInAdvancements));
		generator.addProvider(event.includeClient(), new BuiltInItemModels(packOutput, existingFileHelper));
	}
}
package com.example.examplemod.providers;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.item.*;
import mod.traister101.datagenutils.data.EnhancedLanguageProvider;
import mod.traister101.datagenutils.data.util.LanguageTranslation;

import net.minecraft.data.PackOutput;

import java.util.stream.Stream;

public class BuiltInLanguage extends EnhancedLanguageProvider {

	public BuiltInLanguage(final PackOutput output, final ExtraLanguageProvider... extraLanguageProviders) {
		super(output, ExampleMod.MOD_ID, "en_us", extraLanguageProviders);
	}

	@Override
	protected void addTranslations() {
		// Same as
//		add(LanguageTranslation.item(ExampleModItems.EXAMPLE_ITEM, "Example Item"));
		add(LanguageTranslation.simpleItem(ExampleModItems.EXAMPLE_ITEM));

		add(LanguageTranslation.of(ExampleItem.EXAMPLE_TOOLTIP, "Example Tooltip"));
	}

	@Override
	protected Stream<KnownRegistryContents<?>> knownRegistryContents() {
		return Stream.of(KnownRegistryContents.item(ExampleModItems.ITEMS));
	}
}
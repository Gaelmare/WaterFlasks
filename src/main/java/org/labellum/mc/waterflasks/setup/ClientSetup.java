/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.setup;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.labellum.mc.waterflasks.item.FlaskItem;

import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

public class ClientSetup {

    public static void init()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientSetup::setup);
    }

    private static void setup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ItemProperties.register(Registration.LEATHER_FLASK.get(), new ResourceLocation(MOD_ID, "filled"), (stack, level, living, id) -> {
                return FlaskItem.getEmptinessDisplay(stack);
            });
            ItemProperties.register(Registration.IRON_FLASK.get(), new ResourceLocation(MOD_ID, "filled"), (stack, level, living, id) -> {
                return FlaskItem.getEmptinessDisplay(stack);
            });
        });
    }
}


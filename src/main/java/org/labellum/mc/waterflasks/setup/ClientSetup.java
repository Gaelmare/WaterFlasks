/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks.setup;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.labellum.mc.waterflasks.item.FlaskItem;

import static org.labellum.mc.waterflasks.Waterflasks.MODID;

public class ClientSetup {

    public static void setup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ItemProperties.register(Registration.LEATHER_FLASK.get(), ResourceLocation.fromNamespaceAndPath(MODID, "emptiness"), (stack, level, living, id) -> {
                return FlaskItem.getEmptinessDisplay(stack);
            });
            ItemProperties.register(Registration.IRON_FLASK.get(), ResourceLocation.fromNamespaceAndPath(MODID, "emptiness"), (stack, level, living, id) -> {
                return FlaskItem.getEmptinessDisplay(stack);
            });
            ItemProperties.register(Registration.RED_STEEL_FLASK.get(), ResourceLocation.fromNamespaceAndPath(MODID, "emptiness"), (stack, level, living, id) -> {
                return FlaskItem.getEmptinessDisplay(stack);
            });
        });
    }
}


package org.labellum.mc.waterflasks.setup;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.labellum.mc.waterflasks.item.ItemFlask;

import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ItemProperties.register(Registration.leatherFlask.get(), new ResourceLocation(MOD_ID, "filled"), (stack, level, living, id) -> {
                return ItemFlask.getEmptinessDisplay(stack);
            });
            ItemProperties.register(Registration.ironFlask.get(), new ResourceLocation(MOD_ID, "filled"), (stack, level, living, id) -> {
                return ItemFlask.getEmptinessDisplay(stack);
            });
        });
    }
}


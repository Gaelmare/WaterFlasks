package org.labellum.mc.waterflasks.item;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.objects.CreativeTabsTFC.*;
import org.labellum.mc.waterflasks.ConfigFlasks;

public class ModItems {

    public static void registerModels() {
        leatherFlask.registerItemModel();
        leatherSide.registerItemModel();
        bladder.registerItemModel();
        brokenLeatherFlask.registerItemModel();
        if (ConfigFlasks.GENERAL.enableIron) {
            unfinishedFlask.registerItemModel();
            ironFlask.registerItemModel();
            brokenIronFlask.registerItemModel();
        }
	}
}

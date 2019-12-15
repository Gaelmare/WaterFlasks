package org.labellum.mc.waterflasks.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import org.labellum.mc.waterflasks.ConfigFlasks;

public class ModItems {

    public static ItemBase leatherSide = new ItemBase("leather_side").setCreativeTab(CreativeTabs.MATERIALS);
    public static ItemBase bladder = new ItemBase("bladder").setCreativeTab(CreativeTabs.MATERIALS);
    public static ItemLeatherFlask leatherFlask = new ItemLeatherFlask();
    public static ItemIronFlask ironFlask = new ItemIronFlask();

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                leatherSide,
                bladder,
                leatherFlask
        );
        if (ConfigFlasks.GENERAL.enableIron) {
            registry.register(ironFlask);
        }
    }

    public static void registerModels() {
        leatherFlask.registerItemModel();
        leatherSide.registerItemModel();
        bladder.registerItemModel();
        if (ConfigFlasks.GENERAL.enableIron) {
            ironFlask.registerItemModel();
        }
	}
}

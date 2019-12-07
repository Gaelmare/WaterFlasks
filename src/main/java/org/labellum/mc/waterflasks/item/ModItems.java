package org.labellum.mc.waterflasks.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    public static ItemBase leatherSide = new ItemBase("leather_side").setCreativeTab(CreativeTabs.MATERIALS);
    public static ItemBase bladder = new ItemBase("bladder").setCreativeTab(CreativeTabs.MATERIALS);
    public static ItemWaterFlask leatherFlask = new ItemWaterFlask();

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                leatherSide,
                bladder,
                leatherFlask
        );
    }

    public static void registerModels() {
        leatherFlask.registerItemModel();
        leatherSide.registerItemModel();
        bladder.registerItemModel();
	}
}

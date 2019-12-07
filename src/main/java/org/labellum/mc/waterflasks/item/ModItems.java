package org.labellum.mc.waterflasks.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

    public static ItemBase leatherSide = new ItemBase("leather_side").setCreativeTab(CreativeTabs.MATERIALS);
    public static ItemBase cowBladder = new ItemBase("cow_bladder").setCreativeTab(CreativeTabs.MATERIALS);
    public static ItemBase leatherFlask = new ItemBase("leather_flask").setCreativeTab(CreativeTabs.FOOD);

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                leatherSide,
                cowBladder,
                leatherFlask
        );
    }

    public static void registerModels() {
        leatherFlask.registerItemModel();
        leatherSide.registerItemModel();
        cowBladder.registerItemModel();
	}
}

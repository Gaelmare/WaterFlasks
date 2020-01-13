package org.labellum.mc.waterflasks.item;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.objects.CreativeTabsTFC.*;
import org.labellum.mc.waterflasks.ConfigFlasks;

public class ModItems {

    public static ItemBase leatherSide = new ItemBase("leather_side").setCreativeTab(CT_MISC);
    public static ItemBase bladder = new ItemBase("bladder").setCreativeTab(CT_MISC);
    public static ItemLeatherFlask leatherFlask = new ItemLeatherFlask();
    public static ItemBase unfinishedFlask = new ItemBase("unfinished_iron_flask").setCreativeTab(CT_METAL);
    public static ItemBase brokenLeatherFlask = new ItemBase("broken_leather_flask").setCreativeTab(CT_MISC);
    public static ItemBase brokenIronFlask = new ItemBase("broken_iron_flask").setCreativeTab(CT_METAL);
    public static ItemIronFlask ironFlask = new ItemIronFlask();

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                leatherSide,
                bladder,
                leatherFlask,
                brokenLeatherFlask
        );
        if (ConfigFlasks.GENERAL.enableIron) {
            registry.register(unfinishedFlask);
            registry.register(ironFlask);
            registry.register(brokenIronFlask);
        }
    }

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

package org.labellum.mc.waterflasks.item;


import net.minecraft.world.item.Item;
import org.labellum.mc.waterflasks.ConfigFlasks;

public class ItemLeatherFlask extends ItemFlask {

    protected static int CAPACITY = ConfigFlasks.GENERAL.leatherCap;
    protected static int DRINK = 100; //matches amount of water in TFC Jug

    public ItemLeatherFlask(Item.Properties prop) {
        super(prop, "leather_flask", CAPACITY, DRINK);
    }



}

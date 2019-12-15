package org.labellum.mc.waterflasks.item;


import org.labellum.mc.waterflasks.ConfigFlasks;

public class ItemIronFlask extends ItemFlask {

    protected static int CAPACITY = ConfigFlasks.GENERAL.ironCap;
    protected static int DRINK = 100; //matches amount of water in TFC Jug

    public ItemIronFlask() {
        super("iron_flask", CAPACITY, DRINK);
    }
}

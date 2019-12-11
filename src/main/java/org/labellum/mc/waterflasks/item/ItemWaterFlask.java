package org.labellum.mc.waterflasks.item;


public class ItemWaterFlask extends ItemFlask {

    protected static int CAPACITY = 600;
    protected static int DRINK = 50;

    public ItemWaterFlask() {
        super("leather_flask", CAPACITY, DRINK);
    }
}

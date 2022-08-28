package org.labellum.mc.waterflasks.item;



import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.labellum.mc.waterflasks.ConfigFlasks;

import javax.annotation.Nonnull;

public class ItemIronFlask extends ItemFlask {

    protected static int CAPACITY = ConfigFlasks.GENERAL.ironCap;
    protected static int DRINK = 100; //matches amount of water in TFC Jug

    public ItemIronFlask(Item.Properties prop) {
        super(prop,"iron_flask", CAPACITY, DRINK);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }

}

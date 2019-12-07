package org.labellum.mc.waterflasks.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import org.labellum.mc.waterflasks.Waterflasks;

public class ItemBase extends Item {

    protected String name;

    public ItemBase(String name) {
        this.name = name;
        setTranslationKey(name);
        setRegistryName(name);

        setCreativeTab(CreativeTabs.MATERIALS);

    }

    public void registerItemModel() {
        Waterflasks.proxy.registerItemRenderer(this, 0, name);
	}

	@Override
	public ItemBase setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}

}

/*
 * Work under Copyright. Licensed under GPLv3.
 * See the project README.md and LICENSE.txt for more information.
 */

package org.labellum.mc.waterflasks.fluids;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import net.dries007.tfc.util.Helpers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

/**
 * Lavishly copied from tfc FluidWhitelistHandler. This Handler just inherits from FluidHandlerItemStack instead of
 * FluidHandlerItemStackSimple, which allows it to be partially full.
 */

public class FlaskFluidHandler extends FluidHandlerItemStack
{
    private final TagKey<Fluid> whitelist;

    public FlaskFluidHandler(@Nonnull ItemStack container, int capacity, TagKey<Fluid> whitelist)
    {
        super(container, capacity);
        this.whitelist = whitelist;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        return Helpers.isFluid(fluid.getFluid(), whitelist);
    }
}

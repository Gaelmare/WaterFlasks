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

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

/**
 * Lavishly copied from tfc FluidWhitelistHandler. This Handler just inherits from FluidHandlerItemStack instead of
 * FluidHandlerItemStackSimple, which allows it to be partially full.
 */

public class FlaskFluidHandler extends FluidHandlerItemStack
{
    private final Set<Fluid> whitelist;

    public FlaskFluidHandler(@Nonnull ItemStack container, int capacity, String[] fluidNames)
    {
        this(container, capacity, Arrays.stream(fluidNames).map(FluidRegistry::getFluid).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public FlaskFluidHandler(@Nonnull ItemStack container, int capacity, Set<Fluid> whitelist)
    {
        super(container, capacity);
        this.whitelist = whitelist;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        return whitelist.contains(fluid.getFluid());
    }
}

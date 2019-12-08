package org.labellum.mc.waterflasks.item;

/** Largely borrowed from TFC ItemJug
 *  EUPL license meshes with GPLv3
 */

import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.capability.FluidWhitelistHandler;
import net.dries007.tfc.objects.fluids.properties.DrinkableProperty;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.dries007.tfc.util.FluidTransferHelper;
import org.labellum.mc.waterflasks.Waterflasks;

import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

public abstract class ItemFlask extends ItemTFC {

    private static final int CAPACITY = 600;
    private static final int DRINK = 50;
    private static final int CAP_DAMAGE = 12;

    protected String name;

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.MEDIUM;
    }


    public ItemFlask(String name) {
        super();
        this.name=name;
        setTranslationKey(MOD_ID+"."+name);
        setRegistryName(name);

        setCreativeTab(CreativeTabs.FOOD);

        setMaxDamage (CAP_DAMAGE);
        setNoRepair();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty())
        {
            IFluidHandler flaskCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (flaskCap != null)
            {
                RayTraceResult rayTrace = rayTrace(world, player, true);

                if (flaskCap.drain(CAPACITY, false) != null)
                {
                    player.setActiveHand(hand);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
                else if (!world.isRemote && flaskCap.drain(CAPACITY, false) == null && rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    ItemStack single = stack.copy();
                    single.setCount(1);
                    FluidActionResult result = FluidTransferHelper.tryPickUpFluidGreedy(single, player, world, rayTrace.getBlockPos(), rayTrace.sideHit, CAPACITY, false);
                    if (result.isSuccess())
                    {
                        stack.shrink(1);
                        ItemStack res = result.getResult();
                        res.setItemDamage(CAP_DAMAGE);
                        if (stack.isEmpty()) {
                            return new ActionResult<>(EnumActionResult.SUCCESS, res);
                        }
                        ItemHandlerHelper.giveItemToPlayer(player, res);
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        IFluidHandler flaskCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (flaskCap != null)
        {
            FluidStack fluidConsumed = flaskCap.drain(DRINK, true);
            if (fluidConsumed != null && entityLiving instanceof EntityPlayer)
            {
                DrinkableProperty drinkable = FluidsTFC.getWrapper(fluidConsumed.getFluid()).get(DrinkableProperty.DRINKABLE);
                if (drinkable != null)
                {
                    drinkable.onDrink((EntityPlayer) entityLiving);
                    stack.damageItem(1,entityLiving);
                }
            }
/*            if (Constants.RNG.nextFloat() < 0.02) // 1/50 chance, same as 1.7.10
            {
                stack.shrink(1);
                worldIn.playSound(null, entityLiving.getPosition(), TFCSounds.CERAMIC_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }*/
        }
        return stack;
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(@Nonnull ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack)
    {
        IFluidHandler bucketCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (bucketCap != null)
        {
            FluidStack fluidStack = bucketCap.drain(CAPACITY, false);
            if (fluidStack != null)
            {
                String fluidname = fluidStack.getLocalizedName();
                return new TextComponentTranslation("item.waterflasks."+name+".name", fluidname).getFormattedText();
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            items.add(new ItemStack(this));
            for (FluidWrapper wrapper : FluidsTFC.getAllWrappers())
            {
                if (wrapper.get(DrinkableProperty.DRINKABLE) != null)
                {
                    ItemStack stack = new ItemStack(this);
                    IFluidHandlerItem cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                    if (cap != null)
                    {
                        cap.fill(new FluidStack(wrapper.get(), CAPACITY), true);
                    }
                    items.add(stack);
                }
            }
        }
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidWhitelistHandler(stack, CAPACITY, FluidsTFC.getAllWrappers().stream().filter(x -> x.get(DrinkableProperty.DRINKABLE) != null).map(FluidWrapper::get).collect(Collectors.toSet()));
    }

    public void registerItemModel() {
        Waterflasks.proxy.registerItemRenderer(this, 0, name);
    }

}

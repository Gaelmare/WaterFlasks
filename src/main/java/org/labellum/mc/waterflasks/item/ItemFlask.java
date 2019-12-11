package org.labellum.mc.waterflasks.item;

/** Largely borrowed from TFC ItemJug
 *  EUPL license meshes with GPLv3
 */

import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.properties.DrinkableProperty;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.dries007.tfc.util.FluidTransferHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.items.ItemHandlerHelper;
import org.labellum.mc.waterflasks.Waterflasks;
import org.labellum.mc.waterflasks.fluids.FlaskFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Collectors;

import static net.dries007.tfc.api.capability.food.IFoodStatsTFC.MAX_PLAYER_THIRST;
import static org.labellum.mc.waterflasks.Waterflasks.MOD_ID;

public abstract class ItemFlask extends ItemFluidContainer implements IItemSize {

    private int CAPACITY;
    private int DRINK;

    protected String name;

    public ItemFlask(String name, int CAPACITY, int DRINK) {

        super(CAPACITY);
        this.CAPACITY=CAPACITY;
        this.DRINK=DRINK;
        this.name=name;
        setTranslationKey(MOD_ID+"."+name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.FOOD);

        setMaxDamage (CAPACITY);
        setNoRepair();
        setHasSubtypes(true);
    }
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

    @Override
    public boolean canStack(@Nonnull ItemStack stack) { return false; }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FlaskFluidHandler(stack, CAPACITY, FluidsTFC.getAllWrappers().stream().filter(x -> x.get(DrinkableProperty.DRINKABLE) != null).map(FluidWrapper::get).collect(Collectors.toSet()));
    }

    public void registerItemModel() {
        Waterflasks.proxy.registerItemRenderer(this, 0, name);
    }

    /** nasty dirty hack to handle updating damage from contained amount when filled by right-clicking
     *  would love another option that doesn't involve re-implementing FluidUtil and FluidHandler!
     */
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (!isSelected) return;
//        if (worldIn.getTotalWorldTime() % 17 == 0 ) {
            IFluidHandler flaskCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (flaskCap != null) {
                FluidStack drained = flaskCap.drain(CAPACITY, false);
                if (drained != null) {
                    stack.setItemDamage(CAPACITY - drained.amount);
                }
            }
//      }
    }

    /** Hack to avoid duplicating fluid by filling a barrel from a partially filled flask.
     *  cannot fill or empty a partially full flask with a fluid container.
     */
/*
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        //can't hit a block with this if it's not empty or full
        int dam = getDamage(player.getHeldItem(hand));
        if (dam != 0 && dam != CAPACITY) {
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.PASS;
    }
*/

    @SuppressWarnings("ConstantConditions")
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty())
        {
            // Do not use in creative game mode
            if(player.isCreative())
                return new ActionResult<>(EnumActionResult.PASS, stack);

            IFluidHandler flaskCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (flaskCap != null)
            {
                RayTraceResult rayTrace = rayTrace(world, player, true);

                if (flaskCap.drain(CAPACITY, false) != null)
                {
                    // If contains fluid, allow emptying with shift-right-click
            		if(player.isSneaking())
		            {
			            flaskCap.drain(CAPACITY, true);
			            stack.setItemDamage(CAPACITY);


                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                    player.setActiveHand(hand);
            		// Don't drink if not thirsty
            		FoodStats stats = player.getFoodStats();
                    if (stats instanceof FoodStatsTFC && ((FoodStatsTFC) stats).getThirst() >= MAX_PLAYER_THIRST)
                    {
                        return new ActionResult<>(EnumActionResult.FAIL, stack);
                    }
                }
                else if (!world.isRemote && flaskCap.drain(CAPACITY, false) == null && rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    ItemStack single = stack.copy();
                    single.setCount(1);
                    FluidActionResult result = FluidTransferHelper.tryPickUpFluidGreedy(single, player, world, rayTrace.getBlockPos(), rayTrace.sideHit, Fluid.BUCKET_VOLUME, false);
                    if (result.isSuccess())
                    {
                        stack.shrink(1);
                        ItemStack res = result.getResult();
                        res.setItemDamage(0);
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
            //in case damage is out of date
            FluidStack total = flaskCap.drain(CAPACITY,false);
            if (total != null) {
                stack.setItemDamage((CAPACITY - total.amount) / DRINK);
                FluidStack fluidConsumed = flaskCap.drain(DRINK, true);
                if (fluidConsumed != null) {
                    DrinkableProperty drinkable = FluidsTFC.getWrapper(fluidConsumed.getFluid()).get(DrinkableProperty.DRINKABLE);
                    if (drinkable != null) {
                        drinkable.onDrink((EntityPlayer) entityLiving);
                        stack.damageItem(1, entityLiving);
                    }
                }
            }
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
                return new TextComponentTranslation(MOD_ID+".filled_"+name, fluidname).getFormattedText();
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
}

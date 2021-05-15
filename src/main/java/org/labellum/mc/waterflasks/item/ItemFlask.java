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

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import org.labellum.mc.waterflasks.ConfigFlasks;
import org.labellum.mc.waterflasks.Waterflasks;
import org.labellum.mc.waterflasks.fluids.FlaskFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Collectors;

import static net.dries007.tfc.api.capability.food.IFoodStatsTFC.MAX_PLAYER_THIRST;
import static org.labellum.mc.waterflasks.Waterflasks.FLASK_BREAK;
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

        if (ConfigFlasks.GENERAL.damageFactor == 0)
        {
            setMaxDamage(Integer.MAX_VALUE);
        }
        else {
            setMaxDamage (CAPACITY/ConfigFlasks.GENERAL.damageFactor);
        }
        setHasSubtypes(true);
    }

// Fix #12 by actually implementing the MC function that limits stack sizes
    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return getStackSize(stack);
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
        initModel(this, 0, name);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(Item item, int meta, String id) {
        ModelResourceLocation modelFull = new ModelResourceLocation(Waterflasks.MOD_ID + ":" + id , "inventory");
        ModelResourceLocation model4 = new ModelResourceLocation(Waterflasks.MOD_ID + ":" + id + "-4", "inventory");
        ModelResourceLocation model3 = new ModelResourceLocation(Waterflasks.MOD_ID + ":" + id + "-3", "inventory");
        ModelResourceLocation model2 = new ModelResourceLocation(Waterflasks.MOD_ID + ":" + id + "-2", "inventory");
        ModelResourceLocation model1 = new ModelResourceLocation(Waterflasks.MOD_ID + ":" + id + "-1", "inventory");
        ModelResourceLocation model0 = new ModelResourceLocation(Waterflasks.MOD_ID + ":" + id + "-0", "inventory");

        ModelBakery.registerItemVariants(this, modelFull, model4, model3, model2, model1, model0);

        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                switch ((int) Math.floor(getLiquidAmount(stack)/(double)CAPACITY * 5F)) {
                    case 5:
                        return modelFull;
                    case 4:
                        return model4;
                    case 3:
                        return model3;
                    case 2:
                        return model2;
                    case 1:
                        return model1;
                    default:
                        return model0;
                }
            }
        });
    }

    public int getLiquidAmount(ItemStack stack) {
        int content = 0;
        IFluidHandler flaskCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (flaskCap != null) {
            FluidStack drained = flaskCap.drain(CAPACITY, false);
            if (drained != null) {
                content = drained.amount;
            }
        }
        return content;
    }

    /**
     * Returns the packed int RGB value used to render the durability bar in the GUI.
     * Retrieves no-alpha RGB color from liquid to use in durability bar
     *
     * @param stack Stack to get color from
     * @return A packed RGB value for the durability colour (0x00RRGGBB)
     */
    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        IFluidHandler flaskCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (flaskCap != null) {
            FluidStack drained = flaskCap.drain(CAPACITY, false);
            if (drained != null) {
                Fluid fluid = drained.getFluid();
                return fluid.getColor();
            }
        }
        return super.getRGBDurabilityForDisplay(stack);
    }



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
                // If contains fluid, allow emptying with shift-right-click
                if(player.isSneaking())
                {
                    flaskCap.drain(CAPACITY, true);

                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
                // Try to Fill
                RayTraceResult rayTrace = rayTrace(world, player, true);
                FluidStack cont = flaskCap.drain(CAPACITY, false);
                if (!world.isRemote && (cont == null || cont.amount < CAPACITY) && rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    ItemStack single = stack.copy();
                    single.setCount(1);
                    FluidActionResult result = FluidTransferHelper.tryPickUpFluidGreedy(single, player, world, rayTrace.getBlockPos(), rayTrace.sideHit, Fluid.BUCKET_VOLUME, false);
                    if (result.isSuccess())
                    {
                        stack.shrink(1);
                        ItemStack res = result.getResult();
                        if (stack.isEmpty()) {
                            return new ActionResult<>(EnumActionResult.SUCCESS, res);
                        }
                        ItemHandlerHelper.giveItemToPlayer(player, res);
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                }
                //Try to Drink
                FoodStats stats = player.getFoodStats();
                if (stats instanceof FoodStatsTFC && ((FoodStatsTFC) stats).getThirst() >= MAX_PLAYER_THIRST)
                {
                    // Don't drink if not thirsty
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }
                else if (cont != null && cont.amount >= DRINK)
                {
                    player.setActiveHand(hand);
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
            FluidStack total = flaskCap.drain(CAPACITY,false);
            if (total != null && total.amount > 0) {
                FluidStack fluidConsumed = flaskCap.drain(DRINK, true);
                if (fluidConsumed != null) {
                    DrinkableProperty drinkable = FluidsTFC.getWrapper(fluidConsumed.getFluid()).get(DrinkableProperty.DRINKABLE);
                    if (drinkable != null) {
                        drinkable.onDrink((EntityPlayer) entityLiving);
                        if (stack.getItemDamage() == stack.getMaxDamage()) {
                            ResourceLocation name = stack.getItem().getRegistryName();
                            //break item, play sound
                            worldIn.playSound(null, entityLiving.getPosition(), FLASK_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                            if (name.toString().contains("leather"))
                            {
                                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entityLiving, new ItemStack(ModItems.brokenLeatherFlask));
                            }
                            else
                            {
                                ItemHandlerHelper.giveItemToPlayer((EntityPlayer) entityLiving, new ItemStack(ModItems.brokenIronFlask));
                            }
                            stack.shrink(1); //race condition here, seems to only sometimes work if done before giving broken flask
                        }
                        else
                        {
                            stack.damageItem(1, entityLiving);
                        }
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
                return new TextComponentTranslation("item."+MOD_ID+".filled_"+name+".name", fluidname).getFormattedText();
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

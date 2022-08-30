package org.labellum.mc.waterflasks;

/*
    Copyright (c) 2019 Gaelmare

    This file is part of Waterflasks.

    Waterflasks is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    WaterFlasks is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WaterFlasks.  If not, see <https://www.gnu.org/licenses/>.
*/

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.util.Helpers;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.labellum.mc.waterflasks.item.ModItems;
import org.labellum.mc.waterflasks.proxy.CommonProxy;
import org.labellum.mc.waterflasks.recipe.ModRecipes;
import org.labellum.mc.waterflasks.setup.ClientSetup;
import org.labellum.mc.waterflasks.setup.ModSetup;
import org.labellum.mc.waterflasks.setup.Registration;

@Mod(Waterflasks.MOD_ID)
public class Waterflasks {

    public static final String MOD_ID = "waterflasks";
    public static final String MOD_NAME = "WaterFlasks";
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Many thanks to Shadowfacts' 1.12.2 and McJty's 1.18.2 modding tutorials. Fingerprints from them may remain...
     */

    public static Waterflasks INSTANCE;

    public Waterflasks() {

        // Register the deferred registry
        //ModSetup.setup();
        Registration.init();
        ConfigFlasks.register();

        // Register the setup method for modloading
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        //modbus.addListener(ModSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(ClientSetup::init));
    }

}

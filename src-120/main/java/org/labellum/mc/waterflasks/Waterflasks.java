/*
 * Waterflasks, Copyright (C) 2022 Gaelmare
 * Licensed under v3 of the GPL. You may obtain a copy of the license at:
 * https://github.com/Gaelmare/WaterFlasks/blob/1.18/LICENSE
 */

package org.labellum.mc.waterflasks;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.mojang.logging.LogUtils;
import org.labellum.mc.waterflasks.setup.ClientSetup;
import org.labellum.mc.waterflasks.setup.ModSetup;
import org.labellum.mc.waterflasks.setup.Registration;
import org.slf4j.Logger;

@Mod(Waterflasks.MOD_ID)
public class Waterflasks {

    public static final String MOD_ID = "waterflasks";
    public static final String MOD_NAME = "WaterFlasks";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Many thanks to Shadowfacts' 1.12.2 and McJty's 1.18.2 modding tutorials. Fingerprints from them may remain...
     */
    public Waterflasks() {

        // Register the deferred registry
        ConfigFlasks.register();
        Registration.init();
        ModSetup.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientSetup.init();
        }
    }

}

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

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.labellum.mc.waterflasks.setup.ClientSetup;
import org.labellum.mc.waterflasks.setup.Registration;

@Mod(Waterflasks.MOD_ID)
public class Waterflasks {

    public static final String MOD_ID = "waterflasks";
    public static final String MOD_NAME = "WaterFlasks";
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * Many thanks to Shadowfacts' 1.12.2 and McJty's 1.18.2 modding tutorials. Fingerprints from them may remain...
     */

    public Waterflasks() {

        // Register the deferred registry
        ConfigFlasks.register();
        Registration.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientSetup.init();
        }
    }

}

package com.kamilyatou.kamilsmcmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KamilsMcMod implements ModInitializer {
    public static final String MOD_ID = "kamilsmcmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Kamil's Mod (kamilsmcmod) initialized! Bad Arms Dealers and RE4 Merchant are in town.");
    }
}
